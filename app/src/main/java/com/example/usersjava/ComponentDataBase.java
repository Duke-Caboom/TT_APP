package com.example.usersjava;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

public class ComponentDataBase {
    private static final ComponentDataBase instance = new ComponentDataBase();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static ComponentDataBase getInstance() {
        return instance;
    }

    private ComponentDataBase() {
        if (MainActivity.getDefaultInstance() != null) {
            this.sharedPreferences = MainActivity.getDefaultInstance().getPreferences(Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    private int getLatestIndexBD() {
        int index = sharedPreferences.getInt("indexBD", 0) + 1;
        editor.putInt("indexBD", index);
        editor.apply();
        return index;
    }

    public Integer getIdUser() {
        return sharedPreferences.getInt("idUser", 0);
    }

    public void setIdUser(Integer idUser) {
        editor.putInt("idUser", idUser);
        editor.apply();
    }

    public void setLastIdMensaje(Integer idMensaje) {
        editor.putInt("lastMensaje", idMensaje);
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
        contentValues.put("idUser", mensaje.getIdMensaje());
        contentValues.put("idMensaje", mensaje.getIdMensaje());
        contentValues.put("publico", mensaje.isPublico());
        if (!mensaje.isPublico()){
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
                        fila.getInt(0),
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
