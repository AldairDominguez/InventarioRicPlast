package com.example.gerin.inventory;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gerin.inventory.Search.CustomSuggestionsAdapter;
import com.example.gerin.inventory.Search.RecyclerTouchListener;
import com.example.gerin.inventory.Search.SearchAdapter;
import com.example.gerin.inventory.Search.SearchResult;
import com.example.gerin.inventory.data.ItemContract;
import com.example.gerin.inventory.data.ItemDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;


import java.util.ArrayList;
import java.util.List;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ItemCursorAdapter mCursorAdapter;

    private static final int ITEM_LOADER = 0;

    private String DEFAULT_SORT_ORDER = null;

    private final String[] options = new String[]{"Alphabetical - Ascending", "Alphabetical - Descending", "Oldest first", "Newest first"};

    private static final int ASCENDING = 0;

    private static final int DESCENDING = 1;

    private static final int OLDEST_FIRST = 2;

    private static final int NEWEST_FIRST = 3;


    private static int sort_choice = 2;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchAdapter adapter;

    MaterialSearchBar materialSearchBar;
    CustomSuggestionsAdapter customSuggestionsAdapter;



    List<SearchResult> searchResultList = new ArrayList<>();

    ItemDbHelper database;

    public int flag1 = 0;

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("catalog", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("catalog", "onResume");
        flag1 = 0;
        loadSearchResultList();
        customSuggestionsAdapter.setSuggestions(searchResultList);
    }

    @Override
    protected void onPause() {
        super.onPause();

        flag1 = 1;
        Log.e("catalog", "onPause");
        materialSearchBar.clearSuggestions();
        materialSearchBar.hideSuggestionsList(); // Oculta la lista de sugerencias
        materialSearchBar.closeSearch(); // Cierra la barra de búsqueda

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        database = new ItemDbHelper(this);


        materialSearchBar = (MaterialSearchBar) findViewById(R.id.search_bar1);
        materialSearchBar.setCardViewElevation(0);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customSuggestionsAdapter = new CustomSuggestionsAdapter(inflater);

        if (flag1 == 0) {
            Log.e("catalog", "tried to set adapter");
            loadSearchResultList();
            customSuggestionsAdapter.setSuggestions(searchResultList);
            materialSearchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);
//          ^---- this line causes problems when starting a new intent
        }

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                materialSearchBar.disableSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (flag1 == 0) {
                    List<SearchResult> newSuggestions = loadNewSearchResultList();
                    customSuggestionsAdapter.setSuggestions(newSuggestions);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (flag1 == 0) {
//                    if (!materialSearchBar.isSuggestionsVisible()) {
//                        if (s.toString() != null && !s.toString().isEmpty()) {
//                            materialSearchBar.enableSearch();
//                        }
//                    }
//                }
            }
        });
        // Useless
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
//                if (!enabled)
//                    adapter = new SearchAdapter(getBaseContext(), database.getResult());
////                    recyclerView.setAdapter(null);i
//                if(enabled) {
//                    materialSearchBar.enableSearch();
//                    materialSearchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);
//                }
//                else {
//                    materialSearchBar.clearSuggestions();
//                    materialSearchBar.disableSearch();
//                }
//                if (flag1 == 0) {
//                    if (enabled)
//                        materialSearchBar.showSuggestionsList();
//                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                List<SearchResult> testResult1 = loadNewSearchResultList();
                if(testResult1.isEmpty()) {
                    Toast.makeText(getBaseContext(), "No se han encontrado resultados",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                SearchResult testResult2 = testResult1.get(0);
                String testResult4 = testResult2.getName();
                int testResult3 = testResult2.getId();

                if(text.toString().toLowerCase().equals(testResult4.toLowerCase())){
//                    Toast.makeText(getBaseContext(), "Search Success!",
//                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CatalogActivity.this, ItemActivity.class);

                    Uri currentPetUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, testResult3);
                    // Set the URI on the data field of the intent
                    intent.setData(currentPetUri);

                    flag1 = 1;
                    materialSearchBar.clearSuggestions();
                    materialSearchBar.hideSuggestionsList(); // Oculta la lista de sugerencias
                    materialSearchBar.closeSearch(); // Cierra la barra de búsqueda

                    startActivity(intent);

                }
                else{
                    Toast.makeText(getBaseContext(), "No se han encontrado resultados",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onButtonClicked(int buttonCode) {

//                recyclerView.setAdapter(null);

                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        Log.e("catalog", "button clicked");
                        materialSearchBar.clearSuggestions();
                        materialSearchBar.hideSuggestionsList(); // Oculta la lista de sugerencias
                        materialSearchBar.closeSearch(); // Cierra la barra de búsqueda
                        break;
                    case MaterialSearchBar.BUTTON_SPEECH:
                        break;
                    default:
                        break;
                }
            }
        });
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {

            @Override
            public void OnItemClickListener(int position, View v) {
                Log.e("catalog", "on item click");
                Log.e("on item click", String.valueOf(position));
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                Log.e("catalog", "on item delete");
            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView searchrv = findViewById(R.id.mt_recycler);
        searchrv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), searchrv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                List<SearchResult> testResult1 = loadNewSearchResultList();
                SearchResult testResult2 = testResult1.get(position);
                int testResult3 = testResult2.getId();



                Intent intent = new Intent(CatalogActivity.this, ItemActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, testResult3);
                intent.setData(currentPetUri);

                Log.e("catalog", "list item click");
                flag1 = 1;
                materialSearchBar.clearSuggestions();

                materialSearchBar.hideSuggestionsList(); // Oculta la lista de sugerencias
                materialSearchBar.closeSearch(); // Cierra la barra de búsqueda

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                TextView tv = (TextView) view.findViewById(R.id.search_text);
                materialSearchBar.setText(String.valueOf(tv.getText()));
            }
        }));

        ListView itemListView = (ListView) findViewById(R.id.catalog_list);

        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        mCursorAdapter = new ItemCursorAdapter(this, null, 0);
        itemListView.setAdapter(mCursorAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.catalog_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, ItemActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);

                Log.e("catalog", "list item click");
                flag1 = 1;
                materialSearchBar.clearSuggestions();
                materialSearchBar.hideSuggestionsList(); // Oculta la lista de sugerencias
                materialSearchBar.closeSearch(); // Cierra la barra de búsqueda

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ITEM_LOADER, null, this);

    }

    private void startSearch(String s) {

//        adapter = new SearchAdapter(this, database.getResultNames(s));
    }

    private void loadSearchResultList() {
        searchResultList = database.getResult();
    }

    private List<SearchResult> loadNewSearchResultList() {
        MySuggestions.newSuggestions = new ArrayList<>();
        MySuggestions.newSuggestions_id = new ArrayList<Integer>(10);
        loadSearchResultList();
        int i = 0;
        for (SearchResult searchResult : searchResultList) {
            if (searchResult.getName().toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                MySuggestions.newSuggestions.add(searchResult);
                MySuggestions.newSuggestions_id.add(searchResult.getId());
                MySuggestions.moreresults[i] = searchResult.getId();
                i++;

                Log.d("_id", String.valueOf(searchResult.getId()));
            }
        }

        return MySuggestions.newSuggestions;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete_all_entries:

                showDeleteAllConfirmationDialog();
                return true;
            case R.id.action_sort_all_entries:

                showSortConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllItems();
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

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI, null, null);
        if (rowsDeleted >= 0) {
            Toast.makeText(this, "Todos los elementos eliminados", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Se produjo un error: Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSortConfirmationDialog() {

        String[] options = {
                getString(R.string.alphabetical_ascending),
                getString(R.string.alphabetical_descending),
                getString(R.string.oldest_first),
                getString(R.string.newest_first)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RadioDialogTheme);
        builder.setTitle(R.string.sort_dialog_msg);
        builder.setSingleChoiceItems(options, sort_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortAllItems(which);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sortAllItems(int choice) {

        switch (choice) {
            case ASCENDING:
                DEFAULT_SORT_ORDER = ItemContract.ItemEntry.COLUMN_ITEM_NAME + " COLLATE NOCASE ASC";
                sort_choice = 0;
                break;
            case DESCENDING:
                DEFAULT_SORT_ORDER = ItemContract.ItemEntry.COLUMN_ITEM_NAME + " COLLATE NOCASE DESC";
                sort_choice = 1;
                break;
            case OLDEST_FIRST:
                DEFAULT_SORT_ORDER = null;
                sort_choice = 2;
                break;
            case NEWEST_FIRST:
                DEFAULT_SORT_ORDER = ItemContract.ItemEntry._ID + " DESC";
                sort_choice = 3;
                break;

        }

        getLoaderManager().restartLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE};

        Log.e("onCreateLoader", "DEFAULT_SORT_ORDER = " + DEFAULT_SORT_ORDER);

        return new CursorLoader(this,
                ItemContract.ItemEntry.CONTENT_URI,
                projection,
                null,
                null,
                DEFAULT_SORT_ORDER);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


}
