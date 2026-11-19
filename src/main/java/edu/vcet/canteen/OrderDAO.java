
package edu.vcet.canteen;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    private static class OrderLine {

        String name;
        BigDecimal price;
        int quantity;
        BigDecimal subtotal;

        OrderLine(String name, BigDecimal price, int quantity) {

            this.name = name;
            this.price = price;
            this.quantity = quantity;

            this.subtotal = price.multiply(
                    BigDecimal.valueOf(quantity));
        }

        OrderResult.BillItem toBillItem() {

            return new OrderResult.BillItem(
                    name, quantity, price, subtotal);
        }
    }

    // 1. Place a new order
    public OrderResult placeOrder(Map<Integer, Integer> items)
            throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new SQLException("Order has no food items.");
        }

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                Map<Integer, OrderLine> orderLines =
                        new LinkedHashMap<>();

                BigDecimal total = BigDecimal.ZERO;

                // Check food and stock
                String checkSQL =
                        "SELECT food_name, price, stock_quantity " +
                        "FROM food_items WHERE food_id = ? FOR UPDATE";

                for (Map.Entry<Integer, Integer> item
                        : items.entrySet()) {

                    int foodId = item.getKey();
                    int quantity = item.getValue();

                    if (foodId <= 0 || quantity <= 0) {
                        throw new SQLException(
                                "Invalid food ID or quantity.");
                    }

                    try (PreparedStatement ps =
                            con.prepareStatement(checkSQL)) {

                        ps.setInt(1, foodId);

                        try (ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {
                                throw new SQLException(
                                        "Food ID " + foodId + " not found.");
                            }

                            String name = rs.getString("food_name");
                            BigDecimal price =
                                    rs.getBigDecimal("price");
                            int stock = rs.getInt("stock_quantity");

                            if (stock < quantity) {
                                throw new SQLException(
                                        "Insufficient stock for " + name +
                                        ". Available: " + stock);
                            }

                            OrderLine line =
                                    new OrderLine(name, price, quantity);

                            orderLines.put(foodId, line);
                            total = total.add(line.subtotal);
                        }
                    }
                }

                // Insert order
                String orderSQL =
                        "INSERT INTO orders " +
                        "(total_amount, order_status) " +
                        "VALUES (?, 'COMPLETED') " +
                        "RETURNING order_id, order_date";

                int orderId;
                Timestamp orderDate;

                try (PreparedStatement ps =
                        con.prepareStatement(orderSQL)) {

                    ps.setBigDecimal(1, total);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Could not create order.");
                        }

                        orderId = rs.getInt("order_id");
                        orderDate = rs.getTimestamp("order_date");
                    }
                }

                // Insert order item with original food name
                String itemSQL =
                        "INSERT INTO order_items " +
                        "(order_id, food_id, food_name, quantity, " +
                        "unit_price, subtotal) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                // Reduce stock
                String stockSQL =
                        "UPDATE food_items " +
                        "SET stock_quantity = stock_quantity - ? " +
                        "WHERE food_id = ? AND stock_quantity >= ?";

                // Record inventory movement
                String inventorySQL =
                        "INSERT INTO inventory_transactions " +
                        "(food_id, movement_type, quantity, " +
                        "order_id, notes) " +
                        "VALUES (?, 'SALE', ?, ?, ?)";

                List<OrderResult.BillItem> billItems =
                        new ArrayList<>();

                for (Map.Entry<Integer, OrderLine> entry
                        : orderLines.entrySet()) {

                    int foodId = entry.getKey();
                    OrderLine line = entry.getValue();

                    try (PreparedStatement ps =
                            con.prepareStatement(itemSQL)) {

                        ps.setInt(1, orderId);
                        ps.setInt(2, foodId);
                        ps.setString(3, line.name);
                        ps.setInt(4, line.quantity);
                        ps.setBigDecimal(5, line.price);
                        ps.setBigDecimal(6, line.subtotal);

                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps =
                            con.prepareStatement(stockSQL)) {

                        ps.setInt(1, line.quantity);
                        ps.setInt(2, foodId);
                        ps.setInt(3, line.quantity);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Stock update failed for " + line.name);
                        }
                    }

                    try (PreparedStatement ps =
                            con.prepareStatement(inventorySQL)) {

                        ps.setInt(1, foodId);
                        ps.setInt(2, line.quantity);
                        ps.setInt(3, orderId);
                        ps.setString(4,
                                "Stock reduced after successful order");

                        ps.executeUpdate();
                    }

                    billItems.add(line.toBillItem());
                }

                // Commit all database operations
                con.commit();

                return new OrderResult(
                        orderId, orderDate, total, billItems);

            } catch (SQLException | RuntimeException e) {

                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    // 2. Retrieve an existing bill using Order ID
    public OrderResult getOrderById(int orderId)
            throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {

            String orderSQL =
                    "SELECT order_id, order_date, total_amount " +
                    "FROM orders " +
                    "WHERE order_id = ? AND order_status = 'COMPLETED'";

            try (PreparedStatement ps =
                    con.prepareStatement(orderSQL)) {

                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        return null;
                    }

                    Timestamp orderDate =
                            rs.getTimestamp("order_date");

                    BigDecimal total =
                            rs.getBigDecimal("total_amount");

                    List<OrderResult.BillItem> billItems =
                            new ArrayList<>();

                    String itemSQL =
                            "SELECT food_name, quantity, " +
                            "unit_price, subtotal " +
                            "FROM order_items " +
                            "WHERE order_id = ? " +
                            "ORDER BY order_item_id";

                    try (PreparedStatement itemPS =
                            con.prepareStatement(itemSQL)) {

                        itemPS.setInt(1, orderId);

                        try (ResultSet itemRS =
                                itemPS.executeQuery()) {

                            while (itemRS.next()) {

                                billItems.add(
                                    new OrderResult.BillItem(
                                        itemRS.getString("food_name"),
                                        itemRS.getInt("quantity"),
                                        itemRS.getBigDecimal("unit_price"),
                                        itemRS.getBigDecimal("subtotal")
                                    )
                                );
                            }
                        }
                    }

                    return new OrderResult(
                            orderId, orderDate, total, billItems);
                }
            }
        }
    }
}
