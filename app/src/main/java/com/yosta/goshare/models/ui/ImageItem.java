package com.yosta.goshare.models.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yosta.goshare.R;
import com.yosta.goshare.models.Image;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.User;
import com.yosta.goshare.models.ui.ListItem;
import com.yosta.goshare.utils.AppUtils;

import java.net.URL;

/**
 * Created by HenryPhuc on 3/26/2016.
 */
public class ImageItem extends Image implements ListItem {

    protected Context context;
    protected User owner;
    ImageView imageView;
    String directLink;

    public ImageItem(String directLink, User owner, String caption) {
        super(directLink, owner, caption);
        this.directLink = directLink;
        this.owner = owner;
    }

    @Override
    public int getViewType() {
        return ResourceType.IMAGE.ordinal();
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        this.context = context;

        try {
            View view = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_image_row, null);
                view = convertView;
            }

            imageView = (ImageView) convertView.findViewById(R.id.img_cover);
            Picasso.with(context).load(directLink).into(imageView);
            TextView txtUsername = (TextView) convertView.findViewById(R.id.txt_username);
            txtUsername.setText(getCaption());

            TextView txtTimer = (TextView) convertView.findViewById(R.id.txt_timer);
            txtTimer.setText(AppUtils.convertDate(getDateCreation()));

            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
