package com.example.android.myinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        //Find individual views that we want to modify in the list item layout
//        ImageView itemImageView = (ImageView) view.findViewById(R.id.imageEditView);
        TextView nameTextView = (TextView) view.findViewById(R.id.nameEditText);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceEditText);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantityEditText);

        //Find the column index of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);

        // Read the item attributes from the cursor for the current item
        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);

        // Update the textviews with the attributes for the current item
        nameTextView.setText(name);
        priceTextView.setText(Double.toString(price));
        quantityTextView.setText(Integer.toString(quantity));

    }
}
