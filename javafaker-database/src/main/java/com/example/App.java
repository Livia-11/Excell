package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.javafaker.Faker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description: Multithreaded Java application to insert 10 million fake users into a MySQL database.
 * Uses JavaFaker for data generation and multithreading to optimize performance.
 *
 * @version 2.0
 * @author Livia
 * @since 2025-01-16
 * @see <a href="https://github.com/DiUS/java-faker">JavaFaker</a>
 * @license MIT
 */
public class App {

    // Total number of users to insert
    private static final int TOTAL_USERS = 10_000_000;
    // Number of threads to use for insertion
    private static final int THREAD_COUNT = 10;
    // Number of users each thread will handle
    private static final int USERS_PER_THREAD = TOTAL_USERS / THREAD_COUNT;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Main method to start the application.
     * Divides the task of inserting users into multiple threads to reduce execution time.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("Starting multithreaded insertion...");
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            int threadId = i;
            executorService.submit(() -> insertUsers(threadId));
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Wait for all threads to finish
        }

        System.out.println("All users have been inserted!");
    }

    /**
     * Inserts a portion of the users into the database.
     * Each thread runs this method independently.
     *
     * @param threadId Unique ID of the thread, used to calculate its data range.
     */
    private static void insertUsers(int threadId) {
        Faker faker = new Faker();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO users (first_name, last_name, email, address) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int start = threadId * USERS_PER_THREAD;
                int end = start + USERS_PER_THREAD;

                for (int i = start; i < end; i++) {
                    String firstName = faker.name().firstName();
                    String lastName = faker.name().lastName();
                    String email = faker.internet().emailAddress();
                    String address = faker.address().fullAddress();

                    // Set parameters for the insert query
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, address);
                    statement.addBatch(); // Add to batch

                    // Execute batch every 1000 records
                    if ((i + 1) % 1000 == 0) {
                        statement.executeBatch();
                    }

                    // Log progress every 1 million users
                    if ((i + 1) % 1_000_000 == 0) {
                        System.out.println("Thread " + threadId + " inserted " + (i - start + 1) + " users");
                    }
                }
                // Execute remaining batch
                statement.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Database error in thread " + threadId + ": " + e.getMessage());
        }
    }
}
