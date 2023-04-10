package Phase2;

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
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8:
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 14: 
                    case 15: 
                    case 16: 
                    case 17:  
                    case 18: 
                    case 19: 
                    case 20:
                    case 21: 
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
        }
        catch (SQLException e){
            // code to handle exception here
        }
    }

    public static void dropProfile() {
        String email; 
        System.out.print("Enter the email to remove: ");
        email = sc.nextLine(); 

        try{
            PreparedStatement dropProfile = conn.prepareStatement(
                "DELETE FROM PROFILE WHERE EMAIL = ? CASCADE"
            );
            dr.setString(1, name);
            dr.setString(2, email);
            dr.setString(3, password);
            dr.setString(4, dob);

        } catch (Exception e) {
            
        }
    }
}