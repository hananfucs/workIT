package com.hf.workit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hanan on 30/06/16.
 */
public class AddCardioWorkoutFloat extends Activity {
    private String currentWorkoutId;

    private EditText mTimeText;
    private EditText mDistanceText;
    private EditText mCaloriesText;
    private Button mDateButton;

    private long mWorkoutDate = System.currentTimeMillis();

    private AlertDialog dialog = null;

    @Override
    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.add_cardio_workout);

        currentWorkoutId = getIntent().getStringExtra(IExercise.EXERCISE_ID);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        findViews();
    }

    private void findViews() {
        mTimeText = (EditText)findViewById(R.id.new_cardio_time);
        mTimeText.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        mDistanceText = (EditText)findViewById(R.id.new_cardio_distance);
        mDistanceText.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        mCaloriesText = (EditText)findViewById(R.id.new_cardio_calories);
        mCaloriesText.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        mDateButton = (Button)findViewById(R.id.new_cardio_date);
    }

    public void addCardioWorkoutToLog(View view) {

        float duration = getValueFromText(mTimeText);
        float distance = getValueFromText(mDistanceText);
        int calories = (int) getValueFromText(mCaloriesText);
        if ((calories * distance * duration) == 0)
            Utils.popToast(this, "Please fill valid values", Toast.LENGTH_SHORT);
        else{
            Utils.getDBHelper().addCardio(currentWorkoutId, duration, distance, calories, mWorkoutDate);
            Intent returnIntent = new Intent();
//        returnIntent.putExtra("completed", setsCounter);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            finish();
        }
    }

    private float getValueFromText(EditText editText) {
        String timeS = editText.getText().toString();
        if (timeS.length() == 0)
            return 0;
        return Float.parseFloat(timeS);
    }

    public void openCalanderC(View v) {

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll= (LinearLayout)inflater.inflate(R.layout.calander_alert, null, false);
        CalendarView cv = (CalendarView) ll.getChildAt(0);
        cv.setDate(System.currentTimeMillis());
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                String givenDateString = (month + 1) + " " + dayOfMonth + " " + year;
                SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
                try {
                    Date mDate = sdf.parse(givenDateString);
                    mWorkoutDate = mDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("HHH", "date:" + dayOfMonth + ":" + month + ":" + year);
                setExpirationButtonText(dayOfMonth, month + 1, year);
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(this)
                .setView(ll)
                .create();
        dialog.show();
    }

    private void setExpirationButtonText(int dayOfMonth, int month, int year) {
        String givenDateString = month + " " + dayOfMonth + " " + year;
        SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
        long textEndDate = 0;
        try {
            Date mDate = sdf.parse(givenDateString);
            textEndDate = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String date = (String) DateFormat.format("dd/MM/yyyy", textEndDate);
        mDateButton.setText(date);
    }

    public void cancel(View V) {
        Intent returnIntent = new Intent();
//        returnIntent.putExtra("completed", setsCounter);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
