package com.example.usersjava;

import android.util.Log;

import com.hypelabs.hype.Instance;

import java.util.HashMap;


public class Dispositivos {

    private static final Dispositivos instance = new Dispositivos();
    private static final String TAG = Dispositivos.class.getName();

    private HashMap<Long, Instance> dispositivos;


    private Dispositivos() {
        this.dispositivos = new HashMap<>();
    }

    public static Dispositivos getInstance() {
        return instance;
    }

    public void addDispositivo(Instance instance) {
        Log.i(TAG, "getUserIdentifier:  " + instance.getUserIdentifier());
        Log.i(TAG, "dispositivos: " + dispositivos.toString());
        dispositivos.put(instance.getUserIdentifier(), instance);
    }

    public void deleteDispositivo(Instance instance) {
        dispositivos.remove(instance.getUserIdentifier());
    }

    public int sizeDispositivos() {
        return dispositivos.size();
    }

    public String[] getAdapterDispositivos() {

        String[] adapterArray = new String[dispositivos.size()];
        Log.i(TAG, "dispositivos.size(): " + dispositivos.size());

        if (dispositivos.size() == 0) {
            return null;
        }


        int i = 0;
        for (HashMap.Entry<Long, Instance> entry : dispositivos.entrySet()) {
            Log.i(TAG, "FOR: ");
            adapterArray[i] = entry.getValue().getStringIdentifier();
            Log.i(TAG, "DEspues FOR: ");
            i++;
        }

        return adapterArray;
    }

    public Instance[] getDispositivos() {
        Instance[] disp = new Instance[dispositivos.size()];

        int i = 0;
        for (HashMap.Entry<Long, Instance> entry : dispositivos.entrySet()) {
            Log.i(TAG, "FOR: ");
            disp[i] = entry.getValue();
            Log.i(TAG, "DEspues FOR: ");
            i++;
        }
        return disp;
    }

    public Instance getDispositivo(int id) {
        if (dispositivos.containsKey(id)) {
            return dispositivos.get(id);
        } else {
            return null;
        }

    }
}
