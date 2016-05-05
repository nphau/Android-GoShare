package com.yosta.goshare.models.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yosta.goshare.R;
import com.yosta.goshare.models.Audio;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.User;
import com.yosta.goshare.utils.AppUtils;

import org.w3c.dom.Text;

/**
 * Created by HenryPhuc on 3/26/2016.
 */
public class AudioItem extends Audio implements ListItem {

    protected Context context;
    protected boolean IsPlay = false;
    protected ImageButton btnPlay;

    MediaPlayer mediaPlayer;

    public AudioItem(String filePath, User owner, String name, String singer, String caption) {
        super(filePath, owner, name, singer, caption);
        this.name = name;
        this.singer = singer;
    }


    @Override
    public int getViewType() {
        return ResourceType.AUDIO.ordinal();
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        this.context = context;

        try {

            View view = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.fragment_audio_row, null);
                view = convertView;
            }

            TextView txtTitle = (TextView) convertView.findViewById(R.id.txt_title);
            txtTitle.setText(getName());

            TextView txtSinger = (TextView) convertView.findViewById(R.id.txt_singer);
            txtSinger.setText(getSinger());

            TextView txtTimer = (TextView) convertView.findViewById(R.id.txt_timer);
            txtTimer.setText(AppUtils.convertDate(getDateCreation()));

            btnPlay = (ImageButton) convertView.findViewById(R.id.image_button);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IsPlay();
                }
            });

            Log.d("link_12345", getDirectLink());

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getDirectLink());
            mediaPlayer.prepare();

            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void IsPlay() {
        if(IsPlay)
        {
            btnPlay.setBackgroundResource(R.drawable.ic_play);
            mediaPlayer.pause();
        }
        else {
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        IsPlay = !IsPlay;
    }
}
