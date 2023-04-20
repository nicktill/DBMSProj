import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/** Questions for TA
 * 1. For three degrees, will it be okay to just insert friendships rather than initate and add them?
 */

/**
 * The driver program needs to call all of the above functions and display the
 * content of the affected rows of the affected tables after each call
 */
public class Driver {

    private static CreateProfileObject user1 = new CreateProfileObject("Steven Jarmell", "1234", "sjj27@pitt.edu",
            "2001-11-26");
    private static CreateProfileObject user2 = new CreateProfileObject("Kenny Pickett", "supahbowl", "kp@steelers.com",
            "1999-05-20");
    private static CreateProfileObject user3 = new CreateProfileObject("Kendrick Lamar", "dna", "lamar@lamar.gov",
            "1989-07-02");
    private static CreateProfileObject user4 = new CreateProfileObject("Taylor Swift", "red", "swift@linux.org",
            "2001-11-26");
    private static CreateProfileObject user5 = new CreateProfileObject("George Pickens", "thatguy",
            "pickens@steelers.com", "2000-05-16");

    private static CreateProfileObject[] userList = { user1, user2, user3, user4, user5 };

    private static BeSocial beSocial;
    private static Scanner sc;
    private static Connection conn;

    public static void main(String[] args) {
        // Get the datbase username and password
        String databaseUsername = null;
        String databasePassword = null;

        sc = new Scanner(System.in);

        try {
            System.out.print("User your username for the database: ");
            databaseUsername = sc.nextLine();

            System.out.print("User your password for the database: ");
            databasePassword = sc.nextLine();
        } catch (Exception e) {
            System.out.println("Error retrieving username and password");
            sc.close();
            System.exit(0);
        }

        // Try and connect to the database
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/";
            Properties props = new Properties();
            props.setProperty("escapeSyntaxCallMode", "callIfNoReturn");
            props.setProperty("user", databaseUsername);
            props.setProperty("password", databasePassword);
            conn = DriverManager.getConnection(url, props);
        } catch (Exception e) {
            System.out.println(String.format(
                    "Error Of Type: '%s' Occurred When Connecting to Database.\nPlease re-run program.", e.getClass()));
            sc.close();
            System.exit(0);
        }

        // Create the BeSocial Object
        beSocial = new BeSocial(databaseUsername, databasePassword, true);

        System.out.print("\033\143"); // Clears the command line console, shoutout stack overflow

        System.out.println("Starting Driver Program!\n");

        // Test for method `createProfile` | Task 1
        System.out.println("Press enter to test createProfile");
        sc.nextLine();
        testCreateProfile();

        // Test for method `login` | Task 3
        System.out.println("Press enter to test login");
        sc.nextLine();
        testLogin();

        // Test for method `initiateFriendship` | Task 4
        System.out.println("Press enter to test initiateFriendship");
        sc.nextLine();
        testInitiateFriendship();

        // Test for method `confirmFriendRequests` | Task 5
        System.out.println("Press enter to test confirmFriendRequests");
        sc.nextLine();
        testConfirmFriendRequests();

        // Test for method `createGroup` | Task 6
        System.out.println("Press enter to test createGroup");
        sc.nextLine();
        testCreateGroup();

        // Test for method `initiateAddingGroup` | Task 7
        System.out.println("Press enter to test initiateAddingGroup");
        sc.nextLine();
        testInitiateAddingGroup();

        // Test for method `confirmGroupMembership` | Task 8
        System.out.println("Press enter to test confirmGroupMembership");
        sc.nextLine();
        testConfirmGroupMembership();

        // Test for method `leaveGroup` | Task 9
        System.out.println("Press enter to test leaveGroup");
        sc.nextLine();
        testLeaveGroup();

        // Test for method `searchForProfile` | Task 10
        System.out.println("Press enter to test searchForProfile");
        sc.nextLine();
        testSearchForProfile();

        // Test for method `sendMessageToUser` | Task 11
        System.out.println("Press enter to test sendMessageToUser");
        sc.nextLine();
        testSendMessageToUser();

        // Test for method `sendMessageToGroup` | Task 12
        System.out.println("Press enter to test sendMessageToGroup");
        sc.nextLine();
        testSendMessageToGroup();

        // Test for method `displayMessages` | Task 13
        System.out.println("Press enter to test displayMessages");
        sc.nextLine();
        testDisplayMessages();

        // Test for method `displayNewMessages` | Task 14
        System.out.println("Press enter to test displayNewMessages");
        sc.nextLine();
        testDisplayNewMessages();

        // Test for method `displayFriends` | Task 15
        System.out.println("Press enter to test displayFriends");
        sc.nextLine();
        testDisplayFriends();

        // Test for method `rankGroups` | Task 16
        System.out.println("Press enter to test rankGroups");
        sc.nextLine();
        testRankGroups();

        // Test for method `rankProfiles` | Task 17
        System.out.println("Press enter to test rankProfiles");
        sc.nextLine();
        testRankProfiles();

        // Test for method `topMessages` | Task 18
        System.out.println("Press enter to test topMessages");
        sc.nextLine();
        testTopMessages();

        // Test for method `threeDegrees` | Task 19
        System.out.println("Press enter to test threeDegrees");
        sc.nextLine();
        testThreeDegrees();

        // Test for method `logout` | Task 20
        System.out.println("Press enter to test logout");
        sc.nextLine();
        testLogout();

        // Test for method `dropProfile` | Task 2
        System.out.println("Press enter to test dropProfile");
        sc.nextLine();
        testDropProfile();

        // Test for method `exit` | Task 21
        System.out.println("Press enter to test exit");
        sc.nextLine();
        testExit();

        System.out.println("\nAll tests completed");

        sc.close();
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection");
        }
    }

    /**
     * Test Exit Method
     * After calling the exit method, beSocial should close the scanner and
     * connection to the database
     */
    private static void testExit() {
        // Call the exit function
        beSocial.exit();

        if (beSocial.isProgramRunning()) {
            System.out.println("Test Exit Passed");
        } else {
            System.out.println("Test Exit Failed");
        }
    }

    private static void testLogout() {

        // Get the display before the user logs in
        System.out.println("Menu Before User Logs In:");
        beSocial.displayMenu(beSocial.getIsLoggedIn());

        System.out.println();
        // Log in a user
        beSocial.login("admin", "admin");

        // Get the user's timestamp when they log in
        String profileRowBefore = null;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM profile WHERE userID=0;";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            int userID = rs.getInt("userID");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Date dob = rs.getDate("date_of_birth");
            Timestamp lastLogin = rs.getTimestamp("lastlogin");

            profileRowBefore = userID + " " + name + " " + email + " " + password + " " + dob.toString() + " "
                    + lastLogin.toString();

            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (profileRowBefore == null) {
            System.out.println("Error getting lastlogin timestamp in test logout");
        } else {
            System.out.println("\nProfile Table At Login: " + profileRowBefore);
        }

        // Get the display when logged in
        System.out.println("\nMenu After User Logs In:");
        beSocial.displayMenu(beSocial.getIsLoggedIn());

        // Change the timestamp in the clock
        try {
            Statement st = conn.createStatement();
            String updateTimeStampQuery = "UPDATE Clock SET pseudo_time = '2022-01-01 01:00:00';";
            int rs = st.executeUpdate(updateTimeStampQuery);

            if (rs != 1) {
                System.out.println("Failed to update clock");
            } else {
                System.out.println("Updated the clock's value");
            }

            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Log the user out
        beSocial.logout();

        // Get the display again
        System.out.println("\nMenu After User Logs Out:");
        beSocial.displayMenu(beSocial.getIsLoggedIn());

        // Get the user's timestamp and make sure it changed to the clock timestamp
        String profileRowAfter = null;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM profile WHERE userID=0;";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            int userID = rs.getInt("userID");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            Date dob = rs.getDate("date_of_birth");
            Timestamp lastLogin = rs.getTimestamp("lastlogin");

            profileRowAfter = userID + " " + name + " " + email + " " + password + " " + dob.toString() + " "
                    + lastLogin.toString();

            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (profileRowAfter == null) {
            System.out.println("Error getting lastlogin timestamp in test logout");
        } else {
            System.out.println("\nProfile Row After Logout: " + profileRowAfter);
        }

        System.out.println("\nSetting Clock back to default");
        try {
            Statement st = conn.createStatement();
            String updateTimeStampQuery = "UPDATE Clock SET pseudo_time = '2022-01-01 00:00:00';";
            int rs = st.executeUpdate(updateTimeStampQuery);

            if (rs != 1) {
                System.out.println("Failed to return clock to default");
            } else {
                System.out.println("Updated the clock's value back to default");
            }

            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (profileRowBefore.equals(profileRowAfter)) {
            System.out.println("\nTest Logout Failed");
        } else {
            System.out.println("\nTest Logout Passed");
        }
    }

    private static void testThreeDegrees() {
        // Clear friendship table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM friend;";
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Failed to remove the friends from the relation");
            return;
        }

        System.out.println("Cleared friend table");

        // Add Friendship: Steven Jarmell - Kenny Pickett
        // Add Friendship: Kenny Pickett - George Pickens
        // Add Friendship: George Pickens - Taylor Swift
        // Kendrick Has No Friends :(
        try {
            Statement st = conn.createStatement();
            String query = "INSERT INTO friend VALUES(1, 2, '2023-04-19');INSERT INTO friend VALUES(2, 5, '2023-04-19');INSERT INTO friend VALUES(5, 4, '2023-04-19');";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

        System.out.println("Friendships Added");

        // Log in as Steven Jarmell
        beSocial.login("Steven Jarmell", "1234");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        // Test between logged in user and one of their immediate friends
        beSocial.threeDegrees(2);

        // Test between logged in user and one of their friend's friends
        beSocial.threeDegrees(5);

        // Test between logged in user and one of their friends' friend's friends
        beSocial.threeDegrees(4);

        // Test between logged in user and a user that has no friends
        beSocial.threeDegrees(3);

        System.out.flush();
        System.setOut(old);

        String[] outputs = baos.toString().split("\r?\n|\r");

        String expected1 = "1 --> 2";
        String expected2 = "1 --> 2 --> 5";
        String expected3 = "1 --> 2 --> 5 --> 4";
        String expected4 = "There is no three degree relation with this user.";

        System.out.println("\nFirst Three Degrees:");
        System.out.println("Expected: " + expected1);
        System.out.println("Received: " + outputs[0]);
        boolean result1 = expected1.equals(outputs[0]);
        System.out.println("Passed: " + result1);

        System.out.println("\nSecond Three Degrees:");
        System.out.println("Expected: " + expected2);
        System.out.println("Received: " + outputs[1]);
        boolean result2 = expected2.equals(outputs[1]);
        System.out.println("Passed: " + result2);

        System.out.println("\nThird Three Degrees:");
        System.out.println("Expected: " + expected3);
        System.out.println("Received: " + outputs[2]);
        boolean result3 = expected3.equals(outputs[2]);
        System.out.println("Passed: " + result3);

        System.out.println("\nFourth Three Degrees:");
        System.out.println("Expected: " + expected4);
        System.out.println("Received: " + outputs[3]);
        boolean result4 = expected4.equals(outputs[3]);
        System.out.println("Passed: " + result4);

        if (result1 && result2 && result3 && result4) {
            System.out.println("\nAll tests passed for Three Degrees");
        } else {
            System.out.println("\nThree Degrees Test Failed");
        }

        beSocial.logout();
    }

    // TODO
    private static void testTopMessages() {
        // Assumptions: sendMessagetoUser/Group has been run before
        //
        System.out.println("Test Top Messages Not Implemented");
    }

    // TODO
    private static void testRankProfiles() {
        System.out.println("Test Rank Profiles Not Implemented");
    }

    private static void testRankGroups() {
        beSocial.rankGroups();

        // Log out and log in as admin
        beSocial.logout();
        beSocial.login("admin", "admin");

        // Remove all groups
        try {
            PreparedStatement s = conn.prepareStatement("DELETE FROM groupInfo WHERE TRUE;");
            PreparedStatement s2 = conn.prepareStatement("DELETE FROM groupMember WHERE TRUE;");
            s.execute();
            s2.execute();
        } catch (SQLException e) {
            System.out.println("Error deleting all groups and group members.");
            return;
        }

        // Call rank groups
        System.out.println("\n--------------------------------------");
        System.out.println("Expected output:");
        System.out.println("No Groups to Rank");
        System.out.println("--------------------------------------");
        System.out.println("Actual results:");
        beSocial.rankGroups();
    }

    // TODO
    private static void testDisplayFriends() {
        // create a linkedList 
        LinkedList <Integer> s = new LinkedList<Integer>();
        try {
            PreparedStatement addFriend = conn.prepareStatement("INSERT INTO friend VALUES(1, 3, '2023-04-19', 'hey there bud');");
            addFriend.execute();
        } catch (SQLException e) {
            System.out.println("Error adding friend");
            return;
        }
        s.add(2); //this is the specific profileID of the user you want to search
        s.add(0);
        
        // logout of user
        beSocial.logout(); 
        // login as admin 
        beSocial.login("admin", "admin");
        // display friends of user (should be no friends and should not be prompted to pass the friends for search)
        beSocial.displayFriends(s); // should display no friends
        
        // logout 
        beSocial.logout(); 
        // login as user1 (Steven Jarmell)
        beSocial.login(user1.name, user1.password);
        // display friends passing in 'S' as the search parameter for searching friends
        beSocial.displayFriends(s); 
    }

    private static void testDisplayNewMessages() {
        // log out and log in to user1
        beSocial.logout(); 
        beSocial.login(user1.name, user1.password);

        // Get the current time
        Timestamp curTime;
        try {
            PreparedStatement s = conn.prepareStatement("SELECT pseudo_time FROM clock;");
            ResultSet rs = s.executeQuery();
            rs.next();
            curTime = rs.getTimestamp("pseudo_time");
        } catch (SQLException e) {
            System.out.println("Error getting current time");
            return;
        }

        beSocial.sendMessageToUser("This message should not be shown.", 2);

        // Change the time in the system
        ZonedDateTime zonedDateTime = curTime.toInstant().atZone(ZoneId.of("UTC"));
        Timestamp newTimestamp = Timestamp.from(zonedDateTime.plus(14, ChronoUnit.DAYS).toInstant());
        try {
            PreparedStatement s = conn
                    .prepareStatement("UPDATE clock SET pseudo_time='" + newTimestamp.toString() + "';");
            s.execute();
        } catch (SQLException e) {
            System.out.println("Error setting current time");
            return;
        }

        // Send a message to user2
        beSocial.sendMessageToUser("Hello Kenny Pickett!", 2);

        // log out and log in to user2
        beSocial.logout();
        beSocial.login(user2.name, user2.password);

        // Now display new messages
        beSocial.displayNewMessages();
    }

    // TODO
    private static void testDisplayMessages() {
        // Login to user with no messages

        //

        // Logout
    }

    // TODO
    private static void testSendMessageToGroup() {
        // Login
        beSocial.login(user3.name, user3.password);

        // Clear the message/message recipient table
        // Delete all entries from pendingGroupMember table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM message;";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to remove messages");
            return;
        }

        // Delete all entries from pendingGroupMember table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM messageRecipient;";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to remove messageRecipients");
            return;
        }

        // Get the message table before
        boolean test1 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM message;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Message Table Before Sending Message To Group");
            System.out.println("---------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int fromID = rs.getInt("fromID");
                String messageBody = rs.getString("messageBody");
                int toUserID = rs.getInt("toUserID");
                int toGroupID = rs.getInt("toGroupID");
                Timestamp timeSent = rs.getTimestamp("timeSent");

                System.out.println(msgID + "        " + fromID + "      " + messageBody + "     " + toUserID
                        + "        " + toGroupID + "       " + timeSent.toString());

                count++;
            }
            System.out.println("---------------------------------------------");

            System.out.println("Expected: 0");
            System.out.println("Observed: " + count);

            test1 = (count == 0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Get the message recipient table before
        boolean test2 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM messageRecipient;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("messageRecipient Table Before Sending Message To Group");
            System.out.println("---------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int userID = rs.getInt("userID");

                System.out.println(msgID + "        " + userID);

                count++;
            }
            System.out.println("---------------------------------------------");

            System.out.println("Expected: 0");
            System.out.println("Observed: " + count);

            test2 = (count == 0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try and send a message to a group the user isn't in
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.sendMessageToGroup(0, "Test from driver!");

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();

        boolean test3 = output.contains("You cannot send a message to that group because you are not in it");

        // Get the message table after
        boolean test4 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM message;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Message Table Before Sending Message To Group User Not In");
            System.out.println("---------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int fromID = rs.getInt("fromID");
                String messageBody = rs.getString("messageBody");
                int toUserID = rs.getInt("toUserID");
                int toGroupID = rs.getInt("toGroupID");
                Timestamp timeSent = rs.getTimestamp("timeSent");

                System.out.println(msgID + "        " + fromID + "      " + messageBody + "     " + toUserID
                        + "        " + toGroupID + "       " + timeSent.toString());

                count++;
            }
            System.out.println("---------------------------------------------");

            System.out.println("Expected: 0");
            System.out.println("Observed: " + count);

            test4 = (count == 0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Get the message recipient table after
        boolean test5 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM messageRecipient;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("messageRecipient Table AFter Sending Message To Group User Not In");
            System.out.println("---------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int userID = rs.getInt("userID");

                System.out.println(msgID + "        " + userID);

                count++;
            }
            System.out.println("---------------------------------------------");

            System.out.println("Expected: 0");
            System.out.println("Observed: " + count);

            test5 = (count == 0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Send a message to a group the user is in
        beSocial.sendMessageToGroup(1, "Test from driver!");

        // Get the message table after
        boolean test6 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM message;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Message Table Before Sending Message To Group User Is In");
            System.out.println("---------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int fromID = rs.getInt("fromID");
                String messageBody = rs.getString("messageBody");
                int toUserID = rs.getInt("toUserID");
                int toGroupID = rs.getInt("toGroupID");
                Timestamp timeSent = rs.getTimestamp("timeSent");

                System.out.println(msgID + "        " + fromID + "      " + messageBody + "     " + toUserID
                        + "        " + toGroupID + "       " + timeSent.toString());

                count++;
            }
            System.out.println("---------------------------------------------");

            System.out.println("Expected: 3");
            System.out.println("Observed: " + count);

            test6 = (count == 3);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Get the message recipient table after
        boolean test7 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM messageRecipient;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("messageRecipient Table AFter Sending Message To Group User Is In");
            System.out.println("---------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int msgID = rs.getInt("msgID");
                int userID = rs.getInt("userID");

                System.out.println(msgID + "        " + userID);

                count++;
            }
            System.out.println("---------------------------------------------");

            System.out.println("Expected: 3");
            System.out.println("Observed: " + count);

            test7 = (count == 3);
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (test1 && test2 && test3 && test4 && test5 && test6 && test7) {
            System.out.println("Test Send Message To Group Group Passed");
        } else {
            System.out.println("Test Send Message To Group Failed");
            System.out.println("Test 1: " + test1);
            System.out.println("Test 2: " + test2);
            System.out.println("Test 3: " + test3);
            System.out.println("Test 4: " + test4);
            System.out.println("Test 5: " + test5);
            System.out.println("Test 6: " + test6);
            System.out.println("Test 7: " + test7);
        }

        // Logout
        beSocial.logout();
    }

    private static void testSendMessageToUser() {
        // Log in to a user
        beSocial.logout();
        beSocial.login(user1.name, user1.password);

        // Send a message to another user
        System.out.println("Test send a message to a friend");
        beSocial.sendMessageToUser("Hello, this is a test message.", 2);

        // Access the database to show that the message was successfully sent
        Timestamp curTime;
        try {
            PreparedStatement s = conn.prepareStatement("SELECT * FROM clock;");
            ResultSet rs = s.executeQuery();
            rs.next();
            curTime = rs.getTimestamp(1);
        } catch (SQLException e) {
            System.out.println("Error getting current time");
            return;
        }

        String expectedMsgRow = "0, 1, Hello, this is a test message., 2, 0, " + curTime.toString();
        String expectedMsgRecRow = "0, 2";

        String obMsgRow, obMsgRecRow;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM message WHERE msgid=0;");
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM messagerecipient WHERE msgid=0;");
            ResultSet r1 = ps.executeQuery();
            ResultSet r2 = ps2.executeQuery();

            r1.next();
            r2.next();

            // Get the observed row
            int mid = r1.getInt(1);
            int fromID = r1.getInt(2);
            String msg = r1.getString(3);
            int toID = r1.getInt(4);
            Integer gID = r1.getInt(5);
            Timestamp time = r1.getTimestamp(6);
            obMsgRow = mid + ", " + fromID + ", " + msg + ", " + toID + ", " + gID + ", " + time.toString();

            mid = r2.getInt(1);
            toID = r2.getInt(2);
            obMsgRecRow = mid + ", " + toID;

        } catch (SQLException e) {
            System.out.println("Error getting data after message send");
            return;
        }

        System.out.println("Note that for toGroupID being 0 indicates null. JDBC getInt() returns 0.");
        System.out.println("Expected message and message recipient insertions");
        System.out.println("-----------------------------------------------------");
        System.out.println(expectedMsgRow + "\n" + expectedMsgRecRow);
        System.out.println("-----------------------------------------------------\n");

        System.out.println("Observed message and message recipient insertions");
        System.out.println("-----------------------------------------------------");
        System.out.println(obMsgRow + "\n" + obMsgRecRow);
        System.out.println("-----------------------------------------------------");

        if (expectedMsgRecRow.equals(obMsgRecRow) && expectedMsgRow.equals(obMsgRow)) {
            System.out.println("This test has passed!\n");
        } else {
            System.out.println("This test has failed.\n");
        }

        // Send message to a user that does not exist/bad send
        System.out.println("Test send a message that is not allowed. User sends a message to themselves");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.sendMessageToUser("Hello, this is a test message.", 0);

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();

        System.out.println(output);

        if (output.contains("You are not friends with this user or they do not exist.")) {
            System.out.println("Test Send Message To User Works");
        } else {
            System.out.println("Test Send Message To User Does Not Work!");
        }

        

        beSocial.logout();
    }

    private static void testSearchForProfile() {

        // Choose a random string to search for and show that the results are none
        beSocial.login("admin", "admin");

        System.out.println("Random String Search, should return nothing");
        System.out.println("---------------------------------------");
        beSocial.searchForProfile("sofhesofhsoefhsoeifh");
        System.out.println("---------------------------------------");

        // choose one of the users and enter a string that matches
        System.out.println("Search for anyone named Kenny, should return 1 userID");
        System.out.println("---------------------------------------");
        beSocial.searchForProfile("Kenny");
        System.out.println("---------------------------------------");

        // Choose @ to get all users
        System.out.println("Search for @, since every email has it, it should return 6 userIDs");
        System.out.println("---------------------------------------");
        beSocial.searchForProfile("@");
        System.out.println("---------------------------------------");

        // Now choose two words, each one for a different user and show that the result
        // is a union
        System.out.println(
                "Search using two words, since theres only one Kenny and one Swift, should return two userIDs");
        System.out.println("---------------------------------------");
        beSocial.searchForProfile("Kenny Swift");
        System.out.println("---------------------------------------");
    }

    // Nick
    private static void testLeaveGroup() {
        // Log in to a user that is in a group that is not full (group id 1)
        beSocial.login(user4.name, user4.password);

        // Query the DB for group 2 and show it's group members
        boolean test1 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM groupMember where gID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing group members from group 2 before removal:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String role = rs.getString("role");
                Timestamp lastConfirmed = rs.getTimestamp("lastConfirmed");

                System.out.println(gID + "      " + userID + "      " + role + "     " + lastConfirmed.toString());
                count++;
            }
            System.out.println("----------------------------------------------------");

            System.out.println("Expected number of entries: 4");
            System.out.println("Actual number of entries: " + count);

            test1 = (count == 4);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Leave the group
        beSocial.leaveGroup(2);

        // Query the group again and show that a member was removed
        boolean test2 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM groupMember where gID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing group members from group 2 after removal:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String role = rs.getString("role");
                Timestamp lastConfirmed = rs.getTimestamp("lastConfirmed");

                System.out.println(gID + "      " + userID + "      " + role + "     " + lastConfirmed.toString());
                count++;
            }
            System.out.println("----------------------------------------------------");

            System.out.println("Expected number of entries: 3");
            System.out.println("Actual number of entries: " + count);

            test2 = (count == 3);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try and join group 0 which should be full
        beSocial.initiateAddingGroup(0, "");

        boolean test6 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table before test");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 1");
            System.out.println("Actual number of entries: " + count);

            test6 = (count == 1);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Log in to user 2 who is in group 0
        beSocial.logout();
        beSocial.login(user2.name, user2.password);

        // Show group 0 before user 2 leaves
        boolean test3 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM groupMember where gID=0;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Displaying group 0 before user 2 leaves:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String role = rs.getString("role");
                Timestamp lastConfirmed = rs.getTimestamp("lastConfirmed");

                System.out.println(gID + "      " + userID + "      " + role + "     " + lastConfirmed.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 2");
            System.out.println("Actual number of entries: " + count);

            test3 = (count == 2);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Have user 2 leave group 0
        beSocial.leaveGroup(0);

        // Query for groupMembers in that group and show that the previous member
        // has left but a new member (user 4) filled that whole
        boolean test4 = false;
        boolean test5 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM groupMember where gID=0;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Displaying group 0 after user 2 leaves:");
            System.out.println("----------------------------------------------------");

            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                if (!test5 && userID == 4) {
                    test5 = true;
                }
                String role = rs.getString("role");
                Timestamp lastConfirmed = rs.getTimestamp("lastConfirmed");

                System.out.println(gID + "      " + userID + "      " + role + "     " + lastConfirmed.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 2");
            System.out.println("Actual number of entries: " + count);

            test4 = (count == 2);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Now show pending groupRequests for that group and show that the user who was
        // added had their pending
        // entry removed
        boolean test7 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table after removal from full group");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 0");
            System.out.println("Actual number of entries: " + count);

            test7 = (count == 0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try and leave a group you are not in
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.leaveGroup(0);

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();

        boolean test8 = output.contains("ERROR: Not a member of any Groups");

        if (test1 && test2 && test3 && test4 && test5 && test6 && test7 && test8) {
            System.out.println("All Leave Group Tests Passed");
        } else {
            System.out.println("Test Leave Group Failed");
            System.out.println("Test 1: " + test1);
            System.out.println("Test 2: " + test2);
            System.out.println("Test 3: " + test3);
            System.out.println("Test 4: " + test4);
            System.out.println("Test 5: " + test5);
            System.out.println("Test 6: " + test6);
            System.out.println("Test 7: " + test7);
            System.out.println("Test 8: " + test8);
        }

        beSocial.logout();
    }

    private static void testConfirmGroupMembership() {
        // Clear the pendingGroupMember table
        // Delete all entries from pendingGroupMember table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM pendingGroupMember;";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to remove pendingGroupMember");
            return;
        }

        // Delete all entries from groupMember table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM groupMember WHERE role='member';";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to remove groupMember");
            return;
        }

        // Add some table requests

        beSocial.login(user2.name, user2.password);
        beSocial.initiateAddingGroup(0, "");
        beSocial.initiateAddingGroup(1, "");
        beSocial.initiateAddingGroup(2, "");
        beSocial.logout();

        beSocial.login(user3.name, user3.password);
        beSocial.initiateAddingGroup(0, "");
        beSocial.initiateAddingGroup(1, "");
        beSocial.initiateAddingGroup(2, "");
        beSocial.logout();

        beSocial.login(user4.name, user4.password);
        beSocial.initiateAddingGroup(0, "");
        beSocial.initiateAddingGroup(1, "");
        beSocial.initiateAddingGroup(2, "");
        beSocial.logout();

        // Show the pending group member table before
        boolean test1 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table before test");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 9");
            System.out.println("Actual number of entries: " + count);

            test1 = (count == 9);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Log into user from createGroup test
        beSocial.login(user1.name, user1.password);

        // Try and accept members from group 0 with a size of 2 to show that it won't go
        // over group limit
        List<List<Integer>> usersChosen = new LinkedList<>();
        usersChosen.add(Arrays.asList(0, 2));
        usersChosen.add(Arrays.asList(0, 3));
        usersChosen.add(Arrays.asList(0, 4));

        beSocial.confirmGroupMembership(1, usersChosen);

        System.out.println("There should be two error messages saying cannot exceed max group size");

        // Show pending group members for group 0 to show that the users who were
        // accepted but group is full won't be removed
        boolean test2 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table after adding to a group that fills up test");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 8");
            System.out.println("Actual number of entries: " + count);

            test2 = (count == 8);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Accept pending members for the second and third group
        beSocial.confirmGroupMembership(-1, usersChosen);

        // Show that yet again, the two members waiting to join group 0 are still there
        boolean test3 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table after adding to a group that fills up test");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 2");
            System.out.println("Actual number of entries: " + count);

            test3 = (count == 2);
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Show that the accepted members are no longer in the pendinggroupmember table
        // but are in groupmember table as members
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM groupMember WHERE role='member';";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing groupMember table after adding to a groups");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String role = rs.getString("role");
                Timestamp lastConfirmed = rs.getTimestamp("lastConfirmed");

                System.out.println(gID + "      " + userID + "      " + role + "     " + lastConfirmed.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 7");
            System.out.println("Actual number of entries: " + count);

            test3 = (count == 7);
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        // logout
        beSocial.logout();

        // log into admin
        beSocial.login("admin", "admin");

        // show that admin has no groups
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.confirmGroupMembership(-1, null);

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();
        System.out.println(output);
        boolean test4 = output.contains("No groups are currently managed");

        beSocial.logout();

        // Try and accept request for group that has no requests
        beSocial.login(user1.name, user1.password);

        // Delete last two entries from pendingGroupMember table manually
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM pendingGroupMember;";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to remove pendingGroupMember");
            return;
        }

        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        old = System.out;
        System.setOut(ps);

        beSocial.confirmGroupMembership(-1, null);

        System.out.flush();
        System.setOut(old);

        output = baos.toString();

        System.out.println(output);
        boolean test5 = output.contains("No Pending Group Membership Requests");

        beSocial.logout();

        if (test1 && test2 && test3 && test4 && test5) {
            System.out.println("Test Confirm Group Membership Passed");
        } else {
            System.out.println("Test Confirm Group Membership Failed");
            System.out.println("Test 1: " + test1);
            System.out.println("Test 2: " + test2);
            System.out.println("Test 3: " + test3);
            System.out.println("Test 4: " + test4);
            System.out.println("Test 5: " + test5);
        }

    }

    private static void testInitiateAddingGroup() {
        // Log into a user that was not used in testCreateGroup
        beSocial.login(user2.name, user2.password);

        // Show that there are no entries in pendingGroupMember
        boolean test1 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table before test");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 0");
            System.out.println("Actual number of entries: " + count);

            test1 = (count == 0);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Send a request to group 0, with body of "Hello, I would like to join your
        // group!"
        beSocial.initiateAddingGroup(0, "Hello, I would like to join your group!");

        // Show that there is one entry in pendinggroupmember for group 0 with user id
        // and message
        boolean test2 = false;
        boolean test5 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table after requesting to join");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                if (!test5 && requestText.contains("Hello, I would like to join your group!")) {
                    test5 = true;
                }
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 1");
            System.out.println("Actual number of entries: " + count);

            test2 = (count == 1);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Send a request to group 1 with no body
        beSocial.initiateAddingGroup(1, "");

        // Show that there are two entries now, and one has the default message
        boolean test3 = false;
        boolean test4 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table after requesting to join with no message");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");
                if (!test4 && requestText.contains("I would like to join your group! :)")) {
                    test4 = true;
                }
                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 2");
            System.out.println("Actual number of entries: " + count);

            test3 = (count == 2);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try joining a group that does not exist and show that it will not change
        // pendinggroupmember table
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.initiateAddingGroup(100, "");

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();
        System.out.println(output);
        boolean test6 = output.contains("The group you tried to join does not exist");
        boolean test7 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingGroupMember;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing pendingGroupMember table after requesting to join group that doesn't exist");
            System.out.println("----------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int gID = rs.getInt("gID");
                int userID = rs.getInt("userID");
                String requestText = rs.getString("requestText");

                Timestamp requestTime = rs.getTimestamp("requestTime");

                System.out.println(gID + "      " + userID + "      " + requestText + "     " + requestTime.toString());
                count++;
            }
            System.out.println("----------------------------------------------");

            System.out.println("Expected number of entries: 2");
            System.out.println("Actual number of entries: " + count);

            test7 = (count == 2);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try joining a group we already joined

        // Add user to group 1
        try {
            Statement st = conn.createStatement();
            String query = "INSERT INTO groupMember VALUES(1,2,'member', '2016-04-24 10:47:12');";
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try sending request to join group 1
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        old = System.out;
        System.setOut(ps);

        beSocial.initiateAddingGroup(1, "");

        System.out.flush();
        System.setOut(old);

        output = baos.toString();

        System.out.println(output);
        boolean test8 = output.contains("You are already in this group");

        // Try joining a group we already requested to join
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        old = System.out;
        System.setOut(ps);

        beSocial.initiateAddingGroup(0, "");

        System.out.flush();
        System.setOut(old);

        output = baos.toString();

        boolean test9 = output.contains("You already tried to join this group");

        if (test1 && test2 && test3 && test4 && test5 && test6 && test7 && test8 && test9) {
            System.out.println("Test Initiate Adding Group Passed");
        } else {
            System.out.println("Test Initiate Adding Group Failed");
            System.out.println("Test 1: " + test1);
            System.out.println("Test 2: " + test2);
            System.out.println("Test 3: " + test3);
            System.out.println("Test 4: " + test4);
            System.out.println("Test 5: " + test5);
            System.out.println("Test 6: " + test6);
            System.out.println("Test 7: " + test7);
            System.out.println("Test 8: " + test8);
            System.out.println("Test 9: " + test9);
        }

        // Logout
        beSocial.logout();
    }

    // Nick
    private static void testCreateGroup() {
        // Login to random user, doesn't matter

        // only login user if they are not logged in
        if (BeSocial.userID == -1) {
            beSocial.login(user1.name, user1.password);
        }

        // drop the tables first (if any):
        try {
            Statement st = conn.createStatement();
            String deleteQuery = "DELETE FROM groupInfo";
            st.executeUpdate(deleteQuery);
            String query = "SELECT * FROM groupInfo";
            ResultSet rs = st.executeQuery(query);
            if (!rs.next()) {
                System.out.println("Group info is empty");
            }
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }
        // Create a group with a name and description
        System.out.println("Creating three new groups...");
        beSocial.createGroup("Test Group 1", "This is a test group description #1", 2);
        beSocial.createGroup("Test Group 2", "This is a test group description #2", null); // test default value 10
        beSocial.createGroup("Test Group 3", "This is a test group description #3", 17);
        // Print groupInfo to show that three groups have been created
        try {
            Statement st = conn.createStatement();
            String query = "SELECT COUNT(*) FROM groupInfo;";
            ResultSet rs = st.executeQuery(query);
            rs.next();
            int count = rs.getInt(1);
            System.out.println("Number of groups created: " + count);
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

        // displaying newly created groupInfo

        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM groupInfo;";
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                System.out.println("Group ID: " + rs.getInt(1));
                System.out.println("Group Name: " + rs.getString(2));
                System.out.println("Group Size: " + rs.getInt(3));
                System.out.println("Group Description: " + rs.getString(4));
            }
            st.close();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

        // If all this is good, the test passed
        System.out.println("testCreateGroups passed!");
        // Logout of user
        beSocial.logout();
    }

    // Steven
    private static void testConfirmFriendRequests() {
        // Add two more friend requests to user two
        beSocial.login(user1.name, user1.password);
        beSocial.initiateFriendship(2);
        beSocial.logout();

        beSocial.login(user3.name, user3.password);
        beSocial.initiateFriendship(2);
        beSocial.logout();

        // Log in to user 2
        beSocial.login(user2.name, user2.password);

        // Show that user 2 has three friend requests
        boolean test1 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingFriend WHERE toID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("pendingFriend at start of testConfirmFriendRequest:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int fromID = rs.getInt("fromID");
                int toID = rs.getInt("toID");
                String requestText = rs.getString("requestText");
                System.out.println(fromID + "       " + toID + "        " + requestText);
                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 3");
            System.out.println("Actual Count: " + count);

            test1 = (count == 3);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Show that user 2 has no friends
        boolean test2 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM friend WHERE userID1=2 OR userID2=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("User 2 Friends: ");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int userID1 = rs.getInt("userID1");
                int userID2 = rs.getInt("userID2");
                Date JDate = rs.getDate("JDate");
                String requestText = rs.getString("requestText");

                System.out
                        .println(userID1 + "      " + userID2 + "     " + JDate.toString() + "        " + requestText);

                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 0");
            System.out.println("Actual Count: " + count);

            test2 = (count == 0);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Accept 1 friend
        System.out.println("Accepting Friend Request From User 4");
        beSocial.confirmFriendRequests(2, 4);

        // Show that user 2 has one friend
        boolean test3 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM friend WHERE userID1=2 OR userID2=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("User 2 Friends: ");
            int count = 0;
            while (rs.next()) {
                int userID1 = rs.getInt("userID1");
                int userID2 = rs.getInt("userID2");
                Date JDate = rs.getDate("JDate");
                String requestText = rs.getString("requestText");

                System.out
                        .println(userID1 + "      " + userID2 + "     " + JDate.toString() + "        " + requestText);

                count++;
            }
            System.out.println("Expected Count: 1");
            System.out.println("Actual Count: " + count);

            test3 = (count == 1);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Show that the other two friend requests were deleted
        boolean test4 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingFriend WHERE toID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("pendingFriend table:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int fromID = rs.getInt("fromID");
                int toID = rs.getInt("toID");
                String requestText = rs.getString("requestText");
                System.out.println(fromID + "       " + toID + "        " + requestText);
                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 0");
            System.out.println("Actual Count: " + count);

            test4 = (count == 0);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Add 2 friend requests and show they are there
        beSocial.logout();
        beSocial.login(user1.name, user1.password);
        beSocial.initiateFriendship(2);
        beSocial.logout();

        beSocial.login(user3.name, user3.password);
        beSocial.initiateFriendship(2);
        beSocial.logout();

        // Show that user 2 has two friend requests
        beSocial.login(user2.name, user2.password);
        boolean test5 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingFriend WHERE toID=2";
            ResultSet rs = st.executeQuery(query);

            System.out.println("pendingFriend after adding two friend requests:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int fromID = rs.getInt("fromID");
                int toID = rs.getInt("toID");
                String requestText = rs.getString("requestText");
                System.out.println(fromID + "       " + toID + "        " + requestText);
                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 2");
            System.out.println("Actual Count: " + count);

            test5 = (count == 2);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Accept all friend request for user 2
        beSocial.confirmFriendRequests(1, 0);

        // Show that user 2 has three friends
        boolean test6 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM friend WHERE userID1=2 OR userID2=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("User 2 Friends: ");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int userID1 = rs.getInt("userID1");
                int userID2 = rs.getInt("userID2");
                Date JDate = rs.getDate("JDate");
                String requestText = rs.getString("requestText");

                System.out
                        .println(userID1 + "      " + userID2 + "     " + JDate.toString() + "        " + requestText);

                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 3");
            System.out.println("Actual Count: " + count);

            test6 = (count == 3);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Show that user 2 has no friend requests left
        // Show that user 2 has three friend requests
        boolean test7 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingFriend WHERE toID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("pendingFriend at end of testConfirmFriendRequest:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int fromID = rs.getInt("fromID");
                int toID = rs.getInt("toID");
                String requestText = rs.getString("requestText");
                System.out.println(fromID + "       " + toID + "        " + requestText);
                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 0");
            System.out.println("Actual Count: " + count);

            test7 = (count == 0);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Show that if there are no friend requests nothing happens
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.confirmFriendRequests(2, 10);

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();

        System.out.println(output);
        boolean test8 = output.contains("No Pending Friend Requests");

        // Show that trying to accept a request from a user that did not request to be
        // your friend doesn't work
        beSocial.logout();
        beSocial.login(user1.name, user1.password);
        beSocial.initiateFriendship(2);
        beSocial.logout();

        beSocial.login(user2.name, user2.password);

        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        old = System.out;
        System.setOut(ps);

        beSocial.confirmFriendRequests(2, 10);

        System.out.flush();
        System.setOut(old);

        output = baos.toString();

        System.out.println(output);
        boolean test9 = output.contains("There is no friend request from that user");

        if (test1 && test2 && test3 && test4 && test5 && test6 && test7 && test8 && test9) {
            System.out.println("Confirm Friend Request Passed");
        } else {
            System.out.println("Test Confirm Friend Request Failed");
            System.out.println("Test 1: " + test1);
            System.out.println("Test 2: " + test2);
            System.out.println("Test 3: " + test3);
            System.out.println("Test 4: " + test4);
            System.out.println("Test 5: " + test5);
            System.out.println("Test 6: " + test6);
            System.out.println("Test 7: " + test7);
            System.out.println("Test 8: " + test8);
            System.out.println("Test 9: " + test9);
        }

        // Log out of user 2
        beSocial.logout();
    }

    private static void testInitiateFriendship() {
        // SEND THE FRIEND REQUEST TO USER 2 FROM USER 4

        // Login to user 4
        beSocial.login(user4.name, user4.password);

        // Try sending friend request from user 4 to user 4 to show it doesn't work
        // (should show error message)
        System.out.println("Trying to send friend request to self:");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        beSocial.initiateFriendship(4);

        System.out.flush();
        System.setOut(old);

        String output = baos.toString();

        System.out.println(output);
        boolean test1 = output.contains("You cannot be friends with yourself!");

        // Print friend requests to user 2 to show they have none
        boolean test2 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingFriend WHERE toID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Printing User 2's Friend Requests:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            while (rs.next()) {
                int fromID = rs.getInt("fromID");
                int toID = rs.getInt("toID");
                String requestText = rs.getString("requestText");
                System.out.println(fromID + "       " + toID + "        " + requestText);
                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 0");
            System.out.println("Actual Count: " + count);

            test2 = (count == 0);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // Send friend request to user 2 from user 1
        beSocial.initiateFriendship(2);

        // Query the database for the friendship and show that there is a pendingFriend
        // record in the table
        // From user 2 to user 1
        boolean test3 = false;
        boolean test4 = false;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM pendingFriend WHERE toID=2;";
            ResultSet rs = st.executeQuery(query);

            System.out.println("Showing User 2's Pending Friend Table After Request:");
            System.out.println("----------------------------------------------------");
            int count = 0;
            int fromID = -1;
            while (rs.next()) {
                fromID = rs.getInt("fromID");
                int toID = rs.getInt("toID");
                String requestText = rs.getString("requestText");
                System.out.println(fromID + "       " + toID + "        " + requestText);
                count++;
            }
            System.out.println("----------------------------------------------------");
            System.out.println("Expected Count: 1");
            System.out.println("Actual Count: " + count);

            test3 = (count == 1);

            test4 = (fromID == 4);

            st.close();
        } catch (SQLException e) {
            System.out.println("Exception occurred in testConfirmFriendRequest");
        }

        // If the request is present and matches to user 1, return true, else return
        // false
        if (test1 && test2 && test3 && test4) {
            System.out.println("Initiate Friendship Test Passed");
        } else {
            System.out.println("Initiate Friendship Test Failed");
            System.out.println("Test 1: " + test1);
            System.out.println("Test 2: " + test2);
            System.out.println("Test 3: " + test3);
            System.out.println("Test 4: " + test4);

        }
        // Logout
        beSocial.logout();
    }

    // Nick
    private static void testLogin() {
        // Start by calling logout for pure isolation
        // !ONLY LOGOUT IF USER IS LOGGED IN (to prevent error message 'User is not
        // logged in')
        if (BeSocial.userID != -1) {
            beSocial.logout();
        }
        // ! ACTUAL OUTPUT
        System.out.println("Actual Output: \n\n");
        // Print the userID of beSocial, should be -1
        System.out.println("User ID Before Login: " + BeSocial.userID);
        // Log in user
        beSocial.login(user1.name, user1.password);
        // Print the userID now, should not be -1
        System.out.println("User ID After Login: " + BeSocial.userID);
        // Log out user
        System.out.println("Logging out user to test login with invalid credentials");
        beSocial.logout();
        System.out.println("Testing login with incorrect credentials");
        // Try to log in with incorrect credentials
        beSocial.login(user1.name, "wrongPassword");

        // Print the userID now, should be -1
        System.out.println("User ID After Login With Invalid Credentials " + BeSocial.userID);
        System.out.println("Login test completed\n\n");

        // ! EXPECTED OUTPUT
        System.out.println("Expected Output: \n\n");
        System.out.println("User ID Before Login: -1");
        System.out.println("Successfully logged in as Steven Jarmell");
        System.out.println("User ID After Login: 1");
        System.out.println("Logging out user to test login with invalid credentials");
        System.out.println("Logging out...");
        System.out.println("Successfully logged out");
        System.out.println("Testing login with incorrect credentials");
        System.out.println("Could not log in user, please try again.");
        System.out.println("User ID After Login With Invalid Credentials -1");
        System.out.println("Login test completed\n\n");

        // Do comparison is old == new, if false, login worked if true login didnt work
    }

    // TODO
    private static void testDropProfile() {
        beSocial.login(user1.name, user1.password);
        beSocial.dropProfile();
        System.out.println("Previous Line Should Say: This operation can only be performed by an admin");
        beSocial.logout();
        System.out.println("Test Drop Profile Not Implemented");
    }

    private static void testCreateProfile() {
        // Test to show that this cannot be done by a non-admin
        beSocial.login(user1.name, user1.password);
        beSocial.createProfile("one", "blah@pitt.edu", "1234", "2001-11-26");
        System.out.println("Previous Line Should Say: This operation can only be performed by an admin");
        beSocial.logout();

        // Log in admin since the method can only be performed by the admin
        beSocial.login("admin", "admin");

        // Delete all entries from profile table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM profile WHERE userID != 0;";
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            System.out.println("Failed to remove the profiles from the relation");
            return;
        }

        // Show that profile table is empty
        try {
            Statement st = conn.createStatement();
            String query = "SELECT * FROM profile WHERE userID;";
            ResultSet rs = st.executeQuery(query);
            System.out.println("Displaying profile table at the start of createProfile Method");
            System.out.println("--------------------------------------------------------------");
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String userName = rs.getString("name");
                String userPassword = rs.getString("password");
                String userEmail = rs.getString("email");
                String userDOB = rs.getString("date_of_birth");
                Timestamp lastLogin = rs.getTimestamp("lastlogin");
                System.out.println(userID + "     " + userName + "     " + userPassword + "     " + userEmail + "     "
                        + userDOB + "     " + lastLogin.toString());
            }
            System.out.println("--------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Try and create profiles for each user
        for (int i = 0; i < userList.length; i++) {
            beSocial.createProfile(userList[i].name, userList[i].email, userList[i].password, userList[i].dob);
        }

        beSocial.logout();

        // Display the content of the profiles table after adding the profiles
        try {
            String query = "SELECT * FROM profile where userID != 0;";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("Profile Table Contents");
            System.out.println("----------------------");
            int count = 0;
            while (rs.next()) {

                int userID = rs.getInt("userID");
                String userName = rs.getString("name");
                String userPassword = rs.getString("password");
                String userEmail = rs.getString("email");
                String userDOB = rs.getString("date_of_birth");
                Timestamp lastLogin = rs.getTimestamp("lastlogin");

                System.out.println(userID + "     " + userName + "     " + userPassword + "     " + userEmail + "     "
                        + userDOB + "     " + lastLogin.toString());

                if (!userList[count].name.equals(userName) || !userList[count].password.equals(userPassword)
                        || !userList[count].email.equals(userEmail) || !userList[count].dob.equals(userDOB)) {
                    System.out.println("Profile Table Does Not Match Expected");
                    break;
                }

                count++;
            }

            System.out.println("----------------------");

            if (count != userList.length) {
                System.out.println("Profile Table Does Not Match Expected");
                System.out.println("Expected: " + userList.length);
                System.out.println("Got: " + count);
            } else {
                System.out.println("Create Profile Function Passed Test");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static class CreateProfileObject {
        private String name;
        private String password;
        private String email;
        private String dob;

        public CreateProfileObject(String name, String password, String email, String dob) {
            this.name = name;
            this.password = password;
            this.email = email;
            this.dob = dob;
        }
    }
}
