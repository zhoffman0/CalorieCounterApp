package com.example.caloriecounter;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {
    private static final String databaseName = "diet";
    private static final int databaseVersion = 29;

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    /* 03 Class DbAdapter ---------------------------------- */
    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    /* 04 DatabaseHelper ------------------------------------ */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, databaseName, null, databaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                //creates goal table
                db.execSQL("CREATE TABLE IF NOT EXISTS goal (" +
                        " goal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " goal_current_weight INT, "+
                        " goal_target_weight INT, "+
                        " goal_weekly_goal VARCHAR, "+
                        " goal_date DATE, "+
                        " goal_calories INT, "+
                        " goal_proteins INT, "+
                        " goal_carbs INT, "+
                        " goal_fat INT, "+
                        " goal_notes VARCHAR);");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            try{
                // Create tables
                db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                        " user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " user_email VARCHAR," +
                        " user_password VARCHAR, " +
                        " user_salt VARCHAR, " +
                        " user_alias VARCHAR," +
                        " user_dob DATE, " +
                        " user_gender INT, " +
                        " user_location VARHCAR, " +
                        " user_height INT, " +
                        " user_activity_level INT, " +
                        " user_weight INT, " +
                        " user_target_weight INT, " +
                        " user_target_weight_level INT," +
                        " user_mesurment VARHCAR, " +
                        " user_last_seen TIME," +
                        " user_note VARCHAR);");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            try{
                db.execSQL("CREATE TABLE IF NOT EXISTS food_diary_cal_eaten (" +
                        " cal_eaten_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " cal_eaten_date DATE, " +
                        " cal_eaten_meal_no INT, " +
                        " cal_eaten_calories INT, " +
                        " cal_eaten_proteins INT, " +
                        " cal_eaten_carbs INT, " +
                        " cal_eaten_fat INT);");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            try{
                db.execSQL("CREATE TABLE IF NOT EXISTS food_diary (" +
                        " fd_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " fd_date DATE," +
                        " fd_meal_number INT," +
                        " fd_food_id INT," +
                        " fd_serving_size DOUBLE," +
                        " fd_serving_mesurment VARCHAR," +
                        " fd_calories_calculated DOUBLE," +
                        " fd_protein_calculated DOUBLE," +
                        " fd_carbohydrates_calculated DOUBLE," +
                        " fd_fat_calculated DOUBLE," +
                        " fd_fat_meal_id INT);");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try{
                db.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                        " category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " category_name VARCHAR," +
                        " category_parent_id INT," +
                        " category_icon VARCHAR," +
                        " category_note VARCHAR);");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS food (" +
                        " food_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " food_name VARCHAR," +
                        " food_manufactor_name VARCHAR," +
                        " food_serving_size DOUBLE," +
                        " food_serving_mesurment VARCHAR," +
                        " food_serving_name_number DOUBLE," +
                        " food_serving_name_word VARCHAR," +
                        " food_calories DOUBLE," +
                        " food_proteins DOUBLE," +
                        " food_carbohydrates DOUBLE," +
                        " food_fat DOUBLE," +
                        " food_calories_calculated DOUBLE," +
                        " food_proteins_calculated DOUBLE," +
                        " food_carbohydrates_calculated DOUBLE," +
                        " food_fat_calculated DOUBLE," +
                        " food_user_id INT," +
                        " food_barcode VARCHAR," +
                        " food_category_id INT," +
                        " food_thumb VARCHAR," +
                        " food_image_a VARCHAR," +
                        " food_image_b VARCHAR," +
                        " food_image_c VARCHAR," +
                        " food_notes VARCHAR);");



            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            // ! All tables that are going to be dropped need to be listed here

            db.execSQL("DROP TABLE IF EXISTS users");
            db.execSQL("DROP TABLE IF EXISTS food_diary_cal_eaten");
            db.execSQL("DROP TABLE IF EXISTS food_diary");
            db.execSQL("DROP TABLE IF EXISTS categories");
            db.execSQL("DROP TABLE IF EXISTS food");
            onCreate(db);

            String TAG = "Tag";
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

        } // end public void onUpgrade
    } // DatabaseHelper


    /* 05 Open database --------------------------------------------------------- */
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /* 06 Close database --------------------------------------------------------- */
    public void close() {
        DBHelper.close();
    }

    /* 07 Quote smart ------------------------------------------------------------ */
    public String quoteSmart(String value){
        // Is numeric?
        boolean isNumeric = false;
        try {
            double myDouble = Double.parseDouble(value);
            isNumeric = true;
        }
        catch(NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }
        if(isNumeric == false){
            // Escapes special characters in a string for use in an SQL statement
            if (value != null && value.length() > 0) {
                value = value.replace("\\", "\\\\");
                value = value.replace("'", "\\'");
                value = value.replace("\0", "\\0");
                value = value.replace("\n", "\\n");
                value = value.replace("\r", "\\r");
                value = value.replace("\"", "\\\"");
                value = value.replace("\\x1a", "\\Z");
            }
        }

        value = "'" + value + "'";

        return value;
    }
    public double quoteSmart(double value) {
        return value;
    }
    public int quoteSmart(int value) {
        return value;
    }

    /* 08 Insert data ------------------------------------------------------------ */
    public void insert(String table, String fields, String values){

        try {
            db.execSQL("INSERT INTO " + table +  "(" + fields + ") VALUES (" + values + ")");
        }
        catch(SQLiteException e){
            System.out.println("Insert error: " + e.toString());
        }
    }

    /* 09 Count ------------------------------------------------------------------ */
    public int count(String table)
    {
        try {
            Cursor mCount = db.rawQuery("SELECT COUNT(*) FROM " + table + "", null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            return count;
        }
        catch(SQLiteException e){
            return -1;
        }

    }

    /* 10 Select ----------------------------------------------------------------- */
    public Cursor selectPrimaryKey(String table, String primaryKey, long rowId, String[] fields) throws SQLException {
        /* Select example:
        long row = 3;
        String fields[] = new String[] {
                "food_id",
                "food_name",
                "food_manufactor_name"
        };
        Cursor c = db.select("food", "food_id", row, fields);
        displayRecordFromNotes(c);
         */

        Cursor mCursor = db.query(table, fields, primaryKey + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /* 11 Update ----------------------------------------------------------------- */
    public boolean update(String table, String primaryKey, long rowId, String field, String value) {
        /* Update example:
        long id = 1;
        String value = "xxt@doesthiswork.com";
        String valueSQL = db.quoteSmart(value);
        db.update("users", "user_id", id, "user_email", valueSQL);
         */

        // Remove first and last value of value
        value = value.substring(1, value.length()-1); // removes ' after running quote smart

        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
    public boolean update(String table, String primaryKey, long rowId, String field, double value) {
        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
    public boolean update(String table, String primaryKey, long rowId, String field, int value) {
        ContentValues args = new ContentValues();
        args.put(field, value);
        return db.update(table, args, primaryKey + "=" + rowId, null) > 0;
    }
}

