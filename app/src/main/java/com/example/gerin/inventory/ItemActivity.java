package com.example.gerin.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gerin.inventory.data.ItemContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;

public class ItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentItemUri;

    private static final int EXISTING_ITEM_LOADER = 0;

    private Toolbar toolbar;


    TextView quantityView;
    TextView priceView;
    TextView descriptionView;
    TextView tag1View;
    TextView tag2View;
    TextView tag3View;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        quantityView = (TextView) findViewById(R.id.item_quantity_field);
        priceView = (TextView) findViewById(R.id.item_price_field);
        descriptionView = (TextView) findViewById(R.id.item_description_field);
        tag1View = (TextView) findViewById(R.id.item_tag1_field);
        tag2View = (TextView) findViewById(R.id.item_tag2_field);
        tag3View = (TextView) findViewById(R.id.item_tag3_field);
        imageView = (ImageView) findViewById(R.id.item_image_field);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.item_fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemActivity.this, EditorActivity.class);
                intent.setData(mCurrentItemUri);
                startActivity(intent);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);

    }

    private void deleteItem() {
        int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                    Toast.LENGTH_SHORT).show();
        }

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_current_entry:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_current_entry:
                Intent intent = new Intent(ItemActivity.this, EditorActivity.class);
                intent.setData(mCurrentItemUri);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION,
                ItemContract.ItemEntry.COLUMN_ITEM_TAG1,
                ItemContract.ItemEntry.COLUMN_ITEM_TAG2,
                ItemContract.ItemEntry.COLUMN_ITEM_TAG3,
                ItemContract.ItemEntry.COLUMN_ITEM_IMAGE_PATH};

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
            int descriptionColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION);
            int tag1ColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TAG1);
            int tag2ColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TAG2);
            int tag3ColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_TAG3);
            int imageColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE_PATH);


            String name = data.getString(nameColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            String description = data.getString(descriptionColumnIndex);
            String tag1 = data.getString(tag1ColumnIndex);
            String tag2 = data.getString(tag2ColumnIndex);
            String tag3 = data.getString(tag3ColumnIndex);
            byte[] photo = data.getBlob(imageColumnIndex);

            ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);


            getSupportActionBar().setTitle(name);

            quantityView.setText(Integer.toString(quantity));
            DecimalFormat formatter = new DecimalFormat("#0.00");
            priceView.setText(formatter.format(price));
            descriptionView.setText(description);
            imageView.setImageBitmap(theImage);

            tag1View.setVisibility(View.GONE);
            tag2View.setVisibility(View.GONE);
            tag3View.setVisibility(View.GONE);


            if (!tag1.isEmpty()) {
                tag1View.setText(tag1);
                tag1View.setVisibility(View.VISIBLE);

                if (!tag2.isEmpty()) {
                    tag2View.setText(tag2);
                    tag2View.setVisibility(View.VISIBLE);

                    if (!tag3.isEmpty()) {
                        tag3View.setText(tag3);
                        tag3View.setVisibility(View.VISIBLE);
                        return;
                    }
                    else
                        return;
                } else if (!tag3.isEmpty()) {
                    tag2View.setText(tag3);
                    tag2View.setVisibility(View.VISIBLE);
                    return;
                }
            } else if (!tag2.isEmpty()) {
                tag1View.setText(tag2);
                tag1View.setVisibility(View.VISIBLE);

                if(!tag3.isEmpty()){
                    tag2View.setText(tag3);
                    tag2View.setVisibility(View.VISIBLE);
                    return;
                }
                else
                    return;
            } else if (!tag3.isEmpty()) {
                tag1View.setText(tag3);
                tag1View.setVisibility(View.VISIBLE);
                return;
            }



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        getSupportActionBar().setTitle("");


        Bitmap tempItemBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.product)).getBitmap();

        quantityView.setText("");
        priceView.setText("");
        descriptionView.setText("");
        tag1View.setText("");
        tag2View.setText("");
        tag3View.setText("");
        imageView.setImageBitmap(tempItemBitmap);

    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
