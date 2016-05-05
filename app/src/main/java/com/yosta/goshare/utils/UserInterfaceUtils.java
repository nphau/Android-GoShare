package com.yosta.goshare.utils;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

import com.yosta.goshare.adapters.MenuAdapter;
import com.yosta.goshare.models.MenuItem;
import com.yosta.goshare.tasks.GetSightBigImg;

import java.util.ArrayList;

/**
 * Created by nphau on 11/22/2015.
 */
public class UserInterfaceUtils {


    public static MenuAdapter LoadListMenuAction(Activity activity, int textArrID, int iconArrID) {
        try {

            String[] text = activity.getResources().getStringArray(textArrID);
            TypedArray icon = activity.getResources().obtainTypedArray(iconArrID);

            int iText = text.length;
            int iIcon = icon.length();

            if (iText == iIcon && iText > 0) {

                ArrayList<MenuItem> arrayList = new ArrayList<>();

                for (int i = 0; i < iText; i++)
                    arrayList.add(new MenuItem(icon.getResourceId(i, -1), text[i]));

                return new MenuAdapter(activity, arrayList);
            }

            // Recycle the typed array
            icon.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param activity  - Activity which contains image view
     * @param imageView - which is used to show image
     * @param sightID   - id of sight
     */
    public static void ImageToImageView(Activity activity, ImageView imageView, int sightID) {
        new GetSightBigImg(activity, imageView).execute(sightID);
    }

    public static void ImageToImageView(ImageView imageView, String image) {
        try {
            Bitmap bmp = ConverterUtils.String2Bitmap(image);

            if (bmp != null)
                imageView.setImageBitmap(bmp);
        } catch (Exception e) {
            Log.e("ImageToImageView", e.getMessage());
        }

    }

    public static void StringToWebView(WebView webView, String info) {
        String extra = String.valueOf(Html.fromHtml("<![CDATA[<body style=\"text-align:justify;\">"
                + info
                + "</body>]]>"));

        webView.loadData(extra, "text/html; charset=utf-8", null);
    }
}
