package com.wayyeasy.wayyeasydoctors.CustomDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.wayyeasy.wayyeasydoctors.R;

public class ResponseDialog {
    public void showDialog(Activity activity, Drawable drawable, String header, int color, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_response_layout);

        ImageView responseImage = (ImageView) dialog.findViewById(R.id.dialog_image);
        TextView dialogHeader = (TextView) dialog.findViewById(R.id.dialog_header);
        TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialog_message);

        responseImage.setImageDrawable(drawable);

        dialogHeader.setText(header);
        dialogHeader.setTextColor(color);
        dialogMessage.setText(message);

        TextView btnOK = (TextView) dialog.findViewById(R.id.dialog_btn_ok);

        btnOK.setTextColor(color);

        btnOK.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}