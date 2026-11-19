
-- ============================================
-- VCET COLLEGE CANTEEN MANAGEMENT SYSTEM
-- DATABASE: canteen_db
-- DBMS: PostgreSQL
-- ============================================

-- 1. FOOD ITEMS TABLE
CREATE TABLE IF NOT EXISTS food_items (
    food_id SERIAL PRIMARY KEY,
    food_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price NUMERIC(10,2) NOT NULL
        CHECK (price >= 0),
    stock_quantity INTEGER NOT NULL
        CHECK (stock_quantity >= 0)
);


-- 2. ORDERS TABLE
CREATE TABLE IF NOT EXISTS orders (
    order_id SERIAL PRIMARY KEY,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount NUMERIC(10,2) NOT NULL
        CHECK (total_amount >= 0),
    order_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED'
);


-- 3. ORDER ITEMS TABLE
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id SERIAL PRIMARY KEY,

    order_id INTEGER NOT NULL
        REFERENCES orders(order_id),

    food_id INTEGER NOT NULL
        REFERENCES food_items(food_id),

    -- Stores the food name at the time of purchase
    food_name VARCHAR(100) NOT NULL,

    quantity INTEGER NOT NULL
        CHECK (quantity > 0),

    unit_price NUMERIC(10,2) NOT NULL
        CHECK (unit_price >= 0),

    subtotal NUMERIC(10,2) NOT NULL
        CHECK (subtotal >= 0)
);


-- 4. INVENTORY TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS inventory_transactions (
    movement_id SERIAL PRIMARY KEY,

    food_id INTEGER NOT NULL
        REFERENCES food_items(food_id),

    movement_type VARCHAR(20) NOT NULL
        CHECK (
            movement_type IN (
                'SALE',
                'RESTOCK',
                'ADJUSTMENT'
            )
        ),

    quantity INTEGER NOT NULL
        CHECK (quantity > 0),

    movement_date TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    order_id INTEGER
        REFERENCES orders(order_id),

    notes TEXT,

    CONSTRAINT chk_sale_has_order
        CHECK (
            movement_type <> 'SALE'
            OR order_id IS NOT NULL
        )
);


-- 5. INDEXES

CREATE INDEX IF NOT EXISTS idx_orders_date
ON orders(order_date);

CREATE INDEX IF NOT EXISTS idx_order_items_food
ON order_items(food_id);

CREATE INDEX IF NOT EXISTS idx_order_items_order
ON order_items(order_id);

CREATE INDEX IF NOT EXISTS idx_inventory_food_date
ON inventory_transactions(food_id, movement_date);


-- ============================================
-- DATABASE TABLE CREATION COMPLETED
-- ============================================
