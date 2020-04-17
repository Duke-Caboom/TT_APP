package ViewModels;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.usersjava.MainActivity;
import com.example.usersjava.R;
import com.example.usersjava.VerifyPassword;
import com.example.usersjava.databinding.VerifyEmailBinding;
import com.example.usersjava.databinding.VerifyPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Interfaces.IonClick;
import Library.MemoryData;
import Library.Networks;
import Library.Validate;
import Models.BindableString;

public class LoginViewModels extends ViewModel implements IonClick {
    private Activity _activity;
    public static String emailData = null;
    private static VerifyEmailBinding _bindingEmail;
    private static VerifyPasswordBinding _bindingPassword;
    public BindableString emailUI = new BindableString();
    public BindableString passwordUI = new BindableString();
    private FirebaseAuth mAuth;

    private MemoryData memoryData;

    public LoginViewModels(
            Activity activity,
            VerifyEmailBinding bindingEmail,
            VerifyPasswordBinding bindingPassword) {
        _activity = activity;
        _bindingEmail = bindingEmail;
        _bindingPassword = bindingPassword;
        if (emailData != null) {
            emailUI.setValue(emailData);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                VerifyEmail();
                break;
            case R.id.password_sign_in_button:
                login();
                break;
            case R.id.email_change_in_button:
                if (!emailUI.getValue().isEmpty()) {
                    resetPassword();
                } else {
                    Toast.makeText(_activity, "Debe ingresar el email", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        //Toast.makeText(_activity,emailUI.getValue(), Toast.LENGTH_SHORT).show();
    }

    private void resetPassword() {
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(emailUI.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(_activity, "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(_activity, "No se pudo enviar el correo de restablecer contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void VerifyEmail() {
        boolean cancel = true;
        _bindingEmail.emailEditText.setError(null);
        if (TextUtils.isEmpty(emailUI.getValue())) {
            _bindingEmail.emailEditText.setError(
                    _activity.getString(R.string.error_field_requered));
            _bindingEmail.emailEditText.requestFocus();
            cancel = false;
        } else if (!Validate.isEmail(emailUI.getValue())) {
            _bindingEmail.emailEditText.setError(
                    _activity.getString(R.string.error_invalid_email));
            _bindingEmail.emailEditText.requestFocus();
            cancel = false;
        }
        if (cancel) {
            emailData = emailUI.getValue();
            _activity.startActivity(new Intent(_activity, VerifyPassword.class));
        }
    }

    private void login() {
        boolean cancel = true;
        _bindingPassword.passwordEditText.setError(null);
        if (TextUtils.isEmpty(passwordUI.getValue())) {
            _bindingPassword.passwordEditText.setError(
                    _activity.getString(R.string.error_field_requered));
            cancel = false;
        } else if (!isPasswordValid(passwordUI.getValue())) {
            _bindingPassword.passwordEditText.setError(
                    _activity.getString(R.string.error_invalid_password));
            cancel = false;
        }
        if (cancel) {
            if (new Networks(_activity).verificaNetworks()) {
                mAuth.signInWithEmailAndPassword(emailData, passwordUI.getValue())
                        .addOnCompleteListener(_activity, (task) -> {
                            if (task.isSuccessful()) {
                                DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").
                                        document(emailData);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                memoryData = MemoryData.getInstance(_activity);
                                                memoryData.saveData("user", document.getData().get("Telefono").toString());
                                                memoryData.saveData("email", document.getData().get("Email").toString());
                                                memoryData.saveData("contactos", document.getData().get("Contactos").toString());
                                                memoryData.saveData("nombre", document.getData().get("Nombre").toString() + " " +
                                                        document.getData().get("Apellidos").toString());
                                                _activity.startActivity(new Intent(_activity, MainActivity.class)
                                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent
                                                                .FLAG_ACTIVITY_NEW_TASK));
                                            } else {
                                                Log.e(getClass().getName(), "No such document");
                                            }
                                        } else {
                                            Log.e(getClass().getName(), "get failed: " + task.getException());
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(_bindingPassword.passwordEditText,
                                        R.string.invalid_credentials, Snackbar.LENGTH_LONG).show();
                            }
                        });
            } else {
                Snackbar.make(_bindingPassword.passwordEditText,
                        R.string.networks, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }
}