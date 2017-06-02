package com.example.srv_twry.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by srv_twry on 1/6/17.
 * The Database helper class for the Favourites Database
 */

public class FavouritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME= "PopularMovies.db";
    private static final int VERSION = 1;

    public FavouritesDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + FavouritesDbContract.FavouritesEntry.TABLE_NAME + " (" +
                FavouritesDbContract.FavouritesEntry._ID                + " INTEGER PRIMARY KEY, " +
                FavouritesDbContract.FavouritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavouritesDbContract.FavouritesEntry.COLUMN_TITLE    + " TEXT NOT NULL, "+
                FavouritesDbContract.FavouritesEntry.COLUMN_POSTER   + " BLOB);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesDbContract.FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
