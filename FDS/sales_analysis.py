
import os
from pathlib import Path
from contextlib import closing

import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import psycopg2
from dotenv import load_dotenv


# Project paths
BASE_DIR = Path(__file__).resolve().parent
OUTPUT_DIR = BASE_DIR / "outputs"
OUTPUT_DIR.mkdir(exist_ok=True)

# Load database settings
load_dotenv(BASE_DIR / ".env")


def get_connection():

    password = os.getenv("PGPASSWORD")

    if not password:
        raise ValueError("PostgreSQL password missing in .env")

    return psycopg2.connect(
        host=os.getenv("PGHOST", "localhost"),
        port=os.getenv("PGPORT", "5432"),
        dbname=os.getenv("PGDATABASE", "canteen_db"),
        user=os.getenv("PGUSER", "postgres"),
        password=password
    )


def fetch_dataframe(cursor, query):

    cursor.execute(query)

    columns = [column[0] for column in cursor.description]
    rows = cursor.fetchall()

    return pd.DataFrame(rows, columns=columns)


def save_chart(filename):

    plt.tight_layout()
    plt.savefig(OUTPUT_DIR / filename, dpi=150)
    plt.close()


def main():

    orders_query = """
        SELECT
            order_id,
            order_date,
            total_amount
        FROM orders
        WHERE order_status = 'COMPLETED'
        ORDER BY order_date
    """

    items_query = """
        SELECT
            oi.order_item_id,
            oi.order_id,
            oi.food_name,
            f.category,
            oi.quantity,
            oi.unit_price,
            oi.subtotal
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        JOIN food_items f ON oi.food_id = f.food_id
        WHERE o.order_status = 'COMPLETED'
        ORDER BY oi.order_item_id
    """

    # Retrieve PostgreSQL data
    try:

        with closing(get_connection()) as conn:

            with conn.cursor() as cursor:

                orders = fetch_dataframe(cursor, orders_query)
                items = fetch_dataframe(cursor, items_query)

    except Exception as error:

        print("Database retrieval failed:", error)
        return

    if orders.empty or items.empty:

        print("No completed sales records available.")
        return

    # --------------------------------
    # DATA PREPROCESSING
    # --------------------------------

    orders["order_date"] = pd.to_datetime(
        orders["order_date"], errors="coerce"
    )

    orders["total_amount"] = pd.to_numeric(
        orders["total_amount"], errors="coerce"
    )

    for column in ["quantity", "unit_price", "subtotal"]:

        items[column] = pd.to_numeric(
            items[column], errors="coerce"
        )

    orders = orders.dropna(
        subset=["order_id", "order_date", "total_amount"]
    ).drop_duplicates(subset=["order_id"])

    items = items.dropna(
        subset=[
            "order_item_id",
            "order_id",
            "food_name",
            "quantity",
            "subtotal"
        ]
    ).drop_duplicates(subset=["order_item_id"])

    orders = orders[orders["total_amount"] >= 0]

    items = items[
        (items["quantity"] > 0) &
        (items["unit_price"] >= 0) &
        (items["subtotal"] >= 0)
    ]

    # STANDARDIZE CATEGORY NAMES
    # breakfast, Breakfast and BREAKFAST become Breakfast
    # Extra spaces are also removed.

    items["category"] = (
        items["category"]
        .fillna("Uncategorized")
        .astype(str)
        .str.strip()
        .str.casefold()
        .replace("", "uncategorized")
        .str.title()
    )

    # Keep only items belonging to valid orders
    items = items[
        items["order_id"].isin(orders["order_id"])
    ]

    if orders.empty or items.empty:

        print("No valid sales records after preprocessing.")
        return

    # --------------------------------
    # SALES ANALYSIS
    # --------------------------------

    total_revenue = orders["total_amount"].sum()
    total_orders = orders["order_id"].nunique()
    total_quantity = int(items["quantity"].sum())

    average_order_value = orders["total_amount"].mean()

    item_sales = (
        items.groupby("food_name")["quantity"]
        .sum()
        .sort_values(ascending=False)
    )

    item_revenue = (
        items.groupby("food_name")["subtotal"]
        .sum()
        .sort_values(ascending=False)
    )

    # Combined category revenue
    category_sales = (
        items.groupby("category")["subtotal"]
        .sum()
        .sort_values(ascending=False)
    )

    daily_revenue = (
        orders.groupby(orders["order_date"].dt.date)
        ["total_amount"].sum().sort_index()
    )

    monthly_revenue = (
        orders.groupby(orders["order_date"].dt.to_period("M"))
        ["total_amount"].sum().sort_index()
    )

    popular_items = item_sales.head(5)

    peak_day = daily_revenue.idxmax()
    peak_revenue = daily_revenue.max()

    best_item = item_sales.idxmax()

    # --------------------------------
    # DISPLAY REPORT
    # --------------------------------

    print("\n========================================")
    print("   VCET COLLEGE CANTEEN SALES ANALYSIS")
    print("========================================")

    print("\n--- OVERALL SALES SUMMARY ---")

    print(f"Total Revenue       : Rs. {total_revenue:.2f}")
    print(f"Total Orders        : {total_orders}")
    print(f"Total Quantity Sold : {total_quantity}")
    print(f"Average Order Value : Rs. {average_order_value:.2f}")

    print("\n--- ITEM-WISE SALES ---")
    print(item_sales.to_string())

    print("\n--- TOP 5 POPULAR FOOD ITEMS ---")
    print(popular_items.to_string())

    print("\nBest-Selling Food Item:", best_item)

    print("\n--- CATEGORY-WISE REVENUE ---")
    print(category_sales.to_string())

    print("\n--- MONTHLY REVENUE ---")
    print(monthly_revenue.to_string())

    print("\n--- PEAK SALES DAY ---")
    print("Date:", peak_day)
    print(f"Revenue: Rs. {peak_revenue:.2f}")

    # --------------------------------
    # CHART 1: ITEM-WISE SALES
    # --------------------------------

    plt.figure(figsize=(9, 5))

    item_sales.sort_values().plot(
        kind="barh",
        title="Item-wise Food Sales",
        xlabel="Quantity Sold",
        ylabel="Food Item"
    )

    save_chart("item_wise_sales.png")

    # --------------------------------
    # CHART 2: DAILY REVENUE
    # --------------------------------

    plt.figure(figsize=(9, 5))

    daily_revenue.plot(
        kind="line",
        marker="o",
        title="Daily Canteen Revenue",
        xlabel="Date",
        ylabel="Revenue (Rs.)"
    )

    save_chart("daily_revenue.png")

    # --------------------------------
    # CHART 3: CATEGORY-WISE REVENUE
    # --------------------------------

    plt.figure(figsize=(7, 7))

    category_sales.plot(
        kind="pie",
        autopct="%1.1f%%",
        title="Category-wise Revenue",
        ylabel=""
    )

    save_chart("category_wise_revenue.png")

    # --------------------------------
    # CHART 4: TOP 5 POPULAR ITEMS
    # --------------------------------

    plt.figure(figsize=(9, 5))

    popular_items.sort_values().plot(
        kind="barh",
        title="Top 5 Popular Food Items",
        xlabel="Quantity Sold",
        ylabel="Food Item"
    )

    save_chart("popular_food_items.png")

    # --------------------------------
    # CHART 5: MONTHLY REVENUE
    # --------------------------------

    plt.figure(figsize=(9, 5))

    monthly_revenue.index = monthly_revenue.index.astype(str)

    monthly_revenue.plot(
        kind="bar",
        title="Monthly Revenue Analysis",
        xlabel="Month",
        ylabel="Revenue (Rs.)",
        rot=35
    )

    save_chart("monthly_revenue.png")

    # --------------------------------
    # SAVE REPORTS
    # --------------------------------

    item_sales.to_csv(
        OUTPUT_DIR / "item_sales_report.csv",
        header=["quantity_sold"]
    )

    orders.to_csv(
        OUTPUT_DIR / "orders_report.csv",
        index=False
    )

    print("\nAll sales analysis completed successfully!")
    print("Charts and reports saved in:", OUTPUT_DIR)


if __name__ == "__main__":
    main()
