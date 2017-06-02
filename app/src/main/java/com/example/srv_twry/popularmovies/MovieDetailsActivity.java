package com.example.srv_twry.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.srv_twry.popularmovies.Data.FavouritesDbContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements MovieVideoAdapter.MovieVideoOnClickListener{

    private static final String TAG = MovieListActivity.class.getSimpleName();

    String poster_path;
    String overview;
    String releaseDate;
    String title;
    double voteAverage;
    int id;     //id associated with the movie
    int db_id_movie;    //stores the _id of the movie when it is added to favourites database.

    ImageView posterImageView;
    TextView titleTextView;
    TextView voteAverageTotalTextView;
    TextView releaseDateTextView;
    TextView descriptionTextView;
    RecyclerView trailersRecyclerView;
    RecyclerView reviewRecyclerView;
    Bitmap posterBitmap;   //This will be used to save the poster in database if the user marks it as favourite.
    public static ArrayList<MovieVideo> movieVideoArrayList;
    public static ArrayList<MovieReview> movieReviewArrayList;
    boolean isFavourite;
    MenuItem isFavouriteItem;
    MenuItem setFavouriteItem;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setTitle(getResources().getString(R.string.Movie_Detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        posterImageView = (ImageView) findViewById(R.id.iv_poster_details_activity);
        titleTextView = (TextView) findViewById(R.id.tv_title_details_activity);
        voteAverageTotalTextView = (TextView) findViewById(R.id.tv_vote_average_count_details_activity);
        releaseDateTextView = (TextView) findViewById(R.id.tv_releasedate_details_activity);
        descriptionTextView = (TextView) findViewById(R.id.tv_description_details_activity);
        trailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers_details_activity);
        reviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews_details_activity);
        movieVideoArrayList=new ArrayList<>();
        movieReviewArrayList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            poster_path = bundle.getString(MovieListActivity.POSTER_PATH_KEY);
            overview = bundle.getString(MovieListActivity.OVERVIEW_KEY);
            releaseDate = bundle.getString(MovieListActivity.RELEASE_DATE_KEY);
            id=bundle.getInt(MovieListActivity.ID_KEY);

            try{
                releaseDate = "Year of Release: "+releaseDate.substring(0,4);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e(MovieDetailsActivity.class.getName(),"Wrong Date");
            }

            title = bundle.getString(MovieListActivity.TITLE_KEY);
            voteAverage = bundle.getDouble(MovieListActivity.VOTE_AVERAGE_KEY);

                Picasso.with(getBaseContext()).load(poster_path).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        posterBitmap = bitmap;
                        posterImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        posterImageView.setImageResource(R.drawable.ic_movie_black_75dp);
                        Log.e(TAG,"Unable to load Poster");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                titleTextView.setText(title);
                String voteAverageTotalFinalText = "User Rating: "+voteAverage + "/10";
                voteAverageTotalTextView.setText(voteAverageTotalFinalText);
                releaseDateTextView.setText(releaseDate);
                descriptionTextView.setText(overview);
        }

        if (savedInstanceState !=null){
            isFavourite=savedInstanceState.getBoolean("Is Favourite");
        }

        LinearLayoutManager linearLayoutManagerTrailer = new LinearLayoutManager(this);
        MovieVideoAdapter movieVideoAdapter = new MovieVideoAdapter(movieVideoArrayList,this);
        trailersRecyclerView.setAdapter(movieVideoAdapter);
        trailersRecyclerView.setLayoutManager(linearLayoutManagerTrailer);

        LinearLayoutManager linearLayoutManagerReview = new LinearLayoutManager(this);
        MovieReviewAdapter movieReviewAdapter = new MovieReviewAdapter(movieReviewArrayList);
        reviewRecyclerView.setAdapter(movieReviewAdapter);
        reviewRecyclerView.setLayoutManager(linearLayoutManagerReview);

        //get the trailers and reviews
        if (isNetworkAvailable()){
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequestTrailer = getMovieVideoJsonObject();
            JsonObjectRequest jsonObjectRequestReview = getReviewJsonObject();

            queue.add(jsonObjectRequestTrailer);
            queue.add(jsonObjectRequestReview);

        }else{
            noVideoAvailable();
            noReviewsAvailable();
            Toast toast = Toast.makeText(this,"Unable to fetch Trailers and reviews",Toast.LENGTH_LONG);
            toast.show();
        }

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        db_id_movie = sharedPreferences.getInt("database_id_movie "+title,-1);
    }

    //Helper method to create the JsonObjectRequest for the trailers
    private JsonObjectRequest getReviewJsonObject() {
        //Get the data from the background thread using volley
        final String apiKey = getResources().getString(R.string.Api_key);
        String movieVideoUrl = "https://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+apiKey+"&language=en-US";
        return new JsonObjectRequest(Request.Method.GET,movieVideoUrl,null,new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                movieReviewArrayList = parseReviewJson(response);

                //This to handle case when their are no reviews for a movie.
                if (movieReviewArrayList.size()==0){
                    noReviewsAvailable();
                }else{
                    MovieReviewAdapter movieReviewAdapter = new MovieReviewAdapter(movieReviewArrayList);
                    reviewRecyclerView.setAdapter(movieReviewAdapter);
                    reviewRecyclerView.invalidate();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getBaseContext(),"Check your internet connection",Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    //Helper method to parse the Json to ArrayList<MovieReview>
    private ArrayList<MovieReview> parseReviewJson(JSONObject response) {
        ArrayList<MovieReview> movieReviewArrayList = new ArrayList<>();
        if (response !=null){
            try{
                JSONArray results = response.getJSONArray("results");

                //get the individual reviews
                for (int i=0;i<results.length();i++){
                    JSONObject obj = results.getJSONObject(i);
                    String author = obj.getString("author");
                    String content= obj.getString("content");
                    String movieUrl = obj.getString("url");

                    movieReviewArrayList.add(new MovieReview(author,content,movieUrl));
                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG,"error in parsing JSON");
            }
        }
        return movieReviewArrayList;
    }

    public void noReviewsAvailable(){
        View v = findViewById(R.id.view3);
        v.setVisibility(View.GONE);
        TextView title = (TextView) findViewById(R.id.reviews_title);
        title.setVisibility(View.GONE);
        reviewRecyclerView.setVisibility(View.GONE);
    }

    public void noVideoAvailable(){
        View view = findViewById(R.id.view2);
        TextView textView =(TextView) findViewById(R.id.trailers_title);
        view.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        trailersRecyclerView.setVisibility(View.GONE);
    }

    //Helper method to create the JsonObjectRequest for the trailers
    private JsonObjectRequest getMovieVideoJsonObject() {

        //Get the data from the background thread using volley
        final String apiKey = getResources().getString(R.string.Api_key);
        String movieVideoUrl = "https://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+apiKey+"&language=en-US";
        return new JsonObjectRequest(Request.Method.GET,movieVideoUrl,null,new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                movieVideoArrayList = parseVideoJson(response);

                //If their are no Videos available
                if (movieVideoArrayList.size() ==0){
                    noVideoAvailable();
                }

                MovieVideoAdapter movieVideoAdapter = new MovieVideoAdapter(movieVideoArrayList,MovieDetailsActivity.this);
                trailersRecyclerView.setAdapter(movieVideoAdapter);
                trailersRecyclerView.invalidate();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getBaseContext(),"Check your internet connection",Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }
    //Helper method to parse the Json to ArrayList<MovieVideo>
    private ArrayList<MovieVideo> parseVideoJson(JSONObject response) {

        ArrayList<MovieVideo> movieVideoArrayList = new ArrayList<>();

        if (response !=null){
            try{
                JSONArray results = response.getJSONArray("results");

                //get the individual videos from the array(Only Youtube)
                for (int i=0;i<results.length();i++){
                    JSONObject obj = results.getJSONObject(i);
                    String site = obj.getString("site");
                    if (!site.equals("YouTube")){
                        continue;
                    }
                    String key = obj.getString("key");
                    String name = obj.getString("name");
                    movieVideoArrayList.add(new MovieVideo(key,name,site,id));
                }
            }catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG,"error in parsing JSON");
            }
        }

        return movieVideoArrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites,menu);
        isFavouriteItem= menu.findItem(R.id.is_favourite);
        setFavouriteItem = menu.findItem(R.id.add_to_favourites);

        isFavourite = sharedPreferences.getBoolean(title,false);
        if (isFavourite){
            isFavouriteItem.setVisible(true);
            setFavouriteItem.setVisible(false);
        }else{
            isFavouriteItem.setVisible(false);
            setFavouriteItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home){
            finish();
            return true;
        }else if (id == R.id.add_to_favourites){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            posterBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] posterByteStream = byteArrayOutputStream.toByteArray();

            ContentValues contentValues = new ContentValues();
            contentValues.put(FavouritesDbContract.FavouritesEntry.COLUMN_MOVIE_ID,id);
            contentValues.put(FavouritesDbContract.FavouritesEntry.COLUMN_TITLE,title);
            contentValues.put(FavouritesDbContract.FavouritesEntry.COLUMN_POSTER,posterByteStream);

            Uri uri = getContentResolver().insert(FavouritesDbContract.FavouritesEntry.CONTENT_URI,contentValues);
            Log.v(TAG,uri+ " added");

            if (uri !=null){
                Toast toast = Toast.makeText(getBaseContext(),"Successfully Added to Favourites",Toast.LENGTH_SHORT);
                toast.show();
            }
            isFavouriteItem.setVisible(true);
            setFavouriteItem.setVisible(false);
            isFavourite=true;

            db_id_movie = Integer.parseInt(uri.getPathSegments().get(1));

        }else if(id == R.id.is_favourite){

            String stringId = Integer.toString(db_id_movie);
            Uri uri = FavouritesDbContract.FavouritesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            Log.v(TAG,"Deleting "+uri.toString());
            getContentResolver().delete(uri,null,null);

            isFavouriteItem.setVisible(false);
            setFavouriteItem.setVisible(true);
            db_id_movie=-1;
            isFavourite=false;
            Toast toast = Toast.makeText(this,"Removed from Favourites",Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Handle opening youtube site here.
    @Override
    public void onVideoClicked(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    //A helper method to check Internet Connection Status
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("Is Favourite",isFavourite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(title,isFavourite);
        editor.putInt("database_id_movie "+title,db_id_movie);
        editor.apply();
    }
}
