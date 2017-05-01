package com.eastrivervillage.nasaepic;

/**
 * Created by kannanba on 4/30/2017.
 */
public class CardData  {
    public String date;
    public String lat;
    public String thumbnail;
    public String image;

    public CardData() {}

    public CardData(String date, String lat, String thumbnail, String image) {
        this.date = date;
        this.lat = lat;
        this.thumbnail = thumbnail;
        this.image = image;
    }
}
