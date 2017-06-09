package com.eastrivervillage.epic;

import java.io.Serializable;

/**
 * Created by kannanba on 6/3/2017.
 */

public class SpecialEventsCardData implements Serializable {
    public String content;
    public String title;
    public String imageUrl;
    public int index;
    public boolean hasImages = false;
    public boolean hasVideo = false;
    public String imagesUrl;
    public String videoUrl;

    public SpecialEventsCardData() {}

    public SpecialEventsCardData(String imageUrl, String title, String content, int index, boolean hasImages, boolean hasVideo, String imagesUrl, String videoUrl) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.index = index;
        this.hasImages = hasImages;
        this.hasVideo = hasVideo;
        this.imagesUrl = imagesUrl;
        this.videoUrl = videoUrl;
    }
}
