package com.example.demoiotdashboard.alert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoiotdashboard.R;
import com.example.demoiotdashboard.controller.MainActivity;

public class Alerts {

    // Player has not selected a team
    public void noConnection(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Select your team.");
        builder.setCancelable(true);

        builder.setNeutralButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog a = builder.create();
        a.show();
    }
}