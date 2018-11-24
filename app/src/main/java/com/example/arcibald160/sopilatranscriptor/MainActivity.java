package com.example.arcibald160.sopilatranscriptor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private Recorder recorder;
    private File tempRecFile = new File(Environment.getExternalStorageDirectory(), "demo.wav");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{tempRecFile.getAbsolutePath()},
                null,
                null
        );

        int PERMISSION_ALL = 0;
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        final ToggleButton recButton = findViewById(R.id.rec_button);
        Button playButton = findViewById(R.id.play_btn);
        Button stopButton = findViewById(R.id.stop_play_btn);

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
                                            MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                                            AudioFormat.CHANNEL_IN_MONO, 44100
                                    )
                            )),
                            tempRecFile
                    );

                    recorder.startRecording();
                } else {
                    InsertFileNameDialog filenameDialog = new InsertFileNameDialog(tempRecFile);
                    filenameDialog.show(getSupportFragmentManager(), "filename");

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
                            getApplicationContext(),
                            new String[]{tempRecFile.getAbsolutePath()},
                            null,
                            null
                    );

                }
            }
        });
    }

    // check if app has permissions so we dont spam the user
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
