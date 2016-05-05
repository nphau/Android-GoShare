package com.yosta.goshare.activites;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.yosta.goshare.R;
import com.yosta.goshare.models.Audio;
import com.yosta.goshare.models.Image;
import com.yosta.goshare.models.MyLocation;
import com.yosta.goshare.models.Resource;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.Video;
import com.yosta.goshare.models.ui.AudioItem;
import com.yosta.goshare.models.ui.ImageItem;
import com.yosta.goshare.models.ui.StatusItem;
import com.yosta.goshare.modules.DirectionFinder;
import com.yosta.goshare.modules.DirectionFinderListener;
import com.yosta.goshare.modules.RequestToServer;
import com.yosta.goshare.modules.Route;
import com.yosta.goshare.tasks.GetResourceTask;
import com.yosta.goshare.utils.MapWrapperLayout;
import com.yosta.goshare.utils.OnInterInfoWindowTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, DirectionFinderListener {
    ViewGroup resourceInfoWindow;
    MapFragment mapFragment;
    InputMethodManager imm;
    LinearLayout laySearch;
    ImageButton btnDirection;
    ImageButton btnMoreInfo;
    OnInterInfoWindowTouchListener btnDirectionListener;
    OnInterInfoWindowTouchListener btnMoreInfoListener;
    MapWrapperLayout mapWrapperLayout;

    private ArrayList<Marker> originMarkers = new ArrayList<>();
    private ArrayList<Marker> destinationMarkers = new ArrayList<>();
    private ArrayList<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<Resource> resources = new ArrayList<>();

    HashMap<Marker, Resource> markers = new HashMap<>();

    private GoogleMap mMap;

    Marker currMarker = null;
    Marker myMarker = null;
    Location myLocation;

    MediaPlayer mediaPlayer;
    boolean isInitMedia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_maps);


        onGetView();
        mapFragment.getMapAsync(this);

        mediaPlayer = new MediaPlayer();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            myLocation = new Location(LocationManager.GPS_PROVIDER);
            myLocation.setLatitude(10.762710);
            myLocation.setLongitude(106.682361);
        }
        myMarker = null;

        onMyLocation();

        loadResource();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currMarker != null) {
                    Resource resource = markers.get(currMarker);
                    currMarker.setIcon(BitmapDescriptorFactory.fromResource(getMarker(resource.getType())));
                    currMarker = null;
                }

            }

        });

        btnDirectionListener = new OnInterInfoWindowTouchListener(btnDirection) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                try {
                    new DirectionFinder(MapsActivity.this, myMarker.getPosition(), marker.getPosition()).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };

        btnMoreInfoListener = new OnInterInfoWindowTouchListener(btnMoreInfo) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Resource resource = markers.get(marker);
                try {
                    if (!isInitMedia) {
                        isInitMedia = true;
                        mediaPlayer.setDataSource(resource.getDirectLink());
                        mediaPlayer.prepare();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();

                }
                else {
                    mediaPlayer.start();
                }
            }
        };

        btnDirection.setOnTouchListener(btnDirectionListener);
        btnMoreInfo.setOnTouchListener(btnMoreInfoListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                resetMedia();
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (myMarker != null && marker.getId().equals(myMarker.getId()))
                    return null;

                if (currMarker != null) {
                }

                if (currMarker == null || !currMarker.getId().equals(marker.getId())) {
                    resetMedia();
                }

                currMarker = marker;

                Log.d("get view", "get view");

                Resource resource = markers.get(marker);

                //btnDirectionListener.setMarker(marker);

                ImageView imageView = (ImageView) resourceInfoWindow.findViewById(R.id.picture);
                TextView textView = (TextView)resourceInfoWindow.findViewById(R.id.name);
                ImageView logo = (ImageView)resourceInfoWindow.findViewById(R.id.logo);
                btnMoreInfo.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);


                if (0 == resource.getType().compareTo(ResourceType.IMAGE)) {

                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(((Image) resource).getBitmap());
                    Picasso.with(resourceInfoWindow.getContext()).load(resource.getDirectLink()).into(imageView);

                    textView.setText(resource.getCaption());
                }

                if (0 == resource.getType().compareTo(ResourceType.STATUS)) {

                    textView.setText(resource.getCaption());
                }

                if (0 == resource.getType().compareTo(ResourceType.AUDIO)) {
                    btnMoreInfoListener.setMarker(marker);
                    btnMoreInfo.setVisibility(View.VISIBLE);
                    textView.setText(resource.getCaption());

                    if (isInitMedia) {
                        if (mediaPlayer.isPlaying())
                        {
                            btnMoreInfo.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play));

                        }
                        else {
                            btnMoreInfo.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_pause));
                        }
                    }
                }

                mapWrapperLayout.setMarkerWithInfoWindow(marker, resourceInfoWindow);
                return resourceInfoWindow;

            }
        });

    }

    private void resetMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer();
        btnMoreInfo.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play));
        isInitMedia = false;
    }

    private void loadResource() {
        resources = new ArrayList<>();

        try {

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
                                    } else if (type.equals("video")) {
                                        resource = new Video(null, null, note);
                                    } else if (type.equals("audio")) {
                                        String title = results.getJSONObject(i).getString("title");
                                        String singer = results.getJSONObject(i).getString("artist");
                                        resource = new Audio(directLink, null, title, singer, note);

                                        AudioItem audioItem = new AudioItem(directLink, null, title, singer, note);
                                        audioItem.setDateCreation(inputDate);
                                    } else if (type.equals("status")) {
                                        resource = new com.yosta.goshare.models.Status(note, null);
                                        StatusItem statusItem = new StatusItem(note, null);
                                        statusItem.setDateCreation(inputDate);
                                    }
                                    if (resource != null) {
                                        resource.setLocation(new MyLocation(location_long, location_lat));
                                        resource.setDateCreation(inputDate);
                                        resources.add(resource);
                                    }

                                }
                                onDisplay();

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

    private void hideSearchBox() {
        laySearch.setVisibility(View.GONE);
        imm.hideSoftInputFromWindow(laySearch.getWindowToken(), 0);
    }

    private void onGetView() {
        laySearch = (LinearLayout) findViewById(R.id.laySearch);
        //listAdapter = new ListAdapter(MainActivity.this, R.layout.content_place, addresses);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        resourceInfoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.layout_buildinginfo, null);
        //peopleInfoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.layout_peopleinfo, null);
        btnDirection = new ImageButton(this);
        btnMoreInfo = (ImageButton)resourceInfoWindow.findViewById(R.id.btnPlay);

    }

    public void onDisplay() {
        for (Resource resource : resources) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(resource.getLocation().latitude, resource.getLocation().longitude))
                    .title(resource.getCaption())
                    .icon(BitmapDescriptorFactory.fromResource(getMarker(resource.getType())))
            );
            markers.put(marker, resource);
        }

    }

    private int getMarker(ResourceType type) {
        return R.drawable.marker;
    }

    private void onMyLocation() {
        showMyLocation();
        changeCameraView(myLocation);
    }


    private void resetMarkers() {
        for (Marker marker : markers.keySet()) {
            marker.remove();
        }
        markers.clear();
        currMarker = null;
    }

    public void showMyLocation() {
        if (myMarker != null)
            myMarker.remove();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void changeCameraView(Location location) {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 20, null);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))     // Sets the center of the map to Mountain View
                .zoom(12)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    Bitmap getPlaceMarkerfromID(int id, boolean border) {
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), id),100,100, false);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        if (border) {
            paint.setStrokeWidth(15);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);

            canvas.drawRect(new Rect(0, 0, bitmap.getHeight(), bitmap.getWidth()), paint);
        }

        return bitmap;
    }

    Bitmap getPeopleMarkerfromID(int id) {
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), id),100,100, false);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        canvas.drawCircle(bitmap.getWidth() / 2,
                bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        return output;
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(0xFF039be5)
                    .width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));


            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }
}