import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

// **NOTE** PLEASE USE THE EXTENSION 'BetterNotes' to make this file more readable! **NOTE** 

import java.sql.*;
import java.util.Properties;

public class BeSocial {
    public static Scanner sc;
    public static Connection conn;
    public static int userID;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
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
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/";
        Properties props = new Properties();
        props.setProperty("user", databaseUsername);
        props.setProperty("password", databasePassword);
        conn = DriverManager.getConnection(url, props);

        System.out.println("Connection established");
        System.out.println("Start BeSocial");

        // Run the main program loop until the user exits 
        try {
            int userInput = -1;
            boolean isLoggedIn = false;
            while (userInput != 0) {
                displayMenu(isLoggedIn);
                
                System.out.println("Choose an option from the menu: ");
                userInput = sc.nextInt();

                // Validate user input based on logged in status
                // If the option they selected is invalid, print statement and continue to next loop
                if (isLoggedIn) {
                    if (userInput == 1 || userInput == 3) {
                        System.out.println("This option is invalid for a logged in user");
                        continue;
                    }
                } else {
                    if (!(userInput == 1 || userInput == 3)) {
                        System.out.println("This option is invalid for a user who is not logged in");
                        continue;
                    }
                }

                switch(userInput) {
                    case 1:
                        createProfile(); 
                        break;
                    case 2:  
                        login(); 
                        isLoggedIn = true;
                        break;
                    case 3: 
                        dropProfile(); 
                        break;
                    case 4:
                        initiateFriendship();
                        break;
                    case 5: 
                        confirmFriendRequests();
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
                        logout(userID, isLoggedIn);
                        break;
                    case 21: 
                        exit();
                        break;
                }
            }
        } catch (Exception e) {
            sc.close();
            conn.close();
            System.out.println("End BeSocial");
        }
        
        System.out.println("Thank you for using BeSocial");
        sc.close();
        conn.close();
    }
    
    //case 1
    // * Given a name, email address, password and date of birth, add a new user to the system by
    // * inserting a new entry into the profile relation. userIDs should be auto-generated.
    public static void createProfile() {
        String name, email, password, dob; 
        System.out.print("Enter name: "); 
        name = sc.nextLine(); 
        System.out.print("Enter email: "); 
        email = sc.nextLine();
        System.out.print("Enter Password: "); 
        password = sc.nextLine(); 
        System.out.print("Enter DOB in format 'MM-DD-YYYY"); 
        dob = sc.nextLine(); 
        
        try{
            PreparedStatement createProfile = conn.prepareStatement(
                "INSERT INTO profile VALUES(-1, ? , ? , ? , ?, -1)");
            createProfile.setString(1, name);
            createProfile.setString(2, email);
            createProfile.setString(3, password);
            createProfile.setString(4, dob);

           ResultSet rs = createProfile.executeQuery();
              if (rs.next() == false) {
                System.out.println("Error creating profile, please ensure you are entering a unique name, email, password, dob");
              } else {
                String confirmedEmail = rs.getString("email");
                System.out.printf("Successfully created profile for Name:\n", name + "Email\n" + confirmedEmail);
              }
        }
        catch (SQLException e){
            // print the error
            System.out.println("Error caught in createProfile" + e.getMessage());
        }
    }
        // * case 2
        // * This functions prompts for a user email and removes the profile along with all of their information from the system. When a profile is removed, the system should use a trigger to delete
        // * the user from the groups they are a member of. The system should also use a trigger to
        // * delete any message whose sender and all receivers are deleted. Attention should be paid to
        // * handling integrity constraints.
       public static void login() {
        String username, password;
        System.out.print("Enter BeSocial username: ");
        username = sc.nextLine();
        System.out.print("Enter password: ");
        password = sc.nextLine();
        try {
            String qry = "SELECT * FROM profile WHERE name=? AND password=?;"; // TODO: Possibly 
            PreparedStatement loginQuery = conn.prepareStatement(qry);
            loginQuery.setString(1, username);
            loginQuery.setString(2, password);
            ResultSet rs = loginQuery.executeQuery();

            if (rs.next() == false) {
                System.out.println("Could not log in user, please try again.");
            } else {
                userID = rs.getInt("userID");
                System.out.printf("Successfully logged in as %s\n", username);
            }
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR: Could not access the database");
        }        
    }

    public static void displayMenu(boolean isLoggedIn) {
        if (isLoggedIn){
            System.out.println("Display Partial Menu");
        }
        else{
            System.out.println("Display Full Menu");
        }
        
    }

    // TODO: CASE 3
    // * Given email and password, login as the user in the system when an appropriate match is found.
    public static void dropProfile() {  
        // still needs to be checked by TA
        String email; 
        System.out.print("Enter the email to drop profile for: ");
        email = sc.nextLine(); 

        try{
            // call delete from profile on specified email, cascade (to move other associated data)
            PreparedStatement dropProfile = conn.prepareStatement(
                "DELETE FROM PROFILE WHERE EMAIL = ? CASCADE"
            );
            dropProfile.setString(1, email); //set email as first parameter
            ResultSet rs = dropProfile.executeQuery();
            if (rs.next() == false) {
                System.out.println("Unable to drop that profile, please ensure you are entering a valid email");
            } else {
                String confirmedEmail = rs.getString("email");
                System.out.printf("Successfully dropped profioe for %s\n", confirmedEmail);
            }

        } catch (Exception e) {
            // code to handle exception here
            System.out.println("Error caught in dropProfile function" + e.getMessage());
        }
    }

    // TODO CASE 4
    // * Create a pending friendship from the logged-in user profile to another user profile based on
    // * userID. The application should display the name of the person that will be sent a friend request
    // * and the user should be prompted to enter the text to be sent along with the request. A last
    // * confirmation should be requested of the user before an entry is inserted into the pendingFriend
    // * relation, and success or failure feedback is displayed for the user.
    public static void initiateFriendship(){
        // * write code for initiateFriendship here
    }

    // TODO CASE 5
    // * This task should first display a formatted, numbered list of all the outstanding friend requests
    // * with the associated request text. Then the user should be prompted for a number of the request
    // * they would like to confirm, one at a time, or given the option to confirm them all.
    // * The application should move the selected request(s) from the pendingFriend relation to the
    // * friend relation with JDate set to the current date of the Clock table.
    // * The remaining requests which were not selected are declined and removed from the pendingFriend relation.
    // * In the event that the user has no pending friend requests, a message “No Pending Friend
    // * Requests” should be displayed to the user.
    public static void confirmFriendRequests(){
        // * write code for confirmFriendRequests here
    }

    // TODO CASE 6
    // * Given a name, description, and membership limit (i.e., size), add a new group to the system,
    // * add the current user as its first member with the role manager. gIDs should be auto-generated.
    public static void createGroup(){
        // * write code for createGroup here
    }

    // TODO CASE 7
    // * Given a group ID and the request’s text, create a pending request of adding the logged-in user
    // * to the group by inserting a new entry into the pendingGroupMember relation.
    public static void initiateAddingGroup(){
        // * write code for initiateAddingGroup here
    }
    
    // TODO CASE 8
    // * This task should first display a formatted, numbered list of all the pending group membership
    // * requests with the associated request text for any groups where the user is a group manager.
    // * Then, the user should be prompted for a number of the request they would like to confirm, one
    // * at a time, or given the option to confirm them all.
    // * The application should move the selected request(s) from the pendingGroupMember relation
    // * to the groupMember relation using the current time in Clock for the lastConfirmed timestamp.
    // * If accepting a pending group membership request would exceed the group’s size, the accepted
    // * request should remain in pendingGroupMember. The remaining requests which were not selected 
    // * are declined and removed from the pendingGroupMember relation.
    // * In the event that there are no pending group membership requests for any groups that the user
    // * is a manager of, a message “No Pending Group Membership Requests” should be displayed to
    // * the user. Furthermore, a message “No groups are currently managed” should be displayed if
    // * the user is not a manager of any groups.
    public static void confirmGroupMembership(){
        // * write code for confirmGroupMembership here
    }

    // TODO CASE 9
    // * This task should first prompt the user for the gID of the group they would like to leave.
    // * The application should remove the user from the group in the groupMember relation. Upon
    // * removing the user from the group, you should use a trigger to check if there are pending
    // * group membership requests in pendingGroupMember that were previously accepted, but could
    // * not be added due exceeding the group’s size, and move the earliest such request from the 
    // * pendingGroupMember relation to the groupMember relation without changing the lastConfirmed timestamp.
    // * In the event that the user is not a member of the specified group, a message “Not a Member
    // * of any Groups” should be displayed to the user.
    public static void leaveGroup(){
        // *wrote code for leaveGroup here

    }

    // TODO CASE 10
    // * Given a string on which to match any user profile in the system, any item in this string must be
    // * matched against the “name” and “email” fields of a user’s profile. That is if the user searches
    // * for “xyz abc”, the results should be the set of all user profiles that have “xyz” in their “name”
    // * or “email” union the set of all user profiles that have “abc” in their “name” or “email”.
    public static void searchForProfile(){
        // * write code for searchForProfile here
    }
    
    // TODO CASE 11
    // * With this the user can send a message to one friend given the friend’s userID. The application
    // * should display the name of the recipient and the user should be prompted to enter the body
    // * of the message, which could be multi-lined. Once entered, the application should “send” the
    // * message to the receiving user by adding an appropriate entry into the message relation (msgIDs
    // * should be auto-generated and timeSent should be set to the current time of the Clock table)
    // * and use a trigger to add a corresponding entry into the messageRecipient relation. The user
    // * should lastly be shown success or failure feedback.
    public static void sendMessageToUser(){
        // * write code for sendMessageToUser here

    }
    
    // TODO CASE 12
    // * With this the user can send a message to a recipient group given the group ID, if the user is
    // * within the group. Every member of this group should receive the message. The user should be
    // * prompted to enter the body of the message, which could be multi-lined. Then the application
    // * should “send” the message to the group by adding an appropriate entry into the message
    // * relation (msgIDs should be auto-generated and timeSent should be set to the current time of
    // * the Clock table) and use a trigger to add corresponding entries into the messageRecipient
    // * relation. The user should lastly be shown success or failure feedback.
    // * Note that if the user sends a message to one friend, you only need to put the friend’s userID
    // * to ToUserID in the table of message. If the user wants to send a message to a group, you need
    // * to put the group ID to ToGroupID in the table of message and use a trigger to populate
    // * the messageRecipient table with proper user ID information as defined by the groupMember
    // * relation.
    public static void sendMessageToGroup(){
        // * write code for sendMessageToGroup here
    }
    
    // TODO CASE 13
    // * When the user selects this option, the entire contents of every message sent to the user (including group messages) 
    // *should be displayed in a nicely formatted way.
    public static void displayMessages(){
        // * write code for displayMessages here
    }
    
    // TODO CASE 14
    // * This should display messages in the same fashion as the previous task except that only those
    // * messages sent since the last time the user logged into the system should be displayed (including
    // * group messages).
    public static void displayNewMessages(){
        // * write code for displayNewMessages here
    }
    
    // TODO CASE 15
    // * This task supports the browsing of the logged-in user’s friends’ profiles. It first displays each
    // * of the user’s friends’ names and userIDs. Then it allows the user to either retrieve a friend’s
    // * entire profile by entering the appropriate userID or exit browsing and return to the main menu
    // * by entering 0 as a userID. When selected, a friend’s profile should be displayed in a nicely
    // * formatted way, after which the user should be prompted to either select to retrieve another
    // * friend’s profile or return to the main menu.
    public static void displayFriends(){
        // * write code for displayFriends here
    }

    
    // TODO CASE 16
    // * This task should produce a ranked list of groups based on their number of members.
    // * In the event that there are no groups in the system, a message “No Groups to Rank” should
    // * be displayed to the user.
    public static void rankGroups(){
         // !  ** WORK IN PROGRESS - NOT COMPLETE **
        // USE LEFT JOIN instead of a natural join (INNER JOIN) in this case is very important
        // it will ensure that all groups from the groupInfo table are included in the result, even if they have no members in the groupMember table. (i.e empty groups)
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
                System.out.println(rs.getString("gID") + "\t" + rs.getString("name") + "\t" + rs.getString("member_count"));
            }
            else{
                System.out.println("No Groups to Rank");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // TODO CASE 17
    // * This task should produce a ranked list of user profiles based on the number of friends they
    // * have along with their number of friends.
    // * Note the number of friends of a profile includes those who are members of the groups the user
    // * profile belongs to.
    public static void rankProfiles(){
        // write code for rankProfiles here
    }

    // TODO CASE 18
    // * Display the top k users with respect to the number of messages sent to the logged-in user plus
    // * the number of messages received from the logged-in user in the past x months. x and k are
    // * input parameters to this function. 1 month is defined as 30 days counting back starting from
    // * the current date of the Clock table. Group messages do not need to be considered in this
    // * function.
    public static void topMessages(){
        // write code for topMessages here
    }
    
    // TODO CASE 19
    // Given a userID, find a path, if one exists, between the logged-in user and that user profile with
    // at most 3 hops between them. A hop is defined as a friendship between any two users.
    // *IMPORTANT NOTE* This query should be written using plpgsql and should only use java for interfacing. *IMPORTANT NOTE*
    public static void threeDegrees(){
        // write code for threeDegrees here
    }

    // TODO CASE 20
    // * The function should return the user to the top level of the UI after marking the time of the
    // * user’s logout in the user’s “lastlogin” field of the user relation from the Clock table
    public static void logout(int userID, boolean isLoggedIn) {
        System.out.println("Logging out...");
        // ? grab current time somehow from clock value ?? (this is temporary for now)
        String currentTime = "00-00-2023"; // ? insert time to currentTime (using placeholder for now)
        try {
            PreparedStatement logoutUpdate = conn.prepareStatement(
                "UPDATE profile SET lastlogin = ? WHERE userID = ?"
            );
            logoutUpdate.setString(1, currentTime);
            logoutUpdate.setInt(2, userID);
            ResultSet rs = logoutUpdate.executeQuery();
            if (rs.next() == false) {
                System.out.println("Error logging out, please try again");
            } else {
                System.out.println("Successfully logged out");
            }

        } catch (SQLException e) {
            System.out.println("Error caught in logout function: " + e.getMessage());
        }
        isLoggedIn = false;  //  update state varible loggedIn
        System.out.println("Logged out successfully");  
        }
        
    // TODO CASE 21
    // * This option should cleanly shut down and exit the program.
    public static void exit(){
        // write code for exit here 
        System.out.println("Exiting BeSocial... Goodbye!");
        System.exit(0);
    }
}