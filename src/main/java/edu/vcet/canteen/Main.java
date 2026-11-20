
package edu.vcet.canteen;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        FoodDAO foodDAO = new FoodDAO();
        OrderDAO orderDAO = new OrderDAO();
        SalesReportDAO reportDAO = new SalesReportDAO();

        while (true) {

            System.out.println("\n================================");
            System.out.println("     VCET COLLEGE CANTEEN");
            System.out.println("================================");
            System.out.println("1. Add Food Item");
            System.out.println("2. View Food Items");
            System.out.println("3. Update Food Item");
            System.out.println("4. Delete Food Item");
            System.out.println("5. Place Order");
            System.out.println("6. Sales Report");
            System.out.println("7. View Previous Bill");
            System.out.println("8. Update Food Stock");
            System.out.println("0. Exit");
            System.out.println("================================");

            int choice = readInt("Enter your choice: ");

            try {

                switch (choice) {

                    case 1:
                        addFood(foodDAO);
                        break;

                    case 2:
                        viewFood(foodDAO);
                        break;

                    case 3:
                        updateFood(foodDAO);
                        break;

                    case 4:
                        deleteFood(foodDAO);
                        break;

                    case 5:
                        placeOrder(foodDAO, orderDAO);
                        break;

                    case 6:
                        reportDAO.displaySalesReport();
                        break;

                    case 7:
                        viewPreviousBill(orderDAO);
                        break;

                    case 8:
                        updateStock(foodDAO);
                        break;

                    case 0:
                        System.out.println("Thank you for using VCET Canteen!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } catch (SQLException e) {

                System.out.println("\nOperation failed!");
                System.out.println("Reason: " + e.getMessage());
            }
        }
    }

    // 1. Add Food
    private static void addFood(FoodDAO dao) throws SQLException {

        System.out.println("\n--- ADD FOOD ITEM ---");

        String name = readText("Food name: ");
        String category = readText("Category: ");
        BigDecimal price = readPrice("Price: ");
        int stock = readNonNegativeInt("Stock quantity: ");

        int id = dao.addFood(name, category, price, stock);

        System.out.println("Food added successfully!");
        System.out.println("Food ID: " + id);
    }

    // 2. View Food
    private static void viewFood(FoodDAO dao) throws SQLException {

        System.out.println("\n--- FOOD MENU ---");

        List<FoodItem> foods = dao.getAllFood();

        if (foods.isEmpty()) {
            System.out.println("No food items available.");
            return;
        }

        System.out.printf(
                "%-8s %-20s %-15s %-12s %-10s%n",
                "ID", "Food Name", "Category", "Price", "Stock"
        );

        System.out.println(
                "---------------------------------------------------------------"
        );

        for (FoodItem food : foods) {

            System.out.printf(
                    "%-8d %-20s %-15s %-12s %-10d%n",
                    food.getFoodId(),
                    food.getFoodName(),
                    food.getCategory(),
                    food.getPrice(),
                    food.getStockQuantity()
            );
        }
    }

    // 3. Update Food Details
    private static void updateFood(FoodDAO dao) throws SQLException {

        System.out.println("\n--- UPDATE FOOD ITEM ---");

        int id = readPositiveInt("Enter food ID: ");

        String name = readText("New food name: ");
        String category = readText("New category: ");
        BigDecimal price = readPrice("New price: ");
        int stock = readNonNegativeInt("New stock quantity: ");

        int rows = dao.updateFood(id, name, category, price, stock);

        if (rows > 0) {
            System.out.println("Food updated successfully!");
        } else {
            System.out.println("Food ID not found.");
        }
    }

    // 4. Delete Food
    private static void deleteFood(FoodDAO dao) throws SQLException {

        System.out.println("\n--- DELETE FOOD ITEM ---");

        int id = readPositiveInt("Enter food ID: ");

        int rows = dao.deleteFood(id);

        if (rows > 0) {
            System.out.println("Food deleted successfully!");
        } else {
            System.out.println("Food ID not found.");
        }
    }

    // 5. Place Order
    private static void placeOrder(
            FoodDAO foodDAO,
            OrderDAO orderDAO
    ) throws SQLException {

        System.out.println("\n--- PLACE ORDER ---");

        viewFood(foodDAO);

        Map<Integer, Integer> items = new LinkedHashMap<>();

        System.out.println("\nEnter 0 as Food ID to finish the order.");

        while (true) {

            int foodId = readInt("\nEnter Food ID (0 to finish): ");

            if (foodId == 0) {
                break;
            }

            if (foodId < 0) {
                System.out.println("Invalid Food ID.");
                continue;
            }

            int quantity = readPositiveInt("Enter quantity: ");

            items.merge(foodId, quantity, Integer::sum);
        }

        if (items.isEmpty()) {
            System.out.println("No items selected. Order cancelled.");
            return;
        }

        OrderResult result = orderDAO.placeOrder(items);

        printReceipt(result);
    }

    // 6. Display detailed receipt
    private static void printReceipt(OrderResult result) {

        System.out.println("\n");
        System.out.println("==============================================");
        System.out.println("             VCET COLLEGE CANTEEN");
        System.out.println("              CUSTOMER RECEIPT");
        System.out.println("==============================================");

        System.out.println("Order ID   : " + result.getOrderId());
        System.out.println("Date       : " + result.getOrderDate());
        System.out.println("----------------------------------------------");

        System.out.printf(
                "%-17s %5s %9s %10s%n",
                "Food Item", "Qty", "Price", "Subtotal"
        );

        System.out.println("----------------------------------------------");

        for (OrderResult.BillItem item : result.getItems()) {

            System.out.printf(
                    "%-17s %5d %9s %10s%n",
                    item.getFoodName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getSubtotal()
            );
        }

        System.out.println("----------------------------------------------");

        System.out.printf(
                "TOTAL AMOUNT : Rs. %.2f%n",
                result.getTotalAmount()
        );

        System.out.println("Order Status : COMPLETED");
        System.out.println("==============================================");
        System.out.println("          Thank you! Visit again.");
        System.out.println("==============================================");
    }

    // 7. View Previous Bill
    private static void viewPreviousBill(OrderDAO dao)
            throws SQLException {

        System.out.println("\n--- VIEW PREVIOUS BILL ---");

        int orderId = readPositiveInt("Enter Order ID: ");

        OrderResult result = dao.getOrderById(orderId);

        if (result == null) {
            System.out.println(
                    "No completed bill found for Order ID " + orderId);
            return;
        }

        printReceipt(result);
    }

    // 8. Update Only Food Stock
    private static void updateStock(FoodDAO dao) throws SQLException {

        System.out.println("\n--- UPDATE FOOD STOCK ---");

        // Display existing food items
        viewFood(dao);

        int id = readPositiveInt("\nEnter Food ID: ");

        // Find the selected food
        FoodItem selectedFood = null;

        for (FoodItem food : dao.getAllFood()) {
            if (food.getFoodId() == id) {
                selectedFood = food;
                break;
            }
        }

        if (selectedFood == null) {
            System.out.println("Food ID not found.");
            return;
        }

        System.out.println("Food Name    : " + selectedFood.getFoodName());
        System.out.println("Current Stock: " + selectedFood.getStockQuantity());

        int newStock = readNonNegativeInt("Enter New Stock: ");

        int rows = dao.updateStock(id, newStock);

        if (rows > 0) {
            System.out.println("\nStock updated successfully!");
            System.out.println("Food Name : " + selectedFood.getFoodName());
            System.out.println("Old Stock : " + selectedFood.getStockQuantity());
            System.out.println("New Stock : " + newStock);
        } else {
            System.out.println("Stock update failed.");
        }
    }

    // Read Text
    private static String readText(String message) {

        while (true) {

            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Value cannot be empty.");
        }
    }

    // Read Integer
    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            try {
                return Integer.parseInt(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Read Positive Integer
    private static int readPositiveInt(String message) {

        while (true) {

            int value = readInt(message);

            if (value > 0) {
                return value;
            }

            System.out.println("Enter a number greater than zero.");
        }
    }

    // Read Non-negative Integer
    private static int readNonNegativeInt(String message) {

        while (true) {

            int value = readInt(message);

            if (value >= 0) {
                return value;
            }

            System.out.println("Value cannot be negative.");
        }
    }

    // Read Price
    private static BigDecimal readPrice(String message) {

        while (true) {

            System.out.print(message);

            try {

                BigDecimal price =
                        new BigDecimal(sc.nextLine().trim());

                if (price.signum() >= 0) {
                    return price;
                }

            } catch (NumberFormatException e) {
                // Display validation message below.
            }

            System.out.println("Enter a valid non-negative price.");
        }
    }
}
