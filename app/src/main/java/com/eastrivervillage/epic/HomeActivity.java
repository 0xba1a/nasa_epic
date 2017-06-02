package com.eastrivervillage.epic;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface font = Typeface.createFromAsset( this.getAssets(), "fontawesome-webfont.ttf" );
        ((TextView) findViewById(R.id.tv_daily_images)).setTypeface(font);
        ((TextView) findViewById(R.id.tv_special_events)).setTypeface(font);
        ((TextView) findViewById(R.id.tv_discover)).setTypeface(font);

        ((LinearLayout) findViewById(R.id.ll_daily_images)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.ll_daily_images:
                intent = new Intent(HomeActivity.this, MainActivity.class);
                break;
        }

        if (null != intent) {
            startActivity(intent);
        }
    }
}
