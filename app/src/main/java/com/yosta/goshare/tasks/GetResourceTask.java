package com.yosta.goshare.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yosta.goshare.models.Audio;
import com.yosta.goshare.models.Image;
import com.yosta.goshare.models.MyLocation;
import com.yosta.goshare.models.Status;
import com.yosta.goshare.models.Resource;
import com.yosta.goshare.models.User;
import com.yosta.goshare.models.Video;
import com.yosta.goshare.models.ui.AudioItem;
import com.yosta.goshare.models.ui.ImageItem;
import com.yosta.goshare.adapters.ResourcesAdapter;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.ui.ListItem;
import com.yosta.goshare.models.ui.StatusItem;
import com.yosta.goshare.modules.RequestToServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by HenryPhuc on 3/26/2016.
 */
public class GetResourceTask {

    private ResourcesAdapter adapter = null;

    private ArrayList<Resource> resources;


    ArrayList<ListItem> list;
    public GetResourceTask(Context context, ResourcesAdapter adapter) {
        this.adapter = adapter;
        this.context = context;
    }

    public GetResourceTask() {
    }

    public Context context;

    private Boolean isFinishDownloadData = false;

    public void process() {

        list = new ArrayList<ListItem>();
        adapter.clear();



        try {

            resources = new ArrayList<Resource>();

            //Query to server by location
            RequestParams rparams = new RequestParams();
            rparams.put("location_long", "1.5");
            rparams.put("location_lat", "1.5");
            rparams.put("radius", "1");
            rparams.put("cid", "get_resource");

            RequestToServer.sendRequest(RequestToServer.Method.POST, rparams,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            try {
                                //Toast.makeText(context, "AC", Toast.LENGTH_SHORT).show();
                                list = new ArrayList<ListItem>();
                                JSONArray results = response.getJSONArray("result");
                                for (int i = 0; i < results.length(); ++i) {
                                    String id = results.getJSONObject(i).getString("id");
                                    String link = results.getJSONObject(i).getString("link");
                                    String note = results.getJSONObject(i).getString("note");
                                    String type = results.getJSONObject(i).getString("type");
                                    String strDate = results.getJSONObject(i).getString("date_creation");

                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-dd-mm");
                                    Date inputDate = null;
                                    try {
                                        inputDate = dateFormat.parse(strDate);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    double location_lat = results.getJSONObject(i).getDouble("location_lat");
                                    double location_long = results.getJSONObject(i).getDouble("location_long");

                                    //Toast.makeText(context, note, Toast.LENGTH_SHORT).show();

                                    String directLink = RequestToServer.ServerURL + link;
                                    Log.d("link", link);
                                    Log.d("direct_link", directLink);

                                    Resource resource = null;
                                    if (type.equals("image")) {
                                        resource = new Image(directLink, null, note);
                                        ImageItem imageItem = new ImageItem(directLink, null, note);
                                        imageItem.setDateCreation(inputDate);
                                        list.add(imageItem);
                                    } else if (type.equals("video")) {
                                        resource = new Video(null, null, note);
                                    } else if (type.equals("audio")) {
                                        String title = results.getJSONObject(i).getString("title");
                                        String singer = results.getJSONObject(i).getString("artist");
                                        resource = new Audio(directLink, null, title, singer, note);

                                        AudioItem audioItem = new AudioItem(directLink, null, title, singer, note);
                                        audioItem.setDateCreation(inputDate);
                                        list.add(audioItem);
                                    } else if (type.equals("status")) {
                                        resource = new com.yosta.goshare.models.Status(note, null);
                                        StatusItem statusItem = new StatusItem(note, null);
                                        statusItem.setDateCreation(inputDate);
                                        list.add(statusItem);
                                    }
                                    if (resource != null) {
                                        resource.setLocation(new MyLocation(location_long, location_lat));
                                        resource.setDateCreation(inputDate);
                                        resources.add(resource);
                                    }

                                }
                                adapter.clear();
                                Collections.reverse(list);
                                adapter.addAll(list);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    }
            );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
