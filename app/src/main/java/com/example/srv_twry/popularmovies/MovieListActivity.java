package com.example.srv_twry.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.MovieListRecyclerViewOnClickListener {

    public static String TAG = MovieListActivity.class.getSimpleName();
    public static ArrayList<Movie> movieArrayList;
    RecyclerView movieListRecyclerView;

    public static String POSTER_PATH_KEY = "poster_path";
    public static String IS_ADULT_KEY = "isAdult";
    public static String OVERVIEW_KEY = "overview";
    public static String RELEASE_DATE_KEY = "releaseDate";
    public static String GENRE_ID_KEY = "genreIDs";
    public static String ID_KEY = "id";
    public static String ORIGINAL_TITLE_KEY ="originalTitle";
    public static String ORIGINAL_LANGUAGE_KEY = "originalLanguage";
    public static String TITLE_KEY = "title";
    public static String BACKDROP_PATH_KEY = "backdropPath";
    public static String POPULARITY_KEY = "popularity";
    public static String VOTE_COUNT_KEY = "voteCount";
    public static String VOTE_AVERAGE_KEY = "voteAverage";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        final String baseUrl = "https://api.themoviedb.org/3/discover/movie?api_key=";
        final String apiKey = getResources().getString(R.string.API_key);
        final String postApiKeyUrl= "&language=en-US&sort_by=popularity.desc&include_adult=true&include_video=false&page=1";

        String finalURLPopularity = baseUrl.concat(apiKey.concat(postApiKeyUrl));
        movieArrayList = new ArrayList<>();
        new GetMovieListAsyncTask().execute(finalURLPopularity);
        movieListRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        MovieListAdapter movieListAdapter = new MovieListAdapter(movieArrayList,this,this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        movieListRecyclerView.setAdapter(movieListAdapter);
        movieListRecyclerView.setLayoutManager(gridLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(),"Unable to Parse the JSONResponse, try again!",Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    return null;
                }
            }else{
                Toast toast = Toast.makeText(getBaseContext(),"Unable to fetch response, Check your Internet Connection !",Toast.LENGTH_LONG);
                toast.show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayList) {
            MovieListActivity.movieArrayList = movieArrayList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MovieListAdapter movieListAdapter = new MovieListAdapter(MovieListActivity.movieArrayList,MovieListActivity.this,getBaseContext());
                    movieListRecyclerView.setAdapter(movieListAdapter);
                    movieListRecyclerView.invalidate();
                }
            });
        }
    }
}
