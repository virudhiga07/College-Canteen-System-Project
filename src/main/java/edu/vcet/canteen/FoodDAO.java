
package edu.vcet.canteen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    // 1. Add a new food item
    public int addFood(String name, String category,
                       BigDecimal price, int stock)
                       throws SQLException {

        String sql = "INSERT INTO food_items " +
                     "(food_name, category, price, stock_quantity) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setBigDecimal(3, price);
            ps.setInt(4, stock);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // 2. View all food items
    public List<FoodItem> getAllFood() throws SQLException {

        List<FoodItem> foodList = new ArrayList<>();

        String sql = "SELECT * FROM food_items ORDER BY food_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                FoodItem food = new FoodItem(
                    rs.getInt("food_id"),
                    rs.getString("food_name"),
                    rs.getString("category"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity")
                );

                foodList.add(food);
            }
        }

        return foodList;
    }

    // 3. Update food item details
    public int updateFood(int id, String name,
                          String category, BigDecimal price,
                          int stock) throws SQLException {

        String sql = "UPDATE food_items SET " +
                     "food_name = ?, category = ?, " +
                     "price = ?, stock_quantity = ? " +
                     "WHERE food_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, category);
            ps.setBigDecimal(3, price);
            ps.setInt(4, stock);
            ps.setInt(5, id);

            return ps.executeUpdate();
        }
    }

    // 4. Delete food item
    public int deleteFood(int id) throws SQLException {

        String sql = "DELETE FROM food_items WHERE food_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate();
        }
    }

    // 5. Update only food stock
    public int updateStock(int id, int newStock) throws SQLException {

        String sql = "UPDATE food_items " +
                     "SET stock_quantity = ? " +
                     "WHERE food_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newStock);
            ps.setInt(2, id);

            return ps.executeUpdate();
        }
    }
}
