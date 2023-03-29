----------------------------------------------------------
-- BeSocial Database Triggers                           --
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann --
----------------------------------------------------------

-- TODO: Check input integrity
-- TODO: Add trigger to make sure user is in group message is sent to

-- We want a trigger to make sure that the group size will never go over the max size
CREATE OR REPLACE FUNCTION check_group_size()
RETURNS TRIGGER AS
$$
    DECLARE
        groupMaxSize int := 0;
        groupCurrSize int := 0;

    BEGIN
        SELECT size INTO groupMaxSize
        FROM groupInfo
        WHERE gID = NEW.gID;

        SELECT COUNT(userID) INTO groupCurrSize
        FROM groupMember
        WHERE gID = NEW.gID;

        IF groupCurrSize + 1 > groupMaxSize THEN
            -- Should not make this change, return null
            RAISE EXCEPTION 'Cannot exceed max group size' USING ERRCODE = '00001';
        END IF;

        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER groupSize
    BEFORE INSERT ON groupMember
    FOR EACH ROW
    EXECUTE FUNCTION check_group_size();

-- We want to make sure that the profile ID's will be the next highest integer available
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


-- We want to make sure that friends are not repeated
CREATE OR REPLACE FUNCTION resolve_friend()
RETURNS TRIGGER AS
$$
    DECLARE
        friendRecord friend%ROWTYPE := NULL;
        minID int := 0;
        maxID int := 0;
    BEGIN
        IF NEW.userid1 > NEW.userid2 THEN
            minID := NEW.userid2;
            maxID := NEW.userid1;
        ELSE
            minID := NEW.userid1;
            maxID := NEW.userid2;
        END IF;

        NEW.userid1 := minID;
        NEW.userid2 := maxID;

        -- Checking the friend relationship doesn't already exist
        SELECT * INTO friendRecord
        FROM friend
        WHERE userid1 = minID AND userid2 = maxID;

        IF friendRecord IS NOT NULL THEN
            RAISE EXCEPTION 'Friendship already exists' USING ERRCODE = '00002';
        END IF;

        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER friendOrder
    BEFORE INSERT ON friend
    FOR EACH ROW
    EXECUTE FUNCTION resolve_friend();