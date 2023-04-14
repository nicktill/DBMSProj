import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// **NOTE** PLEASE USE THE EXTENSION 'BetterNotes' to make this file more readable! **NOTE** 

/**
 * Questions
 *  - CASE 19 - Should this also be a admin only task since it requires userID? 
 *  - CASE 4 - Same as the above. It says based on userID but shouldn't it use email?
 */

import java.sql.*;
import java.util.Properties;

public class BeSocial {
    private static Scanner sc;
    private static Connection conn;
    public static int userID = -1;
    private static boolean isLoggedIn;
    private static final int ADMIN_USER_ID = 0;
    private static String userName = null;

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
            System.out.println(String.format("Error Of Type: '%s' Occurred When Connecting to Database.\nPlease re-run program.", e.getClass()));
            sc.close();
            System.exit(0);
        }

        // If the connection is properly established, clear command line and start

        System.out.print("\033\143"); // Clears the command line console, shoutout stack overflow

        System.out.println("Welcome to BeSocial!\n");

        // Run the main program loop until the user exits
        try {
            int userInput = -1;
            isLoggedIn = false;
            while (true) {
                displayMenu(isLoggedIn);

                System.out.println("Choose an option from the menu: ");
                userInput = Integer.parseInt(sc.nextLine());

                // Validate user input based on logged in status
                // If the option they selected is invalid, print statement and continue to next
                // loop
                if (isLoggedIn) {
                    // If they are logged in and choose 3 or if they are not an admin and choose 1, it is invalid
                    if (userInput == 3) {
                        System.out.println("This option is invalid for logged in users");
                        continue;
                    }
                    if ((!(userID == ADMIN_USER_ID) && userInput == 1) || (!(userID == ADMIN_USER_ID) && userInput == 2)) {
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
                        createProfile();
                        break;
                    case 2:
                        dropProfile();
                        break;
                    case 3:
                        login();
                        break;
                    case 4:
                        initiateFriendship();
                        break;
                    case 5:
                        confirmFriendRequests(userID);
                        break;
                    case 6:
                        createGroup();
                        break;
                    case 7:
                        initiateAddingGroup();
                        break;
                    case 8:
                        confirmGroupMembership();
                        break;
                    case 9:
                        leaveGroup();
                        break;
                    case 10:
                        searchForProfile();
                        break;
                    case 11:
                        sendMessageToUser();
                        break;
                    case 12:
                        sendMessageToGroup();
                        break;
                    case 13:
                        displayMessages();
                        break;
                    case 14:
                        displayNewMessages();
                        break;
                    case 15:
                        displayFriends();
                        break;
                    case 16:
                        rankGroups();
                        break;
                    case 17:
                        rankProfiles();
                        break;
                    case 18:
                        topMessages();
                        break;
                    case 19:
                        threeDegrees();
                        break;
                    case 20:
                        logout();
                        break;
                    case 21:
                        exit();
                        break;
                }
            }
        } catch (Exception e) {
            endProgram();
        }

        System.out.println("Thank you for using BeSocial");
        endProgram();
    }

    /**
     * Helper Method that closes the DB Connection, closes the scanner, and ends the program
     */
    private static void endProgram() {
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
    public static void createProfile() {
        if (userID != ADMIN_USER_ID) {
            System.out.println("This operation can only be performed by an admin");
            return;
        }
        // Get information from the user
        String name, email, password, dob;
        System.out.print("Enter name: ");
        name = sc.nextLine();
        while (name.isEmpty()) {
            System.out.print("You must enter a name: ");
            name = sc.nextLine();
        }

        String emailRegex = "^(.+)@(\\S+)$";
        Pattern pat= Pattern.compile(emailRegex);

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

        try {
            /*
             * The Profile Schema is Profile (userID, name, email, password, date_of_birth, lastlogin)
             * We have a database-side trigger to give a user an ID so we can leave it as -1.
             * We also have a database-side trigger to inser the max time from the clock into lastlogin.
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
            // If a SQLException occurs, try to roll back the transaction and if that fails, end the program
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

    // TODO: CASE 3
    // * Given email and password, login as the user in the system when an
    // appropriate match is found.
    public static void login() {
        String username, password;
        System.out.print("Enter BeSocial username: ");
        username = sc.nextLine();
        System.out.print("Enter password: ");
        password = sc.nextLine();
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
                userName = rs.getString("name");
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
    public static void displayMenu(boolean isLoggedIn) {
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
    public static void dropProfile() {
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
    public static void initiateFriendship() {
        System.out.print("Enter the userID of the friend you want to request: ");
        int toID = sc.nextInt();

        // Get the user entry and confirm the operation with the user
        try {
            PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM user WHERE userID = ?;"
            );
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
        if (req.equals("")) req = null;
        try {
            CallableStatement func = conn.prepareCall("{ ? = call addFriendRequest(?, ?, ?) }");
            func.setInt(1, userID);
            func.setInt(2, toID);
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
    // * outstanding friend requests
    // * with the associated request text. Then the user should be prompted for a
    // * number of the request
    // * they would like to confirm, one at a time, or given the option to confirm
    // * them all.
    // * The application should move the selected request(s) from the pendingFriend
    // * relation to the
    // * friend relation with JDate set to the current date of the Clock table.
    // * The remaining requests which were not selected are declined and removed
    // from the pendingFriend relation.
    // * In the event that the user has no pending friend requests, a message No
    // Pending Friend
    // * Requests should be displayed to the user.
    // * from the pendingFriend relation.
    // * In the event that the user has no pending friend requests, a message “No
    // * Pending Friend3
    // * Requests” should be displayed to the user.
    // !    WORK IN PROGRESS this shit is harder than i thought
    public static void confirmFriendRequests(int userID) {
        try {
            String query = "SELECT * FROM listPendingFriends(?);";
            PreparedStatement listFriendsStatement = conn.prepareStatement(query);
            listFriendsStatement.setInt(1, userID);
            // Execute the query and process the results
            ResultSet rs = listFriendsStatement.executeQuery();
            List<Integer> fromIDs = new ArrayList<>();
            int i = 1;
            while (rs.next()) {
                String requestText = rs.getString("requesttext");
                int fromID = rs.getInt("fromid");
                System.out.println(i + ". FromID: " + fromID + ", RequestText: " + requestText);
                fromIDs.add(fromID); //add current fromID to list for use later
                i++;
            }
    
            //if no pending friend requests exist
            if (fromIDs.isEmpty()) {
                System.out.println("No Pending Friend Requests");
                return;
            }

            //otherwise continue with accepting friend requests
            //prompt user to accept all requests or one at a time
            System.out.print("Specify whether you would like to accept all requests, or specify one request at a time: \n\n" +
                    "1. Accept all requests\n" +
                    "2. Specify one request at a time\n");
            int choice = sc.nextInt();
            //validate input
            while (choice != 1 && choice != 2) {
                System.out.println("Invalid choice. Please either enter 1 or 2 as follows:\n\n" +
                        "1. Accept all requests\n" +
                        "2. Specify one request at a time\n");
                choice = sc.nextInt();
            }
    
            //accept all requests
            if (choice == 1) {
                // accept all requests
                for (int fromID : fromIDs) {
                    acceptFriendRequest(userID, fromID);
                }
            } else if (choice == 2) {
                // accept one request at a time
                System.out.println("Enter the fromID of the request you'd like to accept (or enter -1 to stop accepting and exit menu):");
                int fromID = sc.nextInt();
                while (fromID != -1) {
                    //validate input
                    while (!fromIDs.contains(fromID)) {
                        System.out.println("Invalid fromID. Please enter a valid fromID (or enter -1 to stop accepting and exit menu):");
                        fromID = sc.nextInt();
                    }
                    //accept request
                    acceptFriendRequest(userID, fromID);
                    System.out.println("Accepted request from " + fromID + ". Enter the fromID of the next request you'd like to accept (or enter -1 to stop accepting and exit menu):");
                    fromID = sc.nextInt();
                }
                // remove all the requests that were not accepted (specified per pdf)
                String removeDeclinedReqs = "DELETE FROM pendingFriend WHERE toID = ?;";
                PreparedStatement removeDeclinedReqsStatement = conn.prepareStatement(removeDeclinedReqs);
                removeDeclinedReqsStatement.setInt(1, userID);
                removeDeclinedReqsStatement.executeUpdate();
                removeDeclinedReqsStatement.close();
                System.out.print("Exiting menu...\n");
                
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void acceptFriendRequest(int userID, int fromID) throws SQLException {
        String addPendingFriendToFriend = "INSERT INTO friend (fromID, toID) VALUES(?, ?);";
        PreparedStatement addPendingFriendStatement = conn.prepareStatement(addPendingFriendToFriend);
        addPendingFriendStatement.setInt(1, userID);
        addPendingFriendStatement.setInt(2, fromID);
        addPendingFriendStatement.executeUpdate();
        addPendingFriendStatement.close();
    }
    
    
    // !   WORK IN PROGRESS

    // TODO CASE 6
    // * Given a name, description, and membership limit (i.e., size), add a new
    // group to the system,
    // * add the current user as its first member with the role manager. gIDs should
    // be auto-generated.
    public static void createGroup() {
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
        } catch (Exception e) {}
        
        try {
            
            // Make sure it is atomic so that there can be no race condition in making the gID
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

            String createGroupMemberQuery = String.format("INSERT INTO groupMember VALUES(%d, %d, '%s', NULL);", gID, userID, "manager");
            st.executeUpdate(createGroupMemberQuery);

            // Commit both at once - chicken and egg
            conn.commit();
            System.out.println("Group created successfully!");
        } catch (SQLException e) {
            // If a SQLException occurs, try to roll back the transaction and if that fails, end the program
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
    public static void initiateAddingGroup() {
        System.out.print("Enter the group ID you would like to search for: ");
        int gID = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter your request text: ");
        String req = sc.nextLine();
        req = req.substring(0, Math.min(req.length(), 200));
        if (req.equals("")) req = null;

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
            } else if (message.contains ("already exists")) {
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
    public static void confirmGroupMembership() {
        // * write code for confirmGroupMembership here
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
    public static void leaveGroup() {
        // *wrote code for leaveGroup here

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
    public static void searchForProfile() {
        // * write code for searchForProfile here
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
    public static void sendMessageToUser() {
        // * write code for sendMessageToUser here

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
    public static void sendMessageToGroup() {
        // * write code for sendMessageToGroup here
    }

    // TODO CASE 13
    // * When the user selects this option, the entire contents of every message
    // sent to the user (including group messages)
    // *should be displayed in a nicely formatted way.
    public static void displayMessages() {
        // * write code for displayMessages here
    }

    // TODO CASE 14
    // * This should display messages in the same fashion as the previous task
    // except that only those
    // * messages sent since the last time the user logged into the system should be
    // displayed (including
    // * group messages).
    public static void displayNewMessages() {
        // * write code for displayNewMessages here
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
    public static void displayFriends() {
        // * write code for displayFriends here
    }

    // TODO CASE 16
    // * This task should produce a ranked list of groups based on their number of
    // members.
    // * In the event that there are no groups in the system, a message No Groups
    // to Rank should
    // * be displayed to the user.
    public static void rankGroups() {
        // ! ** WORK IN PROGRESS - NOT COMPLETE **
        // USE LEFT JOIN instead of a natural join (INNER JOIN) in this case is very
        // important
        // it will ensure that all groups from the groupInfo table are included in the
        // result, even if they have no members in the groupMember table. (i.e empty
        // groups)
        String rankGroupsStatement = "SELECT g.gID, g.name, COUNT(gm.userID) AS member_count " +
                "FROM groupInfo g " +
                "LEFT JOIN groupMember gm ON g.gID = gm.gID " +
                "GROUP BY g.gID, g.name " +
                "ORDER BY member_count DESC, g.gID";
    
        try {
            PreparedStatement rankGroups = conn.prepareStatement(rankGroupsStatement);
            ResultSet rs = rankGroups.executeQuery();
            System.out.println("Group ID\tGroup Name\tNumber of Members");
            if (rs.next()) {
                System.out.println(
                        rs.getString("gID") + "\t" + rs.getString("name") + "\t" + rs.getString("member_count"));
            } else {
                System.out.println("No Groups to Rank");
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
    public static void rankProfiles() {
        // write code for rankProfiles here
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
    public static void topMessages() {
        // write code for topMessages here
    }

    // TODO CASE 19
    // Given a userID, find a path, if one exists, between the logged-in user and
    // that user profile with
    // at most 3 hops between them. A hop is defined as a friendship between any two
    // users.
    // *IMPORTANT NOTE* This query should be written using plpgsql and should only
    // use java for interfacing. *IMPORTANT NOTE*
    public static void threeDegrees() {
        // write code for threeDegrees here
    }

    // TODO CASE 20
    // * The function should return the user to the top level of the UI after
    // marking the time of the
    // * user's logout in the user's lastlogin field of the user relation from the
    // Clock table
    public static void logout() {
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
            String logoutUpdateQuery = "UPDATE profile SET lastlogin = '" + clockTime + "' WHERE userID = " + userID + ";";
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
    public static void exit() {
        // write code for exit here
        System.out.println("Exiting BeSocial... Goodbye!");
        endProgram();
    }

    private static void printErrors(SQLException e) {
        System.out.println(e.getMessage());
        while ((e = e.getNextException()) != null) {
            System.out.println(e.getMessage());
        }
    }
}