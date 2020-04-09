package com.example.usersjava.ui.gallery;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.usersjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private EditText inputMensaje;
    private Button Restablecer;
    private Button Cancel;
    private String email = "";
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        //Inicializamos los elemntos creados
        initElements(root);
        //Definimos los eventos para cada elemento que se ocupe
        setButtonListeners();
        return root;
    }
    private void initElements(View root) {
        mAuth = FirebaseAuth.getInstance();
        //mDialog = new ProgressBar(this);
        inputMensaje = root.findViewById(R.id.email_edit_text_restablecer);
        Restablecer = root.findViewById(R.id.buttonRestablecer);
        Cancel = root.findViewById(R.id.buttonCancelRestablecer);
    }

    private void setButtonListeners() {
        Restablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = inputMensaje.getText().toString();
                if(!email.isEmpty()){
                    resetPassword();
                }
                else{
                    Toast.makeText(getActivity(),"Debe ingresar el email" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword(){
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Se ha enviado un correo para restablecer tu contraseña" ,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"No se pudo enviar el correo de restablecer contraseña" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}