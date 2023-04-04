----------------------------------------------------------
-- BeSocial Database Triggers                           --
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann --
----------------------------------------------------------

-- TODO: Check input integrity
-- TODO: Check if user being added to a group is a pending member before being added?
-- TODO: Add trigger to make sure user is in group message is sent to?
-- TODO: Ask about using the clock with triggers

-- Group Size Limit Trigger

-- We want a trigger to make sure that the group size will never go over the max size
CREATE OR REPLACE FUNCTION check_group_size()
    RETURNS TRIGGER AS
$$
DECLARE
    groupMaxSize  int := 0;
    groupCurrSize int := 0;

BEGIN
    SELECT size
    INTO groupMaxSize
    FROM groupInfo
    WHERE gID = NEW.gID;

    SELECT COUNT(userID)
    INTO groupCurrSize
    FROM groupMember
    WHERE gID = NEW.gID;

    IF groupMaxSize IS NULL THEN
        -- Group/group size is not valid
        RAISE EXCEPTION 'Group is not valid' USING ERRCODE = '00004';
    END IF;

    IF groupCurrSize + 1 > groupMaxSize THEN
        -- Should not make this change, return null
        RAISE EXCEPTION 'Cannot exceed max group size' USING ERRCODE = '00001';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER groupSize
    BEFORE INSERT
    ON groupMember
    FOR EACH ROW
EXECUTE FUNCTION check_group_size();

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

-- Message Recipient Trigger

-- We want to add a corresponding entry into the messageRecipient relation upon adding a new message to the message relation
CREATE OR REPLACE FUNCTION add_message_recipient()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO messagerecipient VALUES (new.msgid, new.touserid);
    RETURN NEW; -- Good to go ahead and insert
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
BEGIN
    DELETE FROM pendingGroupMember WHERE gID = NEW.gID AND userID = NEW.userID; --REMOVE PENDING MEMBER REQUEST from pendingGroupMember Table
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- addMember trigger
CREATE OR REPLACE TRIGGER updateGroup
    AFTER INSERT
    ON groupMember
    FOR EACH ROW
EXECUTE FUNCTION update_group();

-- Trigger to make sure friendships are not double

-- We want to make sure that friends are not repeated
CREATE OR REPLACE FUNCTION resolve_friend()
    RETURNS TRIGGER AS
$$
DECLARE
    friendRecord friend%ROWTYPE := NULL;
    minID        int            := 0;
    maxID        int            := 0;
BEGIN
    -- Order the foreign keys such that id1 < id2
    IF NEW.userid1 > NEW.userid2 THEN
        minID := NEW.userid2;
        maxID := NEW.userid1;
    ELSE
        minID := NEW.userid1;
        maxID := NEW.userid2;
    END IF;

    -- Now actually set id1 < id2
    NEW.userid1 := minID;
    NEW.userid2 := maxID;

    -- Checking the friend relationship doesn't already exist
    SELECT *
    INTO friendRecord
    FROM friend
    WHERE userid1 = minID
      AND userid2 = maxID;

    IF friendRecord IS NOT NULL THEN
        RAISE EXCEPTION 'Friendship already exists' USING ERRCODE = '00002';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER friendOrder
    BEFORE INSERT
    ON friend
    FOR EACH ROW
EXECUTE FUNCTION resolve_friend();
