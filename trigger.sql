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