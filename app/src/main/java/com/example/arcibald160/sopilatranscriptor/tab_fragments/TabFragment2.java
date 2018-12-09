package com.example.arcibald160.sopilatranscriptor.tab_fragments;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.arcibald160.sopilatranscriptor.helpers.InsertFileNameDialog;
import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;


public class TabFragment2 extends Fragment {

    private Recorder recorder;
    private File tempRecFile = new File(Environment.getExternalStorageDirectory(), "demo.wav");
    private Runnable updater;
    private long durationSec = 0;
    private Handler timerHandler = new Handler();
    TextView durationView, sizeView, freeView;

    public TabFragment2() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_2, container, false);

        durationView = view.findViewById(R.id.time_recorded);
        sizeView = view.findViewById(R.id.size_recorded);
        freeView = view.findViewById(R.id.free_space);

        freeView.setText(Utils.getAvailableInternalMemorySize(getString(R.string.free_space_msg)));

        MediaScannerConnection.scanFile(
                view.getContext(),
                new String[]{tempRecFile.getAbsolutePath()},
                null,
                null
        );

        final ToggleButton recButton = view.findViewById(R.id.rec_button);

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recButton.isChecked()) {
                    recorder = OmRecorder.wav(
                            new PullTransport.Default(new PullableSource.Default(
                                    new AudioRecordConfig.Default(
                                            MediaRecorder.AudioSource.MIC,
                                            AudioFormat.ENCODING_PCM_16BIT,
                                            AudioFormat.CHANNEL_IN_MONO,
                                            44100
                                    )
                            )),
                            tempRecFile
                    );

                    try {
                        recorder.startRecording();
                    } finally {
                        updateRecordInfo();
                    }
                } else {
                    InsertFileNameDialog filenameDialog = new InsertFileNameDialog(tempRecFile, view.getContext());
                    filenameDialog.show(getActivity().getSupportFragmentManager(), "filename");

                    try {
                        recorder.stopRecording();
//                        disable updater
                        resetRecordInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MediaScannerConnection.scanFile(
                            view.getContext(),
                            new String[]{tempRecFile.getAbsolutePath()},
                            null,
                            null
                    );

                }
            }
        });
        return view;
    }


    private void updateRecordInfo() {

        updater = new Runnable() {
            @Override
            public void run() {

                String currDuration = String.format("%02d:%02ds",
                        TimeUnit.SECONDS.toMinutes(durationSec),
                        durationSec - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(durationSec))
                );
                String currSize = Utils.formatFileSize(tempRecFile.length());

                if (currDuration != null && currSize != null) {
                    durationView.setText(currDuration);
                    sizeView.setText(currSize);
                }
//                call update every second
                timerHandler.postDelayed(updater,1000);
                durationSec++;
            }
        };
        timerHandler.post(updater);
    }

    private void resetRecordInfo() {
        // stop Runnable
        timerHandler.removeCallbacks(updater);
        durationView.setText(getString(R.string.duration_default));
        sizeView.setText(getString(R.string.size_default));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        remove updater when activity destroys to prevent memory leak
        timerHandler.removeCallbacks(updater);
    }
}
