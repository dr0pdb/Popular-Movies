package com.example.srv_twry.popularmovies;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    String poster_path;
    boolean isAdult;
    String overview;
    String releaseDate;
    int[] genreIDs;
    int id;
    String originalTitle;
    String originalLanguage;
    String title;
    String backdropPath;
    double popularity;
    int voteCount;
    double voteAverage;

    ImageView posterImageView;
    TextView titleTextView;
    TextView popularityTextView;
    TextView adultTextView;
    TextView voteAverageTotalTextView;
    TextView genresDetailsTextView;
    TextView releaseDateTextView;
    TextView languageDetailsTextView;
    TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        posterImageView = (ImageView) findViewById(R.id.iv_poster_details_activity);
        titleTextView = (TextView) findViewById(R.id.tv_title_details_activity);
        popularityTextView = (TextView) findViewById(R.id.tv_popularity_details_activity);
        adultTextView = (TextView) findViewById(R.id.tv_adult_details_activity);
        voteAverageTotalTextView = (TextView) findViewById(R.id.tv_vote_average_count_details_activity);
        genresDetailsTextView = (TextView) findViewById(R.id.tv_genres_details_activity);
        releaseDateTextView = (TextView) findViewById(R.id.tv_releasedate_details_activity);
        languageDetailsTextView = (TextView) findViewById(R.id.tv_language_details_activity);
        descriptionTextView = (TextView) findViewById(R.id.tv_description_details_activity);

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            poster_path = bundle.getString(MovieListActivity.POSTER_PATH_KEY);
            isAdult = bundle.getBoolean(MovieListActivity.IS_ADULT_KEY);
            overview = bundle.getString(MovieListActivity.OVERVIEW_KEY);
            releaseDate = bundle.getString(MovieListActivity.RELEASE_DATE_KEY);
            genreIDs = bundle.getIntArray(MovieListActivity.GENRE_ID_KEY);
            id = bundle.getInt(MovieListActivity.ID_KEY);
            originalTitle=  bundle.getString(MovieListActivity.ORIGINAL_TITLE_KEY);
            title = bundle.getString(MovieListActivity.TITLE_KEY);
            originalLanguage = bundle.getString(MovieListActivity.ORIGINAL_LANGUAGE_KEY);
            backdropPath = bundle.getString(MovieListActivity.BACKDROP_PATH_KEY);
            popularity = bundle.getDouble(MovieListActivity.POPULARITY_KEY);
            voteCount = bundle.getInt(MovieListActivity.VOTE_COUNT_KEY);
            voteAverage = bundle.getDouble(MovieListActivity.VOTE_AVERAGE_KEY);

            try {
                Picasso.with(getBaseContext()).load(poster_path).into(posterImageView);
                titleTextView.setText(title);
                String popularityFinalText =popularity + "";
                popularityTextView.setText(popularityFinalText);
                if (isAdult){
                    adultTextView.setVisibility(View.VISIBLE);
                }
                String voteAverageTotalFinalText = voteAverage + " Out of "+ voteCount + " Votes";
                voteAverageTotalTextView.setText(voteAverageTotalFinalText);
                String genreDetailsFinalText = genreIDs[0]+"";
                for (int i=1;i<genreIDs.length; i++){
                    genreDetailsFinalText=genreDetailsFinalText.concat(", "+genreIDs[i]);
                }
                genresDetailsTextView.setText(genreDetailsFinalText);
                releaseDateTextView.setText(releaseDate);
                languageDetailsTextView.setText(originalLanguage);
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
