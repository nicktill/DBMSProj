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

        // Test for method `createProfile`
        System.out.println("Press enter to test createProfile");
        sc.nextLine();
        testCreateProfile();

        

        sc.close();
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection");
        }
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
