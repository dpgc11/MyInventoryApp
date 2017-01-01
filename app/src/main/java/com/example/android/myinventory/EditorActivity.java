package com.example.android.myinventory;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.myinventory.data.ItemContract.ItemEntry;

import com.example.android.myinventory.data.ItemDbHelper;

/**
 * Created by Yogesh on 29-12-2016.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PET_LOADER = 0;

    // Content URI for the existing item (null if new)
    private Uri mCurrentItemURi;

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierContactText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mPriceEditText = (EditText) findViewById(R.id.priceEditText);
        mQuantityEditText = (EditText) findViewById(R.id.quantityEditText);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplierNameEditText);
        mSupplierContactText = (EditText) findViewById(R.id.supplierPhoneEditText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                // do nothing for now
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {


        String name = mNameEditText.getText().toString().trim();
        double price = Double.parseDouble(mPriceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierContact = mSupplierContactText.getText().toString().trim();

        ContentValues values = new ContentValues();

        values.put(ItemEntry.COLUMN_ITEM_NAME, name);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(ItemEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ItemEntry.COLUMN_SUPPLIER_CONTACT, supplierContact);

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, "Saving item failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
