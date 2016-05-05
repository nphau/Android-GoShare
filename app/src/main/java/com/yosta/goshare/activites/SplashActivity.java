package com.yosta.goshare.activites;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yosta.goshare.R;
import com.yosta.goshare.utils.SharedPreferencesUtils;

public class SplashActivity extends AppCompatActivity {


    private SharedPreferencesUtils preferencesUtils;

    private final int TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        OnCreate();
        OnChangeLanguageSetting();
    }

    @Override
    protected void onStart() {
        super.onStart();

        OnStart();
        callMainActivity();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void OnCreate() {
        preferencesUtils = new SharedPreferencesUtils(SplashActivity.this);
    }
    private void OnStart() {

       /* new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                callMainActivity();
            }
        }, TIMER);*/
/*
        Path mPath = new Path();
        mPath.moveTo(0.2f, 0.2f);
        mPath.lineTo(1f, 1f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startAnimation(AnimationUtils.loadInterpolator(
                    this,
                    android.R.interpolator.fast_out_slow_in),
                    10000,
                    mPath);
        }*/
        /*RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setRepeatCount(Animation.START_ON_FIRST_FRAME);
        anim.setDuration(TIMER);
        findViewById(R.id.imageView).startAnimation(anim);
*/
       /* Animation translation = new TranslateAnimation(0, 320, 0, 0);
        translation.setDuration(1000);
        translation.setRepeatCount(Animation.INFINITE);
        translation.setRepeatMode(Animation.REVERSE);
        findViewById(R.id.imageView).startAnimation(translation);*/
/*
        Animation resize = new ScaleAnimation(1, 2, 1, 2, 200, 50);
        resize.setDuration(2000);
        resize.setRepeatCount(Animation.INFINITE);
        resize.setRepeatMode(Animation.REVERSE);
        findViewById(R.id.imageView).startAnimation(resize);
*/
    }

    private void callMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        finish();
    }

    private void OnChangeLanguageSetting() {
        int languagePrefs = preferencesUtils.getSettingInt(SharedPreferencesUtils.KEY_LANGUAGE);
        preferencesUtils.changeAppLanguage(languagePrefs);
    }
}
