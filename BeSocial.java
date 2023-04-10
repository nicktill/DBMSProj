import java.util.Scanner;

import java.sql.*;
import java.util.Properties;

public class BeSocial {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);

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
        Connection conn = DriverManager.getConnection(url, props);

        // Close the scanner at the end
        sc.close();
    }
}