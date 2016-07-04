package com.hf.workit.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.DoubleExercise;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.SingleExercise;
import com.hf.workit.components.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hanan on 10/09/15.
 */
public class EditExerciseActivity extends Activity {
    private Button singleDouble;
    private AutoCompleteTextView muscle;
    private EditText breakText;
    private AutoCompleteTextView exName1;
    private AutoCompleteTextView machineNum1;
    private EditText weight1;
    private AutoCompleteTextView exName2;
    private AutoCompleteTextView machineNum2;
    private EditText weight2;
    private NumberPicker numOfSets;
    private LinearLayout secondEx;
    private TextView repeats1view;
    private TextView repeats2view;
    private EditText repeats1;
    private EditText repeats2;


    private boolean isSingle = true;
    private boolean isMachine1= true;
    private boolean isMachine2 = true;
    private boolean isRepeats1 = true;
    private boolean isRepeats2 = true;

    private String currentPlan;
    private int currentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_exercise);
        getUIComponents();
        initUIActions();
        currentPlan = getIntent().getStringExtra(IPlan.PLAN_NAME);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPlan = getIntent().getStringExtra(IPlan.PLAN_NAME);
        if (getIntent().hasExtra(IExercise.EXERCISE_ID)) {
            int id = getIntent().getIntExtra(IExercise.EXERCISE_ID, 0);
            currentId = id;
            ArrayList<IExercise> exercises = PlanManager.getExecisesForPlan(currentPlan);
            for(IExercise exercise : exercises) {
                if (exercise.getId() == id) {
                    initFields(exercise);
                    break;
                }
            }
        }
        initAutoComplete();
        getActionBar().setTitle("Edit Exercise");
    }

    private void initAutoComplete() {
        String[] muscleList = PlanManager.getAcMuscle().toArray(new String[0]);
        ArrayAdapter<String> adapterMuscle =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, muscleList);
        muscle.setAdapter(adapterMuscle);

        String[] exList = PlanManager.getAcExName().toArray(new String[0]);
        ArrayAdapter<String> adapterEx1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exList);
        exName1.setAdapter(adapterEx1);
        exName2.setAdapter(adapterEx1);

        String[] descriptionList = PlanManager.getAcExDescription().toArray(new String[0]);
        ArrayAdapter<String> adapterDescription =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, descriptionList);
        machineNum1.setAdapter(adapterDescription);
        machineNum2.setAdapter(adapterDescription);
    }

    private void initFields(IExercise exercise) {
        try {
            if (exercise instanceof SingleExercise)
                initSingleExercise((SingleExercise) exercise);
            else if (exercise instanceof DoubleExercise)
                initDoubleExercise((DoubleExercise) exercise);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initSingleExercise(SingleExercise exercise) throws JSONException {
        JSONObject exerciseJson = exercise.toJson();
        muscle.setText(exerciseJson.getString(Constatnts.ExerciseJson.MUSCLE));
        singleDouble.setText("Single");
        isSingle = true;
        secondEx.setVisibility(View.GONE);

        numOfSets.setValue(exerciseJson.getInt(Constatnts.ExerciseJson.NUM_OF_SETS));
        breakText.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.BREAK)));
        exName1.setText(exerciseJson.getString(Constatnts.ExerciseJson.NAME));
        machineNum1.setText(exerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION));
        weight1.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.WEIGHT)));
        repeats1view.setText(exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS) ? "Repeats" : "Seconds");
        isRepeats1 = exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS);
        repeats1.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.REPEATS)));

        String[] muscleList = PlanManager.getAcMuscle().toArray(new String[0]);
        ArrayAdapter<String> adapterMuscle =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, muscleList);
        muscle.setAdapter(adapterMuscle);

        String[] exList1 = PlanManager.getAcExName().toArray(new String[0]);
        ArrayAdapter<String> adapterEx =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exList1);
        exName1.setAdapter(adapterEx);

        String[] descriptionList = PlanManager.getAcExDescription().toArray(new String[0]);
        ArrayAdapter<String> adapterDescription =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, descriptionList);
        machineNum1.setAdapter(adapterDescription);

    }

    private void initDoubleExercise(DoubleExercise exercise) throws JSONException {
        JSONObject exerciseJson = exercise.toJson();
        muscle.setText(exerciseJson.getString(Constatnts.ExerciseJson.MUSCLE));
        singleDouble.setText("Double");
        isSingle = false;
        secondEx.setVisibility(View.VISIBLE);

        numOfSets.setValue(exerciseJson.getInt(Constatnts.ExerciseJson.NUM_OF_SETS));
        breakText.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.BREAK)));
        exName1.setText(exerciseJson.getString(Constatnts.ExerciseJson.NAME));
        machineNum1.setText(exerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION));
        weight1.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.WEIGHT)));
        repeats1.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.REPEATS)));
        repeats1view.setText(exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS) ? "Repeats" : "Seconds");
        isRepeats1 = exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS);

        exName2.setText(exerciseJson.getString(Constatnts.ExerciseJson.NAME2));
        machineNum2.setText(exerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION2));
        weight2.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.WEIGHT2)));
        repeats2.setText(String.valueOf(exerciseJson.getInt(Constatnts.ExerciseJson.REPEATS2)));
        repeats2view.setText(exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS2) ? "Repeats" : "Seconds");
        isRepeats2 = exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS2);
    }

    private void initUIActions() {
        singleDouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSingle) {
                    singleDouble.setText("Double");
                    secondEx.setVisibility(View.VISIBLE);
                } else {
                    singleDouble.setText("Single");
                    secondEx.setVisibility(View.GONE);
                }
                isSingle = !isSingle;
            }
        });

        numOfSets.setMaxValue(10);
        numOfSets.setMinValue(1);
        numOfSets.setWrapSelectorWheel(false);
    }

    public void setRepeat1(View v){
        if (isRepeats1)
            repeats1view.setText("Seconds");
        else
            repeats1view.setText("Repeats");
        isRepeats1 = !isRepeats1;
    }

    public void setRepeat2(View v){
        if(isRepeats2)
            repeats2view.setText("Seconds");
        else
            repeats2view.setText("Repeats");
        isRepeats2 = !isRepeats2;
    }


    private void getUIComponents() {
        secondEx = (LinearLayout)findViewById(R.id.second_ex_params);
        singleDouble = (Button)findViewById(R.id.single_double);
        muscle = (AutoCompleteTextView)findViewById(R.id.muscle_group);
        exName1= (AutoCompleteTextView)findViewById(R.id.ex_name_1);
        exName2= (AutoCompleteTextView)findViewById(R.id.ex_name_2);
        machineNum1= (AutoCompleteTextView)findViewById(R.id.machine_num_1);
        machineNum2= (AutoCompleteTextView)findViewById(R.id.machine_num_2);
        numOfSets = (NumberPicker)findViewById(R.id.sets_picker);
        weight1 = (EditText)findViewById(R.id.ex_weight_1);
        weight2 = (EditText)findViewById(R.id.ex_weight_2);
        breakText = (EditText)findViewById(R.id.ex_break);
        repeats1 = (EditText)findViewById(R.id.ex_repeats_1);
        repeats2 = (EditText)findViewById(R.id.ex_repeats_2);
        repeats1view = (TextView)findViewById(R.id.ex_length1);
        repeats2view = (TextView)findViewById(R.id.ex_length2);
    }

    public void saveAndReturn(View v){
        IExercise ret;
        String muscleGroup = String.valueOf(muscle.getText());
        int numOfSets1 = numOfSets.getValue();
        String breakTime = String.valueOf(breakText.getText());

        String ex1name = String.valueOf(exName1.getText());
        if (muscleGroup == null || muscleGroup.length() == 0 || ex1name == null || ex1name.length() ==0) {
//            Toast.makeText(this, "Please enter Muscle Group and Exercise Name", Toast.LENGTH_SHORT).show();
            Utils.popToast(this, "Please enter Muscle Group and Exercise Name", Toast.LENGTH_SHORT);
            return;
        }

        String device1 = String.valueOf(machineNum1.getText());
        String weight1text = String.valueOf(weight1.getText());
        String repeats1text = String.valueOf(repeats1.getText());

        String ex2name = String.valueOf(exName2.getText());
        String device2 = String.valueOf(machineNum2.getText());
        String weight2text = String.valueOf(weight2.getText());
        String repeats2text = String.valueOf(repeats2.getText());

        PlanManager.addToAcDescription(device1);
        PlanManager.addToAcMuscle(muscleGroup);
        PlanManager.addToAcExName(ex1name);

        if (isSingle){
            ret = new SingleExercise((muscleGroup + ex1name).hashCode(), muscleGroup, numOfSets1,
                    ex1name, isMachine1, device1, weight1text, isRepeats1, repeats1text, breakTime);
        } else {
            ret = new DoubleExercise((muscleGroup + ex1name).hashCode(), muscleGroup, numOfSets1,
                    ex1name, isMachine1, device1, weight1text, isRepeats1, repeats1text, ex2name, isMachine2,
                    device2, weight2text, isRepeats2, repeats2text, breakTime);
            PlanManager.addToAcDescription(device2);
            PlanManager.addToAcExName(ex2name);
        }
        PlanManager.addExerciseToPlan(currentPlan, ret);
        finish();

    }

    public void deleteAndReturn(View v) {
        ArrayList<IExercise> exercises = PlanManager.getExecisesForPlan(currentPlan);
        IExercise toRemove = null;
        for (IExercise ex : exercises) {
            if (ex.getId() == currentId) {
                toRemove = ex;
                break;
            }
        }
        exercises.remove(toRemove);
        PlanManager.setExercisesInPlan(currentPlan, exercises);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveAndReturn(null);
        finish();
    }
}
