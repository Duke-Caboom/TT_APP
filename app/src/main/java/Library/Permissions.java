package Library;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    private Activity _activity;

    public Permissions(Activity activity){
        _activity = activity;
    }
    public boolean STORAGE(){
        if ((ContextCompat.checkSelfPermission(_activity, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(_activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) ){
            ActivityCompat.requestPermissions(_activity,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },1);
            return false;
        }else{
            return true;
        }
    }
    public boolean CAMERA(){
        if ((ContextCompat.checkSelfPermission(_activity,  Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(_activity,new String[]{
                    Manifest.permission.CAMERA,
            },2);
            return false;
        }else{
            return true;
        }
    }
}