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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.android.project.setup.DBAssetHelper;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewMain;
    private ListView mListViewResults;
    private ProjectSQLiteOpenHelper mHelper;
    private CursorAdapter mCursorAdapter;
    ImageView mTestImage;
    LinearLayout mLayout;
    String mainText;
    ImageView mBigButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //two lines given for setup
        DBAssetHelper dbSetup = new DBAssetHelper(MainActivity.this);
        dbSetup.getReadableDatabase();

        mTextViewMain = (TextView) findViewById(R.id.textViewOnMainActivityMainText);
        mListViewResults = (ListView) findViewById(R.id.listViewOnMainActivitySearchResults);
        mHelper = ProjectSQLiteOpenHelper.getInstance(MainActivity.this);
        mTestImage = (ImageView) findViewById(R.id.testImageView);
        mLayout = (LinearLayout) findViewById(R.id.layoutMain);
        mBigButton = (ImageView) findViewById(R.id.bigButton);

        mTestImage.setImageResource(R.drawable.main);
        mBigButton.setImageResource(R.drawable.button);

        mainText = "Welcome to Manhattan Eats!  " +
                "\nLet's get you fat!" +
                "\n\nBrowse through our restaurant list below" +
                "\nOr use the magnifying glass above to search!" +
                "\n\nYou can search by" +
                "\n restaurant name, neighborhood, " +
                "\n address, type of food, or price " +
                "\n (cheap, moderate, or expensive).";
        mTextViewMain.setText(mainText);

        //the below gets the data from database and adapter sets it to display on the listview.
        Cursor cursor = mHelper.getRestaurantList();
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.custom_layout, cursor, new String[]{ProjectSQLiteOpenHelper.COL_RESTAURANT_NAME}, new int[]{android.R.id.text1}, 0);
        mListViewResults.setAdapter(mCursorAdapter);

        //searches and displays results
        handleIntent(getIntent());


        //onItemClickListener gets the ID# of the data being clicked, opens up details activity, and passes the ID# of the data.
        mListViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mCursorAdapter.getCursor();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                cursor.moveToPosition(position);
                int theIDNumber = cursor.getInt(cursor.getColumnIndex(ProjectSQLiteOpenHelper.COL_ID));
                intent.putExtra("id", theIDNumber);
                startActivity(intent);
            }
        });

        //removes the main activity image upon clicking
        mTestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestImage.setVisibility(View.GONE);
            }
        });

        //an extra feature I decided to add.  This moves the cursor to a random position and opens said position.
        //After looking over the code again, I realize that this method only works as long as the keyid#s are sequential,
        // need to figure out a way to find the keyid#s and randomize that eventually.
        mBigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = mCursorAdapter.getCursor();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Random rand = new Random();
                int randomNumber = rand.nextInt(cursor.getCount() + 1);
                cursor.moveToPosition(randomNumber);
                intent.putExtra("id", randomNumber);
                startActivity(intent);
//                Log.d("test", String.valueOf(cursor.getCount()));
            }
        });


    }

    //search settings setup as shown in lesson
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    //makes the favorites button in the toolbar functional, opens favorites intent when button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favoritesButton:
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    //all the functions of the search and refreshing the list with the results
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Cursor cursor = mHelper.searchRestaurantList(query);
            mCursorAdapter.changeCursor(cursor);
            if (cursor.getCount() == 0) {
                mTextViewMain.setText("Your search for \"" + query + "\" yielded no results.");
                mTestImage.setVisibility(View.GONE);
                mBigButton.setVisibility(View.GONE);
                mLayout.setBackgroundResource(R.drawable.badsearch);
            } else {
                mTextViewMain.setText("Search results for \"" + query + "\":");
                mTestImage.setVisibility(View.GONE);
                mBigButton.setVisibility(View.GONE);
                mLayout.setBackgroundResource(R.drawable.faded);
            }
        }
    }
}
