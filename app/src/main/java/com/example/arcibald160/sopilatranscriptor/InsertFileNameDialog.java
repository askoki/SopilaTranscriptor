package com.example.arcibald160.sopilatranscriptor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("ValidFragment")
public class InsertFileNameDialog extends DialogFragment {

    private String filename = "";
    private File mFile;

    public InsertFileNameDialog(File tempFile) {
        mFile = tempFile;
    }

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
                        File newFile = new File(
                                Environment.getExternalStorageDirectory(),
                                filename + ".wav"
                        );

                        try {
                            copyFile(mFile, newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // make file visible on pc
                        MediaScannerConnection.scanFile(
                                getContext(),
                                new String[]{newFile.getAbsolutePath()},
                                null,
                                null
                        );
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

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
