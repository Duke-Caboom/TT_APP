package com.example.usersjava.ui.share;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.usersjava.ComponentDataBase;
import com.example.usersjava.MainActivity;
import com.example.usersjava.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

import Library.Networks;

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
            eliminarContacto(parent.getItemAtPosition(position).toString());
        });
    }

    private void eliminarContacto(String conatct) {

        if (new Networks(getActivity()).verificaNetworks()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Eliminar contacto");
            builder.setMessage("Deseas eliminar al contacto...");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    barP.setVisibility(ProgressBar.VISIBLE);
                    ComponentDataBase.getInstance().delContacto(conatct);
                    adaptador();
                    barP.setVisibility(ProgressBar.INVISIBLE);
                }
            });
            builder.show();

        } else {
            Toast.makeText(getActivity(), "Verifica tu conexión a internet!", Toast.LENGTH_LONG).show();
        }
    }

    private void agregarContacto() {
        if (new Networks(getActivity()).verificaNetworks()) {
            barP.setVisibility(ProgressBar.VISIBLE);
            String correo = editTel.getText().toString().trim();
            String parenteco = editParen.getText().toString().trim();

            SharedPreferences sharedPreferences;
            SharedPreferences.Editor editor;

            sharedPreferences = MainActivity.getDefaultInstance().getPreferences(Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(correo);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                @Override
                public void onEvent(@Nullable DocumentSnapshot snap, @Nullable FirebaseFirestoreException e) {
                    String numero;
                    if (snap.exists()) {
                        numero = snap.getData().get("Telefono").toString();
                        String data = sharedPreferences.getString("contactos", "");
                        if (data != "") {
                            Log.e(getClass().getSimpleName(), "Vacio");
                            String[] contactos = data.split(",");
                            for (String contacto : contactos) {
                                String[] split = contacto.split(":");
                                if (split[1].equalsIgnoreCase(numero) || split[0].equalsIgnoreCase(parenteco)) {
                                    Log.e(getClass().getName(), "El contacto ya existe");
                                    Toast.makeText(getActivity(), "El contacto ya existe!", Toast.LENGTH_LONG).show();
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
                        Log.e(getClass().getSimpleName(), "Agregado: " + sharedPreferences.getString("contactos", ""));
                        ComponentDataBase.getInstance().updateContactos();
                        Toast.makeText(getActivity(), "Agregado correctamente!", Toast.LENGTH_LONG).show();
                        barP.setVisibility(ProgressBar.INVISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "El correo no esta registrado!", Toast.LENGTH_LONG).show();
                        Log.e(getClass().getName(), "El correo no esta registrado");
                        return;
                    }
                }
            });
            editParen.setText("");
            editTel.setText("");
        } else {
            Toast.makeText(getActivity(), "Verifica tu conexión a internet!", Toast.LENGTH_LONG).show();
        }

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