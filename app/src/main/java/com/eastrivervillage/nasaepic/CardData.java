package com.eastrivervillage.nasaepic;

import java.io.Serializable;

/**
 * Created by kannanba on 4/30/2017.
 */
public class CardData implements Serializable {
    public String date;
    public String lat;
    public String thumbnail;
    public String imageUrl;

    public CardData() {}

    public CardData(String date, String lat, String thumbnail, String imageUrl) {
        this.date = date;
        this.lat = lat;
        this.thumbnail = thumbnail;
        this.imageUrl = imageUrl;
    }
}
