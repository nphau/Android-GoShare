package com.yosta.goshare.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.yosta.goshare.models.User;

import java.util.Locale;

/**
 * Created by nphau on 9/27/2015.
 */
public class SharedPreferencesUtils {


    public static String KEY_SKIP = "SKIP";
    public static String KEY_MAPS = "VIEW_MAPS";

    public static String KEY_USER = "USER_INFO";
    public static String KEY_EMAIL = "USER_EMAIL";
    public static String KEY_POINT = "USER_POINT";
    public static String KEY_HOBBIES = "USER_HOBBIES";
    public static String KEY_AVATAR = "USER_AVATAR";
    public static String KEY_PASSWORD = "USER_PASSWORD";
    public static String KEY_FULL_NAME = "USER_FULL_NAME";

    public static String KEY_PLACE = "PLACE";
    public static String KEY_LANGUAGE = "LANGUAGE";

    public static String KEY_MAP_STYLE = "MAP_STYLE";

    private Activity activity;
    private SharedPreferences preferences = null;

    public SharedPreferencesUtils(Activity activity) {
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        this.activity = activity;
    }

    public int getSettingInt(String key) {
        int value = 0;
        if (preferences.contains(key))
            value = preferences.getInt(key, 0);
        return value;
    }

    public String getSettingString(String key) {
        String value;

        if (preferences == null || !preferences.contains(key))
            value = "";
        else
            value = preferences.getString(key, "");

        return value;
    }

    public boolean saveSetting(String key, int value) {
        if (preferences == null) return false;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
        return true;
    }

    public boolean saveSetting(String key, String value) {

        if (preferences == null) return false;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
        return true;
    }

    public boolean removeSettings(String key) {
        if (preferences == null) return false;

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();

        return true;
    }

    public void changeAppLanguage(int key) {
        try {
            String keyText = "en";
            switch (key) {
                case 1: {
                    keyText = "vi";
                    break;
                }
                default: {
                    break;
                }
            }

            // Change locale settings in the app.
            Resources res = activity.getResources();

            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(keyText.toLowerCase());
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean saveAccountPrefs(User user) {

        if (user == null || this.preferences == null) return false;

        this.saveSetting(SharedPreferencesUtils.KEY_EMAIL, user.getEmail());
        this.saveSetting(SharedPreferencesUtils.KEY_PASSWORD, user.getPassword());
        this.saveSetting(SharedPreferencesUtils.KEY_FULL_NAME, user.getUsername());

        return true;
    }


    public User getAccountPrefs() {

        User account = new User();

        account.setEmail(this.getSettingString(SharedPreferencesUtils.KEY_EMAIL));
        account.setUsername(this.getSettingString(SharedPreferencesUtils.KEY_FULL_NAME));

        return account;
    }

    public boolean removeAccountPrefs() {

        if (this.preferences == null) return false;

        this.removeSettings(SharedPreferencesUtils.KEY_EMAIL);
        this.removeSettings(SharedPreferencesUtils.KEY_PASSWORD);
        this.removeSettings(SharedPreferencesUtils.KEY_FULL_NAME);

        return true;
    }


    public boolean checkUserIsExist() {

        if (this.preferences == null) return false;

        String email = this.getSettingString(SharedPreferencesUtils.KEY_EMAIL);
        String pass = this.getSettingString(SharedPreferencesUtils.KEY_PASSWORD);

        return !(email == null || pass == null | email.equals("") || pass.equals(""));
    }
}
