package com.eastrivervillage.epic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int ALLOW_INTERNET_DIALOG_CODE = 1;
    public static final int NO_INTERNET_EXIT_CODE = 2;
    public static final int ALLOW_INTERNET_STATUS_DIALOG_CODE = 3;
    public static final int NO_INTERNET_STATUS_EXIT_CODE = 4;
    public static final int ENABLE_INTERNET_DIALOG_CODE = 5;

    public static final int ALLOW_INTERNET_REQUEST_CODE = 1270;
    public static final int ALLOW_NETWORK_STATE_REQUEST_CODE = 1271;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface font = Typeface.createFromAsset( this.getAssets(), "fontawesome-webfont.ttf" );
        ((TextView) findViewById(R.id.tv_daily_images)).setTypeface(font);
        ((TextView) findViewById(R.id.tv_special_events)).setTypeface(font);
        ((TextView) findViewById(R.id.tv_discover)).setTypeface(font);

        ((LinearLayout) findViewById(R.id.ll_daily_images)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_special_events)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_discover)).setOnClickListener(this);

        checkForInternetPermission();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.ll_daily_images:
                intent = new Intent(HomeActivity.this, MainActivity.class);
                break;
            case R.id.ll_special_events:
                intent = new Intent(HomeActivity.this, SpecialEventsActivity.class);
                break;
            case R.id.ll_discover:
                intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                break;
        }

        if (null != intent) {
            startActivity(intent);
        }
    }

    public void checkForInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            /* Need to provide an explanation */
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                showDialog(getString(R.string.permission), getString(R.string.need_Internet_permission),
                        android.R.drawable.ic_dialog_info, getString(R.string.allow), getString(R.string.deny), ALLOW_INTERNET_DIALOG_CODE);
            } else {
                requestInternetPermission();
            }
        } else {
            checkNetworkStatePermission();
        }
    }

    public void showDialog(final String title, final String msg, final int icon, final String positiveStr, final String negativeStr,
                           final int callbackCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle(title)
                        .setMessage(msg)
                        .setIcon(icon)
                        .setPositiveButton(positiveStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogCallback(callbackCode, true);
                            }
                        })
                        .setNegativeButton(negativeStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogCallback(callbackCode, false);
                            }
                        })
                        .show();
            }
        });
    }

    public void dialogCallback(int code, boolean success) {
        switch (code) {
            case ALLOW_INTERNET_DIALOG_CODE:
                if (success) {
                    requestInternetPermission();
                } else {
                    showNoInternetExitDialog();
                }
                break;

            case ALLOW_INTERNET_STATUS_DIALOG_CODE:
                if (success) {
                    requestNetworkStatusPermission();
                } else {
                    showNoNetworkStatusExitDialog();
                }
                break;

            case NO_INTERNET_EXIT_CODE:
                if (success) {
                    checkForInternetPermission();
                } else {
                    System.exit(0);
                }
                break;

            case NO_INTERNET_STATUS_EXIT_CODE:
                if (success) {
                    checkForInternetPermission();
                } else {
                    System.exit(0);
                }
                break;

            case ENABLE_INTERNET_DIALOG_CODE:
                if (success) {
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    SystemClock.sleep(3000);
                } else {
                    System.exit(0);
                }
                break;
        }
    }

    public void checkNetworkStatePermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                showDialog(getString(R.string.permission), getString(R.string.need_nw_state_permissin),
                        android.R.drawable.ic_dialog_info, getString(R.string.allow), getString(R.string.deny), ALLOW_INTERNET_STATUS_DIALOG_CODE);
            } else {
                requestNetworkStatusPermission();
            }
        }
    }

    public void requestInternetPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{Manifest.permission.INTERNET},
                ALLOW_INTERNET_REQUEST_CODE);
    }

    public void requestNetworkStatusPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                ALLOW_NETWORK_STATE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALLOW_INTERNET_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* Permission granted for Internet access */
                    checkNetworkStatePermission();
                } else {
                    showNoInternetExitDialog();
                }
                break;

            case ALLOW_NETWORK_STATE_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* Permission Granted for Network state */
                } else {
                    showNoNetworkStatusExitDialog();
                }
                break;
        }
    }
    
    public void showNoInternetExitDialog() {
        showDialog(getString(R.string.permission), getString(R.string.no_proceed_without_Internet),
                android.R.drawable.ic_dialog_alert, getString(R.string.allow), getString(R.string.exit), NO_INTERNET_EXIT_CODE);
    }

    public void showNoNetworkStatusExitDialog() {
        showDialog(getString(R.string.permission), getString(R.string.no_proceed_without_nw_access),
                android.R.drawable.ic_dialog_alert, getString(R.string.allow), getString(R.string.exit), NO_INTERNET_STATUS_EXIT_CODE);
    }
}
