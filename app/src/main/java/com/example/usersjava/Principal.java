package com.example.usersjava;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import Library.MemoryData;

public class Principal extends AppCompatActivity {
    Button btnregistro;
    Button btninicio;
    String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MemoryData memoryData = MemoryData.getInstance(this);

        Log.e(getClass().getSimpleName(), "user:" + memoryData.getData("nombre").trim());
        Log.e(getClass().getSimpleName(), "nombre:" + memoryData.getData("nombre").trim());
        Log.e(getClass().getSimpleName(), "correo:" + memoryData.getData("nombre").trim());


        if (memoryData.getData("user") != null && !memoryData.getData("user").isEmpty()
                && !memoryData.getData("nombre").trim().equalsIgnoreCase("")) {
            Intent main = new Intent(Principal.this, MainActivity.class);
            startActivity(main);
            finish();
        }
        setContentView(R.layout.principal);
        btnregistro = (Button) findViewById(R.id.registro);
        btninicio = (Button) findViewById(R.id.iniciarsesion);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlue, null));

        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registro = new Intent(Principal.this, AddUser.class);
                startActivity(registro);
            }
        });

        btninicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inicio = new Intent(Principal.this, VerifyEmail.class);
                startActivity(inicio);
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }


    }
}
