-------------------------------------------------
-- BeSocial Database Schema
-- Authors: Steven Jarmell, Jonah Osband, Nick Tillmann
-------------------------------------------------

DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS friend CASCADE;
DROP TABLE IF EXISTS pendingFriend CASCADE;
DROP TABLE IF EXISTS groupInfo CASCADE;
DROP TABLE IF EXISTS groupMember CASCADE;
DROP TABLE IF EXISTS pendingGroupMember CASCADE;
DROP TABLE IF EXISTS message CASCADE;
DROP TABLE IF EXISTS messageRecipient CASCADE;
DROP TABLE IF EXISTS Clock CASCADE;

-- NOTE: Assumptions are inline with our table schemas and triggers

------------------------------------------------
-- TODO:
    -- Check about whether or not to have a NOT NULL on DOB for users
    -- Check if there should be a default timestamp for lastLogin
    -- Decide NULL or DEFAULT for message
------------------------------------------------


-- Stores the user and login information for each user registered in the system.
CREATE TABLE "user" (
    userID INTEGER, -- primary key
    name VARCHAR(50) NOT NULL, -- Cannot make a user without a name. Names can be repeated
    email VARCHAR(50) NOT NULL, -- Will also be unique, but since userID is an integer it is a better PK
    password VARCHAR(50) NOT NULL, -- Users need a password for security reasons
    date_of_birth DATE,
    lastlogin TIMESTAMP,
    CONSTRAINT PK_user PRIMARY KEY (userID),
    CONSTRAINT UQ_email UNIQUE (email),
    CONSTRAINT IC_email_proper_form CHECK (email LIKE '%@%.%') -- email needs to follow this form
);


-- Stores the friends lists for every user in the system.
-- The JDate is when they became friends, and the requestText is the text from the original friend request.
CREATE TABLE friend (
    userID1 INTEGER,
    userID2 INTEGER,
    JDate DATE NOT NULL,
    requestText VARCHAR(200) DEFAULT 'Hey! Lets be friends.', -- Come back to later
    CONSTRAINT PK_friend PRIMARY KEY (userID1, userID2),
    -- When a user is removed from the system, we should remove all dependent friendship entries.
    CONSTRAINT FK1_friend FOREIGN KEY (userID1) REFERENCES "user"(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK2_friend FOREIGN KEY (userID2) REFERENCES "user"(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores pending friends requests that have yet to be confirmed by the recipient of the request.
CREATE TABLE pendingFriend (
    fromID INTEGER,
    toID INTEGER,
    requestText VARCHAR(200) DEFAULT 'Hey! Lets be friends.',
    CONSTRAINT PK_pendingFriend PRIMARY KEY (fromID, toID),
    -- If a user is removed, we want to remove pending requests
    -- Same principle for updates
    CONSTRAINT FK1_pendingFriend FOREIGN KEY (fromID) REFERENCES "user"(userID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK2_pendingFriend FOREIGN KEY (toID) REFERENCES "user"(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores information for each group in the system.
CREATE TABLE groupInfo (
    gID INTEGER,
    name VARCHAR(50) NOT NULL, -- Groups need to have a name associated with them
    size INTEGER DEFAULT 1, -- When a group is created, the owner is the first member
    description VARCHAR(200), -- Group descriptions can be null
    CONSTRAINT PK_groupInfo PRIMARY KEY (gID),
    -- A group must always have at least one member
    CONSTRAINT IC_groupSize CHECK (size >= 1)
);

-- Stores the users who are members of each group in the system.
-- The "role" indicates whether a user is a manager of a group (who can accept joining group requests) or a member.
-- The lastConfirmed attribute stores when the group member was successfully added to the group.
CREATE TABLE groupMember (
    gID INTEGER,
    userID INTEGER,
    role VARCHAR(20)
        CHECK (role IN ('manager', 'member')),
    lastConfirmed TIMESTAMP NOT NULL, -- The system should never have a NULL value for this
    CONSTRAINT PK_groupMember PRIMARY KEY (gID, userID),
    -- When a group is deleted, the group member entries should be deleted since
    -- since the group no longer exists
    CONSTRAINT FK1_groupMember FOREIGN KEY (gID) REFERENCES groupInfo(gID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    -- If a user is deleted, all of their respective groupMember entries should be deleted
    CONSTRAINT FK2_groupMember FOREIGN KEY (userID) REFERENCES "user"(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores pending joining group requests that have yet to be accepted/rejected by the manager of the group.
-- The requestTime is the time when the user requested group membership.
CREATE TABLE pendingGroupMember (
    gID INTEGER,
    userID INTEGER,
    requestText VARCHAR(200) DEFAULT 'I would like to join your group! :)',
    requestTime TIMESTAMP,
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
    msgID INTEGER,
    fromID INTEGER,
    messageBody VARCHAR(200) NOT NULL,
    toUserID INTEGER DEFAULT NULL,
    toGroupID INTEGER DEFAULT NULL,
    timeSent TIMESTAMP,
    CONSTRAINT PK_message PRIMARY KEY (msgID),
    -- If a sender is deleted, we should set the foreign key to null so that
    -- messages can be preserved for other users
    CONSTRAINT FK1_message FOREIGN KEY (fromID) REFERENCES "user"(userID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    -- Same principle for recipient
    CONSTRAINT FK2_message FOREIGN KEY (toUserID) REFERENCES "user"(userID)
        ON DELETE SET NULL ON UPDATE CASCADE,
    -- If a group is deleted, then all the associated messages are deleted
    CONSTRAINT FK3_message FOREIGN KEY (toGroupID) REFERENCES groupInfo(gID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Stores the recipients of each message stored in the system.
CREATE TABLE messageRecipient (
    msgID INTEGER,
    userID INTEGER,
    CONSTRAINT PK_messageRecipient PRIMARY KEY (msgID, userID),
    -- If a message is deleted, this entry should be deleted
    CONSTRAINT FK1_messageRecipient FOREIGN KEY (msgID) REFERENCES message(msgID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    -- If a user is deleted, even though we keep the message, we do not need to have
    -- an entry in the messageRecipient table so we cascade changes
    CONSTRAINT FK2_messageRecipient FOREIGN KEY (userID) REFERENCES "user"(userID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- To facilitate time travel, you are expected to implement a Clock.
-- You must maintain a “pseudo” timestamp (not the real system timestamp) in the auxilliary table Clock.
-- The reason for making such a timestamp and not using the system one is to make it easy to generate scenarios (time traveling)
-- That is, all functions on the Clock relation will be done on the database side and not through JDBC.
CREATE TABLE Clock (
    pseudo_time TIMESTAMP,
    CONSTRAINT PK_Clock PRIMARY KEY (pseudo_time)
);

-- Clock has only one tuple, inserted as part of initialization and is updated during time traveling.
INSERT INTO Clock VALUES ('2023-01-01 00:00:00');

SELECT * FROM "user";
SELECT * FROM friend;
SELECT * FROM pendingFriend;
SELECT * FROM groupInfo;
SELECT * FROM groupMember;
SELECT * FROM pendingGroupMember;
SELECT * FROM message;
SELECT * FROM messageRecipient;
