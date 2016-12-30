package com.example.android.myinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.myinventory.data.ItemContract.ItemEntry;

//Displays list of items that were entered and stored in the app
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ItemCursorAdapter itemCursorAdapter;

    private static final int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find the listview which will be populated with the item data
        ListView itemListView = (ListView) findViewById(R.id.list);

        // Setup an adapter to create a list item for each row of
        // item data in the cursor. There is no item dat yet (until
        // the loader finishes) so pass in null for the Cursor.
        itemCursorAdapter = new ItemCursorAdapter(this, null);
        itemListView.setAdapter(itemCursorAdapter);

        // Setup the item click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                /*
                * Form the content URI that representsthe specific item that was
                * clicked on, by appending the "id" (passed as i/p to this method) onto
                * the ItemEntry.Content_URI. For eg. the URI would be
                * "content://com.example.android.myinventory/items/2" if
                * the item with ID 2 was clicked on
                * */
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

                //Set the Uri on the data field on the intent
                intent.setData(currentItemUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    /*
    *  Helper method to insert hardcoded item data into the db. For debugging purposes only
    * */
    private void insertItem() {
        // Create a content values object where column names are keys,
        // and Veg puff's attributes are values.
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Veg Puff");
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 12);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 100);

        // Insert a new row for VegPuff into the provider using the
        // ContentResolver. Use the PetEntry.ContentUri to indicate
        // that we want to insert into items db table.
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file
        // This adds menu items to the app bar
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch(item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Helper method to delete all items in the db
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns
        // from the table we care about
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY };

        // This loader will execute the ContentProvider's query
        // method on a background thread
        return new CursorLoader(this,
                ItemEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update ItemCursorAdapter with this new cursor
        // containing updated item data
        itemCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        itemCursorAdapter.swapCursor(null);
    }
}
