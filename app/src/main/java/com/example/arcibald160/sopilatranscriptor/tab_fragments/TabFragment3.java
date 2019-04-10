package com.example.arcibald160.sopilatranscriptor.tab_fragments;

import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.PdfDownloadClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


public class TabFragment3 extends Fragment {

    public String TAG = "SopilaTranscriptorPdfDownloader";
    Button test_btn;

    public TabFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_3, container, false);

        test_btn = view.findViewById(R.id.test_btn);

        test_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.sp_secret_key), MODE_PRIVATE);
                                            String serverIpAddress = prefs.getString(getString(R.string.sp_ip_server_address), null);
                                            if (serverIpAddress == null) {
                                                serverIpAddress = getString(R.string.sp_ip_server_address_default);
                                            }
                                            String API_BASE_URL = "http://" + serverIpAddress;

                                            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

                                            Retrofit.Builder builder = new Retrofit.Builder()
                                                    .baseUrl(API_BASE_URL)
                                                    .addConverterFactory(GsonConverterFactory.create());

                                            Retrofit retrofit = builder.client(httpClient.build()).build();


                                            // SopilaClient client =  retrofit.create(SopilaClient.class);
                                            PdfDownloadClient pdfDownloadClient = retrofit.create(PdfDownloadClient.class);

                                            Call<ResponseBody> call = pdfDownloadClient.downloadMusicSheetPdf();
                                            Toast.makeText(view.getContext(), "Sent", Toast.LENGTH_LONG).show();

                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Server contact success", Toast.LENGTH_SHORT).show();

                                                        boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                                                        Log.d(TAG, "file download was a success? " + writtenToDisk);
                                                    } else {
                                                        Toast.makeText(getContext(), "Server contact failed", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(getContext(), "Failed :(", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        }
                                    }
        );
        // Inflate the layout for this fragment
        return view;
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        File downloadsDirectory = new File(Environment.getExternalStorageDirectory(), getContext().getString(R.string.pdf_folder));

//        create folder for recordings if it does not exist
        if (!downloadsDirectory.exists()) {
            if (!downloadsDirectory.mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }
        }

        // make it visible on the filesystem
        MediaScannerConnection.scanFile(
                getContext(),
                new String[]{downloadsDirectory.getAbsolutePath()},
                null,
                null
        );

        try {
            // todo change the file location/name according to your needs
            File futureMusicPdfFile = new File(
                    downloadsDirectory.getAbsolutePath(),
                    "test.pdf"
            );


            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureMusicPdfFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
