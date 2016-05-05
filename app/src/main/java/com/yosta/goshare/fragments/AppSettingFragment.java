package com.yosta.goshare.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yosta.goshare.R;
import com.yosta.goshare.activites.LoginActivity;
import com.yosta.goshare.activites.MainActivity;
import com.yosta.goshare.activites.MapsActivity;
import com.yosta.goshare.adapters.MenuAdapter;
import com.yosta.goshare.utils.AppUtils;
import com.yosta.goshare.utils.SharedPreferencesUtils;
import com.yosta.goshare.utils.UserInterfaceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppSettingFragment extends android.support.v4.app.Fragment {

    private int languagePrefs = 1;
    private Activity activity = null;
    private MenuAdapter menuAdapter = null;
    private int textArrID = 0, iconArrID = 0;
    private SharedPreferencesUtils preferencesUtils = null;
    @Bind(R.id.lv_menu)
    ListView listView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Setup(getActivity(), R.array.settings_text, R.array.settings_icon);
        if (activity != null)
            this.preferencesUtils = new SharedPreferencesUtils(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = getActivity();

        this.menuAdapter = UserInterfaceUtils.LoadListMenuAction(this.activity, this.textArrID, this.iconArrID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        ButterKnife.bind(this, rootView);

        this.listView.setAdapter(menuAdapter);

        fnMainHandle();

        return rootView;
    }

    private void Setup(Activity activity, int textArrID, int iconArrID) {
        this.activity = activity;
        this.textArrID = textArrID;
        this.iconArrID = iconArrID;
    }

    private void fnMainHandle() {

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SetupHandle(position);
            }
        });
    }

    protected void SetupHandle(int position) {
        switch (position) {
            case 0:
                onLoadProfile();
                break;
            case 1:
                onOpenMap();
                break;
            case 2:
                LocationSettings();
                break;
            case 3:
                LanguageSetting();
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                AboutSetting();
                break;
            default:
                break;
        }
    }

    private void onOpenMap() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        startActivity(intent);
    }

    private void LanguageSetting() {

        MenuAdapter languageAdapter = UserInterfaceUtils.LoadListMenuAction(
                activity,
                R.array.language_item_text,
                R.array.language_item_icon);

        if (languageAdapter == null) return;

        final Dialog langDlg = new Dialog(activity);

        langDlg.setContentView(R.layout.fragment_listview);
        langDlg.setTitle(getString(R.string.str_language));
        langDlg.setCanceledOnTouchOutside(true);
        langDlg.setCancelable(true);

        ListView langListView = (ListView) langDlg.findViewById(R.id.lv_menu);
        langListView.setAdapter(languageAdapter);

        langDlg.show();

        langListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: // Vietnamese
                        languagePrefs = 0;
                        break;
                    case 1: // English
                        languagePrefs = 1;
                        break;
                }
                langDlg.dismiss();
                fnConfirmChange(langDlg);
            }
        });
    }

    private void fnConfirmChange(final Dialog langDlg) {

        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(new ContextThemeWrapper(this.activity, R.style.AppTheme_AlertDialog));

        alertDialog.setTitle(getString(R.string.str_action_confirm_change_language))
                .setCancelable(false)
                .setMessage(getString(R.string.str_confirm_change_language));

        alertDialog.setPositiveButton(getString(R.string.str_exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                preferencesUtils.saveSetting(SharedPreferencesUtils.KEY_LANGUAGE, languagePrefs);
                preferencesUtils.changeAppLanguage(languagePrefs);
                langDlg.dismiss();
                dialog.dismiss();
                activity.finish();
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(AppUtils.EXTRA_INTENT, 4);
                startActivity(i);
            }
        });

        alertDialog.setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                langDlg.dismiss();
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void LocationSettings() {
        if (!AppUtils.isGPSEnable(activity)) {
            activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void AboutSetting() {
        Dialog aboutDlg = new Dialog(activity, R.style.AppTheme_Dialog);
        aboutDlg.setContentView(R.layout.fragment_about_dialog);
        aboutDlg.setCanceledOnTouchOutside(false);
        TextView
                txtVersionName = (TextView) aboutDlg.findViewById(R.id.txt_version),
                txtDev = (TextView) aboutDlg.findViewById(R.id.txt_dev);

        txtDev.setText(getString(R.string.app_author));
        txtVersionName.setText(AppUtils.getAppVersion(activity));

        aboutDlg.show();
    }

    private void onLoadProfile() {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
