package com.example.usersjava;

import android.content.Context;
import android.content.SharedPreferences;

public class ComponentDataBase {
    private static final ComponentDataBase instance = new ComponentDataBase();
    SharedPreferences sharedPreferences = MainActivity.getDefaultInstance().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    public static ComponentDataBase getInstance() {
        return instance;
    }

    private ComponentDataBase() { }

    public Integer getIdUser(){
        return sharedPreferences.getInt("idUser", 0);
    }

    public void setIdUser(Integer idUser){
        editor.putInt("idUser",idUser);
        editor.apply();
    }

    public void setLastIdMensaje(Integer idMensaje){
        editor.putInt("lastMensaje", idMensaje);
        editor.apply();

    }

    public Integer getLastIdMensaje(){
        return sharedPreferences.getInt("lastMensaje", 0);
    }

    public void addMensajeEnviado(){

    }

    public void addMensajeRecibido(){

    }


}
