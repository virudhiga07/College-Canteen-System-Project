Software Requirements Specification (SRS) document
Purpose:
The purpose of the VCET College Canteen Ordering and Sales Analysis System is to computerize food-item maintenance, order entry, bill calculation, stock checking, transaction storage and sales analysis. The Java program provides a menu-driven console interface. PostgreSQL stores the operational data, and a Python program reads completed sales for preprocessing, summary analysis and chart generation.
Problem Statement:
The canteen scenario describes manual recording of food orders, bills, inventory and daily sales. Manual records make it difficult to maintain accurate stock, identify popular items and analyse demand. The proposed system stores food and transaction data in a relational database and applies Python-based analysis to the saved sales records.
Objectives:
•	Maintain food item names, categories, prices and stock quantities.
•	Accept orders containing one or more food items and their quantities.
•	Calculate item subtotals and the total bill automatically.
•	Reject orders when the requested quantity is greater than available stock.
•	Reduce inventory only after an order is successfully stored.
•	Save order headers and individual order lines in PostgreSQL.
•	Display sales summaries and analyse sales using Python and charts.
Scope:
Included: food item CRUD operations, multi-item order processing, bill calculation, stock validation and deduction, transaction storage, Java sales reports, Python database retrieval, preprocessing, grouping and chart generation.
Not included in the current version: customer accounts, staff roles, payment gateway, online ordering, delivery tracking, mobile application or web frontend. The application is intentionally console-based.
 General Description:
Product Perspective
The system is a standalone Java console application connected to a PostgreSQL database through JDBC. A separate Python program connects to the same database using psycopg2, retrieves completed sales records, preprocesses them with Pandas and creates charts with Matplotlib. The Java program handles operational transactions; Python is used for analytical reporting.
Major Product Functions
Module	Main responsibility
Food Item Management	Add, view, update and delete food item records.
Order Processing	Collect food IDs and quantities; validate selected items.
Billing	Calculate subtotals and total; print a detailed receipt.
Inventory Control	Check available stock and reduce stock after a successful order.
Transaction Storage	Save order, order-item and inventory movement records.
Previous Bill	Retrieve an existing completed bill using its Order ID.
Java Sales Report	Display order count, revenue, quantity sold, item-wise sales and daily revenue.
Python Sales Analysis	Calculate AOV, popular items, category revenue, daily/monthly revenue and peak sales day.

 User and Operating Environment:
Environment	Requirement / Current choice
Primary user	Canteen staff member or operator using the console menu
Operating system	Windows environment used during development
Programming language	Java 17
Build tool	Apache Maven
Database	PostgreSQL, database name canteen_db
Java connectivity	JDBC PostgreSQL driver
Analytics language	Python
Python libraries	Pandas, Matplotlib, psycopg2-binary and python-dotenv
Data output	PNG charts saved in python/outputs/

User Requirements:
Primary User Goal
The canteen operator needs to maintain the food menu, accept a customer order, issue an accurate bill, ensure that stock is not oversold and review sales information.
User Requirements
ID	User requirement
UR-01	The operator shall be able to add a food item with its name, category, price and stock quantity.
UR-02	The operator shall be able to view the available food menu and stock quantities.
UR-03	The operator shall be able to update or delete a food item.
UR-04	The operator shall be able to create an order containing one or more food items and quantities.
UR-05	The system shall calculate the bill automatically and display a receipt.
UR-06	The system shall prevent an order when a food ID is invalid or requested quantity exceeds stock.
UR-07	The operator shall be able to retrieve a previous completed bill using its Order ID.
UR-08	The operator shall be able to view sales summary and item-wise/daily sales reports.
UR-09	The operator or evaluator shall be able to run Python analysis to view sales trends and generated charts.

User Interaction Overview
1. The operator starts the Java program and selects an option from the main menu.
2. For food management, the operator enters the requested item details or food ID.
3. For order processing, the operator selects one or more food IDs and quantities, then enters 0 to finish item selection.
4. The system validates the order, calculates the amount, saves the transaction and prints a receipt.
5. The operator may retrieve a previous bill by entering its Order ID or view the Java sales report.
6. The Python analysis is executed separately from the terminal to produce sales summaries, charts and CSV files.
Use Case Summary
Actor	Use case	Result
Canteen Staff	Manage Food Items	Food menu records are added, displayed, updated or deleted.
Canteen Staff	Place Order	A valid order is stored and stock is reduced.
Canteen Staff	View Receipt	A detailed bill is displayed for a completed order.
Canteen Staff	View Previous Bill	A saved bill is retrieved by Order ID.
Canteen Staff	View Sales Report	A Java sales summary is displayed.
Project Operator	Run Python Analysis	Sales summary, charts and CSV reports are generated.


System Requirements:
Functional Requirements
ID	Requirement	System behaviour
FR-01	Add food item	Accept food name, category, price and initial stock; insert the record into food_items.
FR-02	View food menu	Retrieve food records from PostgreSQL and display ID, name, category, price and stock.
FR-03	Update food item	Update the selected food item using its food_id.
FR-04	Delete food item	Delete the selected food item when database referential constraints allow it.
FR-05	Place multi-item order	Accept one or more food IDs and quantities in a single order.
FR-06	Validate stock	Check that each food ID exists and requested quantity is positive and available.
FR-07	Calculate bill	Calculate each line subtotal from unit price and quantity, then add line subtotals.
FR-08	Store transaction	Insert an order header and corresponding order_items records.
FR-09	Update inventory	Deduct ordered quantities only after successful transaction processing.
FR-10	Generate Java sales report	Display completed order count, total revenue, quantity sold, item-wise sales and daily revenue.
FR-11	Python data analysis	Read completed order-line data from PostgreSQL and preprocess numeric/date fields.
FR-12	Visualize sales	Create item-wise quantity, daily revenue and category-wise revenue charts.


Non-Functional Requirements
ID	Quality attribute	Requirement
NFR-01	Data integrity	Primary keys and foreign keys link orders and order items to valid records.
NFR-02	Order consistency	Order creation uses a database transaction; an unsuccessful operation is rolled back.
NFR-03	Input validation	The console rejects blank text, invalid numeric input, negative stock and non-positive order quantities.
NFR-04	Maintainability	The application separates console flow, data access, model objects and database connection.
NFR-05	Portability	Java dependencies are managed through Maven; Python packages are listed in requirements.txt.
NFR-06	Credential safety	Database passwords are read from environment variables or a local .env file and must not be committed to Git.
NFR-07	Usability	A numbered console menu provides direct access to the required canteen operations.

 Assumptions and Constraints
•	PostgreSQL service is running locally and the canteen_db database exists.
•	The user has valid PostgreSQL credentials and the required permissions.
•	Prices are represented with decimal numeric types to avoid binary floating-point currency calculations.
•	An order is marked COMPLETED when it is inserted by the current Java order flow.
•	The application is a single-console prototype; authentication and concurrent multi-user workflows are outside the current scope.
 Error Handling
•Non-numeric menu input shall be rejected and the user prompted again.
•Unknown Food IDs shall not be accepted into a completed order.
•Insufficient stock shall reject the order and leave stock and order records unchanged.
•A database exception during order creation shall cause transaction rollback.
•A missing previous Order ID shall produce a not-found message.
•Python database connection or data retrieval errors shall be displayed without generating misleading sales results.
