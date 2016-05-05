package com.yosta.goshare.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yosta.goshare.R;
import com.yosta.goshare.models.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuAdapter extends ArrayAdapter<MenuItem> {

    private Activity activity;

    public MenuAdapter(Activity activity, ArrayList<MenuItem> objects) {
        super(activity, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuItemViewHolder viewHolder;

        try {

            if (convertView == null) {

                convertView = LayoutInflater.from(this.activity).inflate(R.layout.menu_item, null);

                viewHolder = new MenuItemViewHolder(convertView);

                convertView.setTag(viewHolder);

            } else viewHolder = (MenuItemViewHolder) convertView.getTag();

            viewHolder.InitData(getItem(position));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public class MenuItemViewHolder {

        public
        @Bind(R.id.menu_img)
        ImageView img;
        public
        @Bind(R.id.menu_name)
        TextView txtName;

        public MenuItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void InitData(MenuItem menu) {
            img.setImageResource(menu.getImage());
            txtName.setText(menu.getName());
        }
    }
}
