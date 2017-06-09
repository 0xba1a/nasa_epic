package com.eastrivervillage.epic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kannanba on 6/3/2017.
 */

public class SpecialEventsAdapter extends RecyclerView.Adapter<SpecialEventsAdapter.EventHolder> {
    private ArrayList<SpecialEventsCardData> eventList;
    private Context context;
    private EventHolder.LoadSlideShow listener;

    public static class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView titleImage;
        public TextView title;
        public TextView content;
        public Button viewImages;
        public Button watchVideos;
        public LoadSlideShow listener;

        public ArrayList<CardData> cardDataList;
        private ArrayList<SpecialEventsCardData> eventList;
        private String videoUrl = null;

        public EventHolder(View v, ArrayList<SpecialEventsCardData> eventList, LoadSlideShow listener) {
            super(v);

            titleImage = (ImageView) v.findViewById(R.id.iv_title_image);
            title = (TextView) v.findViewById(R.id.tv_title_text);
            content = (TextView) v.findViewById(R.id.tv_content_text);
            viewImages = (Button) v.findViewById(R.id.tv_view_images);
            watchVideos = (Button) v.findViewById(R.id.tv_watch_video);

            listener.setTypeFace(viewImages);
            listener.setTypeFace(watchVideos);

            this.listener = listener;

            cardDataList = new ArrayList<CardData>();

            this.eventList = eventList;

            viewImages.setOnClickListener(this);
            watchVideos.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.showProgressDialog();

            int position = 0;

            switch (v.getId()) {
                case R.id.tv_view_images:
                    position = Integer.parseInt(v.getTag() + "");
                    loadCardData(position);
                    listener.dismissProgressDialog();

                    if (cardDataList.isEmpty()) {
                        listener.noDataAvailableNow();
                        return;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cardDataList", (Serializable)cardDataList);
                    bundle.putInt("selectedPosition", 0);

                    SlideShowFragment newFragment = SlideShowFragment.newInstance();
                    newFragment.setArguments(bundle);
                    listener.loadSlideShow(newFragment);
                    break;

                case R.id.tv_watch_video:
                    position = Integer.parseInt(v.getTag() + "");
                    getVideoUrl(position);
                    listener.dismissProgressDialog();
                    if (videoUrl == null) {
                        listener.noDataAvailableNow();
                        return;
                    }

                    listener.loadVideo(videoUrl);
                    break;
            }
        }

        public interface LoadSlideShow {
            void loadSlideShow(SlideShowFragment slideShowFragment);
            void loadVideo(String url);
            void setTypeFace(View v);
            void showProgressDialog();
            void dismissProgressDialog();
            void noDataAvailableNow();
        }

        public void getVideoUrl(int index) {
            final String url = eventList.get(index).videoUrl;
            videoUrl = null;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(Global.ROOTURL + "/" + url).get();
                        Element element = doc.getElementsByTag("source").get(0);
                        videoUrl = element.attr("src");
                    } catch (Exception e) {
                        listener.noDataAvailableNow();
                        Log.e("SpecialEventsAdapter", e + " 82 " + e.getMessage());
                        return;
                    }
                }
            });

            try {
                thread.start();
                thread.join();
            } catch (Exception e) {
                Log.e("SpecialEventsAdapter", e + " 73 " + e.getMessage());
            }
        }

        public void loadCardData(int index) {
            final String url = eventList.get(index).imagesUrl;

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Document doc = Jsoup.connect(Global.ROOTURL + "/" + url).get();
                        Elements elements = doc.getElementsByClass("img-thumbnail");
                        cardDataList.clear();
                        for (int i = 0; i < elements.size(); i++) {
                            CardData cardData = new CardData("", "", "", elements.get(i).attr("src"));
                            cardDataList.add(i, cardData);
                        }
                    } catch (Exception e) {
                        Log.e("SpecialEventsAdapter", e + " 84 " + e.getMessage());
                    }
                }
            });

            try {
                thread.start();
                thread.join();
            } catch (Exception e) {
                Log.e("SpecialEventsAdapter", e + " 98 " + e.getMessage());
            }
        }
    }

    public SpecialEventsAdapter(Context context, ArrayList<SpecialEventsCardData> list, EventHolder.LoadSlideShow listener) {
        this.context = context;
        this.eventList = list;
        this.listener = listener;
    }

    @Override
    public SpecialEventsAdapter.EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.special_events_card, parent, false);
        return new EventHolder(inflatedView, eventList, listener);
    }

    @Override
    public void onBindViewHolder(SpecialEventsAdapter.EventHolder holder, int position) {
        SpecialEventsCardData cardData = eventList.get(position);

        Glide.with(context).load(cardData.imageUrl).into(holder.titleImage);
        holder.title.setText(cardData.title);
        holder.content.setText(cardData.content);
        holder.watchVideos.setTag(cardData.index);
        holder.viewImages.setTag(cardData.index);
        if (false == cardData.hasImages) {
            holder.viewImages.setVisibility(View.GONE);
        }
        if (false == cardData.hasVideo) {
            holder.watchVideos.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
