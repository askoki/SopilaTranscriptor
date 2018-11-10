package com.example.arcibald160.sopilatranscriptor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int PERMISSION_ALL = 0;
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        String fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        final RecordSound recordSound = new RecordSound(fileName);

        ImageButton recButton = findViewById(R.id.rec_button);
        ImageButton stopRecButton = findViewById(R.id.stop_rec_button);
        Button playButton = findViewById(R.id.play_btn);
        Button stopButton = findViewById(R.id.stop_play_btn);
        
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordSound.startRecording();
            }
        });

        stopRecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordSound.stopRecording();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordSound.startPlaying();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordSound.stopPlaying();
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
}
