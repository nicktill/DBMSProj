----------------------------------------------------------
-- BeSocial Database Triggers                           --
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann --
----------------------------------------------------------

CREATE OR REPLACE FUNCTION check_group_size()
RETURNS TRIGGER AS
$$
    DECLARE
        groupMaxSize int;
        groupCurrSize int;

    BEGIN
        SELECT size INTO groupMaxSize
        FROM groupInfo
        WHERE gID = NEW.gID;

        SELECT COUNT(userID) INTO groupCurrSize
        FROM groupMember
        WHERE gID = NEW.gID;

        IF groupCurrSize + 1 > groupMaxSize THEN
            -- Should not make this change, return null
            RETURN NULL;
        END IF;

        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

-- We want a trigger to make sure that the group size will never go over the max size
CREATE OR REPLACE TRIGGER groupSize
    BEFORE INSERT ON groupMember
    FOR EACH ROW
    EXECUTE PROCEDURE check_group_size();

CREATE OR REPLACE FUNCTION increment_pid()
RETURNS TRIGGER AS
$$
    DECLARE
        maxID int;
    BEGIN
        SELECT MAX(userID) INTO maxID
        FROM profile;

        IF maxID IS NULL THEN
            NEW.userID = 0;
        ELSE
            NEW.userID = maxID + 1;
        END IF;

        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

-- We want to make sure that the profile ID's will be the next highest integer available
CREATE OR REPLACE TRIGGER incrementUserID
    BEFORE INSERT ON profile
    FOR EACH ROW
    EXECUTE FUNCTION increment_pid();

-- We want to add a corresponding entry into the messageRecipient relation upon adding a new message to the message relation
CREATE OR REPLACE FUNCTION add_message_recipient()
RETURNS TRIGGER AS $$
BEGIN
    insert into messagerecipient values (new.msgid, new.touserid);
    return new;
END;
$$ language plpgsql;

CREATE OR REPLACE TRIGGER addMessageRecipient
    AFTER INSERT
    ON message
    FOR EACH ROW -- Why does this have to be a row-level trigger rather than a table-level trigger?
    EXECUTE FUNCTION add_message_recipient();


-- addMemberToGroup Trigger

-- addMember function
CREATE OR REPLACE FUNCTION update_group()
RETURNS TRIGGER AS
$$
BEGIN
        -- removing the pendingGroupMember AFTER INSERT to groupMember, so we can use this trigger to update the groupMember table
        DELETE FROM pendingGroupMember WHERE gID = NEW.gID AND userID = NEW.userID; --REMOVE PENDING MEMBER REQUEST from pendingGroupMember Table
        INSERT INTO groupMember (gID, userID, role, lastConfirmed) VALUES (NEW.gID, NEW.userID, 'member', system_timestamp()); -- Add coresponding data to memberTable
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- addMember trigger
CREATE OR REPLACE TRIGGER update_group
    AFTER INSERT 
    ON groupMember
    FOR EACH ROW
    EXECUTE FUNCTION update_group()