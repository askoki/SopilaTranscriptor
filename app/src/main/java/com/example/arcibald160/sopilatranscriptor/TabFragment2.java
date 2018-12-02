package com.example.arcibald160.sopilatranscriptor;

import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;

import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;


public class TabFragment2 extends Fragment {

    private Recorder recorder;
    private File tempRecFile = new File(Environment.getExternalStorageDirectory(), "demo.wav");

    public TabFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_2, container, false);

        MediaScannerConnection.scanFile(
                view.getContext(),
                new String[]{tempRecFile.getAbsolutePath()},
                null,
                null
        );

        final ToggleButton recButton = view.findViewById(R.id.rec_button);
        Button playButton = view.findViewById(R.id.play_btn);
        Button stopButton = view.findViewById(R.id.stop_play_btn);

        final MediaPlayer mPlayer = new MediaPlayer();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.stop();
            }
        });

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recButton.isChecked()) {
                    // recButton.setChecked(recordSound.startRecording());
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

                    recorder.startRecording();
                } else {
                    InsertFileNameDialog filenameDialog = new InsertFileNameDialog(tempRecFile, view.getContext());
                    filenameDialog.show(getActivity().getSupportFragmentManager(), "filename");

                    try {
                        recorder.stopRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        mPlayer.reset();
                        mPlayer.setDataSource(tempRecFile.getAbsolutePath());
                        mPlayer.prepare();
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
}
