import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

/** Questions for TA
 * 1. Should we manually create users so that is deterministic
 * 2. Should we show that some methods do not work when logged in/out?
 * 3. Should we assume an empty database? Or can we assume at least admin is in there
 */

/**
 * The driver program needs to call all of the above functions and display the
 * content of the
 * affected rows of the affected tables after each cal
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

        // Test for method `dropProfile` | Task 2
        System.out.println("Press enter to test dropProfile");
        sc.nextLine();
        testDropProfile();

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

    /** Test Exit Method
     * After calling the exit method, beSocial should close the scanner and connection to the database
     */
    private static void testExit() {
        // Call the exit function
        beSocial.exit();

        if (beSocial == null) {
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
        Timestamp logInTimestamp = null;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT lastlogin FROM profile WHERE userID=0;";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            logInTimestamp = rs.getTimestamp("lastlogin");

            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (logInTimestamp == null) {
            System.out.println("Error getting lastlogin timestamp in test logout");
        } else {
            System.out.println("\nTime In Profile Table When Logged In: " + logInTimestamp);
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
        Timestamp logOutTimeStamp = null;
        try {
            Statement st = conn.createStatement();
            String query = "SELECT lastlogin FROM profile WHERE userID=0;";
            ResultSet rs = st.executeQuery(query);

            rs.next();
            logOutTimeStamp = rs.getTimestamp("lastlogin");

            st.close();
        } catch (SQLException e) {
            System.out.println(e);
        }

        if (logOutTimeStamp == null) {
            System.out.println("Error getting lastlogin timestamp in test logout");
        } else {
            System.out.println("\nTime In Profile Table When Logged In: " + logOutTimeStamp);
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

        if (logInTimestamp.equals(logOutTimeStamp)) {
            System.out.println("\nTest Logout Failed");
        } else {
            System.out.println("\nTest Logout Passed");
        }
    }

    private static void testThreeDegrees() {
        System.out.println("Test Three Degrees Not Implemented");
    }

    private static void testTopMessages() {
        System.out.println("Test Top Messages Not Implemented");
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
        System.out.println("Test Search For Profile Not Implemented");
    }

    private static void testLeaveGroup() {
        System.out.println("Test Leave Group Not Implemented");
    }

    private static void testConfirmGroupMembership() {
        System.out.println("Test Confirm Group Membership Not Implemented");
    }

    private static void testInitiateAddingGroup() {
        System.out.println("Test Initiate Adding Group Not Implemented");
    }

    private static void testCreateGroup() {
        System.out.println("Test Create Group Not Implemented");
    }

    private static void testConfirmFriendRequests() {
        System.out.println("Test Confirm Friend Requests Not Implemented");
    }

    private static void testInitiateFriendship() {
        System.out.println("Test Initiate Friendship Not Implemented");
    }

    private static void testLogin() {
        System.out.println("Test Login Not Implemented");
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

        // Try and create profiles for each user
        for (int i = 0; i < userList.length; i++) {
            beSocial.createProfile(userList[i].name, userList[i].email, userList[i].password, userList[i].dob);
        }

        beSocial.logout();

        // Display the content of the profiles table after adding the profiles
        try {
            String query = "SELECT name, password, email, date_of_birth FROM profile where userID != 0;";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("Profile Table Contents");
            System.out.println("----------------------");
            int count = 0;
            while (rs.next()) {
                String userName = rs.getString("name");
                String userPassword = rs.getString("password");
                String userEmail = rs.getString("email");
                String userDOB = rs.getString("date_of_birth");

                System.out.println(userName + "     " + userPassword + "        " + userEmail + "       " + userDOB);

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
