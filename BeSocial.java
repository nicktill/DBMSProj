import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.util.ArrayList;

// **NOTE** PLEASE USE THE EXTENSION 'BetterNotes' to make this file more readable! **NOTE** 

/**
 * Questions
 *  - CASE 19 - Should this also be a admin only task since it requires userID? 
 *  - CASE 4 - Same as the above. It says based on userID but shouldn't it use email?
 *  - TASK 8 - Can I display the user ID or do I have to display their names?
 *  - Why do we have the updategroup trigger in phase 1? The confirmGroupMembership function says the accepted
        request should remain in pendingGroupMember. There is no indiction for pendingGroupMember whether or not the member was accepted previously
    - TASK 10 - Do we need the entire user profile or just their userID?
    - 
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class BeSocial {
    private static Scanner sc;
    private static Connection conn;
    private static int userID = -1;
    private static boolean isLoggedIn;
    private static final int ADMIN_USER_ID = 0;
    private static BeSocial beSocial;

    public BeSocial(String databaseUsername, String databasePassword) {
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
    }

    public static void main(String[] args) {
        sc = new Scanner(System.in);

        String databaseUsername = null;
        String databasePassword = null;

        // Try and get the username and password from the user to access the database
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

        beSocial = new BeSocial(databaseUsername, databasePassword);

        // If the connection is properly established, clear command line and start

        System.out.print("\033\143"); // Clears the command line console, shoutout stack overflow

        System.out.println("Welcome to BeSocial!\n");

        // Run the main program loop until the user exits
        try {
            int userInput = -1;
            isLoggedIn = false;
            while (true) {
                beSocial.displayMenu(isLoggedIn);

                System.out.println("Choose an option from the menu: ");
                try{
                    userInput = Integer.parseInt(sc.nextLine());
                }
                catch(Exception e){
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }
                // Validate user input based on logged in status
                // If the option they selected is invalid, print statement and continue to next
                // loop
                if (isLoggedIn) {
                    // If they are logged in and choose 3 or if they are not an admin and choose 1,
                    // it is invalid
                    if (userInput == 3) {
                        System.out.println("This option is invalid for logged in users");
                        continue;
                    }
                    if ((!(userID == ADMIN_USER_ID) && userInput == 1)
                            || (!(userID == ADMIN_USER_ID) && userInput == 2)) {
                        System.out.println("You do not have permission to perform this operation.");
                    }
                } else {
                    // If they are not logged in, they can only choose login or exit
                    if (!(userInput == 3 || userInput == 21)) {
                        System.out.println("This option is invalid for a user who is not logged in");
                        continue;
                    }
                }

                switch (userInput) {
                    case 1:
                        // Get information from the user
                        String name, email, password, dob;
                        System.out.print("Enter name: ");
                        name = sc.nextLine();
                        while (name.isEmpty()) {
                            System.out.print("You must enter a name: ");
                            name = sc.nextLine();
                        }

                        String emailRegex = "^(.+)@(\\S+)$";
                        Pattern pat = Pattern.compile(emailRegex);

                        System.out.print("Enter email: ");
                        email = sc.nextLine();
                        Matcher match = pat.matcher(email);
                        while (email.isEmpty() || !match.matches()) {
                            System.out.print("You must enter a valid email: ");
                            email = sc.nextLine();
                            match = pat.matcher(email);
                        }

                        System.out.print("Enter Password: ");
                        password = sc.nextLine();
                        while (password.isEmpty()) {
                            System.out.print("You must enter a password: ");
                            password = sc.nextLine();
                        }

                        String dobRegex = "^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
                        Pattern pattern = Pattern.compile(dobRegex);

                        System.out.print("Enter DOB in format 'YYYY-MM-DD': ");
                        dob = sc.nextLine();
                        Matcher matcher = pattern.matcher(dob);
                        while (dob.isEmpty() || !matcher.matches()) {
                            System.out.print("You must enter a DOB format in 'YYYY-MM-DD': ");
                            dob = sc.nextLine();
                            matcher = pattern.matcher(dob);
                        }
                        beSocial.createProfile(name, email, password, dob);
                        break;
                    case 2:
                    beSocial.dropProfile();
                        break;
                    case 3:
                        String username;
                        System.out.print("Enter BeSocial username: ");
                        username = sc.nextLine();
                        System.out.print("Enter password: ");
                        password = sc.nextLine();
                        beSocial.login(username, password);
                        break;
                    case 4:
                        beSocial.initiateFriendship();
                        break;
                    case 5:
                        beSocial.confirmFriendRequests(userID);
                        break;
                    case 6:
                    beSocial.createGroup();
                        break;
                    case 7:
                    beSocial.initiateAddingGroup();
                        break;
                    case 8:
                    beSocial.confirmGroupMembership();
                        break;
                    case 9:
                    beSocial.leaveGroup();
                        break;
                    case 10:
                    beSocial.searchForProfile();
                        break;
                    case 11:
                    beSocial.sendMessageToUser();
                        break;
                    case 12:
                    beSocial.sendMessageToGroup();
                        break;
                    case 13:
                    beSocial.displayMessages();
                        break;
                    case 14:
                    beSocial.displayNewMessages();
                        break;
                    case 15:
                    beSocial.displayFriends();
                        break;
                    case 16:
                    beSocial.rankGroups();
                        break;
                    case 17:
                    beSocial.rankProfiles();
                        break;
                    case 18:
                    beSocial.topMessages();
                        break;
                    case 19:
                    beSocial.threeDegrees();
                        break;
                    case 20:
                    beSocial.logout();
                        break;
                    case 21:
                    beSocial.exit();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            beSocial.endProgram();
        }

        System.out.println("Thank you for using BeSocial");
        beSocial.endProgram();
    }

    /**
     * Helper Method that closes the DB Connection, closes the scanner, and ends the
     * program
     */
    private void endProgram() {
        System.out.println("Ending program");

        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing DB connection");
        }
        sc.close();
        System.exit(0);
    }

    // TODO CASE 1
    // * Given a name, email address, password and date of birth, add a new user to
    // the system by
    // * inserting a new entry into the profile relation. userIDs should be
    // auto-generated.
    public void createProfile(String name, String email, String password, String dob) {
        if (userID != ADMIN_USER_ID) {
            System.out.println("This operation can only be performed by an admin");
            return;
        }

        try {
            /*
             * The Profile Schema is Profile (userID, name, email, password, date_of_birth,
             * lastlogin)
             * We have a database-side trigger to give a user an ID so we can leave it as
             * -1.
             * We also have a database-side trigger to inser the max time from the clock
             * into lastlogin.
             */
            PreparedStatement createProfile = conn.prepareStatement(
                    "INSERT INTO profile VALUES(NULL, ? , ? , ? , ?, NULL);");
            createProfile.setString(1, name);
            createProfile.setString(2, email);
            createProfile.setString(3, password);
            createProfile.setDate(4, Date.valueOf(dob));

            // Enforce transaction atomicity
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            createProfile.executeUpdate();
            conn.commit();
            System.out.println("Profile created successfully!");
        } catch (SQLException e) {
            // If a SQLException occurs, try to roll back the transaction and if that fails,
            // end the program
            System.out.println("An error occurred adding a profile to the table");
            try {
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("An error occurred while rolling back the transaction");
                endProgram();
            }
        } finally {
            // No matter what, we need to set Auto Commit back to true
            try {
                conn.setAutoCommit(true);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } catch (SQLException e) {
                // If another error occurs, just tank the program
                System.out.println("Unexpected error occurred while setting auto commit back to false");
                endProgram();
            }
        }
    }

    // * Given email and password, login as the user in the system when an
    // appropriate match is found.
    public void login(String username, String password) {
        try {
            String qry = "SELECT * FROM profile WHERE name=? AND password=?;";
            PreparedStatement loginQuery = conn.prepareStatement(qry);
            loginQuery.setString(1, username);
            loginQuery.setString(2, password);
            ResultSet rs = loginQuery.executeQuery();

            if (rs.next() == false) {
                System.out.println("Could not log in user, please try again.");
            } else {
                userID = rs.getInt("userID");
                System.out.printf("Successfully logged in as %s\n", username);
                isLoggedIn = true;
            }
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: Could not access the database");
        }
    }

    /**
     * Helper method to display the menu for a user's options
     * 
     * @param isLoggedIn Indicates whether the user is logged in or not
     */
    public void displayMenu(boolean isLoggedIn) {
        System.out.println("------------------------------------------------------");

        System.out.println("Input the number of the task you want to perform");

        // Admin has user ID 0
        if (userID == ADMIN_USER_ID) {
            System.out.println("1 - Create Profile");
            System.out.println("2 - Drop Profile");
        }

        if (isLoggedIn) {
            System.out.println("4 - Initiate Friendship");
            System.out.println("5 - Confirm Friend Request(s)");
            System.out.println("6 - Create Group");
            System.out.println("7 - Add Group");
            System.out.println("8 - Confirm Group Member");
            System.out.println("9 - Leave Group");
            System.out.println("10 - Search For Profile");
            System.out.println("11 - Send Message To User");
            System.out.println("12 - Send Message To Group");
            System.out.println("13 - Display Messages");
            System.out.println("14 - Display New Messages");
            System.out.println("15 - Display Friends");
            System.out.println("16 - Rank Groups");
            System.out.println("17 - Rank Profiles");
            System.out.println("18 - Top Messages");
            System.out.println("19 - Three Degrees");
            System.out.println("20 - Logout");
            System.out.println("21 - Exit");
        } else {
            System.out.println("3 - Login");
            System.out.println("21 - Exit");
        }

        System.out.println("------------------------------------------------------");
    }

    // TODO case 2
    // * This functions prompts for a user email and removes the profile along with
    // all of their information from the system. When a profile is removed, the
    // system should use a trigger to delete
    // * the user from the groups they are a member of. The system should also use a
    // trigger to
    // * delete any message whose sender and all receivers are deleted. Attention
    // should be paid to
    // * handling integrity constraints.
    public void dropProfile() {
        if (userID != ADMIN_USER_ID) {
            System.out.println("This operation can only be performed by an admin");
            return;
        }
        // still needs to be checked by TA
        String email;
        System.out.print("Enter the email to drop profile for: ");
        email = sc.nextLine();

        try {
            // call delete from profile on specified email, cascade (to move other
            // associated data)
            PreparedStatement dropProfile = conn.prepareStatement(
                    "DELETE FROM PROFILE WHERE EMAIL = ?;");
            dropProfile.setString(1, email); // set email as first parameter
            int rs = dropProfile.executeUpdate();
            if (rs == 1) {
                System.out.printf("Successfully dropped profile for %s\n", email);
            } else {
                System.out.println("Unable to drop that profile, please ensure you are entering a valid email");
            }

        } catch (Exception e) {
            // code to handle exception here
            System.out.println("Error caught in dropProfile function" + e.getMessage());
        }
    }

    // TODO CASE 4
    // * Create a pending friendship from the logged-in user profile to another user
    // profile based on
    // * userID. The application should display the name of the person that will be
    // sent a friend request
    // * and the user should be prompted to enter the text to be sent along with the
    // request. A last
    // * confirmation should be requested of the user before an entry is inserted
    // into the pendingFriend
    // * relation, and success or failure feedback is displayed for the user.
    public void initiateFriendship() {
        System.out.print("Enter the userID of the friend you want to request: ");
        int toID = 0;
        boolean validInput = false;
        
        do {
            if (sc.hasNextInt()) {
                toID = sc.nextInt();
                validInput = true;
            } else {
                System.out.println("Invalid input please enter a number" + 
                "\nEnter the userID of the friend you want to request: ");
                sc.nextLine();
            }
        } while (!validInput);
        
        sc.nextLine(); // Clear buffer

        // Get the user entry and confirm the operation with the user
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "SELECT * FROM profile WHERE userID = ?;");
            statement.setInt(1, toID);
            ResultSet rs = statement.executeQuery();
            if (rs.next() == false) {
                System.out.println("This user does not exist");
                return;
            }
            String name = rs.getString("name");
            System.out.printf("You want to request a friendship with %s? (y/n): ", name);
            char ans = sc.nextLine().toLowerCase().charAt(0);
            if (ans == 'n') {
                System.out.println("Okay. This user will not be added");
                return;
            }
        } catch (SQLException e) {
            printErrors(e);
        }

        // Now actually insert the pending friendship
        System.out.println("What would you like your request to say?: ");
        String req = sc.nextLine();
        req = req.substring(0, Math.min(req.length(), 200));
        if (req.equals(""))
            req = null;
        try {
            CallableStatement func = conn.prepareCall("{ ? = call addFriendRequest(?, ?, ?) }");
            func.setInt(2, userID);
            func.setInt(3, toID);
            func.setString(4, req);
            func.registerOutParameter(1, Types.BOOLEAN);
            func.execute();

            boolean result = func.getBoolean(1);
            func.close();

            if (result) {
                System.out.println("Successfully added friendship!");
            } else {
                System.out.println("Could not add the user. Try again.");
            }
        } catch (SQLException e) {
            printErrors(e);
        }
    }

    // TODO CASE 5
    // * This task should first display a formatted, numbered list of all the
    // outstanding friend requests
    // * with the associated request text. Then the user should be prompted for a
    // number of the request
    // * they would like to confirm, one at a time, or given the option to confirm
    // them all.
    // * The application should move the selected request(s) from the pendingFriend
    // relation to the
    // * friend relation with JDate set to the current date of the Clock table.
    // * The remaining requests which were not selected are declined and removed
    // from the pendingFriend relation.
    // * In the event that the user has no pending friend requests, a message No
    // Pending Friend
    // * Requests should be displayed to the user.
    // add to jonah branch and test
    public void confirmFriendRequests(int userID) {
        try {
            String query = "SELECT * FROM listPendingFriends(?);";
            PreparedStatement listFriendsStatement = conn.prepareStatement(query);
            listFriendsStatement.setInt(1, userID);
            // Execute the query and process the results
            ResultSet rs = listFriendsStatement.executeQuery();
            List<Integer> fromIDs = new ArrayList<>();
            int i = 1;
            while (rs.next()) {
                String requestText = rs.getString("requestText");
                int fromID = rs.getInt("fromID");
                System.out.println(i + ". FromID: " + fromID + ", RequestText: " + requestText);
                fromIDs.add(fromID); // add current fromID to list for use later
                i++;
            }

            // if no pending friend requests exist
            if (fromIDs.isEmpty()) {
                System.out.println("No Pending Friend Requests");
                return;
            }
            // otherwise continue with accepting friend requests
            // prompt user to accept all requests or one at a time
            System.out.print(
                    "Specify whether you would like to accept all requests, or specify one request at a time: \n\n" +
                            "1. Accept all requests\n" +
                            "2. Specify one request at a time\n");
            int choice = sc.nextInt();
            sc.nextLine();
            // validate input
            while (choice != 1 && choice != 2) {
                System.out.println("Invalid choice. Please either enter 1 or 2 as follows:\n\n" +
                        "1. Accept all requests\n" +
                        "2. Specify one request at a time\n");
                choice = sc.nextInt();
                sc.nextLine();
            }

            // accept all requests
            if (choice == 1) {
                // accept all requests
                for (int fromID : fromIDs) {
                    acceptFriendRequest(userID, fromID);
                }
                System.out.println("Accepted all requests");
            } else if (choice == 2) {
                // accept one request at a time
                System.out.println(
                        "Enter the fromID of the request you'd like to accept (or enter -1 to stop accepting and exit menu):");
                int fromID = sc.nextInt();
                sc.nextLine();
                while (fromID != -1) {
                    // validate input
                    while (!fromIDs.contains(fromID)) {
                        System.out.println(
                                "Invalid fromID. Please enter a valid fromID (or enter -1 to stop accepting and exit menu):");
                        fromID = sc.nextInt();
                        sc.nextLine();
                    }
                    // accept request
                    acceptFriendRequest(userID, fromID);
                    System.out.println("Accepted request from " + fromID
                            + ". Enter the fromID of the next request you'd like to accept (or enter -1 to stop accepting and exit menu):");
                    fromID = sc.nextInt();
                    sc.nextLine();
                }

            }
            // remove all the requests that were not accepted (specified per pdf) to the
            // UserID we are currently on
            String removeDeclinedReqs = "DELETE FROM pendingFriend WHERE toID = ?;";
            PreparedStatement removeDeclinedReqsStatement = conn.prepareStatement(removeDeclinedReqs);
            removeDeclinedReqsStatement.setInt(1, userID);
            removeDeclinedReqsStatement.executeUpdate();
            removeDeclinedReqsStatement.close();
            System.out.print("All other requeste deleted, leaving menu...\n");
            return;

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // * HEPER METHOD FOR CONFIRM FRIEND REQUESTS
    public void acceptFriendRequest(int userID1, int userID2) throws SQLException {
        try {
            // GRAB CLOCK TIME
            String clockQuery = "SELECT pseudo_time FROM clock;";
            PreparedStatement clockQueryStatement = conn.prepareStatement(clockQuery);
            ResultSet clockInfo = clockQueryStatement.executeQuery();
            if (!clockInfo.next()) {
                System.out.println("Error: No clock time found");
                return;
            }
            Timestamp clockTime = clockInfo.getTimestamp("pseudo_time");

            // GRAB REQUEST TEXT
            String reqText = "SELECT requestText FROM pendingFriend WHERE fromID = ? AND toID = ?;";
            PreparedStatement reqTextStatement = conn.prepareStatement(reqText);
            reqTextStatement.setInt(1, userID1);
            reqTextStatement.setInt(2, userID2);
            ResultSet reqTextInfo = reqTextStatement.executeQuery();
            String requestText = null;
            if (reqTextInfo.next()) {
                requestText = reqTextInfo.getString("requestText");
            }

            if (requestText == null) {
                // ADD Friend with no request text
                String addPendingFriendWithNoReqText = "INSERT INTO friend (userID1, userID2, JDate) VALUES(?, ?, ?);";
                PreparedStatement addPendingFriendStatement = conn.prepareStatement(addPendingFriendWithNoReqText);
                addPendingFriendStatement.setInt(1, userID1);
                addPendingFriendStatement.setInt(2, userID2);
                addPendingFriendStatement.setTimestamp(3, clockTime);
                addPendingFriendStatement.executeUpdate();
            } else {
                // ADD Friend with custom request text
                String addPendingFriendWithReqText = "INSERT INTO friend (userID1, userID2, JDate, reqText) VALUES(?, ?, ?, ?);";
                PreparedStatement addPendingFriendStatement = conn.prepareStatement(addPendingFriendWithReqText);
                addPendingFriendStatement.setInt(1, userID1);
                addPendingFriendStatement.setInt(2, userID2);
                addPendingFriendStatement.setTimestamp(3, clockTime);
                addPendingFriendStatement.setString(4, requestText);
                addPendingFriendStatement.executeUpdate();
            }

        } catch (SQLException e) {
            // print error message
            System.err.println("Error: " + e.getMessage());
        }
    }

    // TODO CASE 6
    // * Given a name, description, and membership limit (i.e., size), add a new
    // group to the system,
    // * add the current user as its first member with the role manager. gIDs should
    // be auto-generated.
    public void createGroup() {
        System.out.print("Enter the group name (50 characters or less): ");
        String name = sc.nextLine();
        while (name.isEmpty() || name.length() > 50) {
            System.out.print("Enter a valid group name: ");
            name = sc.nextLine();
        }

        System.out.println("Enter the group description (optional) (200 characters or less): ");
        String groupDescription = sc.nextLine();
        while (groupDescription.length() > 200) {
            System.out.print("Enter a valid group description: ");
            groupDescription = sc.nextLine();
        }

        System.out.println("Enter the group membership limit (default 10): ");
        int membershipLimit = 10;
        try {
            membershipLimit = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
        }

        try {

            // Make sure it is atomic so that there can be no race condition in making the
            // gID
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // Generate a group ID
            Statement st = conn.createStatement();
            String query = "SELECT MAX(gID) AS max_gID FROM groupInfo;";
            ResultSet res = st.executeQuery(query);
            conn.commit();

            // Get the gID from the query
            res.next();
            int gID = res.getInt("max_gID");
            if (res.wasNull()) {
                gID = 0;
            } else {
                gID += 1;
            }

            // Create Group
            PreparedStatement createGroup = conn.prepareStatement(
                    "INSERT INTO groupInfo VALUES(?, ? , ? , ?);");
            createGroup.setInt(1, gID);
            createGroup.setString(2, name);
            createGroup.setInt(3, membershipLimit);
            createGroup.setString(4, groupDescription);
            createGroup.executeUpdate();

            // Add the user to the group as a manager
            String createGroupMemberQuery = String.format("INSERT INTO groupMember VALUES(%d, %d, '%s', NULL);", gID,
                    userID, "manager");
            st.executeUpdate(createGroupMemberQuery);

            // Commit both at once - chicken and egg
            conn.commit();
            System.out.println("Group created successfully!");
        } catch (SQLException e) {
            // If a SQLException occurs, try to roll back the transaction and if that fails,
            // end the program
            System.out.println("An error occurred adding a group to the table");
            System.out.println(e);
            try {
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("An error occurred while rolling back the transaction");
                endProgram();
            }
        } finally {
            // No matter what, we need to set Auto Commit back to true
            try {
                conn.setAutoCommit(true);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } catch (SQLException e) {
                // If another error occurs, just tank the program
                System.out.println("Unexpected error occurred while setting auto commit back to false");
                endProgram();
            }
        }
    }

    // TODO CASE 7
    // * Given a group ID and the request's text, create a pending request of adding
    // the logged-in user
    // * to the group by inserting a new entry into the pendingGroupMember relation.
    public void initiateAddingGroup() {
        System.out.print("Enter the group ID you would like to search for: ");
        int gID = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter your request text: ");
        String req = sc.nextLine();
        req = req.substring(0, Math.min(req.length(), 200));
        if (req.equals(""))
            req = null;

        try {
            CallableStatement callableStatement = conn.prepareCall("{ call addPendingMember(?, ?, ?) }");
            callableStatement.setInt(1, gID);
            callableStatement.setInt(2, userID);
            callableStatement.setString(3, req);
            callableStatement.execute();
            System.out.println("Successfully requested to join.");
        } catch (SQLException e) {
            String message = e.getMessage();
            while ((e = e.getNextException()) != null) {
                message.concat(e.getMessage());
            }

            if (message.contains("violates foreign key constraint")) {
                System.out.println("The group you tried to join does not exist");
            } else if (message.contains("already exists")) {
                System.out.println("You already tried to join this group");
            } else {
                printErrors(e);
            }
        }
    }

    // TODO CASE 8
    // * This task should first display a formatted, numbered list of all the
    // pending group membership
    // * requests with the associated request text for any groups where the user is
    // a group manager.
    // * Then, the user should be prompted for a number of the request they would
    // like to confirm, one
    // * at a time, or given the option to confirm them all.
    // * The application should move the selected request(s) from the
    // pendingGroupMember relation
    // * to the groupMember relation using the current time in Clock for the
    // lastConfirmed timestamp.
    // * If accepting a pending group membership request would exceed the group's
    // size, the accepted
    // * request should remain in pendingGroupMember. The remaining requests which
    // were not selected
    // * are declined and removed from the pendingGroupMember relation.
    // * In the event that there are no pending group membership requests for any
    // groups that the user
    // * is a manager of, a message No Pending Group Membership Requests should be
    // displayed to
    // * the user. Furthermore, a message No groups are currently managed should
    // be displayed if
    // * the user is not a manager of any groups.
    public void confirmGroupMembership() {
        // Get the groups where the user is a manager
        List<Integer> groupIDs = new LinkedList<Integer>();

        try {
            Statement st = conn.createStatement();
            String getGroupsQuery = String.format("SELECT gID from groupMember WHERE userID = %d AND role = 'manager';",
                    userID);
            ResultSet rs = st.executeQuery(getGroupsQuery);
            while (rs.next()) {
                groupIDs.add(rs.getInt("gID"));
            }
            st.close();
        } catch (SQLException e) {
            printErrors(e);
        }

        // If the user is not a manager of any groups display "No groups are currently
        // managed"
        if (groupIDs.size() == 0) {
            System.out.println("No groups are currently managed");
            return;
        }

        // Get a list of group membership requests
        List<PendingGroupMemberObj> groupMembershipRequests = new LinkedList<>();
        HashSet<Integer[]> allUsers = new HashSet<>(); // Keep track of all userIDs

        int counter = 0;
        for (int i = 0; i < groupIDs.size(); i++) {
            try {
                Statement st = conn.createStatement();
                String getGroupsQuery = String.format(
                        "SELECT userID, requestText, requestTime from pendingGroupMember WHERE gID = %d;",
                        groupIDs.get(i));
                ResultSet rs = st.executeQuery(getGroupsQuery);
                while (rs.next()) {
                    int userID = rs.getInt("userID");
                    Integer[] toAdd = { groupIDs.get(i), userID };
                    allUsers.add(toAdd);
                    String requestText = rs.getString("requestText");
                    String requestTime = String.valueOf(rs.getTimestamp("requestTime"));
                    groupMembershipRequests.add(
                            new PendingGroupMemberObj(groupIDs.get(i), userID, requestText, requestTime, counter++));
                }
                st.close();
            } catch (SQLException e) {
                printErrors(e);
            }
        }

        // If there are no pending group membership requests for any groups they are a
        // manager of, display "No Pending Group Membership Requests"
        if (groupMembershipRequests.size() == 0) {
            System.out.println("No Pending Group Membership Requests");
        }

        // For each group, display a formatted numbered list of all the pending group
        // membership requests with the associate request text
        int currentGroup = -1;
        for (PendingGroupMemberObj pendingGroupMember : groupMembershipRequests) {
            if (pendingGroupMember.gID != currentGroup) {
                currentGroup = pendingGroupMember.gID;
                System.out.println("-----------------------------------------");
                System.out.println("Displaying Requests for Group: " + pendingGroupMember.gID);
            }
            System.out.println(pendingGroupMember.index + ":");
            System.out.println("User: " + pendingGroupMember.userID);
            System.out.println("Request Message: " + pendingGroupMember.requestText);
            System.out.println("Time Requested: " + pendingGroupMember.requestTime);
            System.out.println();
        }

        // Prompt the user for the number of request they would like to confirm (one at
        // a time), or let them confirm all
        HashSet<Integer[]> chosenUsers = new HashSet<>();
        int numUsersChosen = 0;
        while (numUsersChosen < allUsers.size()) {
            System.out.print(
                    "Enter one request you would like to confirm, -1 to confirm them all, and -2 when you are done: ");
            int indexChosen = sc.nextInt();
            sc.nextLine();
            if (indexChosen == -1) {
                chosenUsers = allUsers;
                break;
            } else if (indexChosen == -2) {
                break;
            } else {
                numUsersChosen++;
                Integer[] toAdd = { groupMembershipRequests.get(indexChosen).gID,
                        groupMembershipRequests.get(indexChosen).userID };
                chosenUsers.add(toAdd);
            }
        }

        List<List<Integer>> chosenUsersList = new LinkedList<>();
        chosenUsers.stream().forEach(i -> chosenUsersList.add(Arrays.asList(i[0], i[1])));

        List<List<Integer>> allUsersList = new LinkedList<>();
        allUsers.stream().forEach(i -> allUsersList.add(Arrays.asList(i[0], i[1])));

        List<List<Integer>> notChosenUsersList = new LinkedList<>();

        for (int i = 0; i < allUsersList.size(); i++) {
            boolean found = false;
            int k = allUsersList.get(i).get(0);
            int l = allUsersList.get(i).get(1);
            for (int j = 0; j < chosenUsersList.size(); j++) {
                if ((k == chosenUsersList.get(j).get(0)) && (l == chosenUsersList.get(j).get(1))) {
                    found = true;
                }
            }
            if (!found) {
                notChosenUsersList.add(Arrays.asList(k, l));
            }
        }

        // Move the selected request(s) from pendingGroupMember to groupMember using
        // current time in clock for lastConfirmed timestamp
        // There is a trigger to do this automatically
        for (int i = 0; i < notChosenUsersList.size(); i++) {
            try {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

                Statement st = conn.createStatement();
                String dropUserQuery = String.format("DELETE FROM groupMember WHERE gID = %d AND userID = %d;",
                        notChosenUsersList.get(i).get(0), notChosenUsersList.get(i).get(1));
                int rs = st.executeUpdate(dropUserQuery);

                conn.commit();

                if (rs == 1) {
                    System.out.printf("Successfully removed %d from pendingGroupMember\n",
                            notChosenUsersList.get(i).get(1));
                } else {
                    System.out.println("Did not remove from pendingGroupMember");
                }
            } catch (SQLException e) {
                // If a SQLException occurs, try to roll back the transaction and if that fails,
                // end the program
                System.out.println("An error occurred removing a member from pendingGroup");
                System.out.println(e);
                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    System.out.println("An error occurred while rolling back the transaction");
                    endProgram();
                }
            } finally {
                // No matter what, we need to set Auto Commit back to true
                try {
                    conn.setAutoCommit(true);
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                } catch (SQLException e) {
                    // If another error occurs, just tank the program
                    System.out.println("Unexpected error occurred while setting auto commit back to false");
                    endProgram();
                }
            }
        }

        // If a request is accepted, but would cause the groups size to exceed the max,
        // then leave the request in pendingGroupMember

        // Requests not selected are then removed from pendingGroupMember
        for (int i = 0; i < chosenUsersList.size(); i++) {
            try {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

                Statement st = conn.createStatement();
                String addUserQuery = String.format("INSERT INTO groupMember VALUES(%d, %d, 'member', NULL)",
                        chosenUsersList.get(i).get(0), chosenUsersList.get(i).get(1));
                int rs = st.executeUpdate(addUserQuery);

                conn.commit();

                if (rs == 1) {
                    System.out.printf("Successfully added %d as a group member\n", chosenUsersList.get(i).get(1));
                } else {
                    System.out.println("Did not add group member");
                }
                st.close();

                st = conn.createStatement();
                String dropUserQuery = String.format("DELETE FROM pendingGroupMember WHERE gID = %d AND userID = %d;",
                        chosenUsersList.get(i).get(0), chosenUsersList.get(i).get(1));
                rs = st.executeUpdate(dropUserQuery);
                if (rs == 1) {
                    System.out.printf("Successfully removed %d from pendingGroupMember\n",
                            chosenUsersList.get(i).get(1));
                } else {
                    System.out.println("Did not remove from pendingGroupMember");
                }
                st.close();

            } catch (SQLException e) {
                String message = e.getMessage();
                while ((e = e.getNextException()) != null) {
                    message.concat(e.getMessage());
                }

                if (message.contains("Cannot exceed max group size")) {
                    System.out.println("Cannot accept any more people into group " + chosenUsersList.get(i).get(0));
                } else {
                    printErrors(e);
                }

                try {
                    conn.rollback();
                } catch (SQLException e2) {
                    System.out.println("An error occurred while rolling back the transaction");
                    endProgram();
                }
            } finally {
                // No matter what, we need to set Auto Commit back to true
                try {
                    conn.setAutoCommit(true);
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                } catch (SQLException e) {
                    // If another error occurs, just tank the program
                    System.out.println("Unexpected error occurred while setting auto commit back to false");
                    endProgram();
                }
            }
        }
    }

    // TODO CASE 9
    // * This task should first prompt the user for the gID of the group they would
    // like to leave.
    // * The application should remove the user from the group in the groupMember
    // relation. Upon
    // * removing the user from the group, you should use a trigger to check if
    // there are pending
    // * group membership requests in pendingGroupMember that were previously
    // accepted, but could
    // * not be added due exceeding the group's size, and move the earliest such
    // request from the
    // * pendingGroupMember relation to the groupMember relation without changing
    // the lastConfirmed timestamp.
    // * In the event that the user is not a member of the specified group, a
    // message Not a Member
    // * of any Groups should be displayed to the user.
    public void leaveGroup() {
        // Prompt the user to enter the group they would like to leave
        int groupToLeave = -1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM groupMember WHERE userID=" + userID + ";");
            int groups = 0;
            while (rs.next()) {
                System.out.println(rs.getInt("gID"));
                groups++;
            }

            if (!(groups > 0)) {
                System.out.println("Not a member of any Groups.");
                return;
            }

            System.out.print("Enter the group ID which you would like to leave: ");
            groupToLeave = sc.nextInt();
            sc.nextLine(); // Clear the buffer
        } catch (SQLException e) {
            printErrors(e);
        }

        // Remove the member from the group
        try {
            // Set auto commit to false
            conn.setAutoCommit(false);
            PreparedStatement pStatement = conn.prepareStatement("call leaveGroup(?, ?)"); // Add all constraints
                                                                                           // deferred if needed
            pStatement.setInt(1, userID);
            pStatement.setInt(2, groupToLeave);
            pStatement.execute();
            conn.commit();
        } catch (SQLException e) {
            if (e.getSQLState().equals("00001")) {
                System.out.println("ERROR: " + e.getMessage());
            } else {
                printErrors(e);
            }

            try {
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("An error occurred while rolling back the transaction");
                endProgram();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // If another error occurs, just tank the program
                System.out.println("Unexpected error occurred while setting auto commit back to false");
                endProgram();
            }
        }
    }

    // TODO CASE 10
    // * Given a string on which to match any user profile in the system, any item
    // in this string must be
    // * matched against the name and email fields of a user's profile. That is
    // if the user searches
    // * for xyz abc, the results should be the set of all user profiles that have
    // xyz in their name
    // * or email union the set of all user profiles that have abc in their
    // name or email.
    public void searchForProfile() {
        System.out.print("Enter a search string: ");
        String input = sc.nextLine();

        String[] allSearches = input.split(" ");

        HashSet<Integer> resultingProfiles = new HashSet<>();

        Arrays.stream(allSearches).forEach(search -> {
            try {
                PreparedStatement checkUsername = conn.prepareStatement(
                        "SELECT userID FROM profile WHERE name LIKE ? OR email LIKE ?;");
                checkUsername.setString(1, "%" + search + "%");
                checkUsername.setString(2, "%" + search + "%");

                ResultSet res = checkUsername.executeQuery();
                while (res.next()) {
                    int userID = res.getInt("userID");
                    resultingProfiles.add(userID);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                printErrors(e);
            }
        });

        int[] userIDs = resultingProfiles.stream().mapToInt(Integer::intValue).toArray();

        Arrays.stream(userIDs).forEach(i -> System.out.println(i));
    }

    // TODO CASE 11
    // * With this the user can send a message to one friend given the friend's
    // userID. The application
    // * should display the name of the recipient and the user should be prompted to
    // enter the body
    // * of the message, which could be multi-lined. Once entered, the application
    // should send the
    // * message to the receiving user by adding an appropriate entry into the
    // message relation (msgIDs
    // * should be auto-generated and timeSent should be set to the current time of
    // the Clock table)
    // * and use a trigger to add a corresponding entry into the messageRecipient
    // relation. The user
    // * should lastly be shown success or failure feedback.
    public void sendMessageToUser() {
        // Prompt for friend to send message to
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM friend WHERE (userID1=" + userID + ") OR (userID2=" + userID + ");");
            if (!rs.next()) {
                System.out.println("You have no friends.");
                return;
            }

            do {
                int friendID = rs.getInt("userID1");
                if (friendID == userID) {
                    friendID = rs.getInt("userID2");
                }
                System.out.printf("User ID: %d\n", friendID);
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("Error getting the list of friends");
            return;
        }

        // Get friend to send message to
        System.out.print("Enter the userID of the friend you would like to message: ");
        int fID = sc.nextInt();
        sc.nextLine(); // Clear the buffer

        // Get name of the friend
        String friendName = "";
        try {
            PreparedStatement s = conn.prepareStatement("SELECT name FROM profile WHERE userID=?;");
            s.setInt(1, fID);
            ResultSet rs = s.executeQuery();
            if (!rs.next()) {
                System.out.println("This friend does not exist");
                return;
            }
            friendName = rs.getString("name");
        } catch (SQLException e) {
            System.out.println("Error finding friend information");
            printErrors(e);
            return;
        }

        // Get the message to send
        System.out.println("Enter the message you want to send (Max 200 Words) to " + friendName
                + ", ending with 'END' on a new line:");
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("END")) {
                break;
            }
            sb.append(line).append("\n");
        }

        String message = sb.toString().substring(0, Math.min(sb.length(), 200));

        // Send the message
        try {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            CallableStatement c = conn.prepareCall("{ ? = call sendMessageToUser(?, ?, ?)}");
            c.setInt(2, userID);
            c.setInt(3, fID);
            c.setString(4, message);
            c.registerOutParameter(1, Types.BOOLEAN);
            boolean res = c.execute();
            if (res) {
                System.out.println("Message successfully sent!");
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                endProgram();
            }
            System.out.println("Error sending message");
            printErrors(e);
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            } catch (SQLException e) {
                System.out.println("Error setting auto commit back to true");
                printErrors(e);
            }
        }
    }

    // TODO CASE 12
    // * With this the user can send a message to a recipient group given the group
    // ID, if the user is
    // * within the group. Every member of this group should receive the message.
    // The user should be
    // * prompted to enter the body of the message, which could be multi-lined. Then
    // the application
    // * should send the message to the group by adding an appropriate entry into
    // the message
    // * relation (msgIDs should be auto-generated and timeSent should be set to the
    // current time of
    // * the Clock table) and use a trigger to add corresponding entries into the
    // messageRecipient
    // * relation. The user should lastly be shown success or failure feedback.
    // * Note that if the user sends a message to one friend, you only need to put
    // the friend's userID
    // * to ToUserID in the table of message. If the user wants to send a message to
    // a group, you need
    // * to put the group ID to ToGroupID in the table of message and use a trigger
    // to populate
    // * the messageRecipient table with proper user ID information as defined by
    // the groupMember
    // * relation.
    public void sendMessageToGroup() {
        // Get the group they want to send
        System.out.print("Enter the group ID of the group you want to message: ");
        int groupID = sc.nextInt();
        sc.nextLine();

        // Check if the user is in the group
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * from groupMember WHERE userID = ? AND gID = ?;");
            st.setInt(1, userID);
            st.setInt(2, groupID);

            ResultSet rs = st.executeQuery();
            if (rs.next() == false) {
                System.out.println("You cannot send a message to that group because you are not in it");
                return;
            } else {
                System.out.println("Enter the message you want to send (Max 200 Words), ending with 'END': ");
            }

            st.close();
        } catch (SQLException e) {
            printErrors(e);
        }

        // Get the message they want to send
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("END")) {
                break;
            }
            sb.append(line).append("\n");
        }

        String message = sb.toString().substring(0, Math.min(sb.length(), 200));

        // Call pgsql function that will send the msg to everyone in a given group
        // Java try-with-resources automatically closes the connection and callable
        // statement
        try {
            CallableStatement func = conn.prepareCall("{ ? = CALL sendMessageToGroup(?, ?, ?) }");
            func.setInt(2, userID);
            func.setInt(3, groupID);
            func.setString(4, message);
            func.registerOutParameter(1, Types.BIT);
            func.execute();
            boolean result = func.getBoolean(1);
            if (result) {
                System.out.println("Successfully sent the message to the group!");
            } else {
                System.out.println("Sending message to the group failed");
            }
        } catch (SQLException e) {
            printErrors(e);
        }
    }

    // TODO CASE 13
    // * When the user selects this option, the entire contents of every message
    // sent to the user (including group messages)
    // *should be displayed in a nicely formatted way.
    public void displayMessages() {
        // TODO: Clarify if we should also display who sent it
        try {
            // Execute query
            PreparedStatement s = conn.prepareStatement("SELECT * FROM getMessages(?, ?);");
            s.setInt(1, userID);
            s.setBoolean(2, false);
            ResultSet rs = s.executeQuery();

            // Now format the results
            if (!rs.next()) {
                System.out.println("You have no messages");
                return;
            }

            System.out.println();

            int i = 1;
            do {
                // Print formatted message
                System.out.printf("%d.\n%s\n", i++, rs.getString("messageBody"));
                // TODO: Have it display: msgID, who sent, body, and the time sent
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("Error retrieving messages.");
            printErrors(e);
        }
    }

    // TODO CASE 14
    // * This should display messages in the same fashion as the previous task
    // except that only those
    // * messages sent since the last time the user logged into the system should be
    // displayed (including
    // * group messages).
    public void displayNewMessages() {
        try {
            // Execute query
            PreparedStatement s = conn.prepareStatement("SELECT * FROM getMessages(?, ?);");
            s.setInt(1, userID);
            s.setBoolean(2, true);
            ResultSet rs = s.executeQuery();

            // Now format the results
            if (!rs.next()) {
                System.out.println("You have no new messages");
                return;
            }

            System.out.println();

            int i = 1;
            do {
                // Print formatted message
                System.out.printf("%d.\n%s\n", i++, rs.getString("messageBody"));
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("Error retrieving messages.");
            printErrors(e);
        }
    }

    // TODO CASE 15
    // * This task supports the browsing of the logged-in user's friends' profiles.
    // It first displays each
    // * of the user's friends' names and userIDs. Then it allows the user to either
    // retrieve a friend's
    // * entire profile by entering the appropriate userID or exit browsing and
    // return to the main menu
    // * by entering 0 as a userID. When selected, a friend's profile should be
    // displayed in a nicely
    // * formatted way, after which the user should be prompted to either select to
    // retrieve another
    // * friend's profile or return to the main menu.
    public void displayFriends() {
        // List all friends of the user
        try {
            PreparedStatement s = conn.prepareStatement("SELECT * FROM getFriends(?);");
            s.setInt(1, userID);
            ResultSet rs = s.executeQuery();
            if (!rs.next()) {
                System.out.println("You have no friends.");
                return;
            }

            do {
                // Print out each profile
                System.out.println(
                        String.format("User ID: %d\tName: %s",
                                rs.getInt("friendID"),
                                rs.getString("name")));
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("Error accessing user's friends");
            printErrors(e);
        }

        System.out.println("Enter a friend's user ID to get their information, or 0 to exit.");
        int profileID = 0;
        profileID = sc.nextInt();
        sc.nextLine(); // Clear the buffer

        while (profileID > 0) {
            // Retrieve friend information and print it
            try {
                PreparedStatement s = conn.prepareStatement("SELECT * FROM getFriendInfo(?, ?);");
                s.setInt(1, userID);
                s.setInt(2, profileID);
                ResultSet rs = s.executeQuery();

                // Exception would have been thrown if there was no friend
                rs.next();
                String output = "User ID: %d\nName: %s\nEmail: %s\nLast login: %s\n";
                System.out.printf(output,
                        rs.getInt("userID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getTimestamp("lastlogin"));
            } catch (SQLException e) {
                if (e.getSQLState().equals("00001")) {
                    System.out.println("This user is not a friend of the logged in account or does not exist.");
                } else {
                    printErrors(e);
                }
            }

            // Get give user option to enter input again
            System.out.println("Enter a friend's user ID to get their information, or 0 to exit.");
            profileID = sc.nextInt();
            sc.nextLine(); // Clear the buffer
        }
    }

    // TODO CASE 16
    // * This task should produce a ranked list of groups based on their number of
    // members.
    // * In the event that there are no groups in the system, a message No Groups to
    // Rank should
    // * be displayed to the user.
    public static void rankGroups() {
        String rankGroupsStatement = "SELECT g.gID, g.name, COALESCE(COUNT(gm.userID), 0) AS member_count " +
                "FROM groupInfo g " +
                "LEFT JOIN groupMember gm ON g.gID = gm.gID " +
                "GROUP BY g.gID, g.name " +
                "ORDER BY member_count DESC, g.gID";
        try {
            PreparedStatement rankGroups = conn.prepareStatement(rankGroupsStatement);
            ResultSet rs = rankGroups.executeQuery();
            System.out.println(String.format("%-10s %-30s %-20s", "Group ID", "Group Name", "Number of Members"));
    
            boolean hasResults = rs.next();
    
            if (!hasResults) {
                System.out.println("No Groups to Rank");
            }
    
            while (hasResults) {
                System.out.println(String.format("%-10s %-30s %-20s",
                        rs.getString("gID"),
                        rs.getString("name"),
                        rs.getString("member_count")));
    
                hasResults = rs.next();
            }
    
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    

    // TODO CASE 17
    // * This task should produce a ranked list of user profiles based on the number
    // of friends they
    // * have along with their number of friends.
    // * Note the number of friends of a profile includes those who are members of
    // the groups the user
    // * profile belongs to.
    public void rankProfiles() {
        try {
            PreparedStatement s = conn.prepareStatement("SELECT * FROM rankProfiles();");
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                long rank = rs.getLong("rank");
                int id = rs.getInt("uID");
                long numFriends = rs.getLong("numFriends");
                System.out.printf("%d.\tUser ID: %d\tNetwork Size: %d\n", rank, id, numFriends);
            }
        } catch (SQLException e) {
            printErrors(e);
        }
    }

    // TODO CASE 18
    // * Display the top k users with respect to the number of messages sent to the
    // logged-in user plus
    // * the number of messages received from the logged-in user in the past x
    // months. x and k are
    // * input parameters to this function. 1 month is defined as 30 days counting
    // back starting from
    // * the current date of the Clock table. Group messages do not need to be
    // considered in this
    // * function.
    public void topMessages() {
        System.out.print("Enter the number of months you want the search to go back: ");
        int x = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter the top k message senders you would like to see: ");
        int k = sc.nextInt();
        sc.nextLine();

        try {
            PreparedStatement p = conn.prepareStatement("SELECT * FROM topMessages(?, ?, ?);");
            p.setInt(1, userID);
            p.setInt(2, k);
            p.setInt(3, x);
            ResultSet rs = p.executeQuery();
            int remaining = k;
            while (remaining > 0 && rs.next()) {
                System.out.println(String.format("User ID: %d\tYou had %d messages sent with each other.",
                        rs.getInt("recipient"),
                        rs.getLong("mCount")));
                remaining--;
                // TODO: Clarify what how ties should be handled
                // ! CAN BE BOTH
            }

            if (remaining == k) {
                System.out.println("No users have sent messages to you.");
            }
        } catch (SQLException e) {
            System.out.println("Error accessing message data");
            printErrors(e);
        }
    }

    // TODO CASE 19
    // Given a userID, find a path, if one exists, between the logged-in user and
    // that user profile with
    // at most 3 hops between them. A hop is defined as a friendship between any two
    // users.
    // *IMPORTANT NOTE* This query should be written using plpgsql and should only
    // use java for interfacing. *IMPORTANT NOTE*
    public void threeDegrees() {
        System.out.println("Enter the user ID of the user you want to find a relationship with:");
        int toID = sc.nextInt();
        sc.nextLine();

        // Now call the function
        try {
            PreparedStatement s = conn.prepareStatement("SELECT * FROM threeDegrees(?, ?);");
            s.setInt(1, userID);
            s.setInt(2, toID);
            ResultSet rs = s.executeQuery();
            rs.next();
            int firstHop = rs.getInt("secondID");
            int secondHop = rs.getInt("thirdID");

            // TODO: Possibly delete
            if (rs.getInt("fromID") == -1) {
                System.out.println("There is no three degree relation with this user.");
                return;
            }

            if (firstHop == -1) {
                System.out.printf("%d --> %d\n", userID, toID);
            } else if (secondHop == -1) {
                System.out.printf("%d --> %d --> %d\n", userID, firstHop, toID);
            } else {
                System.out.printf("%d --> %d --> %d --> %d\n", userID, firstHop, secondHop, toID);
            }
        } catch (SQLException e) {
            // TODO: Come back to after meeting with Brian about this
            if (e.getSQLState().equals("00001")) {
                System.out.println("There is no three degree relation with this user.");
            } else {
                printErrors(e);
            }
        }
    }

    // TODO CASE 20
    // * The function should return the user to the top level of the UI after
    // marking the time of the
    // * user's logout in the user's lastlogin field of the user relation from the
    // Clock table
    public void logout() {
        System.out.println("Logging out...");
        try {
            // Get the time from the clock
            Statement st = conn.createStatement();
            String query = "SELECT pseudo_time FROM clock;";
            ResultSet res = st.executeQuery(query);
            res.next();
            String clockTime = res.getString("pseudo_time");
            st.close();

            st = conn.createStatement();
            String logoutUpdateQuery = "UPDATE profile SET lastlogin = '" + clockTime + "' WHERE userID = " + userID
                    + ";";
            int rs = st.executeUpdate(logoutUpdateQuery);

            if (rs == 1) {
                System.out.println("Successfully logged out");
                isLoggedIn = false; // update state varible loggedIn
                userID = -1;
            } else {
                System.out.println("Error logging out, please try again");
            }
        } catch (SQLException e) {
            System.out.println("Error caught in logout function: " + e.getMessage());
        }
    }

    // TODO CASE 21
    // * This option should cleanly shut down and exit the program.
    public void exit() {
        // write code for exit here
        System.out.println("Exiting BeSocial... Goodbye!");
        endProgram();
    }

    private void printErrors(SQLException e) {
        System.out.println(e.getMessage());
        while ((e = e.getNextException()) != null) {
            System.out.println(e.getMessage());
        }
    }

    private class PendingGroupMemberObj {
        private int gID;
        private int userID;
        private String requestText;
        private String requestTime;
        private int index;

        public PendingGroupMemberObj(int gID, int userID, String requestText, String requestTime, int index) {
            this.gID = gID;
            this.userID = userID;
            this.requestText = requestText;
            this.requestTime = requestTime;
            this.index = index;
        }
    }
}