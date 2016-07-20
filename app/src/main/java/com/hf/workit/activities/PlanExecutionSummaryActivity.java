package com.hf.workit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hf.workit.R;
import com.hf.workit.components.Constatnts;
import com.hf.workit.components.DBHelper;
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
 * Created by hanan on 08/06/16.
 */
public class PlanExecutionSummaryActivity extends Activity {
    private TextView mPlanNameText;
    private TextView mCoachNameText;
    private TextView mClubNameText;
    private TextView mStartDateText;

    private IPlan mCurrentPlan;

    private ArrayList<PlanExecution> workouts = new ArrayList<PlanExecution>();

    ArrayList<Pair<Long, Float>> chartDots = new ArrayList<Pair<Long, Float>>();

    private LineChartView mChart;
    private LineChartData data;

    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    private Context mContext;

    @Override
    public void onCreate(Bundle bd) {
        super.onCreate(bd);
        setContentView(R.layout.plan_summary);
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
        mCurrentPlan = PlanManager.getPlan(getIntent().getStringExtra(IPlan.PLAN_NAME));

        getAndSetViews();
        mContext = this;
        getWorkouts();

        chartStuff();
        getActionBar().setTitle("LOG");
    }

    private void getWorkouts() {
        Cursor cursor = Utils.getDBHelper().getReadableDatabase().rawQuery(
                "select * from workout_table where plan_id='" + mCurrentPlan.planId() + "'", null);
        if (!cursor.moveToFirst())
            return;
        do {
            String planName = PlanManager.getPlanById(cursor.getString(cursor.getColumnIndex(DBHelper.WorkoutDbColumns.PLAN_ID))).getTitle();
            PlanExecution plan = new PlanExecution(planName,
                    cursor.getString(cursor.getColumnIndex(DBHelper.WorkoutDbColumns.WORKOUT_ID)),
                    cursor.getLong(cursor.getColumnIndex(DBHelper.WorkoutDbColumns.DATE)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.WorkoutDbColumns.COMPLETED)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.WorkoutDbColumns.LENGTH)));
            workouts.add(plan);
        } while (cursor.moveToNext());
        cursor.close();

    }

    public void getAndSetViews() {
        mPlanNameText = (TextView)findViewById(R.id.plan_name_s);
        mPlanNameText.setText(mCurrentPlan.getTitle());

        mCoachNameText = (TextView)findViewById(R.id.coach_name_s);
        mCoachNameText.setText(getResources().getString(R.string.trainer) + ": " + mCurrentPlan.getCoach());

        mClubNameText = (TextView)findViewById(R.id.club_name_s);
        mClubNameText .setText(getResources().getString(R.string.club) + ": " + mCurrentPlan.getClub());

        mStartDateText = (TextView)findViewById(R.id.start_date_s);
        String startDate = (String) DateFormat.format("dd-MM-yyyy", mCurrentPlan.getmCreationTime());
        mStartDateText.setText(getResources().getString(R.string.creation) +": " + startDate);

        mChart = (LineChartView)findViewById(R.id.chart);

    }

    private void chartStuff() {

        mChart.setOnValueTouchListener(new ValueTouchListener());

        generateValues();

        generateData();

        mChart.setViewportCalculationEnabled(false);

        resetViewport();
    }


    private void generateValues() {
        for (PlanExecution plan : workouts) {
            chartDots.add(new Pair<Long, Float>(plan.getmDate(), (float) plan.getmCompleted()));
        }
    }


    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(mChart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        mChart.setMaximumViewport(v);
        mChart.setCurrentViewport(v);
    }

    private void generateData() {
        List<Line> lines = new ArrayList<Line>();
        long firstWorkout;
        if (workouts.size() > 0)
            firstWorkout = workouts.get(0).getmDate();
        else
            firstWorkout = System.currentTimeMillis() - 2000;

        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);

        List<PointValue> values = new ArrayList<PointValue>();
        int i = 0;
        long currentDay = 0;
        for (int j = 0; j < numOfDays + 1; ++j) {
            if (i == chartDots.size())
                break;
            currentDay = firstWorkout + j*Constatnts.DAY_MILLIS;
            if (checkIfSameDay(currentDay, chartDots.get(i).first)) {
                values.add(new PointValue(j, chartDots.get(i).second));
                i++;
            }
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.DEFAULT_COLOR);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);

        data = new LineChartData(lines);
        Axis axisX = Axis.generateAxisFromCollection(generatePointsAxis(),generateDatesAxis());
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(getResources().getString(R.string.workout_num));
        axisY.setName(getResources().getString(R.string.percentage_completed));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        mChart.setLineChartData(data);
    }

    private boolean checkIfSameDay(long currentDay, Long workoutDay) {
        return Math.abs(currentDay - workoutDay) < Constatnts.DAY_MILLIS;
    }

    private List<Float> generatePointsAxis() {
        long firstWorkout;
        if (workouts.size() > 0)
            firstWorkout = workouts.get(0).getmDate();
        else
            firstWorkout = System.currentTimeMillis() - 2000;

        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);
        List<Float> ret = new ArrayList<Float>();
        for (int i = 0; i < numOfDays; i++) {
            ret.add((float) i);
        }
        return ret;
    }

    private List<String> generateDatesAxis() {
        long firstWorkout;
        if (workouts.size() > 0)
            firstWorkout = workouts.get(0).getmDate();
        else
            firstWorkout = System.currentTimeMillis() - 2000;

        long currentTime = System.currentTimeMillis();
        int numOfDays = (int) ((currentTime - firstWorkout) / Constatnts.DAY_MILLIS);
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < numOfDays; i++) {
            String date = (String) DateFormat.format("dd/MM", (firstWorkout + (i * Constatnts.DAY_MILLIS)));
            ret.add(date);
        }
        return ret;
    }


    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            PlanExecution clickedPlan = workouts.get(pointIndex);
            Utils.popToast(mContext, getResources().getString(R.string.date) + ": " + clickedPlan.getFormatedDate() + "\n" + getResources().getString(R.string.length)+": " +
                    clickedPlan.getmLength()/(1000*60) +  getResources().getString(R.string.minutes)+ "\n"+ getResources().getString(R.string.completed) +": " + clickedPlan.getmCompleted()
                    + "%", Toast.LENGTH_SHORT);
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }
    }

    private class PlanExecution {
        public PlanExecution(String name, String id, long date, int completed, int length) {
            mName = name;
            mId = id;
            mDate = date;
            mCompleted = completed;
            mLength = length;
        }

        private String mName;
        private String mId;
        private long mDate;
        private int mCompleted;
        private int mLength;


        public long getmDate() {
            return mDate;
        }

        public int getmCompleted() {
            return mCompleted;
        }

        public int getmLength() {
            return mLength;
        }

        public String getFormatedDate() {
            return (String) DateFormat.format("dd/MM/yy", mDate);
        }
    }

    public void openPlanExerciseSummary(View v) {
        Intent intent = new Intent(this, PlanExercisesSummaryActivity.class);
        intent.putExtra(IPlan.PLAN_NAME, mCurrentPlan.getTitle());
        startActivity(intent);
    }
}
