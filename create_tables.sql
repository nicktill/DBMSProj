DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS friend;
DROP TABLE IF EXISTS pendingFriend;
DROP TABLE IF EXISTS groupInfo;
DROP TABLE IF EXISTS groupMember;
DROP TABLE IF EXISTS pendingGroupMember;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS messageRecipient;

-- Stores the user and login information for each user registered in the system.
CREATE TABLE "user" (
    userID INTEGER, -- primary key
    name VARCHAR(50),
    email VARCHAR(50),
    password VARCHAR(50),
    date_of_birth DATE,
    lastlogin TIMESTAMP
--     primray keys/foreign keys =>
--     CONSTRAINT user_pk PRIMARY KEY (userID)
);


-- Stores the friends lists for every user in the system.
-- The JDate is when they became friends, and the requestText is the text from the original friend request.
CREATE TABLE friend (
    userID1 INTEGER,
    userID2 INTEGER,
    JDate DATE,
    requestText VARCHAR(200)
--     primray keys/foreign keys =>
--     CONSTRAINT friend_pk PRIMARY KEY (userID1, userID2),
--     CONSTRAINT friend_userID1_fk FOREIGN KEY (userID1) REFERENCES "user"(userID),
--     CONSTRAINT friend_userID2_fk FOREIGN KEY (userID2) REFERENCES "user"(userID)
);

-- Stores pending friends requests that have yet to be confirmed by the recipient of the request.
CREATE TABLE pendingFriend (
    fromID INTEGER,
    toID INTEGER,
    requestText VARCHAR(200)
--     primray keys/foreign keys =>
--     CONSTRAINT pendingFriend_pk PRIMARY KEY (fromID, toID),
--     CONSTRAINT pendingFriend_fromID_fk FOREIGN KEY (fromID) REFERENCES "user"(userID),
--     CONSTRAINT pendingFriend_toID_fk FOREIGN KEY (toID) REFERENCES "user"(userID)
);

-- Stores information for each group in the system.
CREATE TABLE groupInfo (
    gID INTEGER PRIMARY KEY,
    name VARCHAR(50),
    size INTEGER,
    description VARCHAR(200)
--     primray keys/foreign keys =>
--     CONSTRAINT groupInfo_pk PRIMARY KEY (gID)

);

-- Stores the users who are members of each group in the system.
-- The “role” indicates whether a user is a manager of a group (who can accept joining group requests) or a member.
-- The lastConfirmed attribute stores when the group member was successfully added to the group.
CREATE TABLE groupMember (
    gID INTEGER,
    userID INTEGER,
    role VARCHAR(20),
    lastConfirmed TIMESTAMP
-- primary keys/foreign keys =>
--     CONSTRAINT groupMember_pk PRIMARY KEY (gID, userID),
--     CONSTRAINT groupMember_gID_fk FOREIGN KEY (gID) REFERENCES groupInfo(gID),
--     CONSTRAINT groupMember_userID_fk FOREIGN KEY (userID) REFERENCES "user"(userID)
);

-- Stores pending joining group requests that have yet to be accepted/rejected by the manager of the group.
-- The requestTime is the time when the user requested group membership.
CREATE TABLE pendingGroupMember (
    gID INTEGER,
    userID INTEGER,
    requestText VARCHAR(200),
    requestTime TIMESTAMP
-- primary keys/foreign keys =>
--     CONSTRAINT pendingGroupMember_pk PRIMARY KEY (gID, userID),
--     CONSTRAINT pendingGroupMember_gID_fk FOREIGN KEY (gID) REFERENCES groupInfo(gID),
--     CONSTRAINT pendingGroupMember_userID_fk FOREIGN KEY (userID) REFERENCES "user"(userID)
);

-- Stores every message sent by users in the system.
-- Note that the default values of toUserID and toGroupID should be NULL.
CREATE TABLE message (
    msgID INTEGER PRIMARY KEY,
    fromID INTEGER,
    messageBody VARCHAR(200),
    toUserID INTEGER,
    toGroupID INTEGER,
    timeSent TIMESTAMP
    -- primary keys/foreign keys =>
--     CONSTRAINT message_pk PRIMARY KEY (msgID),
--     CONSTRAINT message_fromID_fk FOREIGN KEY (fromID) REFERENCES "user"(userID),
--     CONSTRAINT message_toUserID_fk FOREIGN KEY (toUserID) REFERENCES "user"(userID),
--     CONSTRAINT message_toGroupID_fk FOREIGN KEY (toGroupID) REFERENCES groupInfo(gID)
);

-- Stores the recipients of each message stored in the system.
CREATE TABLE messageRecipient (
    msgID INTEGER,
    userID INTEGER
-- primary keys/foreign keys =>
--     CONSTRAINT messageRecipient_pk PRIMARY KEY (msgID, userID),
--     CONSTRAINT messageRecipient_msgID_fk FOREIGN KEY (msgID) REFERENCES message(msgID),
--     CONSTRAINT messageRecipient_userID_fk FOREIGN KEY (userID) REFERENCES "user"(userID)
);

-- To facilitate time travel, you are expected to implement a Clock.
-- You must maintain a “pseudo” timestamp (not the real system timestamp) in the auxilliary table Clock.
-- The reason for making such a timestamp and not using the system one is to make it easy to generate scenarios (time traveling)
-- That is, all functions on the Clock relation will be done on the database side and not through JDBC.
CREATE TABLE Clock (
    pseudo_time TIMESTAMP PRIMARY KEY
);
-- Clock has only one tuple, inserted as part of initialization and is updated during time traveling.
INSERT INTO Clock VALUES ('2022-01-01 00:00:00');

SELECT * FROM user;
SELECT * FROM friend;
SELECT * FROM pendingFriend;
SELECT * FROM groupInfo;
SELECT * FROM groupMember;
SELECT * FROM pendingGroupMember;
SELECT * FROM message;
SELECT * FROM messageRecipient;
