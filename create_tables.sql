----------------------------------------------------------
-- BeSocial Database Schema                             --
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann --
----------------------------------------------------------

-- NOTE: Assumptions are inline with our table schemas and triggers

DROP TABLE IF EXISTS profile CASCADE;
DROP TABLE IF EXISTS friend CASCADE;
DROP TABLE IF EXISTS pendingFriend CASCADE;
DROP TABLE IF EXISTS groupInfo CASCADE;
DROP TABLE IF EXISTS groupMember CASCADE;
DROP TABLE IF EXISTS pendingGroupMember CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS messageRecipient CASCADE;
DROP TABLE IF EXISTS Clock CASCADE;

------------------------------------------------
-- TODO:
    -- Check about whether or not to have a NOT NULL on DOB for users
    -- Check if there should be a default timestamp for lastLogin
    -- Decide NULL or DEFAULT for message
    -- Size is the max size
    -- Write Trigger to make sure friendships are not repeated
------------------------------------------------

-- Stores the user and login information for each user registered in the system.
CREATE TABLE profile (
    userID          INT, -- primary key
    name            VARCHAR(50) NOT NULL, -- Cannot make a user without a name. Names can be repeated
    email           VARCHAR(50) NOT NULL, -- Will also be unique, but since userID is an integer it is a better PK
    password        VARCHAR(50) NOT NULL, -- Users need a password for security reasons
    date_of_birth   DATE        NOT NULL, -- The way the function is described in phase 2, it asks for a DOB
    lastlogin       TIMESTAMP   NOT NULL, -- When we make the profile, the user has not logged in so default to NULL

    -- Constraints
    CONSTRAINT PK_user PRIMARY KEY (userID),
    CONSTRAINT UQ_email UNIQUE (email),
    CONSTRAINT IC_email_proper_form CHECK ( email LIKE '%@%.%' ) -- email needs to follow this form
);


-- Stores the friends lists for every user in the system.
-- The JDate is when they became friends, and the requestText is the text from the original friend request.
CREATE TABLE friend (
    userID1         INT,
    userID2         INT,
    JDate           DATE NOT NULL,
    requestText     VARCHAR(200) DEFAULT 'Hey! Let''s be friends.', -- Come back to later

    -- Constraints
    CONSTRAINT PK_friend PRIMARY KEY (userID1, userID2),
    -- When a user is removed from the system, we should remove all dependent friendship entries.
    CONSTRAINT FK1_friend FOREIGN KEY (userID1) REFERENCES profile(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK2_friend FOREIGN KEY (userID2) REFERENCES profile(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores pending friends requests that have yet to be confirmed by the recipient of the request.
CREATE TABLE pendingFriend (
    fromID          INT,
    toID            INT,
    requestText     VARCHAR(200) DEFAULT 'Hey! Let''s be friends.',

    -- Constraints
    CONSTRAINT PK_pendingFriend PRIMARY KEY (fromID, toID),
    -- If a user is removed, we want to remove pending requests
    -- Same principle for updates
    CONSTRAINT FK1_pendingFriend FOREIGN KEY (fromID) REFERENCES profile(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK2_pendingFriend FOREIGN KEY (toID) REFERENCES profile(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores information for each group in the system.
CREATE TABLE groupInfo (
    gID             INT,
    name            VARCHAR(50) NOT NULL, -- Groups need to have a name associated with them
    size            INT DEFAULT 10, -- When a group is created, the owner is the first member
    description     VARCHAR(200), -- Group descriptions can be null

    -- Constraints
    CONSTRAINT PK_groupInfo PRIMARY KEY (gID),
    -- A group must have a max size of a positive integer
    CONSTRAINT IC_positive_size CHECK (size > 0)
);

-- Stores the users who are members of each group in the system.
-- The "role" indicates whether a user is a manager of a group (who can accept joining group requests) or a member.
-- The lastConfirmed attribute stores when the group member was successfully added to the group.
CREATE TABLE groupMember (
    gID             INT,
    userID          INT,
    role            VARCHAR(20),
    lastConfirmed   TIMESTAMP NOT NULL, -- The system should never have a NULL value for this

    -- Constraints
    CONSTRAINT PK_groupMember PRIMARY KEY (gID, userID),
    -- When a group is deleted, the group member entries should be deleted since
    -- since the group no longer exists
    CONSTRAINT FK1_groupMember FOREIGN KEY (gID) REFERENCES groupInfo(gID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    -- If a user is deleted, all of their respective groupMember entries should be deleted
    CONSTRAINT FK2_groupMember FOREIGN KEY (userID) REFERENCES profile(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    -- The DB manager can have varying roles
    CONSTRAINT IC_group_role CHECK ( role IN ( 'manager', 'member' ) )
);

-- Stores pending joining group requests that have yet to be accepted/rejected by the manager of the group.
-- The requestTime is the time when the user requested group membership.
CREATE TABLE pendingGroupMember (
    gID             INT,
    userID          INT,
    requestText     VARCHAR(200) DEFAULT 'I would like to join your group! :)',
    requestTime     TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_pendingGroupMember PRIMARY KEY (gID, userID),
    -- When a group is deleted, the pending group member entries should be deleted since
    -- since the group no longer exists
    CONSTRAINT FK1_pendingMember FOREIGN KEY (gID) REFERENCES groupInfo(gID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    -- If a user is deleted, all of their respective pendingGroupMember entries should be deleted
    CONSTRAINT FK2_pendingMember FOREIGN KEY (gID) REFERENCES groupInfo(gID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores every message sent by users in the system.
-- Note that the default values of toUserID and toGroupID should be NULL.
CREATE TABLE message (
    msgID           INT,
    fromID          INT,
    messageBody     VARCHAR(200) NOT NULL,
    toUserID        INT DEFAULT NULL,
    toGroupID       INT DEFAULT NULL,
    timeSent        TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_message PRIMARY KEY (msgID),
    -- If a sender is deleted, we should set the foreign key to null so that
    -- messages can be preserved for other users
    CONSTRAINT FK1_message FOREIGN KEY (fromID) REFERENCES profile(userID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    -- Same principle for recipient
    CONSTRAINT FK2_message FOREIGN KEY (toUserID) REFERENCES profile(userID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    -- If a group is deleted, then all the associated messages are deleted
    CONSTRAINT FK3_message FOREIGN KEY (toGroupID) REFERENCES groupInfo(gID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores the recipients of each message stored in the system.
CREATE TABLE messageRecipient (
    msgID           INT,
    userID          INT,

    -- Constraints
    CONSTRAINT PK_messageRecipient PRIMARY KEY (msgID, userID),
    -- If a message is deleted, this entry should be deleted
    CONSTRAINT FK1_messageRecipient FOREIGN KEY (msgID) REFERENCES message(msgID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    -- If a user is deleted, even though we keep the message, we do not need to have
    -- an entry in the messageRecipient table so we cascade changes
    CONSTRAINT FK2_messageRecipient FOREIGN KEY (userID) REFERENCES profile(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- To facilitate time travel, you are expected to implement a Clock.
-- You must maintain a “pseudo” timestamp (not the real system timestamp) in the auxiliary table Clock.
-- The reason for making such a timestamp and not using the system one is to make it easy to generate scenarios (time traveling)
-- That is, all functions on the Clock relation will be done on the database side and not through JDBC.
CREATE TABLE Clock (
    pseudo_time     TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_Clock PRIMARY KEY (pseudo_time)
);

-- Triggers
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

CREATE OR REPLACE TRIGGER incrementUserID
    BEFORE INSERT ON profile
    FOR EACH ROW
    EXECUTE FUNCTION increment_pid();

-- Clock has only one tuple, inserted as part of initialization and is updated during time traveling.
INSERT INTO Clock VALUES ('2023-01-01 00:00:00');

SELECT * FROM profile;
SELECT * FROM friend;
SELECT * FROM pendingFriend;
SELECT * FROM groupInfo;
SELECT * FROM groupMember;
SELECT * FROM pendingGroupMember;
SELECT * FROM message;
SELECT * FROM messageRecipient;
