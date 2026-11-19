
package edu.vcet.canteen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesReportDAO {

    public void displaySalesReport() throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            System.out.println("\n======================================");
            System.out.println("       VCET CANTEEN SALES REPORT");
            System.out.println("======================================");

            // 1. Overall sales summary
            String summarySQL =
                    "SELECT COUNT(*) AS total_orders, " +
                    "COALESCE(SUM(total_amount), 0) AS revenue " +
                    "FROM orders WHERE order_status = 'COMPLETED'";

            try (PreparedStatement ps =
                         con.prepareStatement(summarySQL);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("\n--- SALES SUMMARY ---");
                    System.out.println("Total Orders : "
                            + rs.getInt("total_orders"));
                    System.out.println("Total Revenue: Rs. "
                            + rs.getBigDecimal("revenue"));
                }
            }

            // 2. Total quantity of food sold
            String quantitySQL =
                    "SELECT COALESCE(SUM(oi.quantity), 0) " +
                    "AS total_quantity " +
                    "FROM order_items oi " +
                    "JOIN orders o ON oi.order_id = o.order_id " +
                    "WHERE o.order_status = 'COMPLETED'";

            try (PreparedStatement ps =
                         con.prepareStatement(quantitySQL);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("Items Sold   : "
                            + rs.getInt("total_quantity"));
                }
            }

            // 3. Item-wise sales
            System.out.println("\n--- ITEM-WISE SALES ---");

            String itemSQL =
                    "SELECT oi.food_name, " +
                    "SUM(oi.quantity) AS quantity_sold, " +
                    "SUM(oi.subtotal) AS item_revenue " +
                    "FROM order_items oi " +
                    "JOIN orders o ON oi.order_id = o.order_id " +
                    "WHERE o.order_status = 'COMPLETED' " +
                    "GROUP BY oi.food_name " +
                    "ORDER BY quantity_sold DESC";

            try (PreparedStatement ps =
                         con.prepareStatement(itemSQL);
                 ResultSet rs = ps.executeQuery()) {

                System.out.printf("%-20s %-15s %-15s%n",
                        "Food Name", "Quantity Sold", "Revenue");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.printf("%-20s %-15d Rs. %-12s%n",
                            rs.getString("food_name"),
                            rs.getInt("quantity_sold"),
                            rs.getBigDecimal("item_revenue"));
                }

                if (!found) {
                    System.out.println("No sales records available.");
                }
            }

            // 4. Daily revenue
            System.out.println("\n--- DAILY REVENUE ---");

            String dailySQL =
                    "SELECT CAST(order_date AS DATE) AS sales_date, " +
                    "COUNT(*) AS total_orders, " +
                    "SUM(total_amount) AS daily_revenue " +
                    "FROM orders " +
                    "WHERE order_status = 'COMPLETED' " +
                    "GROUP BY CAST(order_date AS DATE) " +
                    "ORDER BY sales_date DESC";

            try (PreparedStatement ps =
                         con.prepareStatement(dailySQL);
                 ResultSet rs = ps.executeQuery()) {

                System.out.printf("%-18s %-15s %-15s%n",
                        "Date", "Orders", "Revenue");

                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.printf("%-18s %-15d Rs. %-12s%n",
                            rs.getDate("sales_date"),
                            rs.getInt("total_orders"),
                            rs.getBigDecimal("daily_revenue"));
                }

                if (!found) {
                    System.out.println("No daily sales available.");
                }
            }

            System.out.println("\n======================================");
            System.out.println("          END OF REPORT");
            System.out.println("======================================");
        }
    }
}
