package com.example.arcibald160.sopilatranscriptor.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;

import java.io.File;
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

        holder.recEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                view.getContext().startActivity(intent);
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
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(context.getString(R.string.rename_label));
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
                        // TODO: impement delete
                        // TODO: impement export
                        Toast.makeText(view.getContext(), "You have clicked " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
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

    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView recName, recTimeAndSize, recDateCreated;
        ImageButton menuButton, recEntry;

        public ListViewHolder(View itemView) {
            super(itemView);
            recName = itemView.findViewById(R.id.recording_name);
            recTimeAndSize = itemView.findViewById(R.id.time_and_size);
            recDateCreated = itemView.findViewById(R.id.date_created);
            recEntry = itemView.findViewById(R.id.play_recording);
            menuButton = itemView.findViewById(R.id.more_button);
        }
    }
}
