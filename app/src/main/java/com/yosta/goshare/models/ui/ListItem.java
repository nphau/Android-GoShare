package com.yosta.goshare.models.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by nphau on 2/16/2016.
 */
public interface ListItem {
    int getViewType();
    View getView(Context context, LayoutInflater inflater, View convertView);
}
