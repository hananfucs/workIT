package com.hf.workit.components;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hanan on 07/06/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "workitDB.db";

    public void resetDB() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(
                "delete from workout_table ");
        db.execSQL(
                "delete from exercise_table ");
        db.execSQL(
                "delete from cardio_table ");
    }

    public static class WorkoutDbColumns {
        public static String TABLE_NAME = "workout_table";

        public static String WORKOUT_ID = "workout_id";
        public static String PLAN_ID = "plan_id";
        public static String DATE = "date";
        public static String LENGTH = "length";
        public static String COMPLETED = "completed";
    }

    public static class ExerciseDbColumns {
        public static String TABLE_NAME = "exercise_table";

        public static String WORKOUT_ID = "workout_id";
        public static String EXERCISE_ID = "exercise_id";
        public static String WEIGHT = "weight";
        public static String WEIGHT2 = "weight2";
        public static String COMPLETED = "completed";
    }

    public static class CardioDbColumns {
        public static String TABLE_NAME = "cardio_table";

        public static String EXERCISE_TYPE_ID = "exercise_type_id";
        public static String DURATION = "duration";
        public static String DISTANCE = "distance";
        public static String CALORIES = "calories";
        public static String DATE = "date";
    }


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table IF NOT EXISTS workout_table " +
                "(workout_id text, plan_id text, date long, length long, completed integer)"
        );
        db.execSQL(
                "create table IF NOT EXISTS exercise_table " +
                        "(workout_id text, exercise_id text, weight real, weight2 real, completed integer)"
        );

        db.execSQL(
                "create table IF NOT EXISTS cardio_table " +
                        "(exercise_type_id text, duration real, distance real, calories real, date real)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS exercise_table_table");
        db.execSQL("DROP TABLE IF EXISTS workout_table");
        db.execSQL("DROP TABLE IF EXISTS cardio_table");
        onCreate(db);
    }

    public void addWorkout(String workoutId, String planId, long date, long length, int completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorkoutDbColumns.WORKOUT_ID, workoutId);
        contentValues.put(WorkoutDbColumns.PLAN_ID, planId);
        contentValues.put(WorkoutDbColumns.DATE, date);
        contentValues.put(WorkoutDbColumns.LENGTH, length);
        contentValues.put(WorkoutDbColumns.COMPLETED, completed);
        db.insert(WorkoutDbColumns.TABLE_NAME, null, contentValues);
    }

    public void addExercise(String workoutId, String exerciseId, float weight, float weight2, int completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExerciseDbColumns.WORKOUT_ID, workoutId);
        contentValues.put(ExerciseDbColumns.EXERCISE_ID, exerciseId);
        contentValues.put(ExerciseDbColumns.WEIGHT, weight);
        contentValues.put(ExerciseDbColumns.WEIGHT2, weight2);
        contentValues.put(ExerciseDbColumns.COMPLETED, completed);
        db.insert(ExerciseDbColumns.TABLE_NAME, null, contentValues);
    }

    public void addCardio(String exerciseId, float duration, float distance, int calories, long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardioDbColumns.EXERCISE_TYPE_ID, exerciseId);
        contentValues.put(CardioDbColumns.DURATION, duration);
        contentValues.put(CardioDbColumns.DISTANCE, distance);
        contentValues.put(CardioDbColumns.CALORIES, calories);
        contentValues.put(CardioDbColumns.DATE, date);
        db.insert(CardioDbColumns.TABLE_NAME, null, contentValues);
    }

    public int getWorkoutsCount(String planID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from workout_table where plan_id='" + planID + "'", null);
        if (!cursor.moveToFirst())
            return 0;
        int ret = 0;
        do {
            ret++;
        } while (cursor.moveToNext());
        cursor.close();
        return ret;
    }

    public long getPlanAverageLength(String planID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from workout_table where plan_id='" + planID + "'", null);
        if (!cursor.moveToFirst())
            return 0;
        long total = 0;
        do {
            int index = cursor.getColumnIndex(WorkoutDbColumns.LENGTH);
            total = total + cursor.getLong(index);
        } while (cursor.moveToNext());
        long ret = total / cursor.getCount();
        cursor.close();
        return ret/60000;
    }

    public int getPlanAverageCompleted(String planID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from workout_table where plan_id='" + planID + "'", null);
        if (!cursor.moveToFirst())
            return 0;
        int total = 0;
        do {
            total = total + cursor.getInt(cursor.getColumnIndex(WorkoutDbColumns.COMPLETED));
        } while (cursor.moveToNext());
        int ret = total / cursor.getCount();
        cursor.close();
        return ret;
    }

    public int getExerciseAverageCompletion(String exerciseID) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from exercise_table where exercise_id='" + exerciseID + "'", null);
        if (!cursor.moveToFirst())
            return 0;
        int total = 0;
        do {
            total = total + cursor.getInt(cursor.getColumnIndex(ExerciseDbColumns.COMPLETED));
        } while (cursor.moveToNext());
        int ret = total / cursor.getCount();
        cursor.close();
        return ret;
    }

    public long getDateForExerciseExecution(String workoutUniqueId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from workout_table where workout_id='" + workoutUniqueId + "'", null);
        if (!cursor.moveToFirst())
            return 0;
        long ret = 0;
        ret = cursor.getLong(cursor.getColumnIndex(WorkoutDbColumns.DATE));
        cursor.close();
        return ret;
    }

    public JSONObject getLastWorkout() throws JSONException {
        JSONObject ret = new JSONObject();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from workout_table where date = (select MAX(date) from workout_table)", null);
        if (!cursor.moveToFirst())
            return null;
        ret.put("name", cursor.getString(cursor.getColumnIndex(WorkoutDbColumns.PLAN_ID)));
        ret.put("completed", cursor.getInt(cursor.getColumnIndex(WorkoutDbColumns.COMPLETED)));
        ret.put("date", cursor.getLong(cursor.getColumnIndex(WorkoutDbColumns.DATE)));

        cursor.close();
        return ret;
    }

}
