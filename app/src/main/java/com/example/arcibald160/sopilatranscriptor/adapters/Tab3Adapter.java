package com.example.arcibald160.sopilatranscriptor.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tab3Adapter extends RecyclerView.Adapter<Tab3Adapter.ListViewHolder> {

    private Context mContext;
    private File[] mSheets;

    public Tab3Adapter(Context context) {
        mContext = context;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_sheets, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final File file = mSheets[position];
//        bytes to kilo bytes
        String size = Utils.formatFileSize(file.length());
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(new Date(file.lastModified()));

        //Set values
        holder.sheetName.setText(file.getName());
        holder.sheetSize.setText(size);
        holder.sheetDateCreated.setText(date);

        holder.sheetEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri pdf = FileProvider.getUriForFile(view.getContext(), view.getContext().getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setDataAndType(pdf, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (mSheets == null) {
            return 0;
        }

        return mSheets.length;
    }

    public void refreshRecDir() {
        mSheets = null;
        File sheetsDirectory = Utils.getDownloadsDir(mContext);
        mSheets = sheetsDirectory.listFiles();
        notifyDataSetChanged();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sheetEntry;
        TextView sheetName, sheetDateCreated, sheetSize;

        public ListViewHolder(View itemView) {
            super(itemView);
            sheetEntry = itemView.findViewById(R.id.sheet_entry);
            sheetName = itemView.findViewById(R.id.sheet_name);
            sheetDateCreated = itemView.findViewById(R.id.sheet_date_created);
            sheetSize = itemView.findViewById(R.id.sheet_size);
        }
    }
}
