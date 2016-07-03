package com.hf.workit.components;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hanan on 01/04/16.
 */
public class Utils {
    private static Context sContext;

    private static DBHelper sDBHelper;

    public static void initUtils(Context context) {
        sContext = context;
        sDBHelper = new DBHelper(sContext);
    }

    public static DBHelper getDBHelper() {
        return sDBHelper;
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
}
