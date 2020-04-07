package com.example.usersjava.ui.home;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.usersjava.R;

import Interfaces.IonClick;

public class HomeViewModel extends ViewModel{

    private MutableLiveData<String> mText;
    private Button buttonEnviar;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

}