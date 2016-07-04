package com.hf.workit.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.ABPlan;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.SinglePlan;
import com.hf.workit.components.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hanan on 10/09/15.
 */
public class EditPlanDetailsActivity extends Activity {
    private boolean isSingle = true;
    private String planName;
    private String coachName;
    private String clubName;
    private int timesPerWeek;
    private String fileName;
    private long mExpirationDate = System.currentTimeMillis() + (Constatnts.MONTH_MILLIS * 2);

    private IPlan currPlan;
    private ArrayList<IExercise> exercises = new ArrayList<IExercise>();

    private EditText planNameText;
    private AutoCompleteTextView coachNameText;
    private AutoCompleteTextView clubNameText;
    private NumberPicker timesPerWeekPicker;

    private Button mEndDateButton;

    private boolean isNew = true;

    private JSONObject planJson = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_plan_details);
        if(getIntent().hasExtra(IPlan.PLAN_NAME)) {
            currPlan = PlanManager.getPlan(getIntent().getStringExtra(IPlan.PLAN_NAME));
            isNew = false;
        }
        setLayout();
        getActionBar().setTitle("Details");
    }

    private void setLayout() {
        planNameText = (EditText)findViewById(R.id.plan_name);
        coachNameText = (AutoCompleteTextView)findViewById(R.id.trainer_name);
        clubNameText = (AutoCompleteTextView)findViewById(R.id.club_name);
        timesPerWeekPicker = (NumberPicker)findViewById(R.id.daysPicker);
        mEndDateButton = (Button)findViewById(R.id.expiration_button);

        timesPerWeekPicker.setMaxValue(7);
        timesPerWeekPicker.setMinValue(1);
        timesPerWeekPicker.setWrapSelectorWheel(false);

        if(currPlan!=null){
            planName = currPlan.getTitle();
            coachName = currPlan.getCoach();
            clubName = currPlan.getClub();
            timesPerWeek = currPlan.getTimes();
            exercises = currPlan.getExercises();
            fileName = currPlan.getFileName();
            mExpirationDate =currPlan.getExpirationTime();

            planNameText.setText(planName);
            planNameText.setEnabled(false);
            coachNameText.setText(coachName);

            clubNameText.setText(clubName);
            timesPerWeekPicker.setValue(timesPerWeek);
            mEndDateButton.setText(DateFormat.format("dd/MM/yyyy", mExpirationDate));
        }

        if (PlanManager.getAcTrainer() != null) {
            String[] coachList = PlanManager.getAcTrainer().toArray(new String[0]);
            ArrayAdapter<String> adapterCoach =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, coachList);
            coachNameText.setAdapter(adapterCoach);
        }

        if (PlanManager.getAcClub() != null) {
            String[] clubList = PlanManager.getAcClub().toArray(new String[0]);
            ArrayAdapter<String> adapterClub =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, clubList);
            clubNameText.setAdapter(adapterClub);
        }

    }

    public void openPlanContent(View v){
        try {
            planJson.put(Constatnts.PlanJson.NAME, planNameText.getText());
            planJson.put(Constatnts.PlanJson.TRAINER, coachNameText.getText());
            planJson.put(Constatnts.PlanJson.CLUB, clubNameText.getText());
            planJson.put(Constatnts.PlanJson.TIMES_PER_WEEK, timesPerWeekPicker.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!savePlan(isNew)) {
             Utils.popToast(this, "Enter plan name first", Toast.LENGTH_SHORT);
            return;
        }
        Intent i = new Intent(this, EditPlanContentActivity.class);
        i.putExtra("SINGLE", isSingle);
        i.putExtra(IPlan.PLAN_NAME, planNameText.getText().toString());
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void saveAndReturn(View v) {
        if (!savePlan(isNew))
            Utils.popToast(this, "Nothing to save", Toast.LENGTH_SHORT);
        finish();
    }

    public boolean savePlan(boolean isNew){
        IPlan newPlan;
        planName = String.valueOf(planNameText.getText());
        if (planName == null || planName.length() == 0) {
            return false;
        }
        coachName = String.valueOf(coachNameText.getText());
        clubName = String.valueOf(clubNameText.getText());
        timesPerWeek = timesPerWeekPicker.getValue();

        PlanManager.addToAcTrainer(coachName);
        PlanManager.addToAcClub(clubName);

        if(isSingle){
            newPlan = new SinglePlan(currPlan.planId(),planName,coachName,clubName,timesPerWeek,PlanManager.getExecisesForPlan(planName),fileName, isNew, System.currentTimeMillis(), mExpirationDate);
        } else {
            newPlan = new ABPlan(planName,coachName,clubName,timesPerWeek,PlanManager.getExecisesForPlan(planName), fileName, isNew, System.currentTimeMillis());
        }

        PlanManager.addPlan(planName, newPlan);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!savePlan(isNew))
            Utils.popToast(this, "Nothing to save", Toast.LENGTH_SHORT);
//            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show();
        finish();
    }


    public void deletePlan(View v) {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setCustomTitle(Utils.createTitleView(this, "Delete Plan"))
                .setMessage("Are you sure you want to delete this Plan?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (currPlan != null)
                            PlanManager.removePlan(currPlan.getTitle());
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private AlertDialog dialog = null;

    public void openCalander(View v) {

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll= (LinearLayout)inflater.inflate(R.layout.calander_alert, null, false);
        CalendarView cv = (CalendarView) ll.getChildAt(0);
        cv.setMinDate(System.currentTimeMillis());
        cv.setDate(mExpirationDate);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                String givenDateString = (month + 1) + " " + dayOfMonth + " " + year;
                SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
                try {
                    Date mDate = sdf.parse(givenDateString);
                    mExpirationDate = mDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
        mEndDateButton.setText(date);
    }
}
