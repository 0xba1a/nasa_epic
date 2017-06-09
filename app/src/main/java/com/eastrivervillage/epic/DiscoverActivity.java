package com.eastrivervillage.epic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class DiscoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        WebView webView = (WebView) findViewById(R.id.wv_web_container);
        webView.loadUrl(Global.ROOTURL + Global.EPIC);
    }
}
