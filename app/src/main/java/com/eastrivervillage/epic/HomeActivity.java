package com.eastrivervillage.epic;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface font = Typeface.createFromAsset( this.getAssets(), "fontawesome-webfont.ttf" );
        ((TextView) findViewById(R.id.tv_daily_images)).setTypeface(font);
        ((TextView) findViewById(R.id.tv_special_events)).setTypeface(font);
        ((TextView) findViewById(R.id.tv_discover)).setTypeface(font);
    }
}
