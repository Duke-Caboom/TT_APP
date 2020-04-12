package com.example.usersjava.ui.share;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.usersjava.ComponentDataBase;
import com.example.usersjava.Mensajes;
import com.example.usersjava.R;

import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;

    private EditText editTel, editParen;
    private Button buttonAgregar;
    private ListView listContactos;
    private ProgressBar barP;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.text_share);
        shareViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        initElements(root);
        setButtonListeners();
        adaptador();
        barP.setVisibility(ProgressBar.INVISIBLE);
        return root;
    }

    private void setButtonListeners() {
        //Click en el boton enviar
        buttonAgregar.setOnClickListener(view -> agregarContacto());

        listContactos.setOnItemClickListener((parent, view, position, id) -> {
            Log.v(TAG, "##### Click: " + parent.getItemAtPosition(position).toString());
            eliminarContacto(parent.getItemAtPosition(position).toString());
        });
    }

    private void eliminarContacto(String conatct) {
        barP.setVisibility(ProgressBar.VISIBLE);
        ComponentDataBase.getInstance().delContacto(conatct);
        adaptador();
        barP.setVisibility(ProgressBar.INVISIBLE);
    }

    private void agregarContacto() {
        barP.setVisibility(ProgressBar.VISIBLE);
        String cel = editTel.getText().toString().trim();
        String aparen = editParen.getText().toString().trim();
        ComponentDataBase.getInstance().addContacto(aparen, cel);
        editParen.setText("");
        editTel.setText("");
        adaptador();
        barP.setVisibility(ProgressBar.INVISIBLE);
    }

    private void initElements(View root) {
        editTel = root.findViewById(R.id.tel_edit_text);
        editParen = root.findViewById(R.id.paren_edit_text);
        buttonAgregar = root.findViewById(R.id.buttonAgregar);
        listContactos = root.findViewById(R.id.listContactos);
        barP = root.findViewById(R.id.progressBarAgConConf);
    }

    public void adaptador() {
        ArrayAdapter<String> adapter;
        String[] contactArray = ComponentDataBase.getInstance().getAdaptadorContactos();
        if (contactArray == null) {
            Log.i(getClass().getSimpleName(), "Regreso un null el adaptadpr");
            listContactos.setAdapter(null);
        } else {
            adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1,
                    contactArray);
            listContactos.setAdapter(adapter);
        }
    }

}