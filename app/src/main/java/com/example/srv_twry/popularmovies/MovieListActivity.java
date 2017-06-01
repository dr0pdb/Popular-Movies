package com.example.srv_twry.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.MovieListRecyclerViewOnClickListener {

    public static final String TAG = MovieListActivity.class.getSimpleName();
    public static ArrayList<Movie> movieArrayList;
    RecyclerView movieListRecyclerView;
    ProgressBar progressBar;

    public static final String POSTER_PATH_KEY = "poster_path";
    public static final String IS_ADULT_KEY = "isAdult";
    public static final String OVERVIEW_KEY = "overview";
    public static final String RELEASE_DATE_KEY = "releaseDate";
    public static final String GENRE_ID_KEY = "genreIDs";
    public static final String ID_KEY = "id";
    public static final String ORIGINAL_TITLE_KEY ="originalTitle";
    public static final String ORIGINAL_LANGUAGE_KEY = "originalLanguage";
    public static final String TITLE_KEY = "title";
    public static final String BACKDROP_PATH_KEY = "backdropPath";
    public static final String POPULARITY_KEY = "popularity";
    public static final String VOTE_COUNT_KEY = "voteCount";
    public static final String VOTE_AVERAGE_KEY = "voteAverage";

    public final String POPULARITY_INSTANCE_STATE_KEY = "popularity";

    String finalURLPopularity ;
    String finalURLTopRated;
    boolean sortByPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        final String baseUrlPopularity = "https://api.themoviedb.org/3/movie/popular?api_key=";
        final String apiKey = getResources().getString(R.string.Api_key);
        final String postApiKeyUrl= "&language=en-US&page=1";
        final String baseUrlTopRated="https://api.themoviedb.org/3/movie/top_rated?api_key=";

        finalURLPopularity = baseUrlPopularity.concat(apiKey.concat(postApiKeyUrl));
        finalURLTopRated= baseUrlTopRated.concat(apiKey.concat(postApiKeyUrl));
        movieArrayList = new ArrayList<>();

        movieListRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_Data);
        MovieListAdapter movieListAdapter = new MovieListAdapter(movieArrayList,this,this);

        //Added this after the successful code submission and suggestions in the code review.
        // This line reads from the resource file according to the orientation and sets different number of columns for landscape and portrait mode.
        final int numberOfColumns = getResources().getInteger(R.integer.columns_in_grid_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,numberOfColumns);
        movieListRecyclerView.setAdapter(movieListAdapter);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);

        if (savedInstanceState !=null && isNetworkAvailable()){
            boolean value = savedInstanceState.getBoolean(POPULARITY_INSTANCE_STATE_KEY);
            if (value){
                new GetMovieListAsyncTask().execute(finalURLPopularity);
                sortByPopularity=true;
            }else{
                new GetMovieListAsyncTask().execute(finalURLTopRated);
                sortByPopularity=false;
            }
        }
        //sort by popularity for first run
        else if (isNetworkAvailable()){
            new GetMovieListAsyncTask().execute(finalURLPopularity);
            sortByPopularity=true;
        }else{
         //Added this after the successful code submission and suggestions in the code review. To Show an error toast if network not available.
            progressBar.setVisibility(View.GONE);
            Toast toast = Toast.makeText(this,"Check your Internet Connection!",Toast.LENGTH_LONG);
            toast.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popularity && isNetworkAvailable()) {
            new GetMovieListAsyncTask().execute(finalURLPopularity);
            showProgressBar();
            sortByPopularity=true;
        }else if (id == R.id.action_sort_toprated && isNetworkAvailable()){
            new GetMovieListAsyncTask().execute(finalURLTopRated);
            showProgressBar();
            sortByPopularity=false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClicked(int position) {

        String poster_path = movieArrayList.get(position).getPoster_path();
        boolean isAdult= movieArrayList.get(position).isAdult();
        String overview= movieArrayList.get(position).getOverview();
        String releaseDate= movieArrayList.get(position).getReleaseDate();
        int[] genreIDs= movieArrayList.get(position).getGenreIDs();
        int id = movieArrayList.get(position).getId();
        String originalTitle= movieArrayList.get(position).getOriginalTitle();
        String originalLanguage= movieArrayList.get(position).getOriginalLanguage();
        String title= movieArrayList.get(position).getTitle();
        String backdropPath= movieArrayList.get(position).getBackdropPath();
        double popularity= movieArrayList.get(position).getPopularity();
        int voteCount = movieArrayList.get(position).getVoteCount();
        double voteAverage = movieArrayList.get(position).getVoteAverage();

        Intent intent = new Intent(this,MovieDetailsActivity.class);
        intent.putExtra(POSTER_PATH_KEY,poster_path);
        intent.putExtra(IS_ADULT_KEY,isAdult);
        intent.putExtra(OVERVIEW_KEY,overview);
        intent.putExtra(RELEASE_DATE_KEY,releaseDate);
        intent.putExtra(GENRE_ID_KEY,genreIDs);
        intent.putExtra(ID_KEY,id);
        intent.putExtra(ORIGINAL_TITLE_KEY,originalTitle);
        intent.putExtra(ORIGINAL_LANGUAGE_KEY,originalLanguage);
        intent.putExtra(TITLE_KEY,title);
        intent.putExtra(BACKDROP_PATH_KEY,backdropPath);
        intent.putExtra(POPULARITY_KEY,popularity);
        intent.putExtra(VOTE_COUNT_KEY,voteCount);
        intent.putExtra(VOTE_AVERAGE_KEY,voteAverage);

        startActivity(intent);
    }

    //A helper method to check Internet Connection Status
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showData(){
        progressBar.setVisibility(View.GONE);
        movieListRecyclerView.setVisibility(View.VISIBLE);
    }
    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        movieListRecyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(POPULARITY_INSTANCE_STATE_KEY,sortByPopularity);
    }

    private class GetMovieListAsyncTask extends AsyncTask<String,Void,ArrayList<Movie>>{

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            String url = params[0];
            HTTPHandler httpHandler = new HTTPHandler();
            String response = httpHandler.getHTTPResponse(url);
            String baseUrlPosterPath = "http://image.tmdb.org/t/p/w185/";
            if (response !=null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    ArrayList<Movie> movieList = new ArrayList<>();

                    for (int i=0;i<results.length();i++){
                        JSONObject object = results.getJSONObject(i);
                        String returnedPosterPath = object.getString("poster_path");
                        String poster_path = baseUrlPosterPath.concat(returnedPosterPath);
                        boolean isAdult = object.getBoolean("adult");
                        String overView = object.getString("overview");
                        String releaseDate = object.getString("release_date");
                        JSONArray genreIDJsonArray = object.getJSONArray("genre_ids");
                        int[] genreIdArray = new int[genreIDJsonArray.length()];
                        for (int j=0;j<genreIDJsonArray.length();j++){
                            genreIdArray[j]=genreIDJsonArray.getInt(j);
                        }
                        int id = object.getInt("id");
                        String originalTitle = object.getString("original_title");
                        String originalLanguage = object.getString("original_language");
                        String title= object.getString("title");
                        String backdropPath = object.getString("backdrop_path");
                        Double popularity = object.getDouble("popularity");
                        int voteCount = object.getInt("vote_count");
                        Double voteAverage = object.getDouble("vote_average");

                        movieList.add(new Movie(poster_path,isAdult,overView,releaseDate,genreIdArray,id,originalTitle,originalLanguage,title,backdropPath,popularity,voteCount,voteAverage));
                    }
                    return movieList;
                }catch (JSONException e){
                    Log.e(TAG,"JSON exception",e.fillInStackTrace());
                    return null;
                }
            }else{
                Log.e(TAG,"Network connection error !");
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayList) {
            MovieListActivity.movieArrayList = movieArrayList;

            if (movieArrayList == null){
                Toast toast = Toast.makeText(getApplicationContext(),"Unable to fetch response, Check your Internet Connection !",Toast.LENGTH_LONG);
                toast.show();
            }else{
                MovieListAdapter movieListAdapter = new MovieListAdapter(MovieListActivity.movieArrayList,MovieListActivity.this,getBaseContext());
                showData();
                movieListRecyclerView.setAdapter(movieListAdapter);
                movieListRecyclerView.invalidate();
            }
        }
    }
}
