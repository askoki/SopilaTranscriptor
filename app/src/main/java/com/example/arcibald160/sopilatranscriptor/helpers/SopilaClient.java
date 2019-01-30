package com.example.arcibald160.sopilatranscriptor.helpers;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SopilaClient {
    @GET("download/api")
    Call<SopilaServerFile> getFile();
}
