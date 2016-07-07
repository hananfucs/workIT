package com.hf.workit.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanan on 01/04/16.
 */
public class Utils {
    private static Context sContext;

    private static DBHelper sDBHelper;
    private static ShareManager sShareManager;

    public static final int ERR_NO_INTERNET = 0;
    public static final int ERR_ELSE = 1;

    public static void initUtils(Context context) {
        sContext = context;
        sDBHelper = new DBHelper(sContext);
        sShareManager = new ShareManager(context);
    }

    public static DBHelper getDBHelper() {
        return sDBHelper;
    }

    public static ShareManager getShareManager() {
        return sShareManager;
    }


    public static void popToast(Context context, String text, int length) {
        Toast toast = Toast.makeText(context, text, length);
        View view = toast.getView();
        view.setBackgroundColor(Color.parseColor("#FF000000"));
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setBackgroundColor(Color.parseColor("#FF000000"));
        toast.setView(view);
        toast.show();
    }

    public static View createTitleView(Context context, String title) {
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setBackgroundColor(0xFF0A081A);
        mainLayout.setGravity(Gravity.CENTER);
        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setTextSize(20);
        mainLayout.addView(tv);
        return mainLayout;
    }

//    public static String createPlanFile(String planName, JSONObject planJson) {
//        byte[] data = new byte[0];
//        String fileName = planName + "_file.wrkt";
//        try {
//            data = planJson.toString().getBytes("UTF-8");
//            String base64Plan = Base64.encodeToString(data, Base64.DEFAULT);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(sContext.openFileOutput(fileName, Context.MODE_WORLD_READABLE));
//            outputStreamWriter.write(base64Plan);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("HHH", "File write failed: " + e.toString());
//            return null;
//        }
//        return fileName;
//    }


    public static String createPlanFile(Context context, String planName, JSONObject planJson) {
        Environment.getDataDirectory().toString();
        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(context.getExternalCacheDir().getPath());
        File myDir = new File(root + "/workIT");
        myDir.mkdirs();
        String fileName = planName + "_file.wrkt";
        File file = new File (myDir, fileName);
        file.setReadable(true, false);
        if (file.exists ()) file.delete ();
        try {
            byte[] data = new byte[0];
            data = planJson.toString().getBytes("UTF-8");
            FileOutputStream out = new FileOutputStream(file);
            out.write(Base64.encode(data, Base64.DEFAULT));
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    public static void sendFileThroughEmail(Activity context, String fileName) {
//        String filename="contacts_sid.vcf";
        File fileLocation = new File(context.getFilesDir(), fileName);
        Uri path = Uri.fromFile(fileLocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
//        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.setType("message/rfc822");
//        String to[] = {"asd@gmail.com"};
//        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivityForResult(Intent.createChooser(emailIntent, "Share Plan"), 555);
    }

    public static void uploadPlan(final JSONObject planJson, final Handler shareCodeHandler, final Context context) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isInternetAvailable()) {
                    shareCodeHandler.sendEmptyMessage(ERR_NO_INTERNET);
                    return;
                }

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://paste.ee/api");

                try {
                    String data = Base64.encodeToString(planJson.toString().getBytes("UTF-8"), Base64.DEFAULT);

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("key", "86d32030ab7be5087e9de4fb57665aaf"));
                    nameValuePairs.add(new BasicNameValuePair("description", ""));
                    nameValuePairs.add(new BasicNameValuePair("paste", data));
                    nameValuePairs.add(new BasicNameValuePair("expire", "3600"));
                    nameValuePairs.add(new BasicNameValuePair("format", "simple"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);

                    String responseBody = EntityUtils.toString(response.getEntity());
                    Log.d("HHH", "responseBody: " + responseBody);

                    String[] parts = responseBody.split("/");
                    String code = parts[parts.length-1];
                    Message message = Message.obtain();
                    message.obj = code;
                    message.what = 232;
                    shareCodeHandler.sendMessage(message);

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
        });
        th.start();
        // Create a new HttpClient and Post Header

    }

    public static String stringOf(InputStream inputStream) {
        inputStream.mark(Integer.MAX_VALUE);
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder strBuilder = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null)
                strBuilder.append(line);
        } catch (IOException ignored) {}
        try {
            inputStream.reset();
        } catch (IOException ignored) {}
        return strBuilder.toString();
    }

    public static void getSharedPlan(final String code, final Handler refreshHandler, final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isInternetAvailable()) {
                        refreshHandler.sendEmptyMessage(ERR_NO_INTERNET);
                        return;
                    }

                    // Create a URL for the desired page
                    URL url = new URL("https://paste.ee/d/" + code);

                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    String plan64 = "";
                    while ((str = in.readLine()) != null) {
                        plan64 = plan64 + str;
                    }
                    in.close();
                    byte[] decodedPlan = Base64.decode(plan64, Base64.DEFAULT);
                    JSONObject planJSON = new JSONObject(new String(decodedPlan));
                    String planName = planJSON.getString(Constatnts.PlanJson.NAME);
                    PlanManager.addPlan(planName, PlanManager.createPlanFromJson(planJSON));
                    refreshHandler.sendEmptyMessage(232);
                } catch (MalformedURLException e) {
                    makeErrorToast(context);
                } catch (IOException e) {
                    makeErrorToast(context);
                } catch (JSONException e) {
                    makeErrorToast(context);
                } catch (IllegalArgumentException e) {
                    makeErrorToast(context);
                }
            }

            private void makeErrorToast(Context context) {
                refreshHandler.sendEmptyMessage(ERR_ELSE);
            }
        });
        thread.start();
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
