import java.sql.Timestamp;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        beSocial = new BeSocial(databaseUsername, databasePassword);

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

    private static void testTopMessages() {
        // Assumptions: sendMessagetoUser/Group has been run before
        // 
    }

    private static void testRankProfiles() {
        System.out.println("Test Rank Profiles Not Implemented");
    }

    private static void testRankGroups() {
        System.out.println("Test Rank Groups Not Implemented");
    }

    private static void testDisplayFriends() {
        System.out.println("Test Display Friends Not Implemented");
    }

    private static void testDisplayNewMessages() {
        System.out.println("Test Display New Messages Not Implemented");
    }

    private static void testDisplayMessages() {
        System.out.println("Test Display Messages Not Implemented");
    }

    private static void testSendMessageToGroup() {
        System.out.println("Test Send Message To Group Not Implemented");
    }

    private static void testSendMessageToUser() {
        System.out.println("Test Send Message To User Not Implemented");
    }

    private static void testSearchForProfile() {
        // log out and log in to any user you want

        // WILL HAVE TO REFACTOR, should be easy

        // Choose a random string to search for and show that the results are none

        // choose one of the users and enter a string that matches
        
        // Now choose two words, each one for a different user and show that the result is a union
        System.out.println("Test Search For Profile Not Implemented");
    }

    // Nick
    private static void testLeaveGroup() {
        System.out.println("Test Leave Group Not Implemented");
        // Query the DB for the filled group and show it's group members

        // Log in to one user in that group

        // Have that user leave that specified group

        // Query for groupMembers in that group and show that the previous member
        // has left but a new member filled that whole

        // Now show pending groupRequests for that group and show that the user who was added had their pending
        // entry removed
    }

    private static void testConfirmGroupMembership() {
        // Log into user from createGroup test

        // Show the pending group member table before

        // Try and accept members from the third group with a size of 2 to show that it won't go over group limit

        // Show pending group members for group 3 to show that the users who were accepted but group is full won't be removed

        // Accept pending members for the second group

        // Show that the accepted members are no longer in the pendinggroupmember table but are in groupmember table as members

        // logout

        // log into admin

        // show that admin has no groups
    }

    private static void testInitiateAddingGroup() {
        // Log into a user that was not used in testCreateGroup

        // Show that there are no entries in pendingGroupMember

        // Send a request to group 0, with body of "Hello, I would like to join your group!"

        // Show that there is one entry in pendinggroupmember for group 0 with user id and message

        // Send a request to group 1 with no body

        // Show that there are two entries now, and one has the default message

        // Try joining a group that does not exist and show that it will not change pendinggroupmember table

        // Logout
    }

    // Nick
    private static void testCreateGroup() {
        // Login to random user, doesn't matter

        // Print groupInfo to show that it is empty

        // Create three groups, one of which has a max group size of 2

        // Print groupInfo to show that three group have been created

        // Print groupMember to show that the logged in user has been added as a manager of every group

        // If all this is good, the test passed

        // Logout of user
    }

    // Steven
    // TEST IT BY RUNNING BESOCIAL
    // WRITE/TEST DRIVER FUNCTION
    private static void testConfirmFriendRequests() {
        // Log in to user 2

        // Add two more friend requests to user two

        // Show that user 2 has three friend requests

        // Show that user 2 has no friends

        // Accept 1 friend

        // Show that user 2 has one friend
        
        // Show that user 2 has two pending friend requests

        // Accept all friend request for user 2

        // Show that user 2 has three friends

        // Show that user 2 has no friend requests left

        // Log out of user 2
    }

    // Nick
    private static void testInitiateFriendship() {
        // SEND THE FRIEND REQUEST TO THE SAME USER CONFIRMFRIENDREQUESTS SENDS IT TO
        
        // Choose two users

        // Logout, login to user 1

        // Try sending friend request from user 1 to user 1 to show it doesn't work (should show error message)

        // Print friend requests to user 2 to show they have none
        
        // Send friend request to user 2 from user 1
    
        // Query the database for the friendship and show that there is a pendingFriend record in the table
        // From user 2 to user 1
    
        // If the request is present and matches to user 1, return true, else return false

        // Logout
    }

    // Nick
    private static void testLogin() {
        // Start by calling logout for pure isolation

        // Print the userID of beSocial, should be -1
        
        // Log in user

        // Print the userID now, should not be -1

        // Do comparison is old == new, if false, login worked if true login didnt work
    }

    private static void testDropProfile() {
        System.out.println("Test Drop Profile Not Implemented");
    }

    private static void testCreateProfile() {
        // Log in admin since the method can only be performed by the admin
        beSocial.login("admin", "admin");

        // Delete all entries from profile table
        try {
            Statement st = conn.createStatement();
            String query = "DELETE FROM profile WHERE userID != 0;";
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Failed to remove the profiles from the relation");
            return;
        }

        // Show that profile table is empty

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

                System.out.println(userID + "     " + userName + "     " + userPassword + "     " + userEmail + "     " + userDOB + "     " + lastLogin.toString());

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
