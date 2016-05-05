package com.yosta.goshare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.yosta.goshare.R;
import com.yosta.goshare.activites.ShareActivity;
import com.yosta.goshare.adapters.ResourcesAdapter;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.tasks.GetResourceTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResourceViewFragment extends android.support.v4.app.Fragment {

    @Bind(R.id.lv_menu)
    ListView lvResource;

    @Bind(R.id.fab)
    FloatingActionMenu btnMenu;
    @Bind(R.id.fab32)
    FloatingActionButton btnImage;
    @Bind(R.id.fab22)
    FloatingActionButton btnVideo;
    @Bind(R.id.fab12)
    FloatingActionButton btnAudio;
    @Bind(R.id.fabStatus)
    FloatingActionButton btnStatus;
    @Bind(R.id.fabFilter)
    FloatingActionButton btnFilter;


    private int idxFilter = 0;
    private String[] strFiler = {"Day", "Week", "Month", "All"};
    private int[] bgRes = {
            android.R.drawable.ic_menu_day,
            android.R.drawable.ic_menu_week,
            android.R.drawable.ic_menu_month,
            android.R.drawable.ic_menu_my_calendar,
    };


    protected ResourcesAdapter adapter = null;
    protected Activity activity;
    protected Context context;

    public ResourceViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.adapter = new ResourcesAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        ButterKnife.bind(this, rootView);


        lvResource.setAdapter(adapter);


        new GetResourceTask(getContext(), adapter).process();


        OnGetView();

        OnClickEvent();

        return rootView;
    }

    protected void OnGetView() {
        btnMenu.setVisibility(View.VISIBLE);
    }

    protected void OnButtonMenuItemClick(String arg0, String arg1) {
        btnMenu.close(true);
        Intent intent = new Intent(getActivity(), ShareActivity.class);
        intent.putExtra(arg0, arg1);
        startActivityForResult(intent, 0);
    }

    protected void OnClickEvent() {

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idxFilter = (idxFilter + 1) % strFiler.length;
                btnFilter.setLabelText(strFiler[idxFilter]);
                //btnFilter.setBackgroundResource(bgRes[idxFilter]);
                btnFilter.setImageResource(bgRes[idxFilter]);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnButtonMenuItemClick("type", "image");
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnButtonMenuItemClick("type", "video");
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnButtonMenuItemClick("type", "audio");
            }
        });

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnButtonMenuItemClick("type", "status");
            }
        });

        lvResource.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                btnMenu.close(true);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }
}
