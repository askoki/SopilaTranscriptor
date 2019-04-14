package com.example.arcibald160.sopilatranscriptor.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arcibald160.sopilatranscriptor.BuildConfig;
import com.example.arcibald160.sopilatranscriptor.MainActivity;
import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.FileUploadService;
import com.example.arcibald160.sopilatranscriptor.helpers.PdfDownloadClient;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Settings.AUTHORITY;

public class Tab1Adapter extends RecyclerView.Adapter<Tab1Adapter.ListViewHolder> {

    private File[] mRecordings;
    private Context mContext;
    private static String PATH;
    public String TAG = "SopilaTranscriptPdfExport";

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
                            // rename

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
                         } else if (menuItem.getTitle().toString().equals(context.getString(R.string.delete_label))) {
                            // delete

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(context.getString(R.string.delete_recording_warning));

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
                        } else if (menuItem.getTitle().toString().equals(context.getString(R.string.export_label))) {
                            // export

                            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sp_secret_key), MODE_PRIVATE);
                            String serverIpAddress = prefs.getString(context.getString(R.string.sp_ip_server_address), null);

                            if (serverIpAddress == null) {
                                serverIpAddress = context.getString(R.string.sp_ip_server_address_default);
                            }

                            String API_BASE_URL = "http://" + serverIpAddress;

                            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

                            Retrofit.Builder builder = new Retrofit.Builder()
                                    .baseUrl(API_BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create());

                            Retrofit retrofit = builder.client(httpClient.build()).build();

                            Uri fileUri = Uri.fromFile(file);

                            // create RequestBody instance from file
                            RequestBody requestFile = RequestBody.create(
                                    MediaType.parse("audio/*"),
                                    file
                            );

                            // MultipartBody.Part is used to send also the actual file name
                            MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

                            // add another part within the multipart request
                            String descriptionString = "Recording description";
                            RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

                            // finally, execute the request
                            FileUploadService uploadFileService = retrofit.create(FileUploadService.class);
                            Call<ResponseBody> call = uploadFileService.upload(description, body);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call,
                                                       Response<ResponseBody> response) {
                                    Log.v("Upload", "success");
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("Upload error:", t.getMessage());
                                }
                            });

//                            PdfDownloadClient pdfDownloadClient = retrofit.create(PdfDownloadClient.class);
//
//                            Call<ResponseBody> call = pdfDownloadClient.downloadMusicSheetPdf();
//                            Toast.makeText(view.getContext(), "Sent", Toast.LENGTH_LONG).show();
//
//                            call.enqueue(new Callback<ResponseBody>() {
//                                @Override
//                                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
//                                    if (response.isSuccessful()) {
//                                        Toast.makeText(context, "Server contact success", Toast.LENGTH_SHORT).show();
//
//                                        final String shortFilename = file.getName().replaceFirst("[.][^.]+$", "");
//
//                                        new AsyncTask<Void, Void, Void>() {
//                                            @Override
//                                            protected Void doInBackground(Void... voids) {
//                                                boolean writtenToDisk = Utils.writeResponseBodyToDisk(response.body(), context, TAG, shortFilename);
//
//                                                Log.d(TAG, "file download was a success? " + writtenToDisk);
//                                                return null;
//                                            }
//
//                                        }.execute();
//                                    } else {
//                                        Toast.makeText(context, "Server contact failed", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                    Toast.makeText(context, "Failed :(", Toast.LENGTH_LONG).show();
//                                }
//                            });

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
