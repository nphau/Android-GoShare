package com.yosta.goshare.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yosta.goshare.R;
import com.yosta.goshare.activites.MainActivity;
import com.yosta.goshare.modules.ImageVerification;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HenryPhuc on 3/6/2016.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.txt_sign_up)
    TextView txtSignUp;

    @Bind(R.id.btn_login)
    Button btnLogin;

    @Bind(R.id.btn_login_by_face)
    Button btnLoginByFace;

    private Activity activity;

    Uri mUriPhotoTaken;
    final static int REQUEST_TAKE_PHOTO = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, rootView);

        if (rootView.findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return rootView;
            }
        }



        OnCreateView();

        btnLoginByFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Save the photo taken to a temporary file.
                    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        });

        return rootView;
    }

    private void OnCreateView() {

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSignUp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnLogIn();
            }
        });

        btnLoginByFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b = null; // lay tu camera

                Bitmap c = null; // lay tu o cung anh cua thanh an

                ImageVerification imageVerification = new ImageVerification(b, c);
                imageVerification.verify();
                while (imageVerification.isRunning) {

                }
                Boolean res = imageVerification.isIdentical;

            }
        });
    }

    private void OnSignUp() {
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setArguments(activity.getIntent().getExtras());
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, signUpFragment)
                .commit();
    }

    private void OnLogIn() {
        if (IsLoginSuccess()) {
            activity.startActivity(new Intent(getActivity(), MainActivity.class));
            activity.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        }
    }

    private boolean IsLoginSuccess() {

        try {
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    Bitmap mBitmap;

    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                showWaitingDialog("Verifying...");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                showWaitingDialog("Verifying...");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("login", "face");
                startActivity(intent);
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    private ProgressDialog progress;
    private Thread t;
    private void showWaitingDialog(String message) {
        progress = new ProgressDialog(getContext());
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
