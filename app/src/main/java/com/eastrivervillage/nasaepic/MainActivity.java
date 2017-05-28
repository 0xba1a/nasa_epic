package com.eastrivervillage.nasaepic;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DetailFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener, DatePickerDialog.OnDateSetListener {

    public static final String TAG = "MainActivity";

    public static final int ALLOW_INTERNET_DIALOG_CODE = 1;
    public static final int NO_INTERNET_EXIT_CODE = 2;
    public static final int ALLOW_INTERNET_STATUS_DIALOG_CODE = 3;
    public static final int NO_INTERNET_STATUS_EXIT_CODE = 4;
    public static final int ENABLE_INTERNET_DIALOG_CODE = 5;
    public static final int RETRY_LOADING = 6;

    public static final int ALLOW_INTERNET_REQUEST_CODE = 1270;
    public static final int ALLOW_NETWORK_STATE_REQUEST_CODE = 1271;

    private ProgressDialog progressDialog = null;
    private ListFragment listFragment;

    private OkHttpClient httpClient;

    private List<CardData> cardDataList;

    private boolean enhancedImage = false;
    private int userSelectedYear = -1;
    private int userSelectedMonth = -1;
    private int userSelectedDay = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = new OkHttpClient();
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fr_list);
        cardDataList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(this);
        enhancedImage = pf.getBoolean("pref_enhanced", false);

        checkForInternetPermission();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
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

    public void checkNetworkStatePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                showDialog(getString(R.string.permission), getString(R.string.need_nw_state_permissin),
                        android.R.drawable.ic_dialog_info, getString(R.string.allow), getString(R.string.deny), ALLOW_INTERNET_STATUS_DIALOG_CODE);
            } else {
                requestNetworkStatusPermission();
            }
        } else {
            loadNasaData();
        }
    }

    public void showDialog(final String title, final String msg, final int icon, final String positiveStr, final String negativeStr,
                           final int callbackCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
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
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
//                    startActivity(intent);

                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                    SystemClock.sleep(3000);
                    loadNasaData();
                } else {
                    System.exit(0);
                }
                break;

            case RETRY_LOADING:
                if (success) {
                    loadNasaData();
                } else {
                    System.exit(0);
                }
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

    public void requestInternetPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET},
                ALLOW_INTERNET_REQUEST_CODE);
    }

    public void requestNetworkStatusPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
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

                    //TODO: Check network status before loading Nasa data
                    loadNasaData();
                } else {
                    showNoNetworkStatusExitDialog();
                }
                break;
        }
    }

    public void loadNasaData() {
        Log.e(TAG, "loadNasaData");

        if (!isNetworkConnected()) {
            showDialog(getString(R.string.Internet), getString(R.string.enable_Internet), android.R.drawable.ic_dialog_info,
                    getString(R.string.enable), getString(R.string.cancel), ENABLE_INTERNET_DIALOG_CODE);
            return;
        }

        showProgressDialog(getString(R.string.empty_str), getString(R.string.loading_str), false);

        Request request;

        String userSelectedDate = "";
        if (userSelectedYear != -1) {
            userSelectedDate = "/date/" + userSelectedYear + "-" + userSelectedMonth + "-" + userSelectedDay;
        }

        if (enhancedImage == false) {
            request = new Request.Builder()
                    .url(Global.BASEURL + Global.NATURAL + userSelectedDate)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(Global.BASEURL + Global.ENHANCED + userSelectedDate)
                    .build();
        }

        Log.e(TAG, "loadNasaData URL: " + Global.BASEURL + Global.NATURAL + userSelectedDate);

        asynchronousHttpRequest(request);
    }

    public void asynchronousHttpRequest(Request request) {
        try {
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Request failed");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Unexpected error");
                        dismissProgressDialog();
                        showDialog(getString(R.string.connectin), getString(R.string.failed_to_load),
                                android.R.drawable.ic_dialog_alert, getString(R.string.retry), getString(R.string.exit), RETRY_LOADING);
                    } else {
//                        Log.e(TAG, response.body().string());
                        loadCardData(response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listFragment.setArrayList(cardDataList);
                                dismissProgressDialog();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e + " 1 " + e.getMessage());
        }
    }

    public void loadCardData(String jsonStr) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);

            if (jsonArray.length() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(getString(R.string.no_pics_available))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                });

                userSelectedDay = userSelectedMonth = userSelectedYear = -1;
                dismissProgressDialog();
                return;
            }

            cardDataList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString("date");
                String time = date.trim().split("\\s+")[1];
                date = date.trim().split("\\s+")[0];
                String thumpUrl = date;
                String imageUrl;
                thumpUrl = thumpUrl.replace("-", "/");

                if (enhancedImage == false) {
                    imageUrl = Global.ROOTURL + Global.ARCHIVE + Global.NATURAL + "/" + thumpUrl + "/png/" + jsonObject.getString("image") + ".png";
                    thumpUrl = Global.ROOTURL + Global.ARCHIVE + Global.NATURAL + "/" + thumpUrl + "/jpg/" + jsonObject.getString("image") + ".jpg";
                } else {
                    imageUrl = Global.ROOTURL + Global.ARCHIVE + Global.ENHANCED + "/" + thumpUrl + "/png/" + jsonObject.getString("image") + ".png";
                    thumpUrl = Global.ROOTURL + Global.ARCHIVE + Global.ENHANCED + "/" + thumpUrl + "/jpg/" + jsonObject.getString("image") + ".jpg";
                }

                Log.i(TAG, thumpUrl);
                CardData cardData = new CardData(date,
                        time,
                        thumpUrl,
                        imageUrl);
                cardDataList.add(cardData);
            }
        } catch (Exception e) {
            Log.e(TAG, e + " 3 " + e.getMessage());
        }
    }

    public void showProgressDialog(final String title, final String msg, final boolean cancellable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle(title);
                progressDialog.setMessage(msg);
                progressDialog.setCancelable(cancellable);
                progressDialog.show();
            }
        });
    }

    public void dismissProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.calender:
                final Calendar calendar = Calendar.getInstance();
                (new DatePickerDialog(this, MainActivity.this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))).show();
                return true;
            case R.id.dev:
                //TODO: Create and display dev activity here
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        userSelectedYear = year;
        userSelectedMonth = month + 1;
        userSelectedDay = dayOfMonth;

        loadNasaData();
    }
}
