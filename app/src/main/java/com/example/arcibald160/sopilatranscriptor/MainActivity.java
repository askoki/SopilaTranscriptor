package com.example.arcibald160.sopilatranscriptor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arcibald160.sopilatranscriptor.adapters.TabPageAdapter;
import com.example.arcibald160.sopilatranscriptor.tab_fragments.TabFragment1;
import com.example.arcibald160.sopilatranscriptor.tab_fragments.TabFragment2;
import com.example.arcibald160.sopilatranscriptor.tab_fragments.TabFragment3;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    private TabPageAdapter mTabPageAdapter;
    private ViewPager mViewPager;
    private static final int TAB_NUMBER = 3;
    private int[] icons = {
            R.drawable.list_rec_img,
            R.drawable.mic_img,
            R.drawable.list_sheets_icon
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.change_server_ip:
                // edit text dialog for setting ip address
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.server_ip_label));
                final EditText ipAddressEditText = new EditText(this);

                // starting ip address (take from shared preferences or put a default)
                final SharedPreferences prefs = getSharedPreferences(getString(R.string.sp_secret_key), MODE_PRIVATE);
                String serverIpAddress = prefs.getString(getString(R.string.sp_ip_server_address), null);
                if (serverIpAddress == null) {
                    serverIpAddress = getString(R.string.sp_ip_server_address_default);
                }

                ipAddressEditText.setText(serverIpAddress);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                ipAddressEditText.setLayoutParams(lp);
                alertDialog.setView(ipAddressEditText);
                alertDialog.setPositiveButton(getString(R.string.save_label),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(getString(R.string.sp_ip_server_address), String.valueOf(ipAddressEditText.getText()));
                                editor.apply();
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.ip_changed_info), Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
