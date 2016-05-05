package com.yosta.goshare.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.yosta.goshare.config.NetworkConfig;
import com.yosta.goshare.utils.AppUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.yosta.goshare.utils.ConverterUtils.String2Bitmap;

public class GetSightBigImg extends AsyncTask<Integer, Void, Bitmap> {

    private Activity activity;
    private ImageView imageView;
    public GetSightBigImg(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {

        try {
            int SightID = params[0];
            // Check network state
            if (!AppUtils.isNetworkConnected(this.activity)) return null;

            // Webservice URL
            URL url = new URL(NetworkConfig.URL + NetworkConfig.SightsControllers + "/" + SightID);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(NetworkConfig.HTTP_GET);
            connection.setRequestProperty("Content-Type", NetworkConfig.FORMAT_JSON);


            // Check if Status 200 OK
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return null;

            // Read Response data
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            is.close();

            return String2Bitmap(builder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            this.cancel(true);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null)
            this.imageView.setImageBitmap(bitmap);
        else
            imageView.setVisibility(View.GONE);
    }
}