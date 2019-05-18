package com.example.arcibald160.sopilatranscriptor.tab_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.arcibald160.sopilatranscriptor.helpers.InsertFileNameDialog;
import com.example.arcibald160.sopilatranscriptor.R;
import com.example.arcibald160.sopilatranscriptor.helpers.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.gresse.hugo.vumeterlibrary.VuMeterView;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class TabFragment2 extends Fragment {

    private Recorder recorder;
    private File tempRecFile = new File(Environment.getExternalStorageDirectory(), "demo.wav");
    private Runnable updater;
    private long durationSec = 0;
    private Handler timerHandler = new Handler();
    private VuMeterView musicEqualizer;
    private FusedLocationProviderClient fusedLocationClient;
    private Button locationButton;
    private LocationListener locationListener;
    private LocationManager mLocationManager;
    TextView durationView, sizeView, freeView, locationView, dateView;

    public TabFragment2() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_2, container, false);

        musicEqualizer = view.findViewById(R.id.vumeter);
        durationView = view.findViewById(R.id.time_recorded);
        sizeView = view.findViewById(R.id.size_recorded);
        freeView = view.findViewById(R.id.free_space);
        locationButton = view.findViewById(R.id.location_img_btn);
        locationView = view.findViewById(R.id.location_text_view);
        dateView = view.findViewById(R.id.date_view);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        final PulsatorLayout pulsator = view.findViewById(R.id.pulsator);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        String formattedDate = df.format(c);
        dateView.setText(formattedDate);

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) view.getContext().getSystemService(view.getContext().LOCATION_SERVICE);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = getLastKnownLocation();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List<String> stringAddresses = Arrays.asList(addresses.get(0).getAddressLine(0).split(","));
//                    String cityAddress = stringAddresses.get(0);
                    String cityName = stringAddresses.get(1).replaceAll("\\d","").replaceAll(" ","");
                    String countryName = stringAddresses.get(2).replaceAll(" ","");
                    locationView.setText(String.format("%s, %s", cityName, countryName));
                }
            }
        });

        freeView.setText(Utils.getAvailableInternalMemorySize(""));

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
                    pulsator.bringToFront();
                    pulsator.start();
                    musicEqualizer.resume(true);
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
                    pulsator.stop();
                    musicEqualizer.stop(true);
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

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                continue;
            }

            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
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
        durationSec = 0;
        sizeView.setText(getString(R.string.size_default));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        remove updater when activity destroys to prevent memory leak
        timerHandler.removeCallbacks(updater);
    }
}
