package com.example.android.myinventory;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myinventory.data.ItemContract.ItemEntry;

import java.io.File;

/**
 * Created by Yogesh on 29-12-2016.
 */

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PET_LOADER = 0;
    private static final int STORAGE_REQUEST_PERMINNISON = 21;

    private static int PICK_PHOTO_REQUEST = 1;
    private String mCurrentPhotoUri = "no images";

    // Content URI for the existing item (null if new)
    private Uri mCurrentItemUri;

    private boolean mItemHasChanged = false;

    private EditText mNameEditText;
    private Button mUploadPhoto;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierContactText;
    private ImageView mImageView;

    /*
    * OnTouchListener that listens for any user touches
    * on a View, implying that they are modifying
    * the view, and we change the mItemHasChanged
    * boolean to true
    * */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    public void onPhotoProductUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //We are on M or above so we need to ask for runtime permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                invokeGetPhoto();
            } else {
                // we are here if we do not all ready have permissions
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permisionRequest, STORAGE_REQUEST_PERMINNISON);
            }
        } else {
            //We are on an older devices so we dont have to ask for runtime permissions
            invokeGetPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_PERMINNISON
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //We got a GO from the user

        } else {
            Toast.makeText(this, "NEed permissions to access photo", Toast.LENGTH_LONG).show();
        }
    }

    //    public void selectImageFromDevice(View view) {
    //        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
    //                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    //
    //        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    //    }

    private void invokeGetPhoto() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
    }

    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        try {
    //            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
    //                && data != null) {
    //                Uri selectedImage = data.getData();
    //                String[] filePathColumn = { MediaStore.Images.Media.DATA };
    //                Cursor cursor = getContentResolver().query(selectedImage,
    //                        filePathColumn, null, null, null);
    //                cursor.moveToFirst();
    //                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    //                String picturePath = cursor.getString(columnIndex);
    //                cursor.close();
    //                mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    //
    //            }
    //        } finally {
    //
    //        }
    //    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                //If we are here, everything processed successfully and we have an Uri data
                Uri mProductPhotoUri = data.getData();
                mCurrentPhotoUri = mProductPhotoUri.toString();

                //We use Glide to import photo images
                Glide.with(this).load(mProductPhotoUri).crossFade().fitCenter().into(mImageView);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mPriceEditText = (EditText) findViewById(R.id.priceEditText);
        mQuantityEditText = (EditText) findViewById(R.id.quantityEditText);
        mSupplierNameEditText = (EditText) findViewById(R.id.supplierNameEditText);
        mSupplierContactText = (EditText) findViewById(R.id.supplierPhoneEditText);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mUploadPhoto = (Button) findViewById(R.id.selectImageButton);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierContactText.setOnTouchListener(mTouchListener);

        mUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoProductUpdate(view);
            }
        });

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle("Add a new item");
            invalidateOptionsMenu();
        } else {
            setTitle("Update item");
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
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
                return true;
            case R.id.action_delete:
                // do nothing for now
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method is called when the back button is pressed
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Show a dialog that warns the user there are unsaved changes
    // that will be lost if they continue leaving the editor
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Prompt th user t confirm deletion of the item
    private void showDeleteConfirmationDialog() {
        //Create an AlertDialogBuilder and set the msg
        // and click listeners for the +ve and -ve buttons

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.string_delete_dialog_msg);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        // Only perform the delete if this is an existing item
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Delete successfull", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void saveItem() {

        String name = mNameEditText.getText().toString().trim();
        double price = Double.parseDouble(mPriceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        int supplierContact = Integer.parseInt(mSupplierContactText.getText().toString().trim());

        if (mCurrentItemUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(
                String.valueOf(price)) &&
                TextUtils.isEmpty(String.valueOf(quantity)) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(String.valueOf(supplierContact))) {
            return;
        }

        ContentValues values = new ContentValues();

        values.put(ItemEntry.COLUMN_ITEM_NAME, name);

        if (TextUtils.isEmpty(mPriceEditText.getText().toString())) {
            price = 0.0;
        }
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);
        if (TextUtils.isEmpty(mQuantityEditText.getText().toString())) {
            quantity = 0;
        }
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(ItemEntry.COLUMN_SUPPLIER_NAME, supplierName);
        if (TextUtils.isEmpty(mSupplierContactText.getText().toString())) {
            supplierContact = 00000;
        }
        values.put(ItemEntry.COLUMN_SUPPLIER_CONTACT, supplierContact);

        values.put(ItemEntry.COLUMN_IMAGE, mCurrentPhotoUri);

        if (mCurrentItemUri == null) {

            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Saving item failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Item update failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Item update successfull", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID, ItemEntry.COLUMN_ITEM_NAME, ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY, ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemEntry.COLUMN_SUPPLIER_CONTACT, ItemEntry.COLUMN_IMAGE
        };

        return new CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            //Find the column index of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_NAME);
            int supplierContactColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_CONTACT);
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_IMAGE);
            //            int supplierEmailColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_EMAIL);
            // Read the item attributes from the cursor for the current item
            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierContactColumnIndex);
            mCurrentPhotoUri = cursor.getString(imageColumnIndex);
            //            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierContactText.setText(Integer.toString(supplierPhone));
            Glide.with(this).load(mCurrentPhotoUri)
                    .placeholder(R.drawable.default_image)
                    .crossFade()
                    .fitCenter()
                    .into(mImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText(String.valueOf(0.0));
        mQuantityEditText.setText(String.valueOf(0));
        mSupplierNameEditText.setText("");
        mSupplierContactText.setText(String.valueOf(0));
    }
}
