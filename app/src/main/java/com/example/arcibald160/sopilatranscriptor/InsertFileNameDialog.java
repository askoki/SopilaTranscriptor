package com.example.arcibald160.sopilatranscriptor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

public class InsertFileNameDialog extends DialogFragment {

    private String filename = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filename_dialog, null);

        final EditText filenameEditText = dialogView.findViewById(R.id.filename);
        Date currentTime = Calendar.getInstance().getTime();

        DateFormat df = new DateFormat();
        filename = df.format("yyyy_MM_dd_hh_mm_ss", currentTime).toString();

        filenameEditText.setText(filename);

        builder.setView(dialogView)
               .setMessage(R.string.filename_dialog_header)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        filename = filenameEditText.getText().toString();
                    }
                })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
