Test Cases and Actual Results:
The following test cases are based on the execution outputs shared in the project conversation. “Passed – observed” means the provided terminal output showed the stated result; it does not imply an independent re-run by the document preparer.
ID	Test	Input / Action	Expected Result	Actual Result / Evidence	Status
TC-01	Java database connection	Run Java connection test using the configured DB environment variables.	Connection message and successful Maven execution.	Output showed “Java connected to PostgreSQL!” and BUILD SUCCESS.	Passed – observed
TC-02	Add food item	Add Idli, category Breakfast, price 20, stock 2.	Food record is inserted and a food ID is returned.	Output showed “Food added successfully!” and Food ID 1.	Passed – observed
TC-03	View food items	Choose View Food Items after adding records.	Saved items, prices and stock are displayed.	Idli and later menu items appeared with stored prices and stock.	Passed – observed
TC-04	Multi-item order and billing	Order 1 Idli at 20 and 2 Chapathi at 40 each.	Total should be 100 and order should complete.	Receipt showed Order ID 1 and total Rs. 100.00.	Passed – observed
TC-05	Insufficient stock rejection	Request 2 Idlis when only 1 is available.	Order must be rejected with available-stock message.	Output showed “Insufficient stock for Idli. Available: 1”.	Passed – observed
TC-06	Order using final available stock	Order 1 Idli when stock is 1.	Order completes and stock becomes 0.	Receipt showed Order ID 2, Rs. 20.00; subsequent menu showed Idli stock 0.	Passed – observed
TC-07	Zero-stock rejection	Request 1 Idli after stock reaches 0.	Order must be rejected.	Output showed “Insufficient stock for Idli. Available: 0”.	Passed – observed
TC-08	Add further menu items	Add Puri and other items using the Add Food option.	New food IDs are returned.	Puri was added as Food ID 4; Pongal was later visible in menu output.	Passed – observed
TC-09	Python sales retrieval and summary	Run py python/sales_analysis.py against canteen_db.	Python retrieves completed orders and calculates summary metrics.	Output showed revenue 850.00, 5 orders, 14 items and average order value 170.00.	Passed – observed
TC-10	Category standardization	Run Python analysis when category values differ by case or spaces.	Equivalent category labels are consolidated.	Output showed Breakfast once with Rs. 190.00, Lunch Rs. 500.00, Dinner Rs. 160.00.	Passed – observed
TC-11	Advanced analytics	Run upgraded Python sales analysis.	Top item, monthly revenue and peak sales day are reported.	Chicken rice was best-selling; November 2026 revenue Rs. 850.00; peak date 2026-11-18, Rs. 730.00.	Passed – observed
TC-12	Chart and CSV generation	List files in python/outputs after analysis.	Five PNG charts and two CSV reports exist.	Output listing showed item_wise_sales, daily_revenue, category_wise_revenue, popular_food_items, monthly_revenue and two CSV files.	Passed – observed
TC-13	Inventory table creation	Create inventory_transactions and inspect its columns in pgAdmin.	Table contains movement_id, food_id, movement_type, quantity, movement_date, order_id and notes.	Screenshot showed all seven columns and the result grid.	Passed – observed
TC-14	Detailed receipt	Place a Puri order for quantity 3 at Rs. 50 each.	Receipt shows item, quantity, unit price, subtotal and total.	Order ID 5 receipt showed Puri, quantity 3, Rs. 50.00, subtotal Rs. 150.00 and total Rs. 150.00.	Passed – observed
TC-15	Previous bill lookup	Choose View Previous Bill and enter Order ID 3.	Original saved bill is displayed.	Order ID 3 displayed Chapathi, quantity 2, unit price Rs. 40.00 and total Rs. 80.00.	Passed – observed
TC-16	Inventory SALE audit row	After a successful order, query inventory_transactions for its order ID.	A SALE row should exist for each ordered food item.	Java order processing code inserts SALE rows; a result screenshot for the specific movement row was not supplied.	Pending DB evidence
TC-17	Update food item	Use Update Food Item on an existing food ID.	Updated values should be persisted and displayed.	No separate test output was supplied.	Pending
TC-18	Delete food item	Delete an unreferenced food item.	Unreferenced item is deleted; referenced historical items are protected by FK.	No separate test output was supplied.	Pending
TC-19	Invalid food ID rollback	Place an order with a food ID that does not exist.	Order transaction should fail and roll back.	An earlier run showed Food ID 3 not found; a database before/after row-count comparison was not supplied.	Partially evidenced
TC-20	Invalid numeric input	Enter non-numeric values for menu choices, quantities or prices.	Program should prompt again and not crash.	Input validation code is present; a dedicated execution result was not supplied.	Pending

5. Summary of Test Execution
Result category	Count
Passed – observed	15 cases
Partially evidenced	1 case
Pending / requires evidence	4 cases
