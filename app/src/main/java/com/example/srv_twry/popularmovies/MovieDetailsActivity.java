package com.example.srv_twry.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    String poster_path;
    String overview;
    String releaseDate;
    String title;
    double voteAverage;

    ImageView posterImageView;
    TextView titleTextView;
    TextView voteAverageTotalTextView;
    TextView releaseDateTextView;
    TextView descriptionTextView;

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

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            poster_path = bundle.getString(MovieListActivity.POSTER_PATH_KEY);
            overview = bundle.getString(MovieListActivity.OVERVIEW_KEY);
            releaseDate = bundle.getString(MovieListActivity.RELEASE_DATE_KEY);

            try{
                releaseDate = "Year of Release: "+releaseDate.substring(0,4);
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e(MovieDetailsActivity.class.getName(),"Wrong Date");
            }

            title = bundle.getString(MovieListActivity.TITLE_KEY);
            voteAverage = bundle.getDouble(MovieListActivity.VOTE_AVERAGE_KEY);

            try {
                Picasso.with(getBaseContext()).load(poster_path).into(posterImageView);
                titleTextView.setText(title);
                String voteAverageTotalFinalText = "User Rating: "+voteAverage + "/10";
                voteAverageTotalTextView.setText(voteAverageTotalFinalText);
                releaseDateTextView.setText(releaseDate);
                descriptionTextView.setText(overview);
            }catch (Exception e){
                e.printStackTrace();
                Toast toast = Toast.makeText(this,"Check your internet Connection",Toast.LENGTH_LONG);
                toast.show();
            }


        }


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
}
