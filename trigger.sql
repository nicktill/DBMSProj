----------------------------------------------------------
-- BeSocial Database Triggers                           --
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann --
----------------------------------------------------------

-- TODO: Check if user being added to a group is a pending member before being added?
-- TODO: Add trigger to make sure user is in group message is sent to?
-- TODO: Ask about using the clock with triggers

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
DECLARE
    maxLast TIMESTAMP;
    rec_pending pendinggroupmember%ROWTYPE;
    groupSize INT := 0;
    curSize INT := 0;
    curTime TIMESTAMP;

BEGIN
    SELECT COUNT(userID) INTO curSize
    FROM groupMember
    WHERE gID = NEW.gID;

    SELECT MAX(lastConfirmed) INTO maxLast
    FROM groupmember
    WHERE gid = OLD.gid;

    SELECT size INTO groupSize
    FROM groupinfo
    WHERE gid = OLD.gid;

    SELECT pseudo_time INTO curTime
    FROM clock;

    FOR rec_pending IN SELECT * FROM pendinggroupmember WHERE gid = OLD.gid AND requesttime <= maxLast ORDER BY requesttime
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
