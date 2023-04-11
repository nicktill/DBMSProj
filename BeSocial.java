import java.util.Scanner;

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
    //case 2
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

    //case 3
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

    //ADDING ALL FUNCTION HEADERS
    // case 4
    public static void initiateFriendship(){
        // write code for initiateFriendship here
    }

    // case 5
    public static void confirmFriendRequests(){
        // write code for confirmFriendRequests here
    }

    // case 6
    public static void createGroup(){
        // write code for createGroup here
    }

    // case 7
    public static void initiateAddingGroup(){
        // write code for initiateAddingGroup here
    }
    
    // case 8
    public static void confirmGroupMembership(){
        // write code for confirmGroupMembership here
    }

    // case 9
    public static void leaveGroup(){

    }

    // case 10
    public static void searchForProfile(){
        // write code for searchForProfile here
    }
    
    // case 11
    public static void sendMessageToUser(){
        // write code for sendMessageToUser here
    }
    
    // case 12
    public static void sendMessageToGroup(){
        // write code for sendMessageToGroup here
    }
    
    // case 13
    public static void displayMessages(){
        // write code for displayMessages here
    }
    
    // case 14
    public static void displayNewMessages(){
        // write code for displayNewMessages here
    }
    
    // case 15
    public static void displayFriends(){
        // write code for displayFriends here
    }
    
    // case 16
    public static void rankGroups(){
        // write code for rankGroups here
    }
    
    // case 17
    public static void rankProfiles(){
        // write code for rankProfiles here
    }

    // case 18
    public static void topMessages(){
        // write code for topMessages here
    }
    
    // case 19
    public static void threeDegrees(){
        // write code for threeDegrees here
    }

    // case 20
    public static void logout(int userID, boolean isLoggedIn) {
        System.out.println("Logging out...");
        // grab current time somehow from clock value ?? (this is temporary for now )
        String currentTime = "00-00-2023"; // insert time to currentTime (using placeholder for now)
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
        isLoggedIn = false;  // update state varible loggedIn
        System.out.println("Logged out successfully");  
        }
        
    // case 21
    public static void exit(){
        // write code for exit here 
        System.out.println("Exiting BeSocial... Goodbye!");
        System.exit(0);
    }
}