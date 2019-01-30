package com.example.arcibald160.sopilatranscriptor.tab_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.SopilaClient;
import com.example.arcibald160.sopilatranscriptor.helpers.SopilaServerFile;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TabFragment3 extends Fragment {

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
                    String API_BASE_URL = "http://192.168.1.5:8000";

                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

                    Retrofit.Builder builder =
                            new Retrofit.Builder()
                                    .baseUrl(API_BASE_URL)
                                    .addConverterFactory(
                                            GsonConverterFactory.create()
                                    );

                    Retrofit retrofit =
                            builder
                                    .client(
                                            httpClient.build()
                                    )
                                    .build();

                    SopilaClient client =  retrofit.create(SopilaClient.class);
                    Toast.makeText(view.getContext(), "Sent", Toast.LENGTH_LONG).show();

                    // Fetch a list of the Github repositories.
                    Call<SopilaServerFile> call = client.getFile();
                    call.enqueue(new Callback<SopilaServerFile>() {
                        @Override
                        public void onResponse(Call<SopilaServerFile> call, Response<SopilaServerFile> response) {
                            // The network call was a success and we got a response
                            // TODO: use the repository list and display it
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), response.body().getFile(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<SopilaServerFile> call, Throwable t) {
                            // the network call was a failure
                            // TODO: handle error
                            Toast.makeText(getContext(), "Failure", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        );
        // Inflate the layout for this fragment
        return view;
    }
}
