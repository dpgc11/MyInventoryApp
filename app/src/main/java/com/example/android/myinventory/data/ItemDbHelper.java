package com.example.android.myinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.myinventory.data.ItemContract.ItemEntry;

/**
 * Created by Yogesh on 30-12-2016.
 */

// Db helper for Inventory app. Manages db creation and version mgmt

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    //Name of the db file
    private static final String DATABASE_NAME = "inventory.db";

    //DB version. If you change the db schema, you must inc the db version
    private static final int DATABASE_VERSION = 1;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is called when the db is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a string that contains the SQL stmts to
        // create the items table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_PRICE + " DOUBLE NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER DEFAULT 0, "
                + ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + ItemEntry.COLUMN_SUPPLIER_CONTACT + " INTEGER, "
                + ItemEntry.COLUMN_SUPPLIER_EMAIL + " TEXT);";

        //Execute the SQL stmt
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    //This is called when the db needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The db is still at version 1, so there's nothing new to be done here.
    }
}
