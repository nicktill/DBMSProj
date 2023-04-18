----------------------------------------------------------
-- BeSocial Database Triggers                           --
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann --
----------------------------------------------------------

-- Message Recipient Trigger

-- We want to add a corresponding entry into the messageRecipient relation upon adding a new message to the message relation
CREATE OR REPLACE FUNCTION add_message_recipient()
    RETURNS TRIGGER AS
$$
DECLARE
    rec_groupMember groupmember%ROWTYPE;

BEGIN
    -- If both toUserID and gid are both null throw an exception
    IF NEW.touserid IS NULL AND NEW.togroupid IS NULL THEN
        RAISE EXCEPTION 'A message must be sent to a user or group' USING ERRCODE = '00001';
    END IF;

    -- toUserID and toGroupID are mutually exclusive amongst each other
    IF NEW.touserid IS NOT NULL THEN
        -- If the message was sent to a user, add the message recipient entry
        INSERT INTO messagerecipient VALUES (new.msgid, new.touserid);
    ELSE
        -- If the message was sent to a group, add entries for each groupMember
        FOR rec_groupMember IN SELECT * FROM groupmember WHERE gid = NEW.togroupid AND NEW.fromid != groupmember.userid
            LOOP
                INSERT INTO messagerecipient VALUES (new.msgid, rec_groupMember.userid);
            END LOOP;
    END IF;

    RETURN NEW; -- Regardless, return new to signify to the trigger that there were no errors
END;
$$ language plpgsql;

CREATE OR REPLACE TRIGGER addMessageRecipient
    AFTER INSERT
    ON message
    FOR EACH ROW -- Why does this have to be a row-level trigger rather than a table-level trigger?
EXECUTE FUNCTION add_message_recipient();

-- addMemberToGroup Trigger

-- addMember function
-- removing the pendingGroupMember AFTER INSERT to groupMember, so we can use this trigger to update the groupMember table
CREATE OR REPLACE FUNCTION update_group()
    RETURNS TRIGGER AS
$$
DECLARE
    maxLast     TIMESTAMP;
    rec_pending pendinggroupmember%ROWTYPE;
    groupSize   INT := 0;
    curSize     INT := 0;
    curTime     TIMESTAMP;

BEGIN
    SELECT COUNT(userID)
    INTO curSize
    FROM groupMember
    WHERE gID = NEW.gID;

    SELECT MAX(lastConfirmed)
    INTO maxLast
    FROM groupmember
    WHERE gid = OLD.gid;

    SELECT size
    INTO groupSize
    FROM groupinfo
    WHERE gid = OLD.gid;

    SELECT pseudo_time
    INTO curTime
    FROM clock;

    FOR rec_pending IN SELECT *
                       FROM pendinggroupmember
                       WHERE gid = OLD.gid AND requesttime <= maxLast
                       ORDER BY requesttime
        LOOP
            IF curSize < groupSize THEN
                INSERT INTO groupmember VALUES (rec_pending.gid, rec_pending.userid, 'member', curTime);
                DELETE FROM pendinggroupmember WHERE gid = rec_pending.gid AND userid = rec_pending.userid;
                curSize := curSize + 1;
            END IF;
        END LOOP;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- addMember trigger
DROP TRIGGER IF EXISTS updateGroup ON groupmember CASCADE;
CREATE CONSTRAINT TRIGGER updateGroup
    AFTER DELETE
    ON groupMember
    DEFERRABLE INITIALLY IMMEDIATE
    FOR EACH ROW
EXECUTE FUNCTION update_group();

-- Register new user

CREATE OR REPLACE FUNCTION createProfile()
    RETURNS TRIGGER AS
$$
DECLARE
    curTime TIMESTAMP;
BEGIN
    SELECT pseudo_time
    INTO curTime
    FROM clock;

    NEW.lastlogin = curTime;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER createNewProfile
    BEFORE INSERT
    ON profile
    FOR EACH ROW
    EXECUTE FUNCTION createProfile();

-- Trigger to make sure profile IDs keep increasing by 1

-- We want to make sure that the profile ID's will be the next highest integer available
CREATE OR REPLACE FUNCTION increment_pid()
    RETURNS TRIGGER AS
$$
DECLARE
    maxID int := NULL;
BEGIN
    SELECT MAX(userID)
    INTO maxID
    FROM profile;

    IF maxID IS NULL THEN
        NEW.userID = 0;
    ELSE
        NEW.userID = maxID + 1;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER incrementUserID
    BEFORE INSERT
    ON profile
    FOR EACH ROW
EXECUTE FUNCTION increment_pid();

-- add to trigger2.sql

-- IF EXISTS ALREADY DROP
 DROP FUNCTION IF EXISTS listPendingFriends(integer);
-- * tested and works
CREATE OR REPLACE FUNCTION listPendingFriends(userID INT)
    RETURNS TABLE(requestText text, fromID integer)
    AS
$$
BEGIN
    -- cast to text because of the way postgres handles text
    RETURN QUERY SELECT pf.requestText::text AS requestText, pf.fromID AS fromID FROM pendingFriend pf WHERE pf.toID = userID;
END;
$$ 
LANGUAGE plpgsql;


-- * works (could use more testing)
CREATE OR REPLACE FUNCTION deletePending()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM pendingFriend WHERE fromID = NEW.userID1 AND toID = NEW.userID2;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER deletePendingFriendAfterInsert
    AFTER INSERT
    ON friend
    FOR EACH ROW
EXECUTE FUNCTION deletePending();
-- Adds a friend request for a user
CREATE OR REPLACE FUNCTION addFriendRequest(fromUser INT, toUser INT, text VARCHAR(200))
RETURNS BOOLEAN AS
$$
DECLARE

BEGIN
    IF text IS NOT NULL THEN
        INSERT INTO pendingFriend VALUES (fromUser, toUser, text);
    ELSE
        INSERT INTO pendingFriend VALUES (fromUser, toUser);
    END IF;
    
    RETURN TRUE;

    EXCEPTION WHEN OTHERS THEN
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Change timestamp in groupMember for new insert

CREATE OR REPLACE FUNCTION createMember()
    RETURNS TRIGGER AS
$$
DECLARE
    curTime TIMESTAMP;
BEGIN
    SELECT pseudo_time
    INTO curTime
    FROM clock;

    NEW.lastConfirmed = curTime;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER createNewMember
    BEFORE INSERT
    ON groupMember
    FOR EACH ROW
    EXECUTE FUNCTION createMember();

-- Adds a user to pendingGroupMember upon request
CREATE OR REPLACE PROCEDURE addPendingMember(gID INT, uID int, requestText VARCHAR(200))
AS
$$
    BEGIN
        IF requestText IS NULL THEN
            INSERT INTO pendinggroupmember VALUES (addPendingMember.gID, uID, (SELECT pseudo_time FROM clock));
        ELSE
            INSERT INTO pendinggroupmember VALUES (addPendingMember.gID, uID, requestText, (SELECT pseudo_time FROM clock));
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- Change timestamp in groupMember for new insert

CREATE OR REPLACE FUNCTION createPendingGroupMember()
    RETURNS TRIGGER AS
$$
DECLARE
    curTime TIMESTAMP;
BEGIN
    SELECT pseudo_time
    INTO curTime
    FROM clock;

    NEW.requestTime = curTime;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER createPendingGroupMember
    BEFORE INSERT
    ON pendingGroupMember
    FOR EACH ROW
    EXECUTE FUNCTION createPendingGroupMember();

-- Generate a message ID for messages inserted into table

CREATE OR REPLACE FUNCTION createMessageID()
    RETURNS TRIGGER AS
$$
DECLARE
    maxID int := NULL;
    curTime TIMESTAMP;
BEGIN
    SELECT MAX(msgID)
    INTO maxID
    FROM message;

    IF maxID IS NULL THEN
        NEW.msgID = 0;
    ELSE
        NEW.msgID = maxID + 1;
    END IF;

    SELECT pseudo_time
    INTO curTime
    FROM clock;

    NEW.timeSent = curTime;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER createNewMessageID
    BEFORE INSERT
    ON message
    FOR EACH ROW
    EXECUTE FUNCTION createMessageID();

-- Function that sends a message to everyone in a given group
CREATE OR REPLACE FUNCTION sendMessageToGroup(user_ID int, group_ID int, messageText VARCHAR(200))
RETURNS BOOLEAN
AS
$$
DECLARE
    member_record RECORD;
BEGIN
    -- Get the members of gID except for the sender
    -- Loop through these members and for each, insert into the message table
        -- There is a trigger which handles message ID and timestamp
        -- There is also a trigger which handles inserting into messageRecipient table
    FOR member_record IN SELECT userID FROM groupMember WHERE gID = group_ID AND userID != user_ID
    LOOP
        INSERT INTO message VALUES(-1, user_ID, messageText, member_record.userID, group_ID, '2022-01-01 00:00:00');
        -- TODO: Address changes to message id and time with steven
    END LOOP;

    RETURN true;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE leaveGroup(userID INT, gID INT)
AS
$$
    DECLARE
        groupNum INT := NULL;
        rec_pending pendinggroupmember%ROWTYPE := NULL;
    BEGIN
        -- Set the constraints to be deferred
        SET CONSTRAINTS ALL DEFERRED;

        -- Make sure that the user is in the group
        SELECT G.gid INTO groupNum
        FROM groupmember AS G
        WHERE G.userid=leaveGroup.userID AND G.gid=leaveGroup.gID;

        IF groupNum IS NULL THEN
            RAISE EXCEPTION 'Not a member of any Groups' USING ERRCODE = '00001';
        END IF;

        -- Now remove the group member
        DELETE FROM groupmember AS g WHERE g.userid = leaveGroup.userID AND g.gid = leaveGroup.gID;

        -- Now add the FIRST member and update their last confirmed time
        SELECT * INTO rec_pending
        FROM pendinggroupmember AS P
        WHERE p.gid=leaveGroup.gID
        ORDER BY requesttime;

        IF rec_pending IS NOT NULL THEN
            INSERT INTO groupmember VALUES (leaveGroup.gID, rec_pending.userid, 'member', (SELECT pseudo_time FROM clock));
            -- Trigger handles the removal of the member
        END IF;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION sendMessageToUser(fromUser INT, toUser INT, text varchar(200))
RETURNS BOOLEAN AS
$$
    DECLARE
        time timestamp;
        maxMessage INT;
    BEGIN
        -- Get the max user id
        SELECT MAX(msgid) INTO maxMessage
        FROM message;

        IF maxMessage IS NULL THEN
            maxMessage := 0;
        end if;

        -- Get the time for when the message will be sent
        SELECT pseudo_time INTO time
        FROM clock;

        -- Send the message
        INSERT INTO message VALUES (maxMessage + 1, fromUser, text, toUser, NULL, time);

        RETURN true;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION getMessages(toUser INT, newMsg BOOLEAN)
RETURNS TABLE (msgID INT, messageBody VARCHAR(200), timeSent TIMESTAMP) AS
$$
    BEGIN
        IF newMsg THEN
            -- Get all messages sent to the user after they logged in
            RETURN QUERY
                SELECT M.msgid, M.messagebody, M.timesent
                FROM messagerecipient AS MR NATURAL JOIN message AS M
                WHERE MR.userid=toUser AND M.timesent > (SELECT lastlogin FROM profile WHERE userid = toUser)
                ORDER BY M.timesent;
        ELSE
            RETURN QUERY
                SELECT M.msgid, M.messagebody, M.timesent
                FROM messagerecipient AS MR NATURAL JOIN message AS M
                WHERE MR.userid=toUser
                ORDER BY M.timesent;
        end if;
    end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION getFriends(uID INT)
RETURNS TABLE (friendID INT, name VARCHAR(50)) AS
$$
    BEGIN
        -- Get all friends of the user and their respective information
        RETURN QUERY
        (
        SELECT F.userid2, P.name
        FROM friend F JOIN profile P ON F.userid2 = P.userid
        WHERE F.userid1 = uID
        )
        UNION
        (
        SELECT F.userid1, P.name
        FROM friend F JOIN profile P ON F.userid1 = P.userid
        WHERE F.userid2 = uID
        );
    end;
$$ LANGUAGE plpgsql;

-- Returns profile information of a specified friend
CREATE OR REPLACE FUNCTION getFriendInfo(userID INT, friendID INT)
RETURNS SETOF profile AS
$$
    DECLARE
        rec_friend friend%ROWTYPE := NULL;
    BEGIN
        -- Validate that the user is actually a friend
        SELECT * INTO rec_friend
        FROM friend
        WHERE (userid1=getFriendInfo.userID AND userid2=friendID) OR (userid2=getFriendInfo.userID AND userid1=friendID);

        -- Raise exception
        IF rec_friend IS NULL THEN
            RAISE EXCEPTION 'This user is not a friend of the logged in account' USING ERRCODE='00001';
        end if;

        -- Safe to return result
        RETURN QUERY SELECT * FROM profile P WHERE P.userid=getFriendInfo.friendID;
    end;
$$ LANGUAGE plpgsql;

-- CREATE OR REPLACE FUNCTION rankProfiles()
-- RETURNS TABLE (rank INT, numFriends INT, id INT, name VARCHAR(50)) AS
-- $$
--     DECLARE
--
--     BEGIN
--         -- Get number of friends for every users
--         -- This is done by summing the two counts of both sides of the friend relationship
--         SELECT COUNT(userid2)
--         FROM friend
--         GROUP BY userid1;
--
--         SELECT COUNT(userid1)
--         FROM friend
--         GROUP BY userid2;
--
--         -- Combine to get table of
--     end;
-- $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION topMessages(uID INT, k INT, x INT)
RETURNS TABLE (recipient INT, mCount BIGINT, rank BIGINT) AS
$$
    DECLARE
        startDate TIMESTAMP;
        cur_time TIMESTAMP;

    BEGIN
        -- Get the current time
        SELECT pseudo_time INTO cur_time FROM clock;

        -- Calculate the furthest back date
        SELECT cur_time - ((x * 30) || ' days')::INTERVAL INTO startDate;

        -- Yay large query
        RETURN QUERY
            SELECT rec, (coalesce(CT.msgCount, 0) + coalesce(CF.msgCount, 0)) as msgCount, RANK() OVER (ORDER BY msgCount) AS rank
            FROM (
                    SELECT M.fromid AS rec, COUNT(M.msgid) AS msgCount
                    FROM message M
                    WHERE M.touserid=uID AND M.timesent BETWEEN startDate AND cur_time
                    GROUP BY rec
                ) CT NATURAL FULL OUTER JOIN (
                    SELECT M.touserid AS rec, COUNT(M.msgid) AS msgCount
                    FROM message M
                    WHERE M.fromid=uID AND M.timesent BETWEEN startDate AND cur_time AND M.touserid IS NOT NULL
                    GROUP BY rec
                ) CF
            ORDER BY rank
            FETCH FIRST topMessages.k ROWS ONLY;
    end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION threeDegrees(startID INT, endID INT)
RETURNS TABLE (fromID INT, secondID INT, thirdID INT, toID INT) AS
$$
    DECLARE
        rec_user1 friend%ROWTYPE;
        rec_user2 friend%ROWTYPE;
        rec_user3 friend%ROWTYPE;
        secondUser INT;
        thirdUser INT;

    BEGIN
        -- Check if they are friends with the person
        IF (SELECT userid1 FROM friend WHERE (userid1=startID AND userid2=endID) OR (userid1=endID AND userid2=startID))
            IS NOT NULL THEN
            RETURN QUERY SELECT startID, -1, -1, endID;
        end if;

        FOR rec_user1 IN SELECT * FROM friend WHERE userid1=startID OR userid2=startID
        LOOP
            -- First hop
            IF rec_user1.userid1=startID THEN
                secondUser := rec_user1.userid2;
            ELSE
                secondUser := rec_user1.userid1;
            end if;
            FOR rec_user2 IN SELECT * FROM friend WHERE (userid1=secondUser OR userid2=secondUser) AND (userid1!=startID AND userid2!=startID)
            LOOP
                -- Second Hop
                IF rec_user2.userid1=secondUser THEN
                    thirdUser := rec_user2.userid2;
                ELSE
                    thirdUser := rec_user2.userid1;
                end if;

                -- Check if we have finished
                IF thirdUser=endID THEN
                    RETURN QUERY SELECT startID, secondUser, -1, endID;
                end if;

                -- Now check if we can get from third user to the last
                -- Checking if we can do the final hop
                SELECT * INTO rec_user3
                FROM friend
                WHERE (userid1=thirdUser AND userid2=endID) OR (userid2=thirdUser AND userid1=endID);

                IF rec_user3 IS NOT NULL THEN
                    RETURN QUERY SELECT startID, secondUser, thirdUser, endID;
                end if;
            end loop;
        end loop;

        -- RAISE EXCEPTION 'There is no three degree relation with this user' USING ERRCODE = '00001';
        RETURN QUERY SELECT -1, -1, -1, -1;
        -- TODO: Ask brian about returning error
    end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION rankProfiles()
RETURNS TABLE (uID INT, numFriends BIGINT, rank BIGINT) AS
$$
    DECLARE
        rec_profile profile%ROWTYPE;
        rec_groupMember groupmember%ROWTYPE;
        rec_Friendship RECORD;
        rankCount INT;
        friendsCount INT;

    BEGIN
        -- Create temporary table to store results
        CREATE TEMPORARY TABLE profileRanks (
            uID INT PRIMARY KEY,
            numFriends BIGINT
        )
        ON COMMIT DROP;

        -- TODO: Check if we should omit profile 0
        FOR rec_profile IN SELECT * FROM profile
        LOOP
            rankCount := 0;

            -- Get total number of friends that user has
            SELECT coalesce(COUNT(*), 0) INTO rankCount FROM getOneWayFriends() G WHERE G.userid1=rec_profile.userid;

            -- Add the number of friends each of their friends have
            FOR rec_Friendship IN SELECT * FROM getOneWayFriends() G WHERE G.userid1=rec_profile.userid
            LOOP
                SELECT coalesce(COUNT(*), 0) INTO friendsCount FROM getOneWayFriends() G WHERE G.userid1=rec_Friendship.userid2;
                rankCount := rankCount + friendsCount;
            end loop;

            -- Now for each group the user is in, add their total number of group members who are not them or their friend
            FOR rec_groupMember IN SELECT * FROM groupmember WHERE userid=rec_profile.userid
            LOOP
                -- Make sure to not double count the user's friends
                SELECT coalesce(COUNT(G.userid), 0) INTO friendsCount
                FROM groupmember G
                WHERE G.gid=rec_groupmember.gid
                  AND G.userid!=rec_profile.userid
                  AND G.userid NOT IN (SELECT F.userid2 FROM getOneWayFriends() F WHERE F.userid1=rec_profile.userid);
                rankCount := rankCount + friendsCount;
            end loop;

            INSERT INTO profileRanks VALUES (rec_profile.userid, rankCount);
        end loop;

        RETURN QUERY
            SELECT PR.uID, PR.numFriends, RANK() OVER (ORDER BY PR.numFriends DESC) AS rank
            FROM profileRanks PR
            ORDER BY rank;
    end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION getOneWayFriends()
RETURNS TABLE (userid1 INT, userid2 INT) AS
$$
    BEGIN
        -- This is as it sounds
        RETURN QUERY SELECT f1.userid2 AS userid1, f1.userid1 AS userid2 FROM friend f1
                     UNION
                     SELECT f2.userid1, f2.userid2 FROM friend f2;
    end;
$$ LANGUAGE plpgsql;
