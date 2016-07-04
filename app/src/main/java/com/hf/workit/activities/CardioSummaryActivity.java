package com.hf.workit.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.DBHelper;
import com.hf.workit.components.DoubleExercise;
import com.hf.workit.components.IExercise;
import com.hf.workit.components.IPlan;
import com.hf.workit.components.LogManager;
import com.hf.workit.components.PlanManager;
import com.hf.workit.components.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * Created by hanan on 30/06/16.
 */
public class CardioSummaryActivity extends Activity {
    private static final int DATA_DURATION = 0;
    private static final int DATA_DISTANCE = 1;
    private static final int DATA_CALORIES = 2;


    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    private LineChartView mChart;
    private LineChartData data;

    private String mExerciseID;

    private ArrayList<ExerciseExecution> executions = new ArrayList<ExerciseExecution>();

    ArrayList<Pair<Long, Float>> chartDotsExecution = new ArrayList<Pair<Long, Float>>();
    ArrayList<Pair<Long, Float>> chartDotsWeight = new ArrayList<Pair<Long, Float>>();

    @Override
    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.cardio_execution_summary);
    }

    @Override
    public void onResume() {
        super.onResume();
        mExerciseID = getIntent().getStringExtra(IExercise.EXERCISE_ID);
        mChart = (LineChartView)findViewById(R.id.exercise_chart);
        getActionBar().setTitle("LOG");

        TextView title = (TextView)findViewById(R.id.exercise_name_c);
        title.setText(PlanManager.getCardios().get(mExerciseID));

        getAllExecutions();
        chartStuff(DATA_DURATION);
        RadioGroup rg = (RadioGroup)findViewById(R.id.cardio_radio_group);
        rg.check(findViewById(R.id.duration_radio).getId());
    }

    private void chartStuff(int dataType) {
        mChart.setOnValueTouchListener(new ValueTouchListener());

        // Generate some random values.
        generateValues(dataType);

        generateData(dataType);

        // Disable viewport recalculations, see toggleCubic() method for more info.
        mChart.setViewportCalculationEnabled(false);
        resetViewport(dataType);
    }

    private void generateData(int dataType) {
        List<Line> lines = null;
        boolean isNew = false;
        if (data != null)
            lines = data.getLines(); //new ArrayList<Line>();
        if (lines == null || lines.get(0).getValues().size() != executions.size()) {
            lines = new ArrayList<Line>();
            isNew = true;
        }
        long firstWorkout = executions.get(0).date;
        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);

        List<PointValue> values;
        if (lines.size() != 0)
            values = lines.get(0).getValues();//new ArrayList<PointValue>();
        else
            values = new ArrayList<PointValue>();
        int i = 0;
        long currentDay = 0;
        if (values.size() == 0 || values.size() != executions.size()){
            for (int j = 0; j < numOfDays + 1; ++j) {
                if (i == chartDotsWeight.size())
                    break;
                currentDay = firstWorkout + j * Constatnts.DAY_MILLIS;
                if (checkIfSameDay(currentDay, chartDotsWeight.get(i).first)) {
                    values.add(new PointValue(j, chartDotsWeight.get(i).second));
                    i++;
                }
            }
        } else {
            int m = 0;
            for (PointValue value : values) {
                value.setTarget(value.getX(), chartDotsWeight.get(m).second);
                m++;
            }
        }

        if (isNew) {
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
        } else {
            lines.get(0).setValues(values);
        }
        if (data == null)
            data = new LineChartData(lines);
        else
            data.setLines(lines);
        Axis axisX = Axis.generateAxisFromCollection(generatePointsAxis(),generateDatesAxis());
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Workout Date");
        switch (dataType) {
            case DATA_DISTANCE:
                axisY.setName("Distance");
                break;
            case DATA_DURATION:
                axisY.setName("Duration");
                break;
            case DATA_CALORIES:
                axisY.setName("Calories");
                break;
        }

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        mChart.setLineChartData(data);
        mChart.startDataAnimation();
    }

    private void generateValues(int dataType) {
        chartDotsWeight.clear();
        chartDotsExecution.clear();
        for (ExerciseExecution exercise: executions) {
            switch (dataType) {
                case DATA_DURATION:
                    chartDotsExecution.add(new Pair<Long, Float>(exercise.date, (float) exercise.duration));
                    chartDotsWeight.add(new Pair<Long, Float>(exercise.date, exercise.duration));
                    break;
                case DATA_CALORIES:
                    chartDotsExecution.add(new Pair<Long, Float>(exercise.date, (float) exercise.calories));
                    chartDotsWeight.add(new Pair<Long, Float>(exercise.date, (float) exercise.calories));
                    break;
                case DATA_DISTANCE:
                    chartDotsExecution.add(new Pair<Long, Float>(exercise.date, (float) exercise.distance));
                    chartDotsWeight.add(new Pair<Long, Float>(exercise.date, exercise.distance));
                    break;
            }
        }
    }

    private void resetViewport(int dataType) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mChart.getMaximumViewport());

        float min1 = 0;
        float top1 = 0;
        switch (dataType) {
            case DATA_CALORIES:
                min1 = executions.get(0).calories;
                top1 = executions.get(0).calories;
                for (ExerciseExecution execution : executions) {
                    if (execution.calories < min1)
                        min1 = execution.calories;
                    if (execution.calories > top1)
                        top1 = execution.calories;
                }
                break;
            case DATA_DISTANCE:
                min1 = executions.get(0).distance;
                top1 = executions.get(0).distance;
                for (ExerciseExecution execution : executions) {
                    if (execution.distance < min1)
                        min1 = execution.distance;
                    if (execution.distance > top1)
                        top1 = execution.distance;
                }
                break;
            case DATA_DURATION:
                min1 = executions.get(0).duration;
                top1 = executions.get(0).duration;
                for (ExerciseExecution execution : executions) {
                    if (execution.duration < min1)
                        min1 = execution.duration;
                    if (execution.duration > top1)
                        top1 = execution.duration;
                }
                break;


        }
        v.bottom = (float) (min1*0.66);
        v.top = (float) (top1*1.333);
        v.left = 0;
        mChart.setMaximumViewport(v);
        mChart.setCurrentViewport(v);
    }

    private void getAllExecutions() {
        executions.clear();
        Cursor cursor = Utils.getDBHelper().getReadableDatabase().rawQuery(
                "select * from cardio_table where exercise_type_id='" + mExerciseID + "'", null);
        if (!cursor.moveToFirst())
            return;
        do {
            ExerciseExecution execution = new ExerciseExecution();
            execution.date =  cursor.getLong(cursor.getColumnIndex(DBHelper.CardioDbColumns.DATE));
            execution.duration = cursor.getFloat(cursor.getColumnIndex(DBHelper.CardioDbColumns.DURATION));
            execution.distance = cursor.getFloat(cursor.getColumnIndex(DBHelper.CardioDbColumns.DISTANCE));
            execution.calories = cursor.getInt(cursor.getColumnIndex(DBHelper.CardioDbColumns.CALORIES));
            executions.add(execution);
        } while (cursor.moveToNext());
        cursor.close();
        Collections.sort(executions, new Comparator<ExerciseExecution>() {
            @Override
            public int compare(ExerciseExecution lhs, ExerciseExecution rhs) {
                int ret = 0;
                if(lhs.date - rhs.date < 0)
                    ret = -1;
                else if (lhs.date - rhs.date > 0)
                    ret = 1;
                else if (lhs.date - rhs.date == 0)
                    ret = 0;
                return ret;
            }
        });
    }

    private List<Float> generatePointsAxis() {
        long firstWorkout = executions.get(0).date;
        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);
        List<Float> ret = new ArrayList<Float>();
        for (int i = 0; i < numOfDays; i++) {
            ret.add((float) i);
        }
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
        return ret;
    }

    private class ExerciseExecution {
        public long date;
        public float duration;
        public float distance;
        public int calories;
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

    public void addCardioWorkout(View v) {
        Intent intent = new Intent(this, AddCardioWorkoutFloat.class);
        intent.putExtra(IExercise.EXERCISE_ID, mExerciseID);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == Activity.RESULT_OK) {
           finish();
           startActivity(getIntent());
       }
    }

    public void modifyChart(View v) {
        if (v.getId() == R.id.calories_radio)
            chartStuff(DATA_CALORIES);
        else if (v.getId() == R.id.distance_radio)
            chartStuff(DATA_DISTANCE);
        else if (v.getId() == R.id.duration_radio)
            chartStuff(DATA_DURATION);
    }
}
