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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.usersjava.MainActivity;
import com.example.usersjava.Mensajes;
import com.example.usersjava.R;

import java.lang.ref.WeakReference;

import static androidx.constraintlayout.widget.Constraints.TAG;

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
        slideshowViewModel.getText().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        listPrivado = root.findViewById(R.id.listPrivado);
        listPublico = root.findViewById(R.id.listPublico);
        Log.v(TAG, "#################### ################### Me cosntrui");
        start();
        return root;
    }

    public static MainActivity getDefaultInstance() {
        return defaultInstance != null ? defaultInstance.get() : null;
    }

    public void start() {
        Handler mHandler = new Handler();
        Runnable mTicker = new Runnable() {
            @Override
            public void run() {

                    String[] mensajesArray = Mensajes.getInstance().getAdapterMensajes();
                    if (mensajesArray == null) {
                        Log.i(getClass().getSimpleName(), "Regreso un null el adaptadpr");

                    } else {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                                mensajesArray);
                        listPublico.setAdapter(adapter);

                        mensajesArray = Mensajes.getInstance().getAdapterMensajesPrivado();
                        if (mensajesArray == null) {
                            Log.i(getClass().getSimpleName(), "2 Regreso un null el adaptadpr");
                            return;
                        }
                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                                mensajesArray);
                        listPrivado.setAdapter(adapter);
                    }

                    mHandler.postDelayed(this, 10000);
                }

        };
        mTicker.run();
    }
}