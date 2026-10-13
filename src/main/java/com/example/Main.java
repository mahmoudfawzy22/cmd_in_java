package com.example;

import java.sql.*;
import java.util.*;

public class Main {
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/students";
   

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // try_with-resources
                Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to SQL Server!");
            System.out.println("Welcome to the SQL Server CLI App. Type 'help' for commands.");

            boolean exit = false;
            while (!exit) {
                System.out.print("> ");
                String command = scanner.nextLine().trim();

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    exit = true;
                } else if (command.equalsIgnoreCase("help")) {
                    printHelp();
                } else {
                    runQuery(connection, command);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  Any valid SQL query (e.g., SELECT, INSERT, UPDATE).");
        System.out.println("  exit - Exit the application.");
    }

    private static void runQuery(Connection connection, String query) {
        try (Statement statement = connection.createStatement()) {
            boolean isResultSet = statement.execute(query);

            if (isResultSet) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Print column headers
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(metaData.getColumnName(i) + "\t");
                    }
                    System.out.println();

                    // Print rows
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(resultSet.getString(i) + "\t");
                        }
                        System.out.println();
                    }
                }
            } else {
                int updateCount = statement.getUpdateCount();
                System.out.println(updateCount + " rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
    }
}