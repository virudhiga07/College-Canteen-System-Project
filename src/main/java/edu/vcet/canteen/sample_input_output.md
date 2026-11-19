# VCET College Canteen
## Java Application – Sample Inputs and Outputs

**Technology:** Java 17, Maven, JDBC and PostgreSQL  
**Main class:** `edu.vcet.canteen.Main`

This document records representative console inputs and outputs for the Java canteen application. Examples marked **Observed** are based on console runs shared during project development. Examples marked **Capture required** should be run again on the final project version before being submitted as actual results.

---

## 1. Add Food Item – Observed

### Sample input
```text
Enter your choice: 1

Food name: chapathi
Category: dinner
Price: 40
Stock quantity: 5
```

### Output
```text
--- ADD FOOD ITEM ---
Food added successfully!
Food ID: 2
```

Another item was added during testing:

```text
Enter your choice: 1

Food name: chicken rice
Category: lunch
Price: 100
Stock quantity: 50
```

Output:
```text
Food added successfully!
Food ID: 3
```

---

## 2. View Food Items – Observed

### Sample input
```text
Enter your choice: 2
```

### Output
```text
--- FOOD MENU ---
ID       Food Name            Category        Price        Stock
---------------------------------------------------------------
1        Idli                 Breakfast       20.00        2
2        chapathi             dinner          40.00        5
3        chicken rice         lunch           100.00       50
```

*Note: This is an earlier menu snapshot. Stock values change after successful orders.*

---

## 3. Place Order – Successful Order – Observed

### Sample input
```text
Enter your choice: 5

Enter Food ID (0 to finish): 4
Enter quantity: 3

Enter Food ID (0 to finish): 0
```

### Output
```text
==============================================
             VCET COLLEGE CANTEEN
              CUSTOMER RECEIPT
==============================================
Order ID   : 5
Date       : 2026-11-18 16:06:40.414052
----------------------------------------------
Food Item           Qty     Price   Subtotal
----------------------------------------------
puri                  3     50.00     150.00
----------------------------------------------
TOTAL AMOUNT : Rs. 150.00
Order Status : COMPLETED
==============================================
          Thank you! Visit again.
==============================================
```

This successful order demonstrates automatic bill calculation, transaction saving and stock reduction.

---

## 4. Place Order – Insufficient Stock – Observed

In the earlier test snapshot, Idli had only one unit available.

### Sample input
```text
Enter your choice: 5

Enter Food ID (0 to finish): 1
Enter quantity: 2

Enter Food ID (0 to finish): 0
```

### Output
```text
Operation failed!
Reason: Insufficient stock for Idli. Available: 1
```

The order should not be completed when the requested quantity exceeds available stock.

---

## 5. View Previous Bill – Observed

### Sample input
```text
Enter your choice: 7

Enter Order ID: 3
```

### Output
```text
==============================================
             VCET COLLEGE CANTEEN
              CUSTOMER RECEIPT
==============================================
Order ID   : 3
Date       : 2026-11-18 12:18:57.798108
----------------------------------------------
Food Item           Qty     Price   Subtotal
----------------------------------------------
chapathi              2     40.00      80.00
----------------------------------------------
TOTAL AMOUNT : Rs. 80.00
Order Status : COMPLETED
==============================================
          Thank you! Visit again.
==============================================
```

This demonstrates retrieval of a previous bill using its Order ID.

---

## 6. Sales Report – Sample Input

### Input
```text
Enter your choice: 6
```

### Latest sales figures confirmed by the Python analysis

The following values were retrieved from PostgreSQL by the Python analysis run shared during project testing:

```text
========================================
   VCET COLLEGE CANTEEN SALES ANALYSIS
========================================

Total Revenue       : Rs. 850.00
Total Orders        : 5
Total Quantity Sold : 14
Average Order Value : Rs. 170.00

Best-Selling Food Item: chicken rice

CATEGORY-WISE REVENUE
Lunch        500.0
Breakfast    190.0
Dinner       160.0

MONTHLY REVENUE
2026-11    850.0

PEAK SALES DAY
Date: 2026-11-18
Revenue: Rs. 730.00
```

**Important:** These are confirmed Python analysis figures, not a captured transcript of Java menu option 6. Run option 6 in the final Java version and paste its actual console output here if the Java sales-report output is required separately.

---

## 7. Update Food Item – Capture Required

Run the final Java application and use an existing food ID.

### Sample input pattern
```text
Enter your choice: 3

Enter food ID: 2
New food name: chapathi
New category: dinner
New price: 45
New stock quantity: 8
```

### Expected result pattern
```text
Food updated successfully!
```

**Capture required:** Replace this expected result with the actual console output from the final application.

---

## 8. Delete Food Item – Capture Required

Use a food ID that is safe to delete and is not referenced by existing order records. The current database has foreign-key relationships, so a food item used in an order may not be deletable.

### Sample input pattern
```text
Enter your choice: 4

Enter food ID: <unused food ID>
```

### Expected result pattern
```text
Food deleted successfully!
```

**Capture required:** Record the actual result. If the item is referenced by an order, PostgreSQL may reject deletion because of the foreign-key constraint. Do not delete existing sales data to make this test pass.

---

## 9. Exit Application – Observed

### Sample input
```text
Enter your choice: 0
```

### Output
```text
Thank you!
```

The final application version may display:
```text
Thank you for using VCET Canteen!
```

Use the exact message shown by the version submitted.

---

## 10. How to Run the Java Application

Open the project folder containing `pom.xml` in the VS Code terminal.

Compile:
```powershell
mvn clean compile
```

Run:
```powershell
mvn exec:java
```

The PostgreSQL server must be running, and database connection settings must be configured correctly.

---



