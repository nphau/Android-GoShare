package com.yosta.goshare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {

    public static final String EXTRA_INTENT = "EXTRA_INTENT";

    public static String convertDate(Date date) {
        String res = "";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        res = df.format(date);
        return res;
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (info.getState() == NetworkInfo.State.CONNECTED);
    }

    private static boolean isMobileDataConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (info.getState() == NetworkInfo.State.CONNECTED);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return (ni != null);
    }

    public static boolean isGPSEnable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void ShowSnackBarNotify(View v, String msg) {
        Snackbar.make(v, Html.fromHtml("<font color=\"yellow\">" + msg + "</font>"), Snackbar.LENGTH_LONG).show();
    }

    public static void ShowSnackBarNotifyWithAction(
            View v,
            String msg,
            String action,
            View.OnClickListener listener) {

        Snackbar.make(v, Html.fromHtml("<font color=\"white\">" + msg + "</font>"), Snackbar.LENGTH_SHORT)
                .setAction(action, listener)
                .setDuration(Snackbar.LENGTH_LONG)
                .setActionTextColor(ColorStateList.valueOf(Color.GREEN))
                .show();
    }

    public static void setFont(Context context, TextView textView, String fontName) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
        textView.setTypeface(font);
    }

    public static void setFont(Context context, Button button, String fontName) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
        button.setTypeface(font);
    }

    public static void setFont(Context context, CheckBox checkBox, String fontName) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), fontName);
        checkBox.setTypeface(font);
    }

    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            return info.versionName;
        }
        return null;
    }

    public static Serializable receiveDataThroughBundle(Activity activity, String key) {
        Serializable serializable = null;
        try {
            Intent intent = activity.getIntent();
            Bundle bundle = intent.getExtras();
            serializable = bundle.getSerializable(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serializable;
    }

    /**
     * Send a serializable object into another activity
     * @param src - Source activity
     * @param dest - Destination activity, which is active
     * @param key - key
     * @param object - Which is sent
     * @param isCall - If true, destination activity is active
     */
    public static void sendObjectThroughBundle(Context src, Class dest, String key, Serializable object, boolean isCall) {
        try {
            Intent intent = new Intent(src.getApplicationContext(), dest);
            intent.putExtra(key, object);
            if (isCall) {
                src.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
