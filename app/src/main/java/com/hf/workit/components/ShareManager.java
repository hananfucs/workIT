package com.hf.workit.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by hanan on 05/07/16.
 */
public class ShareManager extends FileProvider {

    private Context mContext;

    public ShareManager(Context cn) {
        mContext = cn;
    }

    public String createPlanFile(String planName, JSONObject planJson) {
        byte[] data = new byte[0];
        String fileName = "SharedPlan.wrkt";
        File mypath;
        try {
            File folder= new File(mContext.getFilesDir(),"plans");
            if (!folder.exists())
                folder.mkdirs();

            mypath=new File(folder, fileName);

            BufferedWriter writer = new BufferedWriter(new FileWriter(mypath));

            data = planJson.toString().getBytes("UTF-8");
            String base64Plan = Base64.encodeToString(data, Base64.DEFAULT);


            writer.write(base64Plan);

            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            Log.e("HHH", "File write failed: " + e.toString());
            return null;
        }
        return mypath.getAbsolutePath();
    }

    public void sendFileThroughEmail(Activity context, String fileName) {

        File imagePath = new File(mContext.getFilesDir(), "plans");
        File newFile = new File(imagePath, "SharedPlan.wrkt");
        Uri contentUri = getUriForFile(getContext(), "com.hf.workit.fileprovider", newFile);



        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivityForResult(Intent.createChooser(emailIntent, "Share Plan"), 555);
    }
}
