package com.example.arcibald160.sopilatranscriptor.helpers;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface PdfDownloadClient {
    @Streaming
    @GET("download/api")
    Call<ResponseBody> downloadMusicSheetPdf();
}