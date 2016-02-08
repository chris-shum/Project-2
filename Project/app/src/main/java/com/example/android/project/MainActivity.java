package com.example.android.project;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.android.project.setup.DBAssetHelper;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewMain;
    private ListView mListViewResults;
    private ProjectSQLiteOpenHelper mHelper;
    private CursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ignore the two lines below, they are for setup
        DBAssetHelper dbSetup = new DBAssetHelper(MainActivity.this);
        dbSetup.getReadableDatabase();

        mTextViewMain = (TextView) findViewById(R.id.textViewOnMainActivityMainText);
        mListViewResults = (ListView) findViewById(R.id.listViewOnMainActivitySearchResults);
        mHelper = ProjectSQLiteOpenHelper.getInstance(MainActivity.this);

        mTextViewMain.setText("Restaurants List:");

        Cursor cursor = mHelper.getRestaurantList();

        mCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{ProjectSQLiteOpenHelper.COL_RESTAURANT_NAME}, new int[]{android.R.id.text1}, 0);
        mListViewResults.setAdapter(mCursorAdapter);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Cursor cursor = mHelper.searchRestaurantList(query);
            mCursorAdapter.changeCursor(cursor);
            mTextViewMain.setText("Search results for \"" + query + "\":");
        }
    }
}