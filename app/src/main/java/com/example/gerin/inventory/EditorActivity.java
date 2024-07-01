package com.example.gerin.inventory;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.gerin.inventory.data.ItemContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    
    private static final int EXISTING_ITEM_LOADER = 0;
    private static final int GALLERY_REQUEST = 0;

    private Uri mCurrentItemUri;
    
    private EditText mNameEditText;
    
    private EditText mQuantityEditText;
    
    private EditText mPriceEditText;
    
    private EditText mTag1EditText;
    
    private EditText mTag2EditText;

    private EditText mTag3EditText;
    
    private EditText mDescriptionEditText;
    
    private ImageView mItemImageView;
    
    public Bitmap mItemBitmap;
    
    public FloatingActionButton fab;
    
    private boolean mItemHasChanged = false;

    private static final int FIVE_MB = 20000000;
    
    private Uri selectedImage = null;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        
        if (mCurrentItemUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_item));
            
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }


        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_item_description);
        mItemImageView = (ImageView) findViewById(R.id.edit_item_image);
        mTag1EditText = (EditText) findViewById(R.id.edit_item_tag1);
        mTag2EditText = (EditText) findViewById(R.id.edit_item_tag2);
        mTag3EditText = (EditText) findViewById(R.id.edit_item_tag3);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mTag1EditText.setOnTouchListener(mTouchListener);
        mTag2EditText.setOnTouchListener(mTouchListener);
        mTag3EditText.setOnTouchListener(mTouchListener);
        fab.setOnTouchListener(mTouchListener);

        mItemBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.product)).getBitmap();

        mItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog d = new Dialog(EditorActivity.this);
                d.setContentView(R.layout.custom_dialog);
                ImageView image_full = (ImageView) d.findViewById(R.id.image_full);
                if(mItemBitmap != null)
                    image_full.setImageBitmap(mItemBitmap);
                d.show();
            }
        });

    }

    private void saveItem() {

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String tag1String = mTag1EditText.getText().toString().trim();
        String tag2String = mTag2EditText.getText().toString().trim();
        String tag3String = mTag3EditText.getText().toString().trim();
        String imageUri;
        if(selectedImage == null)
            imageUri = "null";
        else
            imageUri = selectedImage.toString();   

        int quantityInteger = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantityInteger = Integer.parseInt(quantityString);
        }

        double priceDouble = 0;
        if (!TextUtils.isEmpty(priceString)) {
            priceDouble = Double.parseDouble(priceString);
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mItemBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();

        Log.e("save method","La imagen ha sido convertida");
        
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantityInteger);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, priceDouble);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_DESCRIPTION, descriptionString);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_TAG1, tag1String);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_TAG2, tag2String);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_TAG3, tag3String);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE_PATH, photo);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_URI, imageUri);
        
        if (mCurrentItemUri == null) {

            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
            
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
            case R.id.action_delete_entry:

                showDeleteConfirmationDialog();

                return true;
            case android.R.id.home:

                if(mItemHasChanged)
                    showUnsavedChangesDialog();
                else
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
                ItemContract.ItemEntry.COLUMN_ITEM_IMAGE_PATH,
                ItemContract.ItemEntry.COLUMN_ITEM_URI};
        
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
            int uriColumnIndex = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_URI);

            
            String name = data.getString(nameColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            double price = data.getDouble(priceColumnIndex);
            String description = data.getString(descriptionColumnIndex);
            String tag1 = data.getString(tag1ColumnIndex);
            String tag2 = data.getString(tag2ColumnIndex);
            String tag3 = data.getString(tag3ColumnIndex);
            byte[] photo = data.getBlob(imageColumnIndex);
            String imageURI = data.getString(uriColumnIndex);

            ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);

            
            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantity));
            DecimalFormat formatter = new DecimalFormat("#0.00");
            mPriceEditText.setText(formatter.format(price));
            mDescriptionEditText.setText(description);
            mTag1EditText.setText(tag1);
            mTag2EditText.setText(tag2);
            mTag3EditText.setText(tag3);
            mItemImageView.setImageBitmap(theImage);
            mItemBitmap = theImage;
            if(imageURI == "null")
                selectedImage = null;
            else
                selectedImage = Uri.parse(imageURI);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Bitmap tempItemBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.product)).getBitmap();

        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mDescriptionEditText.setText("");
        mTag1EditText.setText("");
        mTag2EditText.setText("");
        mTag3EditText.setText("");
        mItemImageView.setImageBitmap(tempItemBitmap);
        selectedImage = null;
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

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.return_dialog_msg);
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void insertImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    selectedImage = data.getData();
                    Log.e("editor activity", selectedImage.toString());
                    try {
                        mItemBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        int i = mItemBitmap.getAllocationByteCount();

                        if(i < FIVE_MB) {
                            mItemImageView.setImageBitmap(mItemBitmap);
                            Log.e("Editor Activity", "Imagen convertida exitosamente");
                        }
                        else{
                            mItemBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.product)).getBitmap();
                            selectedImage = null;
                            Log.e("Editor Activity", "Imagen demasiado grande");
                            Toast.makeText(this,"Imagen demasiado grande", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("Editor Activity", String.valueOf(i));
                    } catch (IOException e) {
                        Log.e("onActivityResult", "Alguna excepción " + e);
                    }
                    break;
            }
    }

}
