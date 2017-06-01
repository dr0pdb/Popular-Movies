package com.example.srv_twry.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements MovieVideoAdapter.MovieVideoOnClickListener{

    private static final String TAG = MovieListActivity.class.getSimpleName();

    String poster_path;
    String overview;
    String releaseDate;
    String title;
    double voteAverage;
    int id;     //id associated with the movie

    ImageView posterImageView;
    TextView titleTextView;
    TextView voteAverageTotalTextView;
    TextView releaseDateTextView;
    TextView descriptionTextView;
    RecyclerView trailersRecyclerView;
    public static ArrayList<MovieVideo> movieVideoArrayList;

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
        movieVideoArrayList=new ArrayList<>();

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

                Picasso.with(getBaseContext()).load(poster_path).into(posterImageView);
                titleTextView.setText(title);
                String voteAverageTotalFinalText = "User Rating: "+voteAverage + "/10";
                voteAverageTotalTextView.setText(voteAverageTotalFinalText);
                releaseDateTextView.setText(releaseDate);
                descriptionTextView.setText(overview);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        MovieVideoAdapter movieVideoAdapter = new MovieVideoAdapter(movieVideoArrayList,this);
        trailersRecyclerView.setAdapter(movieVideoAdapter);
        trailersRecyclerView.setLayoutManager(linearLayoutManager);

        //get the trailers and reviews
        if (isNetworkAvailable()){
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = getMovieVideoJsonObject();
            queue.add(jsonObjectRequest);
        }else{
            //show error toast and hide progress bar of trailers and reviews.
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home){
            finish();
            return true;
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


}
