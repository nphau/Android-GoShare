package com.yosta.goshare.activites;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yosta.goshare.R;
import com.yosta.goshare.adapters.ViewPagerAdapter;
import com.yosta.goshare.fragments.AppSettingFragment;
import com.yosta.goshare.fragments.NoConnectionFragment;
import com.yosta.goshare.fragments.ResourceViewFragment;
import com.yosta.goshare.transformers.ZoomOutPageTransformer;
import com.yosta.goshare.utils.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private boolean isBackPress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        OnCreate();

    }

    @Override
    protected void onResume() {
        super.onResume();
        OnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isBackPress) {
            finish();
        }

        isBackPress = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                isBackPress = false;
            }
        }, 2500);
    }

    private void OnResume() {
        if (!AppUtils.isGPSEnable(this)) {
            AskForGPS();
        } else {
            LoadDataWhenGPSEnable();

        }
    }

    private void AskForGPS() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        };
        AppUtils.ShowSnackBarNotifyWithAction(findViewById(R.id.root_view),
                getString(R.string.str_notify_err_no_gps),
                getString(R.string.str_turn_on), listener);
    }

    protected void LoadDataWhenGPSEnable()
    {

    }

    private void OnCreate() {

        isBackPress = false;

        setToolbar();

        setViewPager();

        setTabLayout();
    }

    private void setToolbar() {

    }

    private void setTabLayout() {

        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.setIcon(R.drawable.tab_ic_home);
        }
        tab = tabLayout.getTabAt(1);
        if (tab != null) {
            tab.setIcon(R.drawable.tab_ic_more);
        }
        tabLayout.setSmoothScrollingEnabled(true);

    }

    private void setViewPager() {

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(AppUtils.isNetworkConnected(this))
        {
            adapter.addFrag(new ResourceViewFragment());
        }
        else
        {
            adapter.addFrag(new NoConnectionFragment());
        }

        adapter.addFrag(new AppSettingFragment());

        viewPager.setAdapter(adapter);
    }
}