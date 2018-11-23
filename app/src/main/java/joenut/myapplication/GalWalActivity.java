package joenut.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class GalWalActivity extends Activity {

    private ImageView selectedImage;
    private Bitmap currentImage;
    private final int GALLERY = 1, CAMERA = 2;
    private String IMAGE_DIRECTORY = "image/*";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gal_wal);
        selectedImage = (ImageView) findViewById(R.id.selectedImage);
        Button openGallery = (Button) findViewById(R.id.opengallery);
        Button takePhoto = (Button) findViewById(R.id.takePhoto);

        openGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, CAMERA);
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            Uri photoUri = data.getData();
//            if (photoUri != null) {
//                try {
//                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
//                    selectedImage.setImageBitmap(currentImage);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(GalWalActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    selectedImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(GalWalActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(GalWalActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}