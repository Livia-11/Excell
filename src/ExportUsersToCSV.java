import java.io.*;
import java.sql.*;

public class ExportUsersToCSV {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/users_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String CSV_FILE_PATH = "backup.csv";
    private static final int TOTAL_RECORDS_NEEDED = 10_000_000;

    public static void main(String[] args) {
        while (true) {
            int currentCount = getCSVRecordCount();

            if (currentCount >= TOTAL_RECORDS_NEEDED) {
                System.out.println("CSV file has reached 10 million records. Stopping execution...");
                break;
            }

            exportUsersToCSV();

            try {
                Thread.sleep(5000); // Wait 5 seconds before checking again
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getCSVRecordCount() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("CSV file not found. It will be created.");
        }
        return count > 0 ? count - 1 : 0; // Exclude header row
    }

    private static void exportUsersToCSV() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users LIMIT 10000");
             ResultSet resultSet = stmt.executeQuery();
             FileWriter csvWriter = new FileWriter(CSV_FILE_PATH, true)) {

            // If CSV is empty, write header
            if (getCSVRecordCount() == 0) {
                csvWriter.append("ID,First Name,Last Name,Email,Address\n");
            }

            int batchCount = 0;
            while (resultSet.next() && getCSVRecordCount() < TOTAL_RECORDS_NEEDED) {
                csvWriter.append(resultSet.getInt("id") + ",")
                        .append(resultSet.getString("first_name") + ",")
                        .append(resultSet.getString("last_name") + ",")
                        .append(resultSet.getString("email") + ",")
                        .append(resultSet.getString("address") + "\n");

                batchCount++;
            }

            System.out.println(batchCount + " records exported to CSV.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}






