package in.cdac.JDBC;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Connection con = null;
        Statement smt = null;
        PreparedStatement pstmt = null;

        try {
            Scanner sc = new Scanner(System.in);

            // Establishing the connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Product", "root", "Harsh@123");
            System.out.println("Connection established: " + con);

            smt = con.createStatement();
            System.out.print("Choose 1 for Insert, 2 for Search, 3 for Delete, 4 for Update: ");
            int op = sc.nextInt();

            if (op == 1) {
                System.out.print("Enter number of rows to insert: ");
                int n = sc.nextInt();
                String sql = "INSERT INTO product (product_Name, product_Price, product_Quantity) VALUES (?, ?, ?)";
                pstmt = con.prepareStatement(sql);

                for (int i = 0; i < n; i++) {
                    System.out.print("Enter Name: ");
                    String name = sc.next();
                    System.out.print("Enter Price: ");
                    double price = sc.nextDouble();
                    System.out.print("Enter Quantity: ");
                    String quantity = sc.next();

                    // Setting values into the PreparedStatement
                    pstmt.setString(1, name);
                    pstmt.setDouble(2, price);
                    pstmt.setString(3, quantity);

                    // Executing the update
                    int rowsInserted = pstmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Row inserted successfully");
                    }
                }
            } else if (op == 2) {
                System.out.print("Enter ID to find: ");
                int id = sc.nextInt();

                String sql2 = "SELECT * FROM product WHERE product_ID =" + id;
                ResultSet rs = smt.executeQuery(sql2);
                if (rs.next()) {
                    System.out.println("ID: " + rs.getInt(1));
                    System.out.println("Name: " + rs.getString(2));
                    System.out.println("Price: " + rs.getDouble(3));
                    System.out.println("Quantity: " + rs.getString(4));
                } else {
                    System.out.println("No product found with ID: " + id);
                }
            } else if (op == 3) {
                System.out.print("Enter ID to delete: ");
                int id = sc.nextInt();
                String sql3 = "DELETE FROM product WHERE product_ID=" + id;
                int rowsDeleted = smt.executeUpdate(sql3);
                if (rowsDeleted > 0) {
                    System.out.println("Product deleted successfully");
                } else {
                    System.out.println("No product found with ID: " + id);
                }
            } 
            else if (op == 4) {
                System.out.print("Enter ID to update: ");
                int id = sc.nextInt();
                System.out.print("Enter Name: ");
                String name = sc.next();
                System.out.print("Enter Price: ");
                double price = sc.nextDouble();
                System.out.print("Enter Quantity: ");
                String quantity = sc.next();

                // Update all the fields: name, price, and quantity
                String sql = "UPDATE product SET product_Name = ?, product_Price = ?, product_Quantity = ? WHERE product_ID = ?";
                pstmt = con.prepareStatement(sql);

                // Set the new values for the product
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setString(3, quantity);
                pstmt.setInt(4, id);  // ID to specify which product to update

                // Execute the update
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Product updated successfully");
                } else {
                    System.out.println("No product found with ID: " + id);
                }
            }

             else {
                System.out.println("Invalid option selected.");
            }

        } catch (Exception e) {
            e.printStackTrace();  // Printing full stack trace for better debugging
        } finally {
            // Closing resources
            try {
                if (pstmt != null) pstmt.close();
                if (smt != null) smt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
