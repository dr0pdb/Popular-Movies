package com.example.srv_twry.popularmovies;

/**
 * Created by srv_twry on 31/5/17.
 * A class to denote a single Video asset of a movie like trailer, Featurette etc.
 */

public class MovieVideo {
    private int key;
    private String name;
    private String finalVideoURL=null;
    private int id;     //id associated with the movie

    public MovieVideo(int key, String name, String site, int id){
        this.key=key;
        this.name=name;
        this.id=id;
        if (site.equals("YouTube")){
            finalVideoURL= "https://www.youtube.com/watch?v="+key;
        }
    }

    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getFinalVideoURL() {
        return finalVideoURL;
    }

    public int getId() {
        return id;
    }
}
