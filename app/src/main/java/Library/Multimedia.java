package Library;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Multimedia {
    private Activity _activity;
    private static final int RESQUEST_CODE_CROP_IMAGE = 1;
    public static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final String TEMP_PHOYO_FILE = "temporary_img.png";
    private String mCurrentPhotoPath;
    private Uri photoURI;

    public Multimedia(Activity activity) {
        _activity = activity;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(_activity.getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            photoURI = _activity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            _activity.startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    public void cropCapturedImage(int action) {
        Intent cropIntent = null;
        switch (action) {
            case 0:
                cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(photoURI, "image/*");
                //indicamos los limites de nuestra imagen a cortar
                cropIntent.putExtra("outputX", 400);
                cropIntent.putExtra("outputY", 250);
                break;
            case 1:
                cropIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                cropIntent.setType("image/*");
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,getTempoFile());
                cropIntent.putExtra("outputX", 400);
                cropIntent.putExtra("outputY", 250);
        }
        List<ResolveInfo> list = _activity.getPackageManager().queryIntentActivities(cropIntent, 0);
        if (0 == list.size()) {
            Toast.makeText(_activity, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        } else {
            //Habilitamos el crop en este intent
            cropIntent.putExtra("crop", "true");

            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG);
            cropIntent.putExtra("scale", true);
            //True: retornara la imagen como un bitmap, False: retornara la url de la imagen la guardada.
            cropIntent.putExtra("return-data", true);

            //iniciamos nuestra activity y pasamos un codigo de respuesta.
            _activity.startActivityForResult(cropIntent, RESQUEST_CODE_CROP_IMAGE);
        }
    }

    public Uri getTempoFile(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file =new File(Environment.getExternalStorageDirectory(),TEMP_PHOYO_FILE);
            try{
                file.createNewFile();
            }catch (IOException e){}
            return Uri.fromFile(file);
        }else{
            return null;
        }
    }

    public byte[] ImgaeByte (ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        return baos.toByteArray();
    }
}