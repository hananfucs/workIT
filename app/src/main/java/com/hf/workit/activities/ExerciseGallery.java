package com.hf.workit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by hanan on 18/07/16.
 */
public class ExerciseGallery extends Activity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    int mExerciseId;
    ArrayList<ImageItem> pictures;
    private Context mContext;


    private ProgressDialog progress;
    private Handler imageLoadingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            displayImages();
            progress.dismiss();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_gallery);

        mContext = this;
        progress = new ProgressDialog(this);
        progress.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                pictures = getData();
                imageLoadingHandler.sendEmptyMessage(0);
            }
        });
        thread.start();
//        displayImages();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void displayImages() {
        gridView = (GridView) findViewById(R.id.grid_view);
        gridAdapter = new GridViewAdapter(this, R.layout.ex_grid_image, pictures);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, DisplayImageActivity.class);
                intent.putExtra("image_name", item.getTitle());
                startActivityForResult(intent, 2);
            }
        });
    }




    private ArrayList<ImageItem> getData() {
        ArrayList<String> photosNames = (ArrayList<String>) getIntent().getSerializableExtra("exercise_photos");
        if (photosNames == null)
            return new ArrayList<ImageItem>();

        File exerciseDir = new File(Constatnts.PICTURES_DIR);
        if (exerciseDir == null)
            try {
                exerciseDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


        final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
        for (String photoName : photosNames) {
            File photoFile = new File(Constatnts.PICTURES_DIR + "/" + photoName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

//            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options), 100, 100);
            Bitmap thumbnail = getPreview(photoFile);
//            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
            imageItems.add(new ImageItem(thumbnail, photoName));
        }

        return imageItems;
    }


    public class GridViewAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList<ImageItem> data = new ArrayList();

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
                ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) row.findViewById(R.id.image);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            ImageItem item = data.get(position);
            holder.image.setImageBitmap(item.getImage());
            return row;
        }

        class ViewHolder {
            ImageView image;
        }
    }

    public class ImageItem {
        private Bitmap image;
        private String title;

        public ImageItem(Bitmap image, String title) {
            super();
            this.image = image;
            this.title = title;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public void addPhoto(View V) {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCustomTitle(Utils.createTitleView(this, getResources().getString(R.string.get_new_pic)))
                .setMessage(getResources().getString(R.string.get_new_pic_msg))
                .setPositiveButton(getResources().getString(R.string.take_pic), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getPicFromCamera();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.from_gallery), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                    }
                })
                .setIcon(android.R.drawable.picture_frame)
                .show();
    }

    ///Shit For Camera///

    private Uri mImageUri;

    private void getPicFromCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo;
        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
            Log.v("HHH", "Can't create file to take picture!");
            return;
        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //start camera intent
        startActivityForResult(intent, 0);
    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public Bitmap grabImage()
    {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap = null;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
        }
        catch (Exception e)
        {
            Log.d("HHH", "Failed to load", e);
        }
        return bitmap;
    }

    ///Shit For Camera - END///

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    loadPicFromCamera();
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    getPicFromGallery(selectedImage);
                    Log.d("HHH", selectedImage.toString());
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Bundle extras = imageReturnedIntent.getExtras();
                    Log.d("HHH", extras.toString());
                    if (imageReturnedIntent.hasExtra("deleted"))
                        removeFromPictures(imageReturnedIntent.getStringExtra("deleted"));
                    displayImages();
                }
                break;
        }
    }

    private void loadPicFromCamera() {
        progress = new ProgressDialog(this);
        progress.show();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap photo = grabImage();
                String fileName = UUID.randomUUID().toString().substring(0, 7);

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(new File(new File(Constatnts.PICTURES_DIR), fileName));
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                pictures.add(new ImageItem(ThumbnailUtils.extractThumbnail(photo, 100, 100), fileName));
                photo.recycle();
                imageLoadingHandler.sendEmptyMessage(0);
            }
        });
        th.start();


//        displayImages();
    }

    private void removeFromPictures(String deleted) {
        ImageItem toBeDeleted = null;
        for (ImageItem item : pictures) {
            if (item.getTitle().equals(deleted))
                toBeDeleted = item;
        }
        pictures.remove(toBeDeleted);
    }

    private void getPicFromCamera(Intent imageReturnedIntent) {

        Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
        String fileName = UUID.randomUUID().toString().substring(0, 7);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(new File(Constatnts.PICTURES_DIR), fileName));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        pictures.add(new ImageItem(photo, fileName));
        displayImages();


    }

    private void getPicFromGallery(Uri imageUri) {
        String fileName = UUID.randomUUID().toString().substring(0, 7);
        File newImageFile = new File(new File(Constatnts.PICTURES_DIR), fileName);
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            OutputStream out = new FileOutputStream(newImageFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = imageStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            imageStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File photoFile = new File(Constatnts.PICTURES_DIR + "/" + fileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);

//        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options), 100, 100);
        Bitmap thumbnail = getPreview(photoFile);
        pictures.add(new ImageItem(thumbnail, fileName));
        displayImages();
    }

    Bitmap getPreview(File image) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 100;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    @Override
    public void onBackPressed() {
        ArrayList<String> newPicturesList = new ArrayList<String>();
        for (ImageItem item : pictures) {
            newPicturesList.add(item.getTitle());
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("new_photos_list", newPicturesList);
        setResult(Activity.RESULT_OK, returnIntent);

        super.onBackPressed();
    }

}
