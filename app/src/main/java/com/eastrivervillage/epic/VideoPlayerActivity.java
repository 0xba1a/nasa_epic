package com.eastrivervillage.epic;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity implements OnPreparedListener, OnErrorListener {

    private VideoView videoView;

    private static final String TAG = "videoPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);

        String url = getIntent().getStringExtra("video_url");
        Log.e(TAG, "URL: " + url);
        videoView.setVideoURI(Uri.parse(url));
        //videoView.setVideoURI(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
    }

    @Override
    public void onPrepared() {
        videoView.start();;
    }

    @Override
    public boolean onError(Exception e) {
        //TODO: Show option to watch the video in browser
        Log.e(TAG, e + " 764 " + e.getMessage());
        return false;
    }
}
