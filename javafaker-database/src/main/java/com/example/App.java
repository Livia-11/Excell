package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.javafaker.Faker;
/**
 * description: This is a simple Java application that uses the JavaFaker library to generate fake data and insert it into a MySQL database.
 * It generates 10 million users with fake names, emails, and addresses, and inserts them into the 'users' table.
 * The application includes a progress indicator to show the number of users inserted.
 * @version 1.0
 * @author Livia
 * @since 2025-01-16
 * @see <a href="https://github.com/DiUS/java-faker">JavaFaker</a>
 * @license MIT
 */
public class App {
    public static void main(String[] args) {
        /**
         * Faker is a library that generates fake data for testing and development purposes.
         * It provides a wide range of data types, including names, addresses, phone numbers, and more.
         * The Faker class is used to create instances of the Faker class, which can then be used to generate fake data.
         * @see <a href="https://github.com/DiUS/java-faker">JavaFaker</a>
         */
        Faker faker = new Faker();

        // Database connection details
        /**
         * url is the URL of the database.
         * username is the username of the database.
         * password is the password of the database.
         */
        String url = "jdbc:mysql://localhost:3306/users_db";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Prepare SQL statement to insert data
            /**
             * sql is the SQL statement to insert data into the database.
             */
            String sql = "INSERT INTO users (first_name, last_name, email, address) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                /**
                 * Generate and insert 10 million users
                 */
                for (int i = 0; i < 10_000_000; i++) {
                    String firstName = faker.name().firstName();
                    String lastName = faker.name().lastName();
                    String email = faker.internet().emailAddress();
                    String address = faker.address().fullAddress();

                    // Set parameters and execute the insert
                    /**
                     * statement.setString(1, firstName);
                     * statement.setString(2, lastName);
                     * statement.setString(3, email);
                     * statement.setString(4, address);
                     * statement.executeUpdate();
                     */
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, address);
                    statement.executeUpdate();
                    /**
                     * Print progress every 1 million users
                     */
                    if (i % 1_000_000 == 0) {
                        System.out.println("Inserted " + (i + 1) + " users");
                    }
                }
            }
        } catch (SQLException e) {
            /**
             * Print error message
             */
            System.err.println("Database error occurred: " + e.getMessage());
            // Or for more serious applications, use a logging framework:
            // logger.error("Database error occurred", e);
        }
    }
}