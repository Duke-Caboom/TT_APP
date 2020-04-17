package com.example.usersjava;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

import Library.MemoryData;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int REQUEST_ACCESS_COARSE_LOCATION_ID = 0;
    private static WeakReference<MainActivity> defaultInstance;

    String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }

        setContactActivity(this);

        MemoryData memoryData = MemoryData.getInstance(this);

        if (ComponentDataBase.getInstance().getNombre().trim().equalsIgnoreCase("")) {
            ComponentDataBase.getInstance().setNombre(memoryData.getData("nombre").trim());

        }
        Log.e(getClass().getSimpleName(), "Mi correo: " + memoryData.getData("email").trim());
        Log.e(getClass().getSimpleName(), "Mi user: " + memoryData.getData("user").trim());
        Log.e(getClass().getSimpleName(), "Mi name: " + memoryData.getData("nombre").trim());
        if (ComponentDataBase.getInstance().getEmail().trim().equalsIgnoreCase("")) {
            ComponentDataBase.getInstance().setEmail(memoryData.getData("email").trim());
        }

        if (ComponentDataBase.getInstance().getContactos().equalsIgnoreCase("")) {
            ComponentDataBase.getInstance().setContactos(memoryData.getData("contactos").trim());
        }

        if (ComponentDataBase.getInstance().getIdUser().equalsIgnoreCase("")
                && !memoryData.getData("user").trim().equalsIgnoreCase("")) {
            ComponentDataBase.getInstance().setIdUser(memoryData.getData("user").trim());
            //ComponentDataBase.getInstance().setIdUser(String.valueOf((int)((Math.random()*100) +1)));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_close)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final ChatApplication chatApplication = (ChatApplication) getApplication();
        chatApplication.setActivity(this);
        chatApplication.configChatApp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static MainActivity getDefaultInstance() {
        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setContactActivity(MainActivity instance) {
        defaultInstance = new WeakReference<>(instance);
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION_ID);
        } else {
            ChatApplication chatApplication = (ChatApplication) getApplication();
            chatApplication.requestHypeToStart();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION_ID:

                ChatApplication chatApplication = (ChatApplication) getApplication();
                chatApplication.requestHypeToStart();

                break;
        }
    }


}
