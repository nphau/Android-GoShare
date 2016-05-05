package com.yosta.goshare.modules;
/**
 * Created by AN on 26/03/2016.
 */
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ImageVerification {

    private UUID _mFaceId0;
    private UUID _mFaceId1;
    private Bitmap mBitmap0;
    private Bitmap mBitmap1;
    public boolean isRunning;
    public boolean isIdentical;
    private boolean isNonDetected;

    private class VerificationTask extends AsyncTask<Void, String, VerifyResult> {
        private UUID mFaceId0;
        private UUID mFaceId1;

        VerificationTask (UUID faceId0, UUID faceId1) {
            mFaceId0 = faceId0;
            mFaceId1 = faceId1;
        }

        @Override
        protected VerifyResult doInBackground(Void... params) {
            FaceServiceClient faceServiceClient = new FaceServiceRestClient("50039a943518488e93e06ba94cc9d88b");
            try{
                publishProgress("Verifying...");

                // Start verification.
                VerifyResult res = faceServiceClient.verify(
                        mFaceId0,      /* The first face ID to verify */
                        mFaceId1);     /* The second face ID to verify */
                setUiAfterVerification(res);
                return res;
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(VerifyResult result) {
        }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        // Index indicates detecting in which of the two images.
        private int mIndex;
        private boolean mSucceed = true;

        DetectionTask(int index) {
            mIndex = index;
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient("50039a943518488e93e06ba94cc9d88b");
            try{
                publishProgress("Detecting...");

                // Start detection.
                Face[] res = faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
                setUiAfterDetection(res, mIndex, mSucceed);
                return res;
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(Face[] result) {
        }
    }

    public ImageVerification(Bitmap bmpTest, Bitmap bmpSource) {
        mBitmap0 = bmpTest;
        _mFaceId0 = null;
        mBitmap1 = bmpSource;
        _mFaceId1 = null;
    }

    public void verify() {
        _mFaceId0 = _mFaceId1 = null;
        isRunning = true;
        detect(mBitmap0, 0);
        detect(mBitmap1, 1);
        if (isNonDetected == true)
        {
            isIdentical = false;
            isRunning = false;
            return;
        }
        new VerificationTask(_mFaceId0, _mFaceId1).execute();
    }

    private void setUiAfterVerification(VerifyResult result) {
        addLog("Verification result: " + (result.isIdentical? "True": "False"));
        isIdentical = result.isIdentical;
        isRunning = false;
    }

    private void setUiAfterDetection(Face[] result, int index, boolean succeed) {
        addLog("Detected " + result.length + "faces");
        if (result.length == 0)
        {
            isIdentical = false;
            isNonDetected = true;
        }
        else
        {
            if (index == 0)
            {
                _mFaceId0 = result[0].faceId;
            }
            else
            {
                _mFaceId1 = result[0].faceId;
            }
        }
    }

    private void detect(Bitmap bitmap, int index) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        try {
            new DetectionTask(index).execute(inputStream).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void addLog(String log) {
        Log.d("Verification Log: ", log);
    }
}