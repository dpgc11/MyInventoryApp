package com.example.android.myinventory.data;

import android.content.ClipData;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.myinventory.data.ItemContract.ItemEntry;

/**
 * Created by Yogesh on 30-12-2016.
 */

public class ItemProvider extends ContentProvider {

    //Uri matcher code for the content URI for the items table
    private static final int ITEMS = 100;

    // Uri matcher code for the content URI
    // for a single item in the items table
    private static final int ITEM_ID = 101;

    /*
    * UriMatcher object to match a content URI to a corresponding code
    * The i/p passed into the constructor represents the code to return
    * for the root URI. It's common to use NO_MATCH as the i/p for this case.
    * */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        /*
        * The calls to addUri() go here, for all of the content URI
        * patterns that the provider should recognize. All paths to the
        * UriMatcher have a corresponding code to return when a
        * match is found.
        * */
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        /*
        * The content URI of the form "content://com.example.android.myinventory/items/#"
        * will map to the integer code #ITEM_ID. This URI is used to
        * provide access to ONE single row of the items table.
        * In this case, the '#' wildcard is used where '#' can be
        * substituted for an integer
        * */
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);

    }

    // DB helper object
    private ItemDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable db
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to
        // a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                /*
                * For the ITEMS code, query the items table
                * directly with the given projection,
                * selecton, selection args and sort order.
                * The cursor could contain multiple rows of
                * the items table
                * */
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs
                        , null, null, sortOrder);
                break;
            case ITEM_ID:
                /*
                * For the ITEM_ID code, etract out the ID from the URI.
                * For an eg. URI suck as "content://com.example.android.myinventory/items/3",
                * the selection will be "_id=?" and the selection arg will be
                * String array containing the actual ID of 3 in this case.
                * For every "?" in the selection, we need to have an element in the
                * selection args that will fill in the "?". Since we have 1
                * ? in the selection, we have 1 String in the selection
                * args String array
                * */
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                //This will perform a query on the items table where the _id equals 3
                // to return a Cursor cntaining that row of the table
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs
                        , null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);

        }
        /*
        * Set notification URI on the cursor, so we know
        * what content URI the Cursor was created for.
        * If the data at this URI changes, then we know
        * we need to update the Cursor.
        * */
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /*
    * Insert an item in the db with the given values. Return the new content URI
    * for that specific row in the db
    * */
    private Uri insertItem(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
        double price = values.getAsDouble(ItemEntry.COLUMN_ITEM_PRICE);
        String supplierName = values.getAsString(ItemEntry.COLUMN_SUPPLIER_NAME);
        int supplierNumber = values.getAsInteger(ItemEntry.COLUMN_SUPPLIER_CONTACT);

        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        if (price == 0) {
            throw new IllegalArgumentException("Item must have a price");
        }

        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier requires a name");
        }

        if (supplierNumber == 0) {
            throw new IllegalArgumentException("Supplier requires a number");
        }
        // Get writeable db
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the item with the given values
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(ItemEntry.TABLE_NAME, null, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
