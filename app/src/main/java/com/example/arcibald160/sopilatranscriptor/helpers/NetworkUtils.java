package com.example.arcibald160.sopilatranscriptor.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.arcibald160.sopilatranscriptor.R;

import java.io.File;
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

public class NetworkUtils {
    public static String TAG = "SopilaTranscriptorNetworkUtils";

    private static void downloadSheetPdf(final Context context, Retrofit retrofit, final File file) {
        Log.v("Upload", "success");
        // download pdf
        PdfDownloadClient pdfDownloadClient = retrofit.create(PdfDownloadClient.class);

        String urlAppend = "download/api/" + file.getName().replaceFirst("[.][^.]+$", "") + "/";
        Call<ResponseBody> callDownload = pdfDownloadClient.downloadMusicSheetPdf(urlAppend);
        Toast.makeText(context, context.getString(R.string.upload_success_message), Toast.LENGTH_LONG).show();

        callDownload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, context.getString(R.string.download_inprogress), Toast.LENGTH_SHORT).show();

                    final String shortFilename = file.getName().replaceFirst("[.][^.]+$", "");

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = Utils.writeResponseBodyToDisk(response.body(), context, TAG, shortFilename);

                            Log.d(TAG, "file download was a success? " + writtenToDisk);
                            return null;
                        }

                    }.execute();
                } else {
                    Toast.makeText(context, context.getString(R.string.download_fail_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed :(", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void uploadRecording(final Context context, final File file) {
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

        final Retrofit retrofit = builder.client(httpClient.build()).build();

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
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                downloadSheetPdf(context, retrofit, file);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
