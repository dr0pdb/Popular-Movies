package com.example.srv_twry.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by srv_twry on 30/5/17.
 * RecyclerView Adapter for the movie list recyclerview.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {


    private final ArrayList<Movie> movieArrayList;
    private final MovieListRecyclerViewOnClickListener movieListRecyclerViewOnClickListener;
    private final Context context;    //This to use the Picasso library

    public MovieListAdapter(ArrayList<Movie> movieArrayList, MovieListRecyclerViewOnClickListener movieListRecyclerViewOnClickListener,Context context){
        this.movieArrayList = movieArrayList;
        this.movieListRecyclerViewOnClickListener = movieListRecyclerViewOnClickListener;
        this.context = context;
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int resourceIdForViewHolder = R.layout.single_movie_layout;
        View view = inflater.inflate(resourceIdForViewHolder,parent,false);
        return new MovieListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    //An interface to be implemented by the movie list activity in order to setup onclick listener
    public interface MovieListRecyclerViewOnClickListener{
        void onListItemClicked(int position);
    }

    //View Holder class for the recycler view adapter
    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView posterImageView;

        public MovieListViewHolder(View view){
            super(view);
            posterImageView = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            movieListRecyclerViewOnClickListener.onListItemClicked(position);
        }

        public void bind(int position){
            String imageURL = movieArrayList.get(position).getPoster_path();

            try{
                Picasso.with(context).load(imageURL).into(posterImageView);
            }catch (Exception e){
                String TAG = "MovieListAdapter";
                Log.e(TAG,"Unable to load poster");
            }

        }
    }
}
