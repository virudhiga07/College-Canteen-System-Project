
-- ============================================
-- VCET COLLEGE CANTEEN MANAGEMENT SYSTEM
-- EXISTING DATABASE MIGRATION
-- DATABASE: canteen_db
-- ============================================

-- IMPORTANT:
-- Take a backup before executing this script.
-- Run this script on your EXISTING canteen_db.
-- Do not run schema.sql again.


-- 1. ADD FOOD_NAME COLUMN TO ORDER_ITEMS

ALTER TABLE order_items
ADD COLUMN IF NOT EXISTS food_name VARCHAR(100);


-- 2. UPDATE EXISTING FOOD NAMES
-- Copy food names from food_items into old order records.

UPDATE order_items oi
SET food_name = fi.food_name
FROM food_items fi
WHERE oi.food_id = fi.food_id
AND oi.food_name IS NULL;


-- 3. CHECK FOR MISSING FOOD NAMES

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM order_items
        WHERE food_name IS NULL
    ) THEN
        RAISE EXCEPTION
        'Migration stopped: Some food names could not be updated. Check order_items records.';
    END IF;
END $$;


-- 4. MAKE FOOD_NAME NOT NULL

ALTER TABLE order_items
ALTER COLUMN food_name SET NOT NULL;


-- 5. CREATE INVENTORY TRANSACTIONS TABLE

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


-- 6. CREATE INDEXES

CREATE INDEX IF NOT EXISTS idx_order_items_order
ON order_items(order_id);

CREATE INDEX IF NOT EXISTS idx_inventory_food_date
ON inventory_transactions(food_id, movement_date);

CREATE INDEX IF NOT EXISTS idx_orders_date
ON orders(order_date);


-- ============================================
-- MIGRATION COMPLETED
-- ============================================
