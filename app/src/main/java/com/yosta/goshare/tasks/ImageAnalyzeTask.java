package com.yosta.goshare.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalyzeResult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.contract.Face;
import com.microsoft.projectoxford.vision.contract.GenderEnum;
import com.yosta.goshare.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by HenryPhuc on 3/27/2016.
 */
public class ImageAnalyzeTask extends AsyncTask<Bitmap, Void, String> {

    protected Context mContext;
    protected AnalyzeResult mResult;
    protected VisionServiceClient mClient;
    protected LinearLayout layout;

    protected Button button;

    public ImageAnalyzeTask(EditText editText, Context context,
                            LinearLayout linearLayout, Button button) {
        this.mContext = context;
        this.button = button;
        if (mClient == null) {
            mClient = new VisionServiceRestClient("dcf826243e0d49e4b72f8f991ea5cfd6");
        }
        this.layout = linearLayout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.button.setText("Analyzing ... ");
        this.button.setEnabled(false);
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        try {
            Gson gson = new Gson();
            String[] features = {"All"};

            // Put the image into an input stream for detection.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            params[0].compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            mResult = this.mClient.analyzeImage(inputStream, features);
            return gson.toJson(mResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Analyze(mResult);
        this.button.setText("Upload");
        this.button.setEnabled(true);
        Toast.makeText(mContext, "Analyzing completed.", Toast.LENGTH_SHORT).show();
    }

    protected void CloneHashTagView(LinearLayout layoutContent, String hashTag) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(mContext, R.layout.fragment_hashtag_row, null);
        TextView view = (TextView) linearLayout.findViewById(R.id.txt_tag);
        view.setText(hashTag);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutContent.addView(linearLayout);
    }

    protected void Analyze(AnalyzeResult analyzeResult) {

        if (analyzeResult != null) {
            if (analyzeResult.adult.isAdultContent) {
                CloneHashTagView(layout, "Adult Content");
            }
            if (analyzeResult.adult.isRacyContent) {
                CloneHashTagView(layout, "Racy Content");
            }

            if (analyzeResult.categories.size() > 0) {
                for (Category category : analyzeResult.categories) {
                    if (category.score > 0.5) {
                        CloneHashTagView(layout, category.name);
                    }
                }
            }
            String tmpFaceCount;
            int facesCount = analyzeResult.faces.size();
            if (facesCount == 1) {
                tmpFaceCount = "One person";
            } else if (facesCount > 1) {
                tmpFaceCount = facesCount + " people";
            } else {
                tmpFaceCount = "No ones";
                CloneHashTagView(layout, tmpFaceCount);
                return;
            }
            CloneHashTagView(layout, tmpFaceCount);

            int maleCount = 0, femaleCount = 0;

            int faceCount = 0;
            for (Face face : analyzeResult.faces) {

                if (face.gender == GenderEnum.Male) {
                    ++maleCount;
                } else {
                    ++femaleCount;
                }

                faceCount++;
                String tmp = "[ " + faceCount + " ]" + " Gender:" + face.gender + " Age: " + face.age;

                CloneHashTagView(layout, tmp);
            }
            String tmp;
            if (maleCount != 0 && femaleCount != 0) {
                tmp = maleCount + " male | " + femaleCount + " female";
            } else {
                if (maleCount == 0) {
                    tmp = femaleCount + " female";
                } else {
                    tmp = maleCount + " male";
                }
            }
            CloneHashTagView(layout, tmp);
        }
    }
}
