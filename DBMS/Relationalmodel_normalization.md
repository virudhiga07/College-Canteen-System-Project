# VCET College Canteen
# Relational Model and Normalization Details

## 1. Database Overview

Database: `canteen_db`  
DBMS: PostgreSQL

The database contains four relations: `FOOD_ITEMS`, `ORDERS`, `ORDER_ITEMS` and `INVENTORY_TRANSACTIONS`.

## 2. Relational Model

**PK** = Primary Key; **FK** = Foreign Key.

### 2.1 FOOD_ITEMS

`FOOD_ITEMS(food_id [PK], food_name, category, price, stock_quantity)`

Primary key: `food_id`.  
`price` and `stock_quantity` must be non-negative.

Purpose: Stores menu information and the current stock balance.

### 2.2 ORDERS

`ORDERS(order_id [PK], order_date, total_amount, order_status)`

Primary key: `order_id`.  
`order_date` defaults to the current timestamp. `total_amount` must be non-negative. `order_status` defaults to `COMPLETED` in the supplied schema.

Purpose: Stores order-header and billing-total information.

### 2.3 ORDER_ITEMS

`ORDER_ITEMS(order_item_id [PK], order_id [FK], food_id [FK], food_name, quantity, unit_price, subtotal)`

Primary key: `order_item_id`.  
Foreign keys: `order_id` references `ORDERS(order_id)`; `food_id` references `FOOD_ITEMS(food_id)`.  
`quantity` must be positive; `unit_price` and `subtotal` must be non-negative.

Purpose: Stores each purchased line. `food_name` and `unit_price` are transaction snapshots, preserving the original bill if the current menu name or price later changes.

### 2.4 INVENTORY_TRANSACTIONS

`INVENTORY_TRANSACTIONS(movement_id [PK], food_id [FK], movement_type, quantity, movement_date, order_id [FK, nullable], notes)`

Primary key: `movement_id`.  
Foreign keys: `food_id` references `FOOD_ITEMS(food_id)`; `order_id` references `ORDERS(order_id)` and may be NULL for non-order stock movements.  
`movement_type` is `SALE`, `RESTOCK` or `ADJUSTMENT`. Quantity must be positive. A `SALE` must reference an order.

Purpose: Records changes to stock and the reason for each movement.

## 3. Relationships and Cardinality

1. One food item can appear in zero or many order-item records; each order-item record refers to exactly one food item.
2. One order contains one or more order-item records; each order-item record belongs to exactly one order.
3. One food item can have zero or many inventory transactions; each movement refers to exactly one food item.
4. One order can be linked to zero or many inventory movements. Each movement can reference zero or one order; a SALE movement must reference an order.

`ORDER_ITEMS` connects `ORDERS` and `FOOD_ITEMS`.

## 4. Normalization Details

Normalization organizes relations to reduce unnecessary duplication and update anomalies.

### 4.1 First Normal Form (1NF)

A relation is in 1NF when each field contains one atomic value and repeating groups are not stored in a single field.

In this project, each food item, order header, order line and inventory movement is stored as an individual row. The tables have primary keys to identify each row. The design follows the 1NF requirement.

### 4.2 Second Normal Form (2NF)

A relation is in 2NF when it is in 1NF and every non-key attribute depends on the whole candidate key, not just part of a composite key.

All four tables use single-column primary keys. Therefore, no partial dependency on part of a composite primary key occurs in these relations.

### 4.3 Third Normal Form (3NF)

A relation is in 3NF when it is in 2NF and non-key attributes do not have inappropriate transitive dependencies on the key.

This design separates current food details, order headers, purchased order lines and inventory movements into four relations. This reduces unnecessary duplication of current menu and order information.

### 4.4 Intentional Transaction Snapshots and Calculated Values

The project deliberately stores:
- `ORDER_ITEMS.food_name` as the name at the time of purchase.
- `ORDER_ITEMS.unit_price` as the selling price at the time of purchase.
- `ORDER_ITEMS.subtotal` as the line amount calculated from quantity and unit price.
- `ORDERS.total_amount` as the final order total.

The snapshot values preserve historical bills when current menu details change. The calculated values make bill retrieval and reporting convenient.

**Normalization qualification:** `subtotal` can be derived from `quantity × unit_price`, and `total_amount` can be derived from the order's line subtotals. Storing these calculated values is controlled redundancy. Therefore, the physical schema is a practical transaction-oriented design and should not be described as perfectly redundancy-free 3NF. The application must calculate and save these values consistently.

## 5. Integrity Constraints

- Primary keys uniquely identify rows.
- Foreign keys preserve valid relationships.
- Check constraints reject negative prices, amounts and stock quantities.
- Positive-quantity constraints reject zero or negative order and movement quantities.
- A SALE inventory movement must have a related order.

