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

/*
CREATE OR REPLACE FUNCTION confirmFriendRequests()
    RETURNS VOID $$
    $$ LANGUAGE plpgsql;
*/
/*
-- ! WORK IN PROGRESS
CREATE OR REPLACE FUNCTION listPendingFriends(int userID)
    RETURNS SETOF pendingFriend 
    AS
$$
BEGIN
    RETURN QUERY SELECT requestText FROM pendingFriend WHERE toID = userID;
END;
$$ LANGUAGE plpgsql;
-- ! WORK IN PROGRESS
*/
/*
CREATE OR REPLACE FUNCTION addFriendRequest(from INT, to INT, text VARCHAR(200))
RETURNS BOOLEAN AS
$$
DECLARE

BEGIN
    IF text IS NOT NULL THEN
        INSERT INTO pendingFriend VALUES (from, to);
    ELSE
        INSERT INTO pendingFriend VALUES (from, to, text);
    END IF;
    
    RETURN TRUE;

    EXCEPTION WHEN OTHERS THEN
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

 */

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
            INSERT INTO pendinggroupmember VALUES (gID, uID);
        ELSE
            INSERT INTO pendinggroupmember VALUES (gID, uID, requestText);
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
        INSERT INTO message VALUES(NULL, user_ID, messageText, member_record.userID, group_ID, NULL);
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
        WHERE G.userid=leaveGroup.userID AND G.gid=leaveGroup.gID
        FOR UPDATE OF pendinggroupmember, groupmember;

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
            DELETE FROM pendinggroupmember AS P
                   WHERE P.userid=rec_pending.userid AND P.gid=rec_pending.gid;
        END IF;
    END;
$$ LANGUAGE plpgsql;