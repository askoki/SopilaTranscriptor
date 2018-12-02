package com.example.arcibald160.sopilatranscriptor;

import android.Manifest;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private TabPageAdapter mTabPageAdapter;
    private ViewPager mViewPager;
    private static final int TAB_NUMBER = 3;
    private int[] icons = {
            R.drawable.list_rec_img,
            R.drawable.mic_img,
            R.drawable.settings_img
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // control tabs
        mTabPageAdapter = new TabPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(TAB_NUMBER);
        setupViewPager(mViewPager);

        mViewPager.setCurrentItem(1);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
//        set icons
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }


        int PERMISSION_ALL = 0;
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
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

    private void setupViewPager(ViewPager viewPager) {
        TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), "1");
        adapter.addFragment(new TabFragment2(), "2");
        adapter.addFragment(new TabFragment3(), "3");
        viewPager.setAdapter(adapter);
    }
}
