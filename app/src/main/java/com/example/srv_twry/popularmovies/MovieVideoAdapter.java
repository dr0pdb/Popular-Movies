package com.example.srv_twry.popularmovies;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by srv_twry on 31/5/17.
 * The recyclerview adapter for the recyclerview of trailers and clips
 */

public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.MovieVideoViewHolder> {

    private ArrayList<MovieVideo> movieVideoArrayList;
    private MovieVideoOnClickListener movieVideoOnClickListener;

    public MovieVideoAdapter(ArrayList<MovieVideo>movieVideoArrayList,MovieVideoOnClickListener movieVideoOnClickListener){
        this.movieVideoArrayList = movieVideoArrayList;
        this.movieVideoOnClickListener=movieVideoOnClickListener;
    }

    @Override
    public MovieVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int resourceIdForViewHolder = R.layout.single_movie_video_layout;
        View view = inflater.inflate(resourceIdForViewHolder,parent,false);
        return new MovieVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieVideoArrayList.size();
    }

    public interface MovieVideoOnClickListener{
        void onVideoClicked(String url);
    }

    public class MovieVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cardView;
        ImageView videoImage;
        TextView videoTitle;

        public MovieVideoViewHolder(View view){
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view_movie_video);
            videoImage=(ImageView) view.findViewById(R.id.image_play_youtube);
            videoTitle=(TextView) view.findViewById(R.id.title_video);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = getAdapterPosition();
            String url = movieVideoArrayList.get(id).getFinalVideoURL();
            movieVideoOnClickListener.onVideoClicked(url);
        }

        public void bind(int position) {
            String title = movieVideoArrayList.get(position).getName();
            videoTitle.setText(title);
            videoImage.setImageResource(R.drawable.youtube_icon_full_color);
        }
    }
}
