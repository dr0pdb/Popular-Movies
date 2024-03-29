package com.example.srv_twry.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by srv_twry on 1/6/17.
 * The contract class for the Favourites database
 */

public class FavouritesDbContract {

    //The constants for the content provider
    public static final String AUTHORITY= "com.example.srv_twry.popularmovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVOURITES = "favourites";

    public static final class FavouritesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        public static final String TABLE_NAME = "favouritemovies";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_TITLE = "movieTitle";

        //Poster will be saved in byte Stream
        public static final String COLUMN_POSTER = "poster";
    }
}
