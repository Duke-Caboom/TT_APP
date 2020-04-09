package com.example.usersjava.ui.slideshow;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.usersjava.MainActivity;
import com.example.usersjava.Mensajes;
import com.example.usersjava.R;

import java.lang.ref.WeakReference;
import java.util.Objects;


public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    private static WeakReference<MainActivity> defaultInstance;

    private ListView listPublico;
    private ListView listPrivado;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(getActivity(), s -> textView.setText(s));
        listPrivado = root.findViewById(R.id.listPrivado);
        listPublico = root.findViewById(R.id.listPublico);
        start();
        return root;
    }

    public void start() {

        Handler mHandler = new Handler();
        Runnable mTicker = new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayAdapter<String> adapter;
                    Log.e(getClass().getSimpleName(), "Hilo Alive");
                    String[] mensajesArray = Mensajes.getInstance().getAdapterMensajes();
                    if (mensajesArray == null) {
                        Log.i(getClass().getSimpleName(), "Regreso un null el adaptadpr");
                    } else {
                        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1,
                                mensajesArray);
                        listPublico.setAdapter(adapter);
                    }

                    mensajesArray = Mensajes.getInstance().getAdapterMensajesPrivado();

                    if (mensajesArray == null) {
                        Log.i(getClass().getSimpleName(), "Regreso un null el adaptadpr PRIVADO");
                        return;
                    }else{
                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                                mensajesArray);
                        listPrivado.setAdapter(adapter);
                    }
                    mHandler.postDelayed(this, 10000);
                } catch (Throwable throwable) {
                    Log.e(getClass().getSimpleName(), "Error: "+throwable);
                }
            }
        };
        mTicker.run();

    }
}