package in.cdac.JDBC;

import java.util.*;
import java.sql.*;

public class CRUD {
    static Connection con = null;
    static Statement stmt = null;
    static PreparedStatement pst = null;
    static Scanner sc = new Scanner(System.in);

    // Method to establish connection
    static void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Harsh@123");
            System.out.println("Connection established.");
        } catch (SQLException e) {
            System.out.println("Error establishing connection: " + e.getMessage());
        }
    }

    // Method to create a database
    static void createDatabase() {
        try {
            System.out.print("Enter the database name you want to create: ");
            String db_name = sc.next();
            stmt = con.createStatement();
            String query = "CREATE DATABASE " + db_name;
            stmt.executeUpdate(query);
            System.out.println("Database created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    // Method to use a database
    static void useDatabase() {
        try {
            System.out.print("Enter the database name you want to use: ");
            String db_name = sc.next();
            stmt = con.createStatement();
            String query = "USE " + db_name;
            stmt.execute(query);
            System.out.println("Now using the database: " + db_name);
        } catch (SQLException e) {
            System.out.println("Error using database: " + e.getMessage());
        }
    }

    // Method to create a table with dynamic columns
    public static void createTable() {
        try {
            System.out.print("Enter table name: ");
            String tableName = sc.next();

            System.out.print("Enter the number of columns: ");
            int numColumns = sc.nextInt();

            StringBuilder query = new StringBuilder("CREATE TABLE " + tableName + " (");
            String[] columnNames = new String[numColumns];
            String[] columnTypes = new String[numColumns];

            // Collect column names and data types
            for (int i = 1; i <= numColumns; i++) {
                System.out.print("Enter column " + i + " name: ");
                columnNames[i - 1] = sc.next();

                System.out.print("Enter data type for column " + columnNames[i - 1] + " (e.g., INT, VARCHAR(100), DOUBLE): ");
                columnTypes[i - 1] = sc.next().toUpperCase();

                query.append(columnNames[i - 1]).append(" ").append(columnTypes[i - 1]);

                if (i != numColumns) {
                    query.append(", ");
                }
            }

            // Ask the user which column should be the primary key
            System.out.print("Enter the column name to be used as the primary key: ");
            String primaryKey = sc.next();

            boolean autoIncrement = false;
            String primaryKeyType = "";

            // Check if the primary key column is an integer type and ask for AUTO_INCREMENT
            for (int i = 0; i < numColumns; i++) {
                if (columnNames[i].equalsIgnoreCase(primaryKey)) {
                    primaryKeyType = columnTypes[i];

                    if (primaryKeyType.equals("INT") || primaryKeyType.equals("BIGINT")) {
                        System.out.print("Do you want to make the primary key column AUTO_INCREMENT? (yes/no): ");
                        String isAutoIncrement = sc.next();

                        if (isAutoIncrement.equalsIgnoreCase("yes")) {
                            autoIncrement = true;
                        }
                    }
                    break;
                }
            }

            // Add AUTO_INCREMENT if needed
            if (autoIncrement) {
                query = new StringBuilder(query.toString().replace(primaryKey + " " + primaryKeyType,
                        primaryKey + " " + primaryKeyType + " AUTO_INCREMENT"));
            }

            // Add the primary key constraint at the end
            query.append(", PRIMARY KEY (").append(primaryKey).append(")");

            query.append(")");

            // Execute the SQL query
            stmt = con.createStatement();
            stmt.executeUpdate(query.toString());
            System.out.println("Table created successfully!");

        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    // Method to insert data into a table
 // Method to insert data into a table with dynamic input based on table structure
 // Method to insert data into a table with dynamic input based on table structure
    static void insertData() {
        try {
            System.out.print("Enter table name: ");
            String tableName = sc.next();

            // Query to get the table structure (columns and their data types)
            String query = "DESCRIBE " + tableName;
            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            List<String> columnNames = new ArrayList<>();
            List<String> columnTypes = new ArrayList<>();

            // Fetch column names and types
            while (rs.next()) {
                columnNames.add(rs.getString(1)); // Column name
                columnTypes.add(rs.getString(2)); // Column data type
            }

            // Construct the insert query
            StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder valuesQuery = new StringBuilder("VALUES (");

            // Loop through columns to build the query and ask the user for data
            for (int i = 0; i < columnNames.size(); i++) {
                insertQuery.append(columnNames.get(i));
                valuesQuery.append("?");

                if (i < columnNames.size() - 1) {
                    insertQuery.append(", ");
                    valuesQuery.append(", ");
                }
            }

            insertQuery.append(") ").append(valuesQuery).append(")");

            // Prepare the insert statement
            pst = con.prepareStatement(insertQuery.toString());

            // Get user input for each column based on its type
            for (int i = 0; i < columnNames.size(); i++) {
                System.out.print("Enter value for " + columnNames.get(i) + " (" + columnTypes.get(i) + "): ");
                String columnType = columnTypes.get(i).toLowerCase();

                // Based on the column type, set the appropriate value in the prepared statement
                if (columnType.startsWith("int")) {
                    pst.setInt(i + 1, sc.nextInt());
                } else if (columnType.startsWith("varchar") || columnType.startsWith("text")) {
                    pst.setString(i + 1, sc.next());
                } else if (columnType.startsWith("double") || columnType.startsWith("float")) {
                    pst.setDouble(i + 1, sc.nextDouble());
                } else if (columnType.startsWith("date")) {
                    // Use java.sql.Date
                    System.out.print("Enter date in YYYY-MM-DD format: ");
                    pst.setDate(i + 1, java.sql.Date.valueOf(sc.next()));
                } else {
                    System.out.println("Unsupported data type: " + columnType);
                    return;
                }
            }

            // Execute the insert query
            pst.executeUpdate();
            System.out.println("Record inserted successfully!");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    // Method to update data in a table
    static void updateData() {
        try {
            System.out.print("Enter table name: ");
            String tableName = sc.next();
            System.out.print("Enter ID of record to update: ");
            int id = sc.nextInt();
            System.out.print("Enter new salary: ");
            double salary = sc.nextDouble();

            String query = "UPDATE " + tableName + " SET salary = ? WHERE id = ?";
            pst = con.prepareStatement(query);
            pst.setDouble(1, salary);
            pst.setInt(2, id);
            pst.executeUpdate();

            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating data: " + e.getMessage());
        }
    }

    // Method to delete data from a table
    static void deleteData() {
        try {
            System.out.print("Enter table name: ");
            String tableName = sc.next();
            System.out.print("Enter ID of record to delete: ");
            int id = sc.nextInt();

            String query = "DELETE FROM " + tableName + " WHERE id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();

            System.out.println("Record deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting data: " + e.getMessage());
        }
    }

    // Method to delete a table
    static void deleteTable() {
        try {
            System.out.print("Enter the table name you want to delete: ");
            String tableName = sc.next();

            String query = "DROP TABLE " + tableName;
            stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Table deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting table: " + e.getMessage());
        }
    }

    // Method to delete a database
    static void deleteDatabase() {
        try {
            System.out.print("Enter the database name you want to delete: ");
            String db_name = sc.next();

            String query = "DROP DATABASE " + db_name;
            stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Database deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting database: " + e.getMessage());
        }
    }

    // Method to view all tables in the current database
    static void viewTables() {
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            System.out.println("Tables in the database:");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing tables: " + e.getMessage());
        }
    }
    //to search in a table form id
    static void search() {
        try {
            // Ask for the table name first
            System.out.print("Enter table name to search in: ");
            String tableName = sc.next();

            // Ask for the product ID to search
            System.out.print("Enter product ID to find: ");
            int id = sc.nextInt();

            // Use PreparedStatement to search the specific table
            String query = "SELECT * FROM " + tableName + " WHERE Id = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, id);

            // Execute the query
            ResultSet rs = pst.executeQuery();

            // Check if the product is found
            if (rs.next()) {
                // Get metadata about the result set
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // Print column names
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rsmd.getColumnName(i) + ": ");
                    // Print corresponding values based on the type
                    switch (rsmd.getColumnType(i)) {
                        case Types.INTEGER:
                            System.out.println(rs.getInt(i));
                            break;
                        case Types.VARCHAR:
                            System.out.println(rs.getString(i));
                            break;
                        case Types.DOUBLE:
                            System.out.println(rs.getDouble(i));
                            break;
                        case Types.FLOAT:
                            System.out.println(rs.getFloat(i));
                            break;
                        case Types.DATE:
                            System.out.println(rs.getDate(i));
                            break;
                        case Types.TIMESTAMP:
                            System.out.println(rs.getTimestamp(i));
                            break;
                        // Add more cases for other types as needed
                        default:
                            System.out.println("Unsupported type.");
                    }
                }
            } else {
                System.out.println("No product found with ID: " + id + " in table " + tableName);
            }

            // Close the ResultSet
            rs.close();
        } catch (SQLException e) {
            System.out.println("Error searching product: " + e.getMessage());
        }
    }

 // Method to print all data from a table
    static void printAllTableData() {
        try {
            System.out.print("Enter the table name to view its data: ");
            String tableName = sc.next();

            // Query to select all data from the specified table
            String query = "SELECT * FROM " + tableName;
            pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Get metadata to retrieve column names dynamically
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print table rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving table data: " + e.getMessage());
        }
    }

    // Main method to provide options to the user
    public static void main(String[] args) {
        connect();
        
        while (true) {
            System.out.println("\nChoose an operation:");
            System.out.println("1. Create Database");
            System.out.println("2. Use Database");
            System.out.println("3. Create Table");
            System.out.println("4. Insert Data");
            System.out.println("5. Update Data");
            System.out.println("6. Delete Data");
            System.out.println("7. Delete Table");
            System.out.println("8. Delete Database");
            System.out.println("9. View Tables");
            System.out.println("10. Print All Table Data");
            System.out.println("11. Search in Table ");
            System.out.println("12. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createDatabase();
                    break;
                case 2:
                    useDatabase();
                    break;
                case 3:
                    createTable();
                    break;
                case 4:
                    insertData();
                    break;
                case 5:
                    updateData();
                    break;
                case 6:
                    deleteData();
                    break;
                case 7:
                    deleteTable();
                    break;
                case 8:
                    deleteDatabase();
                    break;
                case 9:
                    viewTables();
                    break;
                case 10:
                    printAllTableData();
                    break;
                case 11:
                	search();;
                	break;
                case 12:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

}
