package com.example.arcibald160.sopilatranscriptor.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;

import java.io.File;
import java.io.IOException;
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
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        final File file = mRecordings[position];
//        bytes to kilo bytes
        String size = Utils.formatFileSize(file.length());
        String duration = Utils.getFileDuration(file);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(new Date(file.lastModified()));

        //Set values
        holder.recName.setText(file.getName());
        holder.recTimeAndSize.setText(duration + " - " + size);
        holder.recDateCreated.setText(date);

        final MediaPlayer mPlayer = new MediaPlayer();
        try {
            mPlayer.reset();
            mPlayer.setDataSource(file.getAbsolutePath());
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.recEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.start();
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
        LinearLayout recEntry;

        public ListViewHolder(View itemView) {
            super(itemView);
            recName = itemView.findViewById(R.id.recording_name);
            recTimeAndSize = itemView.findViewById(R.id.time_and_size);
            recDateCreated = itemView.findViewById(R.id.date_created);
            recEntry = itemView.findViewById(R.id.rec_entry);
        }
    }
}
