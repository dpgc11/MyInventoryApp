package com.example.android.myinventory;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myinventory.data.ItemContract.ItemEntry;

/**
 * Created by Yogesh on 30-12-2016.
 */

/*
* ItemCursorAdapter is an adapter for a list or grid view
* that uses a Cursor of item data as its data source. This
* adapter knows how to create list items for each row of
* item data in the Cursor
* */
public class ItemCursorAdapter extends CursorAdapter {


    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /*
    * Makes a new blank list item view. No data is set (or bound)
    * to the views yet.
    * */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item using the layout specified in the list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /*
    * This method binds the item data (in the current row pointed to by the cursor) to the
    * given list item layout. For eg, the name for the current item can be set to the
    * name EditView in the list item layout.
    * */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find individual views that we want to modify in the list item layout
//        ImageView itemImageView = (ImageView) view.findViewById(R.id.imageEditView);
        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextview);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextview);
        priceTextView.setMovementMethod(new ScrollingMovementMethod());
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantityTextview);
        ImageView itemImage = (ImageView) view.findViewById(R.id.itemImage);
        Button sellButton = (Button) view.findViewById(R.id.btnSellButton);
        //Find the column index of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int itemImageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_IMAGE);
        int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);

        // Read the item attributes from the cursor for the current item
        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        Uri imageUri = Uri.parse(cursor.getString(itemImageColumnIndex));
        int id = cursor.getInt(idColumnIndex);
        // Update the textviews with the attributes for the current item
        nameTextView.setText(name);
        priceTextView.setText(Double.toString(price));
        quantityTextView.setText(Integer.toString(quantity));
        Glide.with(context).load(imageUri)
                .placeholder(R.drawable.ic_local_grocery_store_black_24dp)
                .crossFade()
                .centerCrop()
                .into(itemImage);

        final Uri currentProductUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();

                if (quantity > 0) {
                    int q = quantity;
                    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, q--);
                    contentResolver.update(currentProductUri, values, null, null);
                    context.getContentResolver().notifyChange(currentProductUri, null);

                } else {
                    Toast.makeText(context, "Item out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
