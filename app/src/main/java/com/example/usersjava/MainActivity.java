package com.example.usersjava;

import android.os.Bundle;
import android.view.Menu;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.ref.WeakReference;

import Library.MemoryData;


public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;

    private static final int REQUEST_ACCESS_COARSE_LOCATION_ID = 0;
    private static WeakReference<MainActivity> defaultInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContactActivity(this);

        MemoryData memoryData = MemoryData.getInstance(this);

        if(ComponentDataBase.getInstance().getIdUser() == 0){
            ComponentDataBase.getInstance().setIdUser(Integer.valueOf(memoryData.getData("user")));
        }

        final ChatApplication chatApplication = (ChatApplication)getApplication();
        chatApplication.setActivity(this);

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

    /*public void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION_ID);
        } else {
            ChatApplication chatApplication = (ChatApplication) getApplication();
            chatApplication.requestHypeToStart();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION_ID) {
            ChatApplication chatApplication = (ChatApplication) getApplication();
            chatApplication.requestHypeToStart();
        }
    }

}
