
package edu.vcet.canteen;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class OrderResult {

    private int orderId;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private List<BillItem> items;

    public OrderResult(
            int orderId,
            Timestamp orderDate,
            BigDecimal totalAmount,
            List<BillItem> items) {

        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<BillItem> getItems() {
        return items;
    }

    // Individual item details in a bill
    public static class BillItem {

        private String foodName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public BillItem(
                String foodName,
                int quantity,
                BigDecimal unitPrice,
                BigDecimal subtotal) {

            this.foodName = foodName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public String getFoodName() {
            return foodName;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }
}
