package com.eastrivervillage.epic;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity implements OnPreparedListener, OnErrorListener {

    private VideoView videoView;

    private static final String TAG = "videoPlayerActivity";
    private static String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);

        String url = getIntent().getStringExtra("video_url");
        this.videoUrl = url;
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
        new AlertDialog.Builder(VideoPlayerActivity.this)
                .setTitle(getString(R.string.error_caps))
                .setMessage(getString(R.string.error_playing_video))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                            startActivity(myIntent);
                            finish();
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(VideoPlayerActivity.this, getString(R.string.no_related_activity), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();

        Log.e(TAG, e + " 764 " + e.getMessage());
        return false;
    }
}
