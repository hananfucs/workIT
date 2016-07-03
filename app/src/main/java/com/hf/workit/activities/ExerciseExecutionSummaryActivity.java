package com.hf.workit.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.DBHelper;
import com.hf.workit.components.DoubleExercise;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by hanan on 15/06/16.
 */
public class ExerciseExecutionSummaryActivity extends Activity {
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    private TextView mExerciseNameText;
    private TextView mMuscleNameText;
    private TextView mPlanNameText;
    private TextView mCompletionText;

    private LineChartView mChart;
    private LineChartData data;

    private int mExerciseID;
    private String mPlanID;
    private IExercise mExercise;

    private ArrayList<ExerciseExecution> executions = new ArrayList<ExerciseExecution>();

    ArrayList<Pair<Long, Float>> chartDotsExecution = new ArrayList<Pair<Long, Float>>();
    ArrayList<Pair<Long, Float>> chartDotsWeight = new ArrayList<Pair<Long, Float>>();
    ArrayList<Pair<Long, Float>> chartDotsWeight2 = new ArrayList<Pair<Long, Float>>();

    @Override
    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.exercise_execution_summary);
    }

    @Override
    public void onResume() {
        super.onResume();
        mExerciseID = Integer.parseInt(getIntent().getStringExtra(IExercise.EXERCISE_ID));
        mPlanID = getIntent().getStringExtra(IPlan.PLAN_NAME);

        mExercise = PlanManager.getExerciseFromPlan(mPlanID, mExerciseID);
        TextView legend1 = (TextView)findViewById(R.id.legend1);
        legend1.setText(mExercise.getExercise());
        legend1.setTextColor(ChartUtils.COLOR_RED);
        if (mExercise instanceof DoubleExercise) {
            TextView legend2 = (TextView)findViewById(R.id.legend2);
            legend2.setVisibility(View.VISIBLE);
            legend2.setText(mExercise.getExercise2());
            legend2.setTextColor(ChartUtils.COLOR_BLUE);
        }
        getAllExecutions();
        getAndSetViews();
        chartStuff();
        getActionBar().setTitle("LOG");
    }

    private void chartStuff() {
        mChart.setOnValueTouchListener(new ValueTouchListener());

        // Generate some random values.
        generateValues();

        generateData();

        // Disable viewport recalculations, see toggleCubic() method for more info.
        mChart.setViewportCalculationEnabled(false);

        resetViewport();
    }

    private void generateData() {
        List<Line> lines = new ArrayList<Line>();

        long firstWorkout = executions.get(0).date;
        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);

        List<PointValue> values = new ArrayList<PointValue>();
        List<PointValue> values2 = new ArrayList<PointValue>();
        int i = 0;
        long currentDay = 0;
        for (int j = 0; j < numOfDays + 1; ++j) {
            if (i == chartDotsWeight.size())
                break;
            currentDay = firstWorkout + j*Constatnts.DAY_MILLIS;
            if (checkIfSameDay(currentDay, chartDotsWeight.get(i).first)) {
                values.add(new PointValue(j, chartDotsWeight.get(i).second));
                if (mExercise instanceof DoubleExercise)
                    values2.add(new PointValue(j, chartDotsWeight2.get(i).second));
                i++;
            }
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_RED);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);

        if (mExercise instanceof DoubleExercise) {
            Line line2 = new Line(values2);
            line2.setColor(ChartUtils.COLOR_BLUE);
            line2.setShape(ValueShape.DIAMOND);
            line2.setCubic(isCubic);
            line2.setFilled(isFilled);
            line2.setHasLabels(hasLabels);
            line2.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line2.setHasLines(hasLines);
            line2.setHasPoints(hasPoints);
            lines.add(line2);
        }

        data = new LineChartData(lines);
        Axis axisX = Axis.generateAxisFromCollection(generatePointsAxis(),generateDatesAxis());
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Workout Date");
        axisY.setName("Weight");

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        mChart.setLineChartData(data);
    }

    private void generateValues() {
        for (ExerciseExecution exercise: executions) {
            chartDotsExecution.add(new Pair<Long, Float>(exercise.date, (float) exercise.percentage));
        }

        for (ExerciseExecution exercise: executions) {
            chartDotsWeight.add(new Pair<Long, Float>(exercise.date, exercise.weight));
            if (mExercise instanceof DoubleExercise)
                chartDotsWeight2.add(new Pair<Long, Float>(exercise.date, exercise.weight2));
        }
    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mChart.getMaximumViewport());

        float min1 = executions.get(0).weight;
        float min2 = executions.get(0).weight2;
        float top1 = executions.get(executions.size()-1).weight;
        float top2 = executions.get(executions.size()-1).weight2;


        v.bottom = (float) (Math.min(min1, min2)*0.66);
        v.top = (float) (Math.max(top1, top2)*1.333);
        v.left = 0;
        mChart.setMaximumViewport(v);
        mChart.setCurrentViewport(v);
    }

    private void getAllExecutions() {
        Cursor cursor = Utils.getDBHelper().getReadableDatabase().rawQuery(
                "select * from exercise_table where exercise_id='" + mExerciseID + "'", null);
        if (!cursor.moveToFirst())
            return;
        do {
            ExerciseExecution execution = new ExerciseExecution();
            execution.date = Utils.getDBHelper().getDateForExerciseExecution(cursor.getString(cursor.getColumnIndex(DBHelper.ExerciseDbColumns.WORKOUT_ID)));
            execution.weight = cursor.getFloat(cursor.getColumnIndex(DBHelper.ExerciseDbColumns.WEIGHT));
            execution.weight2 = cursor.getFloat(cursor.getColumnIndex(DBHelper.ExerciseDbColumns.WEIGHT2));
            execution.percentage = cursor.getInt(cursor.getColumnIndex(DBHelper.ExerciseDbColumns.COMPLETED));
            executions.add(execution);
        } while (cursor.moveToNext());
        cursor.close();
    }

    private List<Float> generatePointsAxis() {
        long firstWorkout = executions.get(0).date;
        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);
        List<Float> ret = new ArrayList<Float>();
        for (int i = 0; i < numOfDays; i++) {
            ret.add((float) i);
        }
        Log.d("HHH", "points size: " + ret.size());

        return ret;
    }

    private List<String> generateDatesAxis() {
        long firstWorkout = executions.get(0).date;
        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < numOfDays; i++) {
            String date = (String) DateFormat.format("dd/MM", (firstWorkout + (i * Constatnts.DAY_MILLIS)));
            ret.add(date);
        }
        Log.d("HHH", "dates size: " + ret.size());
        return ret;
    }

    private void getAndSetViews() {
        mExerciseNameText = (TextView)findViewById(R.id.exercise_name_s);
        mExerciseNameText.setText(mExercise.getExercise());

        mMuscleNameText = (TextView)findViewById(R.id.muscle_name_s);
        mMuscleNameText.setText("Muscle Group: " + mExercise.getTitle());

        mPlanNameText = (TextView)findViewById(R.id.in_plan_name_s);
        mPlanNameText.setText("Plan Name: " + mPlanID);

        mCompletionText = (TextView)findViewById(R.id.average_completion_s);
        mCompletionText.setText("Average Completion: " + Utils.getDBHelper().getExerciseAverageCompletion(String.valueOf(mExerciseID)) + "%");

        mChart = (LineChartView)findViewById(R.id.exercise_chart);
    }

    private class ExerciseExecution {
        public long date;
        public float weight;
        public float weight2;
        public int percentage;
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int i, int i1, PointValue pointValue) {

        }

        @Override
        public void onValueDeselected() {

        }
    }

    private boolean checkIfSameDay(long currentDay, Long workoutDay) {
        return Math.abs(currentDay - workoutDay) < Constatnts.DAY_MILLIS;
    }
}
