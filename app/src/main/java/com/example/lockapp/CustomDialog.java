package com.example.lockapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class CustomDialog {
    private void createCustomDialog(String title, String description, AlertDialog alertDialog, Context context) {
        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Get the LayoutInflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_successfull_failed_alert_dialog, null);
        // Set the custom layout to the AlertDialog.Builder
        builder.setView(dialogView);
        // Find views in the custom layout
        TextView titleTextView = dialogView.findViewById(R.id.custom_dialog_alert_title);
        TextView descriptionTextView = dialogView.findViewById(R.id.custom_dialog_alert_description);
        Button _validationButton = dialogView.findViewById(R.id.validation_button);
        // Set the title and description
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        _validationButton.setText(context.getResources().getString(R.string.close_txt));
        // Set the selection listener for the options
        AlertDialog finalAlertDialog = alertDialog;
        _validationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAlertDialog.dismiss();
            }
        });

        // Create the AlertDialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.white_corner_background);
        alertDialog.show();
    }
}
