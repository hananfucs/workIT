package com.hf.workit.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hanan on 18/07/16.
 */
public class DisplayImageActivity extends Activity {
    String imageName;
    Bitmap image;

    @Override
    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.display_image);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageName = getIntent().getStringExtra("image_name");
        File imgFile = new  File(new File(Constatnts.PICTURES_DIR), imageName);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.image_viewer);
            myImage.setImageBitmap(myBitmap);
            image = myBitmap;
        }
    }

    public void deleteImage(View v) {
        File file = new File(Constatnts.PICTURES_DIR, imageName);
        boolean deleted = file.delete();
        Log.d("HHH", "deleted? " + deleted);
        Intent intent = new Intent();
        intent.putExtra("deleted", imageName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void shareImage(View v) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    @Override
    public void onBackPressed() {
        image.recycle();
        image = null;
        finish();
    }

}
