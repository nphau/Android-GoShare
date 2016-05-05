package com.yosta.goshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yosta.goshare.R;

public class EventsFragment extends android.support.v4.app.Fragment {

    protected ImageView imageCover;
    protected TextView txtContent, txtPrice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        imageCover = (ImageView) rootView.findViewById(R.id.img_event_cover);
        txtContent = (TextView) rootView.findViewById(R.id.txt_event_content);
        txtPrice = (TextView) rootView.findViewById(R.id.txt_event_price);

        onLoadData();

        return rootView;
    }

    private void onLoadData() {

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            imageCover.setBackground(getResources().getDrawable(R.drawable.event01, getActivity().getTheme()));
        } else {
            imageCover.setBackground(getResources().getDrawable(R.drawable.event01));
        }

        txtContent.setText("Chu du năm mới với vé đi Kuala Lumpur giá rẻ từ 20 USD");
        txtPrice.setText("20 USD");
    }
}
