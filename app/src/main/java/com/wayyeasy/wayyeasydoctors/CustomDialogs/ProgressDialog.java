package com.wayyeasy.wayyeasydoctors.CustomDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.wayyeasy.wayyeasydoctors.R;

public class ProgressDialog {
    Dialog dialog;
    Activity activity;

    public ProgressDialog() {
    }

    public ProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_process_layout);

        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
