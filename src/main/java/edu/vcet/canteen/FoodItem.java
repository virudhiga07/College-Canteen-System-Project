
package edu.vcet.canteen;

import java.math.BigDecimal;

public class FoodItem {

    private int foodId;
    private String foodName;
    private String category;
    private BigDecimal price;
    private int stockQuantity;

    public FoodItem(int foodId, String foodName,
                    String category, BigDecimal price,
                    int stockQuantity) {

        this.foodId = foodId;
        this.foodName = foodName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
}
