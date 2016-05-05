package com.yosta.goshare.models.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yosta.goshare.R;
import com.yosta.goshare.models.Audio;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.Status;
import com.yosta.goshare.models.User;
import com.yosta.goshare.utils.AppUtils;

/**
 * Created by HenryPhuc on 3/26/2016.
 */
public class StatusItem extends Status implements ListItem {

    protected Context context;

    public StatusItem(String status, User owner) {
        super(status, owner);
    }

    @Override
    public int getViewType() {
        return ResourceType.STATUS.ordinal();
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        this.context = context;

        try {

            View view = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_status_row, null);
                view = convertView;
            }

            TextView txtUsername = (TextView) convertView.findViewById(R.id.txt_username);
            txtUsername.setText(getCaption());

            TextView txtTimer = (TextView) convertView.findViewById(R.id.txt_timer);
            txtTimer.setText(AppUtils.convertDate(getDateCreation()));

            TextView txtStatus = (TextView) convertView.findViewById(R.id.txt_status);
            txtStatus.setText(getCaption());

            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
