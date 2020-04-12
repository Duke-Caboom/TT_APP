package com.example.usersjava;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;

public class ComponentDataBase {
    private static final ComponentDataBase instance = new ComponentDataBase();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    //private ArrayList<String> contactos;

    public static ComponentDataBase getInstance() {
        return instance;
    }

    private ComponentDataBase() {
        this.sharedPreferences = MainActivity.getDefaultInstance().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private int getLatestIndexBD() {
        int index = sharedPreferences.getInt("indexBD", 0) + 1;
        editor.putInt("indexBD", index);
        editor.apply();
        return index;
    }

    public String getIdUser() {
        return sharedPreferences.getString("idUser", "");
    }

    public void setIdUser(String idUser) {
        editor.putString("idUser", idUser);
        editor.apply();
    }

    public void setLastIdMensaje(Integer idMensaje) {
        editor.putInt("lastMensaje", idMensaje);
        editor.apply();
    }

    public String getContactos() {
        String data = sharedPreferences.getString("contactos", "");
        String[] contac;
        if (data.equalsIgnoreCase("")) {
            return data;
        }
        String[] contactos = data.split(",");
        data ="";
        for (String contacto : contactos) {
            contac = contacto.split(":");
            data = data + contac[1] + ",";
        }
        data = data + ",,";
        data = data.replace(",,,", "");
        return data;
    }

    public String[] getAdaptadorContactos() {
        String data = sharedPreferences.getString("contactos", "");

        String[] contac;
        if (data.equalsIgnoreCase("")) {
            return null;
        }
        String[] contactos = data.split(",");
        String[] adapatdor = new String[contactos.length];
        int i = 0;
        for (String contacto : contactos) {
            contac = contacto.split(":");
            adapatdor[i] = contac[0] +":"+ contac[1];
            i++;
        }
        return adapatdor;
    }


    public void addContacto(String parenteco, String numero) {
        String data = sharedPreferences.getString("contactos", "");
        String[] contac;
        if(data != ""){
            String[] contactos = data.split(",");
            for (String contacto : contactos) {
                String[] split = contacto.split(":");
                if (split[1].equalsIgnoreCase(numero)) {
                    Log.e(getClass().getName(), "El contacto ya existe");
                    return;
                }
            }
        }

        if (data != "") {
            editor.putString("contactos", data + "," + parenteco + ":" + numero);
        } else {
            editor.putString("contactos", parenteco + ":" + numero);
        }
        editor.apply();
    }

    public void delContacto(String delCont) {
        String data = sharedPreferences.getString("contactos", "");

        data = data.replace(delCont,"");

        if(data != ""){
            data = data.replace(",,","");

            if (data.charAt(0) == ','){
                data = data.replaceFirst(",","");
            }

            if (data.charAt(data.length()-1) == ','){
                data = data.substring(0, data.length()-1);
            }
        }
        editor.putString("contactos", data);
        editor.apply();
    }

    public Integer getLastIdMensaje() {
        return sharedPreferences.getInt("lastMensaje", 0);
    }

    public void addMensaje(TipoMensaje mensaje) {
        DataBase dataBase = new DataBase(MainActivity.getDefaultInstance(), "base", null, 1);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        ContentValues contentValues = new
                ContentValues();
        contentValues.put("secuencia", getLatestIndexBD());
        contentValues.put("idUser", mensaje.getIdUser());
        contentValues.put("idMensaje", mensaje.getIdMensaje());
        contentValues.put("publico", mensaje.isPublico());
        if (!mensaje.isPublico()) {
            contentValues.put("destinatarios", mensaje.getDestinatarios());
        }
        contentValues.put("mensaje", mensaje.getMensajes());
        contentValues.put("fecha", mensaje.getFecha());
        contentValues.put("hora", mensaje.getHora());

        sqLiteDatabase.insert("mensajes", null, contentValues);
        sqLiteDatabase.close();
    }

    public HashMap<Integer, TipoMensaje> getLatestMessages() {
        //try {
        HashMap<Integer, TipoMensaje> msg = new HashMap<>();
        int i = 0;
        DataBase dataBase = new DataBase(MainActivity.getDefaultInstance(), "base", null, 1);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        Cursor fila = sqLiteDatabase.rawQuery("select idUser, idMensaje, publico, destinatarios, mensaje, fecha, hora from mensajes order by secuencia", null);
        if (fila.moveToFirst()) {
            TipoMensaje tipoMensaje = new TipoMensaje(-1L,
                    fila.getString(0),
                    fila.getString(1),
                    fila.getInt(2),
                    fila.getString(3),
                    fila.getString(4),
                    fila.getString(5),
                    fila.getString(6));
            msg.put(i, tipoMensaje);
            i++;
        } else {
            return null;
        }
        return msg;
        /*} catch (Exception e) {
            Log.e(getClass().getSimpleName(), "----------------> ERROR EN BD: "+e);
            return null;
        }*/
    }


}
