package com.example.srv_twry.popularmovies;

/**
 * Created by srv_twry on 29/5/17.
 * The class to represent a single Movie.
 */


public class Movie {

    private String poster_path;
    private boolean isAdult;
    private String overview;
    private String releaseDate;
    private int[] genreIDs;
    private int id;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private double popularity;
    private int voteCount;
    private double voteAverage;

    public Movie(String poster_path , boolean isAdult , String overview , String releaseDate, int[] genreIDs , int id , String originalTitle , String originalLanguage , String title , String backdropPath , double popularity , int voteCount , double voteAverage ){
        this.poster_path = poster_path;
        this.isAdult=isAdult;
        this.overview = overview;
        this.releaseDate= releaseDate;
        this.genreIDs= genreIDs;
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title=title;
        this.backdropPath= backdropPath;
        this.popularity=popularity;
        this.voteCount=voteCount;
        this.voteAverage=voteAverage;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int[] getGenreIDs() {
        return genreIDs;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }
}
