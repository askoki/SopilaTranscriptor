package com.example.arcibald160.sopilatranscriptor.helpers;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface PdfDownloadClient {
    @Streaming
    @GET
    Call<ResponseBody> downloadMusicSheetPdf(@Url String url);
}