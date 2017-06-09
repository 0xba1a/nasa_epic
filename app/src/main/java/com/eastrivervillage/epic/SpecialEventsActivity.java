package com.eastrivervillage.epic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpecialEventsActivity extends AppCompatActivity implements SpecialEventsAdapter.EventHolder.LoadSlideShow {

    private static final String TAG = "SpecialEventsActivity";

    private ArrayList<SpecialEventsCardData> cardDataList;
    private MainActivity mainActivity;
    private OkHttpClient httpClient;
    private RecyclerView recyclerView;

    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_events);

        recyclerView = (RecyclerView) findViewById(R.id.rv_container);
        cardDataList = new ArrayList<SpecialEventsCardData>();

        showProgressDialog();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getCardData();
            }
        });
        thread.start();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void getCardData() {
        if (!isNetworkConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(SpecialEventsActivity.this)
                            .setTitle(getString(R.string.no_connection_caps))
                            .setMessage(getString(R.string.internet_is_required))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
            });
            dismissProgressDialog();
            return;
        }

        try {
            Document doc = Jsoup.connect(Global.ROOTURL + Global.GALLERY).get();

            Elements elements = doc.getElementsByClass("clearfix");

            if (null != cardDataList) {
                cardDataList.clear();
            }

            for (int i = 1; i < elements.size(); i++) {
                loadCardData(elements.get(i), i-1);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SpecialEventsAdapter cardAdapter = new SpecialEventsAdapter(getApplicationContext(), SpecialEventsActivity.this.cardDataList, SpecialEventsActivity.this);
                    RecyclerView.LayoutManager mLayoutManager;

                    mLayoutManager = new GridLayoutManager(SpecialEventsActivity.this, SpecialEventsActivity.this.getResources().getInteger(R.integer.special_events_column_count));
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(cardAdapter);
                    cardAdapter.notifyDataSetChanged();

                    dismissProgressDialog();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(SpecialEventsActivity.this)
                            .setTitle(getString(R.string.error_caps))
                            .setMessage(getString(R.string.server_error))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
            });
            Log.e(TAG, e + " 87 " + e.getMessage());
            return;
        }
    }

//    public void showProgressDialog() {
//        dismissProgressDialog();
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage(getString(R.string.loading_3dot));
//        progressDialog.show();
//    }

    public void loadCardData(Element element, int index) {
        boolean hasImages = false;
        boolean hasVideo = false;
        int anchor_index = 0;
        String imagesUrl = "";
        String videoUrl = "";
        String title = element.getElementsByClass("panel-title").get(0).text();
        String content = element.getElementsByClass("panel-body").get(0).getElementsByTag("div").get(2).text();
        if (content.contains("View the Images")) {
            imagesUrl = element.getElementsByClass("panel-body").get(0).getElementsByTag("div").get(2).getElementsByTag("a").get(anchor_index++).attr("href");
            content = content.replace("View the Images", "");
            hasImages = true;
        }
        if (content.contains("Watch the Video")) {
            videoUrl = element.getElementsByClass("panel-body").get(0).getElementsByTag("div").get(2).getElementsByTag("a").get(anchor_index).attr("href");
            content = content.replace("Watch the Video", "");
            hasVideo = true;
        }
        String imageUrl = element.getElementsByClass("panel-body").get(0).getElementsByTag("div").get(0).getElementsByTag("img").get(0).attr("src");
        imageUrl = Global.ROOTURL + "/" + imageUrl;
        SpecialEventsCardData cardData = new SpecialEventsCardData(imageUrl, title, content, index, hasImages, hasVideo, imagesUrl, videoUrl);
        cardDataList.add(index, cardData);
    }

    @Override
    public void noDataAvailableNow() {
        new AlertDialog.Builder(SpecialEventsActivity.this)
                .setTitle(getString(R.string.error_caps))
                .setMessage(getString(R.string.no_data_available))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void loadSlideShow(SlideShowFragment slideShowFragment) {
        slideShowFragment.show(getSupportFragmentManager(), "TAG");
    }

    @Override
    public void loadVideo(String url) {
        Intent intent = new Intent(SpecialEventsActivity.this, VideoPlayerActivity.class);
        intent.putExtra("video_url", url);
        startActivity(intent);
    }

    @Override
    public void setTypeFace(View v) {
        Typeface font = Typeface.createFromAsset( this.getAssets(), "fontawesome-webfont.ttf" );
        ((TextView) v).setTypeface(font);
    }

    @Override
    public void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissProgressDialog();

                progressDialog = new ProgressDialog(SpecialEventsActivity.this);
                progressDialog.setTitle("");
                progressDialog.setMessage(getString(R.string.loading_3dot));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }

    @Override
    public void dismissProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
}
