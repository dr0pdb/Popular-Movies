package com.example.srv_twry.popularmovies;

/**
 * Created by srv_twry on 31/5/17.
 * A class to denote a single Video asset of a movie like trailer, Featurette etc.
 */

class MovieVideo {
    private final String key;
    private final String name;
    private String finalVideoURL=null;
    private final int id;     //id associated with the movie

    public MovieVideo(String key, String name, String site, int id){
        this.key=key;
        this.name=name;
        this.id=id;
        if (site.equals("YouTube")){
            finalVideoURL= "https://www.youtube.com/watch?v="+key;
        }
    }

    public String getName() {
        return name;
    }

    public String getFinalVideoURL() {
        return finalVideoURL;
    }

}
