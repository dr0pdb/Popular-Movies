package com.example.srv_twry.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by srv_twry on 1/6/17.
 */

public class FavouritesContentProvider extends ContentProvider {

    public static final int FAVOURITES =100;
    public static final int FAVOURITE_INDIVIDUAL = 101;

    public static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavouritesDbContract.AUTHORITY,FavouritesDbContract.PATH_FAVOURITES,FAVOURITES);
        uriMatcher.addURI(FavouritesDbContract.AUTHORITY,FavouritesDbContract.PATH_FAVOURITES + "/#",FAVOURITE_INDIVIDUAL);

        return uriMatcher;
    }

    private FavouritesDbHelper favouritesDbHelper;

    @Override
    public boolean onCreate() {
        favouritesDbHelper = new FavouritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = favouritesDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        Uri returnUri ;

        switch (match){
            case FAVOURITES:
                long id = db.insertWithOnConflict(FavouritesDbContract.FavouritesEntry.TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_ABORT);
                if (id>0){
                    returnUri = ContentUris.withAppendedId(uri,id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = favouritesDbHelper.getReadableDatabase();
        Cursor returnCursor;

        int match = uriMatcher.match(uri);

        switch (match){
            case FAVOURITES:
                returnCursor = db.query(FavouritesDbContract.FavouritesEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = favouritesDbHelper.getWritableDatabase();
        int moviesDeleted;

        int match = uriMatcher.match(uri);

        switch (match){
            case FAVOURITE_INDIVIDUAL:
                String stringId = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(FavouritesDbContract.FavouritesEntry.TABLE_NAME,"_id=?", new String[]{stringId});
                Log.v("ContentProvider ","Deleted id="+stringId + " from the database, Number of movies deleted = "+ moviesDeleted);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
