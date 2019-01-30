package com.example.arcibald160.sopilatranscriptor.helpers;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PdfDownloadClient {
    @GET("download/api")
    Call<ResponseBody> downloadMusicSheetPdf();
}