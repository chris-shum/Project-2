package com.example.android.project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ShowMe on 2/6/16.
 */
public class ProjectSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = ProjectSQLiteOpenHelper.class.getCanonicalName();

    private static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "RESTAURANTS_DB";
    public static final String RESTAURANT_LIST_TABLE_NAME = "RESTAURANT_LIST";

    public static final String COL_ID = "_id";
    public static final String COL_RESTAURANT_NAME = "RESTAURANT_NAME";
    public static final String COL_RESTAURANT_PRICE = "PRICE";
    public static final String COL_RESTAURANT_TYPE = "TYPE";
    public static final String COL_NEIGHBORHOOD = "NEIGHBORHOOD";
    public static final String COL_ADDRESS = "ADDRESS";
    public static final String COL_RESTAURANT_DESCRIPTION = "DESCRIPTION";
    public static final String COL_IMAGE = "IMAGE";
    public static final String COL_REVIEW = "REVIEW";
    public static final String COL_FAVORITES = "FAVORITES";
    public static final String[] RESTAURANT_COLUMNS = {COL_ID, COL_RESTAURANT_NAME, COL_RESTAURANT_PRICE, COL_RESTAURANT_TYPE, COL_NEIGHBORHOOD, COL_ADDRESS, COL_RESTAURANT_DESCRIPTION, COL_IMAGE, COL_REVIEW, COL_FAVORITES};

    private static final String CREATE_RESTAURANT_LIST_TABLE =
            "CREATE TABLE " + RESTAURANT_LIST_TABLE_NAME +
                    "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RESTAURANT_NAME + " TEXT, " +
                    COL_RESTAURANT_PRICE + " TEXT, " +
                    COL_RESTAURANT_TYPE + " TEXT, " +
                    COL_NEIGHBORHOOD + " TEXT, " +
                    COL_ADDRESS + " TEXT, " +
                    COL_RESTAURANT_DESCRIPTION + " TEXT, " +
                    COL_IMAGE + " TEXT, " +
                    COL_REVIEW + " TEXT, " +
                    COL_FAVORITES + " INTEGER )";

    private static ProjectSQLiteOpenHelper mInstance;

    public static ProjectSQLiteOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ProjectSQLiteOpenHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private ProjectSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RESTAURANT_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RESTAURANT_LIST_TABLE_NAME);
        this.onCreate(db);
    }

    public Cursor getRestaurantList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                RESTAURANT_COLUMNS, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        return cursor;
    }

    //the three methods below allow for searching multiple terms to narrow down searches
    public String specialSearchesColumns(String query) {
        String[] splitSearchedWords = query.split(" ");
        String columnsSearched = "(" + COL_RESTAURANT_NAME + " LIKE ? OR " + COL_NEIGHBORHOOD + " LIKE ? OR " + COL_ADDRESS + " LIKE ? OR " + COL_RESTAURANT_TYPE + " LIKE ? OR " + COL_RESTAURANT_PRICE + " LIKE ?)"; // c. selections
        int i;
        String newColumnsSearched = columnsSearched;

        if (splitSearchedWords.length < 2) {
            return columnsSearched;
        } else {
            for (i = 1; i < splitSearchedWords.length; i++) {
                newColumnsSearched = newColumnsSearched + " AND " + columnsSearched;
            }
        }
        return newColumnsSearched;
    }

    public String[] specialSearchesQuery(String query) {
        String[] splitSearchedWords = query.split(" ");
        int i = 0;
        String[] searchTerms = new String[]{"%" + splitSearchedWords[i] + "%", "%" + splitSearchedWords[i] + "%", "%" + splitSearchedWords[i] + "%", "%" + splitSearchedWords[i] + "%", "%" + splitSearchedWords[i] + "%"};

        if (splitSearchedWords.length < 2) {
            return searchTerms;
        } else {
            int j = 0;

            ArrayList list = new ArrayList<String>();
            for (j = 0; j < splitSearchedWords.length; j++) {
                String[] multipleSearchTerms = new String[]{"%" + splitSearchedWords[j] + "%", "%" + splitSearchedWords[j] + "%", "%" + splitSearchedWords[j] + "%", "%" + splitSearchedWords[j] + "%", "%" + splitSearchedWords[j] + "%"};
                list.addAll(Arrays.asList(multipleSearchTerms));
            }
            String[] newSearchTerms = new String[list.size()];
            list.toArray(newSearchTerms);
            return newSearchTerms;
        }
    }

    public Cursor searchRestaurantList(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                RESTAURANT_COLUMNS, // b. column names
                specialSearchesColumns(query),
                specialSearchesQuery(query),
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        return cursor;
    }

    //modifies sql search to have favorites included as well
    public Cursor searchRestaurantListFromFavorites(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] newString = new String[(specialSearchesQuery(query).length)+1];
        for (int i = 0; i < (specialSearchesQuery(query).length); i++)
        {newString[i] = specialSearchesQuery(query)[i];}
        newString[specialSearchesQuery(query).length] = "1";
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                RESTAURANT_COLUMNS, // b. column names
                specialSearchesColumns(query)+ " AND "+COL_FAVORITES+" LIKE ?",
                newString,
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        return cursor;
    }


    public String getNameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_RESTAURANT_NAME}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_RESTAURANT_NAME));
        } else {
            return "Description not found";
        }
    }

    public String getPriceById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_RESTAURANT_PRICE}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_RESTAURANT_PRICE));
        } else {
            return "Description not found";
        }
    }

    public String getTypeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_RESTAURANT_TYPE}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_RESTAURANT_TYPE));
        } else {
            return "Description not found";
        }
    }

    public String getNeighborhoodById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_NEIGHBORHOOD}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_NEIGHBORHOOD));
        } else {
            return "Description not found";
        }
    }

    public String getAddressById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_ADDRESS}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_ADDRESS));
        } else {
            return "Description not found";
        }
    }

    public String getDescriptionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_RESTAURANT_DESCRIPTION}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_RESTAURANT_DESCRIPTION));
        } else {
            return "Description not found";
        }
    }

    public String getImageById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_IMAGE}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_IMAGE));
        } else {
            return "Description not found";
        }
    }

    public String getReviewById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_REVIEW}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_REVIEW));
        } else {
            return "Description not found";
        }
    }

    public void updateReviewStatus(String favorites, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = ("UPDATE " + RESTAURANT_LIST_TABLE_NAME + " SET " + COL_REVIEW + " = " + favorites + " WHERE " + COL_ID + " = " + id);
        db.execSQL(command);
    }


    public String getFavoritesById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                new String[]{COL_FAVORITES}, // b. column names
                COL_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_FAVORITES));
        } else {
            return "Description not found";
        }
    }

    public Cursor getFavoritesList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(RESTAURANT_LIST_TABLE_NAME, // a. table
                RESTAURANT_COLUMNS, // b. column names
                COL_FAVORITES + " LIKE ?", // c. selections
                new String[]{"%1%"}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        return cursor;
    }

    public void updateFavoriteStatus(String favorites, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String command = ("UPDATE " + RESTAURANT_LIST_TABLE_NAME + " SET " + COL_FAVORITES + " = " + favorites + " WHERE " + COL_ID + " = " + id);
        db.execSQL(command);
    }
}
