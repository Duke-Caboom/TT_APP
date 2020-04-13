package ViewModels;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.usersjava.AddUser;
import com.example.usersjava.R;
import com.example.usersjava.VerifyEmail;

import Interfaces.IonClick;

public class PrincipalViewModel extends ViewModel implements IonClick {
    private Activity _activity;

    public PrincipalViewModel(Activity activity) {
        _activity = activity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registro:
                registro();
                break;
            case R.id.iniciarsesion:
                iniciarsesion();
                break;
        }
        //Toast.makeText(_activity,emailUI.getValue(), Toast.LENGTH_SHORT).show();
    }

    private void registro() {
        _activity.startActivity(new Intent(_activity, AddUser.class));
    }

    private void iniciarsesion() {
        _activity.startActivity(new Intent(_activity, VerifyEmail.class));
    }
}
