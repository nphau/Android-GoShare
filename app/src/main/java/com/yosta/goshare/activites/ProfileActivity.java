package com.yosta.goshare.activites;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.yosta.goshare.R;
import com.yosta.goshare.adapters.ViewPagerAdapter;
import com.yosta.goshare.fragments.AppSettingFragment;
import com.yosta.goshare.fragments.NoConnectionFragment;
import com.yosta.goshare.models.User;
import com.yosta.goshare.transformers.ZoomOutPageTransformer;
import com.yosta.goshare.utils.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        OnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        OnStart();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    private void OnStart() {
        String fullName = "Nguyễn Phúc Hậu";
        user = new User(fullName);

        //
        toolbarLayout.setTitle(user.getUsername());
    }

    private void OnCreate() {

        OnSettingToolBar();
        OnSettingViewPage();
        OnSettingTabLayout();
        OnSettingToolbarLayout();

    }

    private void OnSettingTabLayout() {

        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.setIcon(R.drawable.ic_user_group);
        }
        tab = tabLayout.getTabAt(1);

        if (tab != null) {
            tab.setIcon(R.drawable.ic_comment);
        }
        tab = tabLayout.getTabAt(2);

        if (tab != null)
            tab.setIcon(R.drawable.ic_favorite_red);
        tab = tabLayout.getTabAt(3);
        if (tab != null)
            tab.setIcon(R.drawable.ic_menu);

        tabLayout.setSmoothScrollingEnabled(true);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void OnSettingViewPage() {
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (!AppUtils.isNetworkConnected(this)) {
            adapter.addFrag(new NoConnectionFragment());
            adapter.addFrag(new NoConnectionFragment());
        } else {
            adapter.addFrag(new NoConnectionFragment());
            adapter.addFrag(new NoConnectionFragment());
        }

        adapter.addFrag(new NoConnectionFragment());
        adapter.addFrag(new AppSettingFragment());
        viewPager.setAdapter(adapter);
    }

    private void OnSettingToolbarLayout() {
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
            toolbarLayout.setExpandedTitleTypeface(font);
            toolbarLayout.setCollapsedTitleTypeface(font);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OnSettingToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
