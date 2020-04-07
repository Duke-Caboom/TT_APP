package com.example.usersjava.ui.close;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.usersjava.AddUser;
import com.example.usersjava.Principal;
import com.example.usersjava.VerifyEmail;
import com.google.firebase.auth.FirebaseAuth;

import Library.MemoryData;

public class CloseFragment extends Fragment {
    private MemoryData memoryData;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        FirebaseAuth.getInstance().signOut();
        memoryData = MemoryData.getInstance(this.requireContext());
        memoryData.saveData("user","");
        startActivity(new Intent(this.requireContext(), Principal.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        return null;
    }
}
