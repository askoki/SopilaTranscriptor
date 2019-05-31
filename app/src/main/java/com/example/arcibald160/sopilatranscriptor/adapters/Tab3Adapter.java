package com.example.arcibald160.sopilatranscriptor.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.arcibald160.sopilatranscriptor.PdfActivity;
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
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int position) {
        final File file = mSheets[position];
//        bytes to kilo bytes
        String size = Utils.formatFileSize(file.length());
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(new Date(file.lastModified()));

        //Set values
        holder.sheetName.setText(file.getName());
        holder.sheetSize.setText(size);
        holder.sheetDateCreated.setText(date);

        holder.sheetEntry.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                PopupMenu sheetMenu = new PopupMenu(view.getContext(), holder.sheetEntry);
                sheetMenu.getMenuInflater().inflate(R.menu.sheet_utils_menu, sheetMenu.getMenu());

                sheetMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        final Context context = view.getContext();

                        if (menuItem.getTitle().toString().equals(context.getString(R.string.rename_label))) {
                            // rename
                            renameListItem(context, file);
                        } else if (menuItem.getTitle().toString().equals(context.getString(R.string.delete_label))) {
                            deleteListItem(context, file);
                        }
                        return true;
                    }
                });
                sheetMenu.show();

                return true;
            }
        });

        holder.sheetEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PdfActivity.class);

                intent.putExtra(view.getContext().getString(R.string.pdf_extra_key), file);
                intent.setType("application/pdf");

                view.getContext().startActivity(intent);
            }
        });
    }

    private void renameListItem(Context context, final File file) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.rename_title, file.getName()));
        final EditText newNameEditText = new EditText(context);
        newNameEditText.setText(file.getName());

        alertDialog.setView(newNameEditText);
        alertDialog.setPositiveButton(
            context.getString(R.string.save_label),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    File newFile = new File(file.getParent(), newNameEditText.getText().toString());
                    Utils.renameFile(file, newFile);
                    refreshSheetDir();
                }
            }
        );
        alertDialog.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void deleteListItem(Context context, final File file) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.delete_sheet_warning, file.getName()));

        alertDialog.setPositiveButton(context.getString(R.string.delete_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        file.delete();
                        refreshSheetDir();
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

    @Override
    public int getItemCount() {

        if (mSheets == null) {
            return 0;
        }

        return mSheets.length;
    }

    public void refreshSheetDir() {
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
