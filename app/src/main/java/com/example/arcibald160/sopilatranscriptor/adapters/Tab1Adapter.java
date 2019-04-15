package com.example.arcibald160.sopilatranscriptor.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.NetworkUtils;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Tab1Adapter extends RecyclerView.Adapter<Tab1Adapter.ListViewHolder> {

    private File[] mRecordings;
    private Context mContext;
    private static String PATH;


    public Tab1Adapter(File[] list, Context context) {
        mRecordings = list;
        mContext = context;
        PATH = Environment.getExternalStorageDirectory().toString() + "/" + context.getString(R.string.rec_folder);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // new view
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_recordings_view, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {

        final File file = mRecordings[position];
//        bytes to kilo bytes
        String size = Utils.formatFileSize(file.length());
        String duration = Utils.getFileDuration(file);
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(new Date(file.lastModified()));

        //Set values
        holder.recName.setText(file.getName());
        holder.recTimeAndSize.setText(duration + " - " + size);
        holder.recDateCreated.setText(date);

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= 24){
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri apkURI = FileProvider.getUriForFile(
                        view.getContext(),
                        view.getContext().getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, "audio/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                view.getContext().startActivity(intent);
            }
            }
        });


        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu recordingMenu = new PopupMenu(view.getContext(), holder.menuButton);
                recordingMenu.getMenuInflater().inflate(R.menu.recording_utils_menu, recordingMenu.getMenu());

                recordingMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                    final Context context = view.getContext();

                    if (menuItem.getTitle().toString().equals(context.getString(R.string.rename_label))) {
                        renameListItem(context, file);
                    } else if (menuItem.getTitle().toString().equals(context.getString(R.string.delete_label))) {
                        deleteListItem(context, file);
                    } else if (menuItem.getTitle().toString().equals(context.getString(R.string.export_label))) {
                        NetworkUtils.uploadRecording(view.getContext(), file);
                    }
                    return true;
                    }
                });
                recordingMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {

        if (mRecordings == null) {
            return 0;
        }

        return mRecordings.length;
    }

    public void refreshRecDir() {
        mRecordings = null;
        File recordingsDirectory = new File(PATH);
        mRecordings = recordingsDirectory.listFiles();
        notifyDataSetChanged();
    }

    private void renameListItem(Context context, final File file) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.rename_title, file.getName()));
        final EditText newNameEditText = new EditText(context);
        newNameEditText.setText(file.getName());

        alertDialog.setView(newNameEditText);
        alertDialog.setPositiveButton(context.getString(R.string.save_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        File newFile = new File(file.getParent(), newNameEditText.getText().toString());
                        Utils.renameFile(file, newFile);
                        refreshRecDir();
                    }
                });
        alertDialog.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void deleteListItem(Context context, final File file) {
        // delete

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.delete_recording_warning, file.getName()));

        alertDialog.setPositiveButton(context.getString(R.string.delete_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                        refreshRecDir();
                    }
                });
        alertDialog.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView recName, recTimeAndSize, recDateCreated;
        ImageButton menuButton, playButton;

        public ListViewHolder(View itemView) {
            super(itemView);
            recName = itemView.findViewById(R.id.recording_name);
            recTimeAndSize = itemView.findViewById(R.id.time_and_size);
            recDateCreated = itemView.findViewById(R.id.date_created);
            playButton = itemView.findViewById(R.id.play_recording);
            menuButton = itemView.findViewById(R.id.more_button);
        }
    }
}
