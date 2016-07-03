package com.hf.workit.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hanan on 22/06/16.
 */
public class MainFragment extends Fragment {
    TextView mSwipeHint;
    TextView mLastWorkoutText;
    Thread hintThread;
    private boolean stopThread = false;
    Handler mAnimateHandler = new Handler() {
        public void handleMessage(Message msg) {
            String text;
            switch (msg.what) {
                case 0:
                    text = "<font color='white'>></font><font color='black'>></font><font color='black'>></font> swipe for options";
                    mSwipeHint.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    break;
                case 1:
                    text = "<font color='black'>></font><font color='white'>></font><font color='black'>></font> swipe for options";
                    mSwipeHint.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    break;
                case 2:
                    text = "<font color='black'>></font><font color='black'>></font><font color='white'>></font> swipe for options";
                    mSwipeHint.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fregmant_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeHint = (TextView)view.findViewById(R.id.swipe_hint);
        mLastWorkoutText = (TextView)view.findViewById(R.id.last_workout_text);
        createHintThread();
        writeLastWorkout();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopThread = true;
    }


    private void writeLastWorkout() {
        String name;
        long date;
        int completed;
        try {
            JSONObject lastWorkout = Utils.getDBHelper().getLastWorkout();
            name = PlanManager.getPlanById(lastWorkout.getString("name")).getTitle();
            date = lastWorkout.getLong("date");
            completed = lastWorkout.getInt("completed");
        } catch (JSONException e) {
            return;
        }
        String dateS = (String) DateFormat.format("dd/MM/yyyy", date);
        mLastWorkoutText.setText(dateS + "\n" + name + "\n" + "Completed: " + completed + "%");
    }

    private void createHintThread() {
        if (hintThread != null)
            return;
        hintThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    if (stopThread)
                        return;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAnimateHandler.sendEmptyMessage(i);
                    i++;
                    if (i == 3)
                        i = 0;
                }

            }
        });
        hintThread.start();
    }

}
