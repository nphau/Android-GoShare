package com.yosta.goshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.yosta.goshare.R;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.ui.ListItem;

/**
 * Created by HenryPhuc on 3/26/2016.
 */
public class ResourcesAdapter extends ArrayAdapter<ListItem> {

    protected Context context;
    protected LayoutInflater mInflater;

    public ResourcesAdapter(Context context) {
        super(context, 0);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            ViewHolder holder;

            int rowType = getItemViewType(position);

            if (convertView == null) {

                holder = new ViewHolder();

                if (rowType == ResourceType.AUDIO.ordinal()) {
                    convertView = mInflater.inflate(R.layout.fragment_audio_row, null);
                    holder.View = getItem(position).getView(context, mInflater, convertView);
                }
                if (rowType == ResourceType.IMAGE.ordinal()) {
                    convertView = mInflater.inflate(R.layout.fragment_image_row, null);
                    holder.View = getItem(position).getView(context, mInflater, convertView);
                }
                if (rowType == ResourceType.STATUS.ordinal()) {
                    convertView = mInflater.inflate(R.layout.fragment_status_row, null);
                    holder.View = getItem(position).getView(context, mInflater, convertView);
                }
                if (convertView != null) {
                    convertView.setTag(holder);
                }
            } else {
               // holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class ViewHolder {
        public View View;
    }
}
