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
        RAISE EXCEPTION 'A message must be sent to a user or group' USING ERRCODE '00001';
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
CREATE OR REPLACE TRIGGER updateGroup
    AFTER DELETE
    ON groupMember
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
EXECUTE FUNCTION createProfile();
