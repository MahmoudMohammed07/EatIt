package com.android.eatit.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.android.eatit.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "EatItDB.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID", "ProductId", "ProductName", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor cursor = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(new Order(cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("ProductId")),
                        cursor.getString(cursor.getColumnIndex("ProductName")),
                        cursor.getString(cursor.getColumnIndex("Quantity")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Discount"))));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void clearCart() {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

    public void addToFavorites(String foodId) {

        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES('%s');", foodId);
        database.execSQL(query);

    }

    public void removeFromFavorites(String foodId) {

        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s';", foodId);
        database.execSQL(query);

    }

    public boolean isFavorites(String foodId) {

        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId='%s';", foodId);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCart() {
        int count = 0;

        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID = %d", order.getQuantity(), order.getID());
        database.execSQL(query);
    }
}
