package com.example.usersjava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import Library.MemoryData;

public class Principal extends AppCompatActivity {
    Button btnregistro;
    Button btninicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        MemoryData memoryData = MemoryData.getInstance(this);

        if (memoryData.getData("user") != null && !memoryData.getData("user").isEmpty()) {
            Intent main = new Intent(Principal.this, MainActivity.class);
            startActivity(main);
        } else {
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
        }
    }
}
