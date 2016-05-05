package com.yosta.goshare.activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yosta.goshare.R;
import com.yosta.goshare.models.Audio;
import com.yosta.goshare.models.Image;
import com.yosta.goshare.models.Resource;
import com.yosta.goshare.models.ResourceType;
import com.yosta.goshare.models.User;
import com.yosta.goshare.models.Video;
import com.yosta.goshare.modules.ImageHelper;
import com.yosta.goshare.modules.RequestToServer;
import com.yosta.goshare.tasks.ImageAnalyzeTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ShareActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;
    private static final int REQUEST_TAKE_VIDEO = 2;
    private static final int REQUEST_SELECT_VIDEO_IN_ALBUM = 3;
    private static final int REQUEST_SELECT_AUDIO_IN_ALBUM = 4;

    private Uri mUriPhotoTaken;
    private Uri mUriVideoTaken;
    private Uri mUriAudio;

    @Bind(R.id.img_view)
    ImageView imgView;
    @Bind(R.id.video_view)
    VideoView videoView;
    @Bind(R.id.btnUpload)
    Button btnUpload;
    @Bind(R.id.txt_view_song_duration)
    TextView txtViewSongDuration;
    @Bind(R.id.seekBar)
    SeekBar seekBar;
    @Bind(R.id.btn_rew)
    ImageButton btnRewind;
    @Bind(R.id.btn_pause)
    ImageButton btnPause;



    @Bind(R.id.btn_play)
    ImageButton btnPlay;
    @Bind(R.id.btn_ff)
    ImageButton btnFastFoward;
    @Bind(R.id.txt_song_name)
    EditText txtSongName;
    @Bind(R.id.txt_singer)
    EditText txtSinger;
    @Bind(R.id.txt_caption)
    EditText txtCaption;

    private Resource resource;
    private User owner;

    @Bind(R.id.layout_content)
    LinearLayout layoutContent;

    private Bitmap mBitmap;
    private String filename = null;

    private MediaPlayer mediaPlayer;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();

    protected Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        ButterKnife.bind(this);

        OnCreate();
    }

    private void OnCreate() {
        context = getApplicationContext();
        Intent intent = getIntent();
        String type = (String) intent.getExtras().get("type");

        if (type != null) {
            if (type.equals("image")) {
                findViewById(R.id.scview).setVisibility(View.VISIBLE);
                UpdateToolBar("Image sharing ...");
            } else if (type.equals("video")) {
                findViewById(R.id.video_view).setVisibility(View.VISIBLE);
                findViewById(R.id.scview).setVisibility(View.GONE);
                UpdateToolBar("Video sharing ...");
            } else if (type.equals("audio")) {
                findViewById(R.id.layout_audio).setVisibility(View.VISIBLE);
                findViewById(R.id.scview).setVisibility(View.GONE);
                UpdateToolBar("Audio sharing ...");
                initAudioComponents();
            } else if (type.equals("status")) {
                UpdateToolBar("Status sharing ...");
            }
        }
        initComponent();
    }

    private void UpdateToolBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initAudioComponents() {

        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if we can go forward at forwardTime seconds before song endes
                if (filename == null)
                    return;
                if ((timeElapsed - backwardTime) > 0) {
                    timeElapsed = timeElapsed - backwardTime;

                    //seek to the exact second of the track
                    mediaPlayer.seekTo((int) timeElapsed);
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filename == null)
                    return;
                mediaPlayer.pause();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filename == null)
                    return;
                mediaPlayer.start();
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                durationHandler.postDelayed(updateSeekBarTime, 100);
            }
        });

        btnFastFoward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filename == null)
                    return;
                //check if we can go forward at forwardTime seconds before song endes
                if ((timeElapsed + forwardTime) < finalTime) {
                    timeElapsed = timeElapsed + forwardTime;

                    //seek to the exact second of the track
                    mediaPlayer.seekTo((int) timeElapsed);
                }
            }
        });

        (findViewById(R.id.btn_pick_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAudio();
            }
        });
    }


    private void initComponent() {

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectPhoto();
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSelectVideo();
                return false;

            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitingDialog("Uploading...");
                uploadResource();
            }
        });
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekBar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            txtViewSongDuration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };


    private void showSelectVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select video");
        builder.setPositiveButton("Record video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Save the photo taken to a temporary file.
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    try {
                        File file = File.createTempFile("video_", ".mp4", storageDir);
                        mUriVideoTaken = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriVideoTaken);
                        startActivityForResult(intent, REQUEST_TAKE_VIDEO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        })
                .setNegativeButton("From gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_SELECT_VIDEO_IN_ALBUM);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSelectPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select photo");
        builder.setPositiveButton("Take photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Save the photo taken to a temporary file.
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    try {
                        File file = File.createTempFile("image_", ".jpg", storageDir);
                        mUriPhotoTaken = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        })
                .setNegativeButton("From gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        //if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
                        //}
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSelectAudio() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, REQUEST_SELECT_AUDIO_IN_ALBUM);
    }

    private void uploadResource() {
        RequestParams params = new RequestParams();
        params.put("cid", "insert_resource");
        params.put("note", txtCaption.getText());
        String type = null;
        if (resource.getType() == ResourceType.STATUS) {
            type = "status";
        }
        else if (resource.getType() == ResourceType.IMAGE) {
            type = "image";
        }
        else if (resource.getType() == ResourceType.VIDEO) {
            type = "video";
        }
        else if (resource.getType() == ResourceType.AUDIO) {
            type = "audio";
        }
        params.put("user_id", "1");
        params.put("location_lat", "10.763"); //params.put("location_lat", resource.getLocation().latitude);
        params.put("location_long", "106.682"); //params.put("location_long", resource.getLocation().longitude);

        if (resource.getType() != ResourceType.STATUS) {
            try {
                params.put("upload_file", new File(resource.getLocalFilePath()));
                if (resource.getType() == ResourceType.AUDIO) {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(resource.getLocalFilePath());
                    params.put("title", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                    params.put("artist", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "File not found!", Toast.LENGTH_SHORT).show();
            }
        }

        RequestToServer.sendRequest(RequestToServer.Method.POST, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            int error = response.getInt("error");
                            if (error == 0) {
                                Toast.makeText(getApplicationContext(), "Resource upload successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                String error_message = response.getString("error_message");
                                Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(), "Upload resource failed!", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
        );
    }

    private void uploadFile(String filePath) {
        try {
            if (filePath == null) {
                return;
            }
            File myFile = new File(filePath);
            RequestParams params = new RequestParams();
            params.put("file_name", myFile);


            RequestToServer.sendRequest(RequestToServer.Method.POST, params,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Toast.makeText(getApplicationContext(), "Upload Successfully!", Toast.LENGTH_SHORT).show();
                            filename = null;
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Toast.makeText(getApplicationContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap ScaleBitmap(Bitmap bmp, int w, int h) {

        int height = 0, width = 0;

        if ((1.0 * width / bmp.getWidth()) < (1.0 * height / bmp.getHeight())) {
            height = (int) (bmp.getHeight() * 1.0 * width / bmp.getWidth());
            width = (int) (bmp.getWidth() * 1.0 * width / bmp.getWidth());
        } else {
            height = (int) (bmp.getHeight() * 1.0 * height / bmp.getHeight());
            width = (int) (bmp.getWidth() * 1.0 * height / bmp.getHeight());
        }

        return Bitmap.createScaledBitmap(bmp, w, h, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());


                    if (mBitmap != null) {
                        // Show the image on screen.
                        //imgView.setImageBitmap(mBitmap);
                        Drawable background = new BitmapDrawable(getResources(), mBitmap);
                        imgView.setBackground(background);
                    }

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/GoShare_images";
                    File dir = new File(file_path);
                    if (!dir.exists())
                        dir.mkdirs();
                    File file = new File(dir, "image_" + timeStamp + ".jpg");
                    try {
                        FileOutputStream fOut = new FileOutputStream(file);

                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);


                        fOut.flush();
                        fOut.close();

                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                        filename = file.toString();

                        layoutContent.removeAllViews();
                        new ImageAnalyzeTask(txtCaption, getApplicationContext(), layoutContent, btnUpload).execute(mBitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    resource = new Image(filename, owner, txtCaption.getText().toString());
                }
                break;
            case REQUEST_SELECT_VIDEO_IN_ALBUM:
            case REQUEST_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {
                    Uri videoUri;
                    if (data == null || data.getData() == null) {
                        videoUri = mUriVideoTaken;
                    } else {
                        videoUri = data.getData();
                    }

                    videoView.setVideoURI(videoUri);


                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/GoShare_videos";
                    File dir = new File(file_path);
                    if (!dir.exists())
                        dir.mkdirs();

                    String sourceFilename = videoUri.getPath();
                    String destinationFilename = file_path + "/video_" + timeStamp + ".mp4";
                    filename = destinationFilename;

                    final int chunkSize = 1024;  // We'll read in one kB at a time
                    byte[] videoData = new byte[chunkSize];

                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = getContentResolver().openInputStream(videoUri);
                        out = new FileOutputStream(destinationFilename);  // I'm assuming you already have the File object for where you're writing to

                        int bytesRead;
                        while ((bytesRead = in.read(videoData)) > 0) {
                            out.write(Arrays.copyOfRange(videoData, 0, Math.max(0, bytesRead)));
                        }

                    } catch (Exception ex) {
                        //Log.e("Something went wrong.", ex);
                    } finally {
                        try {
                            if (in != null) in.close();
                            if (out != null) out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    resource = new Video(filename, owner, txtCaption.getText().toString());
                }
                break;
            case REQUEST_SELECT_AUDIO_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    mUriAudio = data.getData();


                    mediaPlayer = MediaPlayer.create(this, mUriAudio);
                    finalTime = mediaPlayer.getDuration();

                    seekBar.setMax((int) finalTime);
                    seekBar.setClickable(false);

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/GoShare_audios";
                    File dir = new File(file_path);
                    if (!dir.exists())
                        dir.mkdirs();

                    String sourceFilename = mUriAudio.getPath();
                    String destinationFilename = file_path + "/audio_" + timeStamp + ".mp3";
                    filename = destinationFilename;


                    final int chunkSize = 1024;  // We'll read in one kB at a time
                    byte[] audioData = new byte[chunkSize];

                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = getContentResolver().openInputStream(mUriAudio);
                        out = new FileOutputStream(destinationFilename);  // I'm assuming you already have the File object for where you're writing to

                        int bytesRead;
                        while ((bytesRead = in.read(audioData)) > 0) {
                            out.write(Arrays.copyOfRange(audioData, 0, Math.max(0, bytesRead)));
                        }

                    } catch (Exception ex) {
                        //Log.e("Something went wrong.", ex);
                    } finally {
                        try {
                            if (in != null) in.close();
                            if (out != null) out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    resource = new Audio(filename, owner, txtSongName.getText().toString(), txtSinger.getText().toString(), txtCaption.getText().toString());
                }
                break;
            default:
                break;
        }
    }
    private ProgressDialog progress;
    private Thread t;
    private void showWaitingDialog(String message) {
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        t = new Thread() {
            public void run() {
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
