package com.example.android.myinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yogesh on 29-12-2016.
 */

/*
* API Contract for the Inventory App
* */
public final class ItemContract {

    /*
    * To prevent someone from accidently instantiating the contract class,
    * give it an empty private constructor
    * */

    private ItemContract() {
    }

    /*
    * The 'Content authority' is a name for the entire content provider, similar to the
    * relationship between a domain name and its website. A convenient string to use for the
    * content authority is the package name for the app, which is guaranteed to be unique
    * on the device.
    * */

    public static final String CONTENT_AUTHORITY = "com.example.android.myinventory";

    /*
    * Use CONTENT AUTHORITY to create the base of all URI's which apps will use to contact
    * the content provider
    * */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
    * Possible path (appended to base content URI for possible URI's)
    * For instance, content://com.example.android.inventory/items is a valid path for
    * looking at inventory data. content://com.example.android.inventory/owner will fail,
    * as the ContentProvider hasn't been given any info on what too do with "owner"
    * */
    public static final String PATH_ITEMS = "items";

    /*
    * Inner class that defines constant values for the items db table.
    * Each entry in the table represents a single item
    * */

    public static final class ItemEntry implements BaseColumns {

        //The content Uri to access the item data in the provider

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        //The MIME type of the contentUri for a list of items
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        //The MIME type of the ContentUri for a single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Name of the db for items
        public static final String TABLE_NAME = "items";

        //Unique ID number for the item(only for the use in the db table)
        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_SUPPLIER_CONTACT = "supplierNumber";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplierEmail";

    }


}
