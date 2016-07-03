package com.hf.workit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.ClockService;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.DoubleExercise;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ExecuteExercise extends Activity {
    public static final int PHASE_NONE = 0;
    public static final int PHASE_EX_1 = 1;
    public static final int PHASE_EX_2 = 2;
    public static final int PHASE_BREAK = 3;

    private int mCurrentPhase = 0;

    private TextView mSetsLeftText;

    private TextView mExerciseNameText;
    private TextView mDescriptionText;
    private TextView mAmountText;
    private TextView mUnitText;
    private TextView mWeightText;
    private TextView mClockText;

    private TextView mExerciseNameText2;
    private TextView mDescriptionText2;
    private TextView mAmountText2;
    private TextView mUnitText2;
    private TextView mWeightText2;
    private TextView mClockText2;

    private TextView mBreakClockText;

    private Button mStartButton;

    private String mCurrentPlan;
    private int currentExerciseID;
    private IExercise mCurrentExercise;
    private JSONObject mCurrentExerciseJson;

    private boolean isRepeats = true;
    private boolean isRepeats2 = true;

    private boolean isDouble = false;

    private int setsCounter;
    private int mBreakTime;
    private int mAmount;
    private int mAmount2;
    private Context mContext;

    private ClockReceiver mClockReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excute_exercise);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentPlan = getIntent().getStringExtra(IPlan.PLAN_NAME);
        currentExerciseID = getIntent().getIntExtra(IExercise.EXERCISE_ID, 0);
        mCurrentExercise = PlanManager.getExerciseFromPlan(mCurrentPlan, currentExerciseID);
        mCurrentExerciseJson = mCurrentExercise.toJson();
        mCurrentPhase = getIntent().getIntExtra("last_phase", 0);
        getUIComponents();
        try {
            fillTexts();
        } catch (JSONException e) {}
        mContext = this;
        getActionBar().setTitle(mCurrentExercise.getTitle().toUpperCase());
        registerClockReceiver();
    }

    private void registerClockReceiver() {
            mClockReceiver = new ClockReceiver();
            registerReceiver(mClockReceiver, new IntentFilter("timer_update"));
    }

    private void fillTexts() throws JSONException {
        mExerciseNameText.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.NAME));
        mDescriptionText.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION));
        mAmountText.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.REPEATS));
        mAmount = mCurrentExerciseJson.getInt(Constatnts.ExerciseJson.REPEATS);
        mUnitText.setText(mCurrentExerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS) ? "Repeats" : "Seconds");
        isRepeats = mCurrentExerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS);
        mWeightText.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.WEIGHT));
        mBreakTime = mCurrentExerciseJson.getInt(Constatnts.ExerciseJson.BREAK);

        if (LogManager.getExerciseExecution(currentExerciseID) != 0)
            setsCounter = LogManager.getExerciseExecution(currentExerciseID);
        else
            setsCounter = mCurrentExerciseJson.getInt(Constatnts.ExerciseJson.NUM_OF_SETS);


        if (isDouble) {
            mExerciseNameText2.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.NAME2));
            mDescriptionText2.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION2));
            mAmountText2.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.REPEATS2));
            mAmount2 = mCurrentExerciseJson.getInt(Constatnts.ExerciseJson.REPEATS2);
            mUnitText2.setText(mCurrentExerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS2) ? "Repeats" : "Seconds");
            isRepeats2 = mCurrentExerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS2);
            mWeightText2.setText(mCurrentExerciseJson.getString(Constatnts.ExerciseJson.WEIGHT2));
            mClockText2.setText("");
        }
        mClockText.setText("");

        mSetsLeftText.setText(String.valueOf(setsCounter));
    }

    private void getUIComponents() {
        mExerciseNameText = (TextView)findViewById(R.id.exercise_name_1);
        mDescriptionText = (TextView)findViewById(R.id.exercise_description);
        mSetsLeftText = (TextView)findViewById(R.id.sets_left);
        mAmountText = (TextView)findViewById(R.id.amount_left);
        mUnitText = (TextView)findViewById(R.id.units_each_set);
        mWeightText = (TextView)findViewById(R.id.weight);
        mClockText = (TextView)findViewById(R.id.exercise_clock_1);
        mBreakClockText = (TextView)findViewById(R.id.break_clock);
        mBreakClockText.setText("");
        mStartButton = (Button)findViewById(R.id.button_start);
        mClockText.setTypeface(null, Typeface.BOLD);


        if(mCurrentExercise instanceof DoubleExercise) {
            LinearLayout ex2Layout = (LinearLayout)findViewById(R.id.exercise_exec_2);
            ex2Layout.setVisibility(View.VISIBLE);
            mExerciseNameText2 = (TextView)findViewById(R.id.exercise_name_2);
            mDescriptionText2 = (TextView)findViewById(R.id.exercise_description_2);
            mAmountText2 = (TextView)findViewById(R.id.amount_left_2);
            mUnitText2 = (TextView)findViewById(R.id.units_each_set_2);
            mWeightText2 = (TextView)findViewById(R.id.weight_2);
            mClockText2 = (TextView)findViewById(R.id.exercise_clock_2);
            mClockText2.setTypeface(null, Typeface.BOLD);
            isDouble = true;
        }
        mBreakClockText.setTypeface(null, Typeface.NORMAL);
    }

    public void buttonAction(View v) {
        if (mCurrentPhase == PHASE_BREAK || mCurrentPhase == PHASE_NONE) {
            start(null);
        } else if (mCurrentPhase == PHASE_EX_2) {
            done(null);
        } else if (mCurrentPhase == PHASE_EX_1) {
            if (isDouble)
                start(null);
            else {
                done(null);
            }
        }
    }


    public void start(View v) {
        mBreakClockText.setTextColor(0xFF00FF7F);
        mBreakClockText.setTypeface(null, Typeface.NORMAL);
        if (mCurrentPhase == PHASE_NONE) {
            mCurrentPhase = PHASE_EX_1;
            if (isDouble)
                mStartButton.setText("START");
            else
                mStartButton.setText("DONE");
        }
        else if (mCurrentPhase == PHASE_EX_1) {
            mCurrentPhase = PHASE_EX_2;
            mStartButton.setText("DONE");
        } else if (mCurrentPhase == PHASE_BREAK) {
            mCurrentPhase = PHASE_EX_1;
            if (isDouble)
                mStartButton.setText("START");
            else
                mStartButton.setText("DONE");
        } else if (mCurrentPhase == PHASE_EX_2)
            return;
        updateUiState();
    }

    private void updateUiState() {
        switch (mCurrentPhase) {
            case PHASE_NONE:
                mClockText.setVisibility(View.INVISIBLE);
                mClockText2.setVisibility(View.INVISIBLE);
                mBreakClockText.setVisibility(View.INVISIBLE);
                break;
            case PHASE_EX_1:
            case PHASE_EX_2:
                setClockTextAndNotification();
        }
    }

    private void setClockTextAndNotification() {
        boolean isRepeatsLocal;
        mBreakClockText.setVisibility(View.INVISIBLE);
        if (mCurrentPhase == PHASE_EX_1) {
            mClockText.setVisibility(View.VISIBLE);
            isRepeatsLocal = isRepeats;
        }
        else {
            isRepeatsLocal = isRepeats2;
            mClockText.setVisibility(View.INVISIBLE);
            mClockText2.setVisibility(View.VISIBLE);
        }

        if (isRepeatsLocal) {
            stopService(new Intent(this, ClockService.class));
            if (mCurrentPhase == PHASE_EX_1) {
                mClockText.setText("GO!");
                postNonRpeatNotificatoion(mExerciseNameText.getText());
            }
            else if (mCurrentPhase == PHASE_EX_2) {
                mClockText2.setText("GO!");
                postNonRpeatNotificatoion(mExerciseNameText2.getText());
            }
        } else {
            if (mCurrentPhase == PHASE_EX_1) {
                int drillTime = mAmount;
                startClockService(drillTime, PHASE_EX_1, (String) mExerciseNameText.getText());
            }
            else if (mCurrentPhase == PHASE_EX_2) {
                int drillTime = mAmount2;
                startClockService(drillTime, PHASE_EX_2, (String) mExerciseNameText2.getText());
            }
        }



    }

    private void startClockService(int drillTime, int phase, String exercise) {
        Intent intent = new Intent(this, ClockService.class);
        intent.putExtra("target", phase);
        intent.putExtra("timer_seconds", drillTime);
        intent.putExtra("title", exercise);
        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan);
        intent.putExtra(IExercise.EXERCISE_ID, currentExerciseID);
        stopService(new Intent(this, ClockService.class));
        startService(intent);
    }

    private void postNonRpeatNotificatoion(CharSequence text) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(mContext);
        notification.setContentTitle("GO GO GO!!!")
                .setSubText(text)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_fitness_center_white_48dp)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo4));
        nm.notify(232, notification.build());

    }


    public void done(View v) {
        mStartButton.setText("START");
        if (isDouble && mCurrentPhase == PHASE_EX_2) {
            mCurrentPhase = PHASE_BREAK;
        } else if (!isDouble && mCurrentPhase == PHASE_EX_1) {
            mCurrentPhase = PHASE_BREAK;
        } else {
            return;
        }
        setsCounter--;
        if (setsCounter == 0) {
            endExercise();
            return;
        }
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mSetsLeftText.setText(String.valueOf(setsCounter));
        startClockService(mBreakTime, PHASE_BREAK, null);
        mClockText.setVisibility(View.INVISIBLE);
        if (mClockText2 != null)
            mClockText2.setVisibility(View.INVISIBLE);
        mBreakClockText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCustomTitle(Utils.createTitleView(this, "Finish Exercise"))
                .setMessage("Are you sure you want to end this exercise??")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        endExercise();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    public static final int EXERCISE_NOT_STARTED = 0;
    public static final int EXERCISE_MIDWAY = 1;
    public static final int EXERCISE_FINISHED = 2;

    private void endExercise() {
        int totalSets = 0;
        try {
            totalSets = mCurrentExerciseJson.getInt(Constatnts.ExerciseJson.NUM_OF_SETS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("completed", setsCounter);
        if (setsCounter == totalSets)
            returnIntent.putExtra("execution", EXERCISE_NOT_STARTED);
        else if (setsCounter == 0)
            returnIntent.putExtra("execution", EXERCISE_FINISHED);
        else
            returnIntent.putExtra("execution", EXERCISE_MIDWAY);
        setResult(Activity.RESULT_OK, returnIntent);
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(232);
        stopService(new Intent(this, ClockService.class));
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(232);
    }

    private void notifyUser(boolean vibrate) {
        MediaPlayer p = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getApplicationContext().getAssets().openFd("whistle.mp3");
            p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            p.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        p.start();
        if (vibrate) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }
    }

    private class ClockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            float time = intent.getFloatExtra("time_value", 0);
            int target = intent.getIntExtra("target", 3);
            if (time == 0)
                notifyUser(true);

            switch (target) {
                case PHASE_EX_1:
                    mClockText.setText(String.valueOf(String.format("%.1f", time)));
                    break;
                case PHASE_EX_2:
                    mClockText2.setText(String.valueOf(String.format("%.1f", time)));
                    break;
                case PHASE_BREAK:
                    mBreakClockText.setText(String.valueOf(String.format("%.1f", time)));
                    break;
            }
        }
    }

}
