
-- ============================================
-- VCET COLLEGE CANTEEN MANAGEMENT SYSTEM
-- SQL QUERIES
-- DATABASE: PostgreSQL
-- ============================================


-- 1. DISPLAY ALL FOOD ITEMS

SELECT *
FROM food_items
ORDER BY food_id;


-- 2. DISPLAY AVAILABLE FOOD ITEMS

SELECT *
FROM food_items
WHERE stock_quantity > 0
ORDER BY food_name;


-- 3. DISPLAY FOOD ITEMS BY CATEGORY

SELECT *
FROM food_items
WHERE category = 'Breakfast'
ORDER BY food_name;


-- 4. DISPLAY ALL ORDERS

SELECT *
FROM orders
ORDER BY order_date DESC;


-- 5. DISPLAY COMPLETED ORDERS

SELECT *
FROM orders
WHERE order_status = 'COMPLETED'
ORDER BY order_date DESC;


-- 6. DISPLAY ORDER DETAILS WITH FOOD NAMES

SELECT
    o.order_id,
    o.order_date,
    oi.food_name,
    oi.quantity,
    oi.unit_price,
    oi.subtotal
FROM orders o
JOIN order_items oi
    ON o.order_id = oi.order_id
ORDER BY o.order_id;


-- 7. TOTAL REVENUE

SELECT
    COALESCE(SUM(total_amount), 0) AS total_revenue
FROM orders
WHERE order_status = 'COMPLETED';


-- 8. TOTAL NUMBER OF ORDERS

SELECT
    COUNT(*) AS total_orders
FROM orders
WHERE order_status = 'COMPLETED';


-- 9. AVERAGE ORDER VALUE

SELECT
    COALESCE(AVG(total_amount), 0) AS average_order_value
FROM orders
WHERE order_status = 'COMPLETED';


-- 10. TOTAL QUANTITY OF FOOD SOLD

SELECT
    COALESCE(SUM(oi.quantity), 0) AS total_quantity_sold
FROM order_items oi
JOIN orders o
    ON oi.order_id = o.order_id
WHERE o.order_status = 'COMPLETED';


-- 11. ITEM-WISE SALES REPORT

SELECT
    oi.food_name,
    SUM(oi.quantity) AS quantity_sold,
    SUM(oi.subtotal) AS total_revenue
FROM order_items oi
JOIN orders o
    ON oi.order_id = o.order_id
WHERE o.order_status = 'COMPLETED'
GROUP BY oi.food_name
ORDER BY quantity_sold DESC;


-- 12. TOP 5 POPULAR FOOD ITEMS

SELECT
    oi.food_name,
    SUM(oi.quantity) AS quantity_sold
FROM order_items oi
JOIN orders o
    ON oi.order_id = o.order_id
WHERE o.order_status = 'COMPLETED'
GROUP BY oi.food_name
ORDER BY quantity_sold DESC
LIMIT 5;


-- 13. CATEGORY-WISE REVENUE

SELECT
    f.category,
    SUM(oi.subtotal) AS category_revenue
FROM order_items oi
JOIN food_items f
    ON oi.food_id = f.food_id
JOIN orders o
    ON oi.order_id = o.order_id
WHERE o.order_status = 'COMPLETED'
GROUP BY f.category
ORDER BY category_revenue DESC;


-- 14. DAILY REVENUE REPORT

SELECT
    CAST(order_date AS DATE) AS sales_date,
    COUNT(*) AS total_orders,
    SUM(total_amount) AS daily_revenue
FROM orders
WHERE order_status = 'COMPLETED'
GROUP BY CAST(order_date AS DATE)
ORDER BY sales_date;


-- 15. MONTHLY REVENUE REPORT

SELECT
    DATE_TRUNC('month', order_date) AS sales_month,
    SUM(total_amount) AS monthly_revenue
FROM orders
WHERE order_status = 'COMPLETED'
GROUP BY DATE_TRUNC('month', order_date)
ORDER BY sales_month;


-- 16. BEST-SELLING FOOD ITEM

SELECT
    oi.food_name,
    SUM(oi.quantity) AS quantity_sold
FROM order_items oi
JOIN orders o
    ON oi.order_id = o.order_id
WHERE o.order_status = 'COMPLETED'
GROUP BY oi.food_name
ORDER BY quantity_sold DESC
LIMIT 1;


-- 17. CURRENT STOCK REPORT

SELECT
    food_id,
    food_name,
    category,
    stock_quantity
FROM food_items
ORDER BY stock_quantity ASC;


-- 18. OUT-OF-STOCK FOOD ITEMS

SELECT *
FROM food_items
WHERE stock_quantity = 0;


-- 19. INVENTORY TRANSACTION HISTORY

SELECT
    it.movement_id,
    f.food_name,
    it.movement_type,
    it.quantity,
    it.movement_date,
    it.order_id,
    it.notes
FROM inventory_transactions it
JOIN food_items f
    ON it.food_id = f.food_id
ORDER BY it.movement_date DESC;


-- 20. INVENTORY TRANSACTIONS BY TYPE

SELECT
    movement_type,
    COUNT(*) AS total_transactions,
    SUM(quantity) AS total_quantity
FROM inventory_transactions
GROUP BY movement_type
ORDER BY movement_type;


-- ============================================
-- END OF SQL QUERIES
-- ============================================
