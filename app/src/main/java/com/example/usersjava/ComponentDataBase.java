package com.example.usersjava;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ComponentDataBase {
    private static final ComponentDataBase instance = new ComponentDataBase();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }

    public void setEmail(String email) {
        editor.putString("email", email);
        editor.apply();
    }

    public String getNombre() {
        return sharedPreferences.getString("nombre", "");
    }

    public void setNombre(String nombre) {
        editor.putString("nombre", nombre);
        editor.apply();
    }

    public void setLastIdMensaje(Integer idMensaje) {
        editor.putInt("lastMensaje", idMensaje);
        editor.apply();
    }

    public void updateContactos() {
        Log.i(getClass().getSimpleName(), "Actualizando contactos en FB: " + sharedPreferences.getString("contactos", ""));
        Log.i(getClass().getSimpleName(), "Mi correo: " + getEmail());
        FirebaseFirestore.getInstance().collection("Users").
                document(getEmail()).update("Contactos", sharedPreferences.getString("contactos", ""));
    }

    public String getContactos() {
        String data = sharedPreferences.getString("contactos", "");
        String[] contac;
        if (data.equalsIgnoreCase("")) {
            return data;
        }
        String[] contactos = data.split(",");
        data = "";
        for (String contacto : contactos) {
            contac = contacto.split(":");
            data = data + contac[1] + ",";
        }
        data = data + ",,";
        data = data.replace(",,,", "");
        return data;
    }

    public void setContactos(String contactos) {
        editor.putString("contactos", contactos);
        editor.apply();
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
            adapatdor[i] = contac[0] + ":" + contac[1];
            i++;
        }
        return adapatdor;
    }

    public void delContacto(String delCont) {
        String data = sharedPreferences.getString("contactos", "");

        data = data.replace(delCont, "");

        if (data != "") {
            data = data.replace(",,", "");

            if (data.charAt(0) == ',') {
                data = data.replaceFirst(",", "");
            }

            if (data.charAt(data.length() - 1) == ',') {
                data = data.substring(0, data.length() - 1);
            }
        }
        editor.putString("contactos", data);
        editor.apply();
        updateContactos();
    }

    public Integer getLastIdMensaje() {
        return sharedPreferences.getInt("lastMensaje", 0);
    }

    public void addMensaje(TipoMensaje mensaje) {
        DataBase dataBase = new DataBase(MainActivity.getDefaultInstance(), "base", null, 1);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
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

    public void deleteMessages(){
        DataBase dataBase = new DataBase(MainActivity.getDefaultInstance(), "base", null, 1);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        sqLiteDatabase.rawQuery("delete from mensajes", null);
    }

    public HashMap<Integer, TipoMensaje> getLatestMessages() {

        HashMap<Integer, TipoMensaje> msg = new HashMap<>();
        int i = 0;
        DataBase dataBase = new DataBase(MainActivity.getDefaultInstance(), "base", null, 1);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        Cursor fila = sqLiteDatabase.rawQuery("select idUser, idMensaje, publico, destinatarios, mensaje, fecha, hora from mensajes order by secuencia", null);
        Log.e(getClass().getSimpleName(), "fila: " + fila.getCount());
        if (fila.moveToFirst()) {
            while (!fila.isAfterLast()) {
                TipoMensaje tipoMensaje = new TipoMensaje(-100L,
                        fila.getString(0),
                        fila.getString(1),
                        fila.getInt(2),
                        fila.getString(3),
                        fila.getString(4),
                        fila.getString(5),
                        fila.getString(6));
                msg.put(i, tipoMensaje);
                i++;
                fila.moveToNext();
            }
        } else {
            return null;
        }
        return msg;
    }


}
