package com.example.srv_twry.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by srv_twry on 1/6/17.
 * Adapter for the review recyclerview
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewViewHolder> {

    private final ArrayList<MovieReview> movieReviewArrayList;

    public MovieReviewAdapter(ArrayList<MovieReview>movieReviewArrayList){
        this.movieReviewArrayList=movieReviewArrayList;
    }


    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_review_layout,parent,false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieReviewArrayList.size();
    }

    //View Holder class for the MovieReviewAdapter
    public class MovieReviewViewHolder extends RecyclerView.ViewHolder{

        final TextView content;
        final TextView author;

        public MovieReviewViewHolder(View view){
            super(view);
            content = (TextView) view.findViewById(R.id.review_content);
            author = (TextView) view.findViewById(R.id.review_author);
        }

        public void bind(int position) {
            content.setText(movieReviewArrayList.get(position).getContent());
            String authorText = "-"+movieReviewArrayList.get(position).getAuthor();
            author.setText(authorText);
        }
    }
}
