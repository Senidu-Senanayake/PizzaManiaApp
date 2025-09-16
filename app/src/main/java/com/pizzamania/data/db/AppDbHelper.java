package com.pizzamania.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "pizza_mania.db";
    public static final int DB_VERSION = 5;

    public AppDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // enable foreign key support
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Branch table
        db.execSQL("CREATE TABLE Branch (" +
                "branch_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "address TEXT," +
                "lat REAL," +
                "lng REAL," +
                "phone TEXT)");

        // MenuItem table
        db.execSQL("CREATE TABLE MenuItem (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "price_cents INTEGER NOT NULL," +
                "image_uri TEXT," +
                "category TEXT," +
                "is_available INTEGER DEFAULT 1)");

        // Stock table
        db.execSQL("CREATE TABLE Stock (" +
                "branch_id INTEGER NOT NULL," +
                "item_id INTEGER NOT NULL," +
                "qty INTEGER NOT NULL DEFAULT 0," +
                "PRIMARY KEY(branch_id,item_id))");

        // User table
        db.execSQL("CREATE TABLE User (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "full_name TEXT NOT NULL," +
                "phone TEXT," +
                "email TEXT UNIQUE," +
                "password_hash TEXT NOT NULL," +
                "address TEXT," +
                "lat REAL," +
                "lng REAL)");

        // Order table with tracking_status
        db.execSQL("CREATE TABLE `Order` (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "branch_id INTEGER NOT NULL," +
                "status TEXT NOT NULL," +
                "created_at INTEGER NOT NULL," +
                "total_cents INTEGER NOT NULL," +
                "delivery_address TEXT," +
                "delivery_lat REAL," +
                "delivery_lng REAL," +
                "payment_method TEXT," +
                "tracking_status TEXT DEFAULT 'Placed')");

        // OrderItem table
        db.execSQL("CREATE TABLE OrderItem (" +
                "order_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER NOT NULL," +
                "item_id INTEGER NOT NULL," +
                "qty INTEGER NOT NULL," +
                "unit_price_cents INTEGER NOT NULL)");

        // CartItem table
        db.execSQL("CREATE TABLE CartItem (" +
                "cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "item_id INTEGER NOT NULL," +
                "qty INTEGER NOT NULL," +
                "unit_price_cents INTEGER," +
                "size TEXT," +
                "UNIQUE(user_id, item_id) ON CONFLICT REPLACE)");

        // Seed some sample data
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS OrderItem");
        db.execSQL("DROP TABLE IF EXISTS `Order`");
        db.execSQL("DROP TABLE IF EXISTS CartItem");
        db.execSQL("DROP TABLE IF EXISTS Stock");
        db.execSQL("DROP TABLE IF EXISTS MenuItem");
        db.execSQL("DROP TABLE IF EXISTS Branch");
        db.execSQL("DROP TABLE IF EXISTS User");
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Insert branches
        ContentValues cv = new ContentValues();
        cv.put("name", "Colombo Branch");
        cv.put("address", "Colombo 03");
        cv.put("lat", 6.9271);
        cv.put("lng", 79.8612);
        cv.put("phone", "+94 11 123 4567");
        db.insert("Branch", null, cv);

        cv.clear();
        cv.put("name", "Galle Branch");
        cv.put("address", "Galle Fort");
        cv.put("lat", 6.0260);
        cv.put("lng", 80.2170);
        cv.put("phone", "+94 91 123 4567");
        db.insert("Branch", null, cv);

        // Insert sample menu items
        ContentValues m = new ContentValues();

        // Pizza
        m.put("name", "Classic Margherita");
        m.put("description", "Fresh tomatoes, mozzarella, basil");
        m.put("price_cents", 2800);
        m.put("category", "Pizza");
        m.put("image_uri", "pizza_margherita"); // ✅ image in drawable
        db.insert("MenuItem", null, m);

        m.clear();
        m.put("name", "Spicy Devilled Chicken");
        m.put("description", "Sri Lankan style, extra heat");
        m.put("price_cents", 3500);
        m.put("category", "Pizza");
        m.put("image_uri", "pizza_chicken");
        db.insert("MenuItem", null, m);

        m.clear();
        m.put("name", "BBQ Chicken Pizza");
        m.put("description", "Tangy BBQ sauce, chicken, onions");
        m.put("price_cents", 4000);
        m.put("category", "Pizza");
        m.put("image_uri", "pizza_bbq");
        db.insert("MenuItem", null, m);

        // Appetizers
        m.clear();
        m.put("name", "Garlic Bread");
        m.put("description", "Toasted bread with garlic butter");
        m.put("price_cents", 800);
        m.put("category", "Appetizers");
        m.put("image_uri", "appetizer_garlic_bread");
        db.insert("MenuItem", null, m);

        m.clear();
        m.put("name", "Cheesy Nachos");
        m.put("description", "Crispy nachos with melted cheese");
        m.put("price_cents", 1200);
        m.put("category", "Appetizers");
        m.put("image_uri", "appetizer_nachos");
        db.insert("MenuItem", null, m);

        // Drinks
        m.clear();
        m.put("name", "Coca Cola");
        m.put("description", "Chilled 500ml bottle");
        m.put("price_cents", 500);
        m.put("category", "Drinks");
        m.put("image_uri", "drink_coke");
        db.insert("MenuItem", null, m);

        m.clear();
        m.put("name", "Fresh Orange Juice");
        m.put("description", "Freshly squeezed orange juice");
        m.put("price_cents", 1000);
        m.put("category", "Drinks");
        m.put("image_uri", "drink_orange");
        db.insert("MenuItem", null, m);

        // Desserts
        m.clear();
        m.put("name", "Chocolate Lava Cake");
        m.put("description", "Warm cake with molten chocolate center");
        m.put("price_cents", 1500);
        m.put("category", "Desserts");
        m.put("image_uri", "dessert_lava_cake");
        db.insert("MenuItem", null, m);

        m.clear();
        m.put("name", "Ice Cream Sundae");
        m.put("description", "Vanilla ice cream with chocolate syrup");
        m.put("price_cents", 900);
        m.put("category", "Desserts");
        m.put("image_uri", "dessert_icecream");
        db.insert("MenuItem", null, m);
    }
}
