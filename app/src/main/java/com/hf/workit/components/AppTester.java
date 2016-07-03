package com.hf.workit.components;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by hanan on 07/06/16.
 */
public class AppTester {
    private static final String workoutIdA = UUID.randomUUID().toString();
    private static final String workoutIdB = UUID.randomUUID().toString();
    private static final String workoutIdC = UUID.randomUUID().toString();
    private static final String workoutIdD = UUID.randomUUID().toString();
    private static final String workoutIdE = UUID.randomUUID().toString();
    private static final String workoutIdF = UUID.randomUUID().toString();
    private static final String workoutIdG = UUID.randomUUID().toString();
    private static final String workoutIdH = UUID.randomUUID().toString();

    static String muscleA = "forearms";
    static String exNameA = "up-lifting";

    static String muscleB = "forearms";
    static String exNameB = "up-lifting2";

    static int exIdA = (muscleA + exNameA).hashCode();
    static int exIdB = (muscleB + exNameB).hashCode();


    public static void createPlans() {
        SinglePlan planA = new SinglePlan(UUID.randomUUID().toString(), "PlanA", "Hanan Fucs", "Target", 3, getExercises(), null, false, 1463906573000L, 1463906573000L + Constatnts.MONTH_MILLIS);
        SinglePlan planB = new SinglePlan(UUID.randomUUID().toString(), "PlanB", "Hanan Fucs", "Target", 3, getExercises(), null, false, 1463906573000L, 1463906573000L + Constatnts.MONTH_MILLIS);

        PlanManager.addPlan("PlanA", planA);
        PlanManager.addPlan("PlanB", planB);
    }

    private static ArrayList<IExercise> getExercises() {
        ArrayList<IExercise> ret = new ArrayList<IExercise>();
        String muscleA = "forearms";
        String exNameA = "up-lifting";
        SingleExercise exerciseA = new SingleExercise((muscleA + exNameA).hashCode(), muscleA, 3, exNameA, false,"hello my name is hanan and i'm from israel I really like to eat tomatoes", "40", true, "12", "60");

        String muscleB = "forearms";
        String exNameB = "up-lifting2";
        DoubleExercise exerciseB = new DoubleExercise((muscleB + exNameB).hashCode(), muscleB, 3, exNameB, false,"machine 43", "40", false, "12", "arm work", false, "do some shit", "45", true, "12", "12");

        ret.add(exerciseA);
        ret.add(exerciseB);
        return ret;
    }

    public static void addWorkoutsToLog() {
        if (checkForExistingRecords()) {
            Utils.getDBHelper().resetDB();
        }
        Utils.getDBHelper().addWorkout(workoutIdA, PlanManager.getPlan("PlanA").planId(), 1460215361000L, 1000*60*60, 100);
        Utils.getDBHelper().addWorkout(workoutIdB, PlanManager.getPlan("PlanA").planId(), 1460474561000L, 1000*64*60, 95);
        Utils.getDBHelper().addWorkout(workoutIdC, PlanManager.getPlan("PlanA").planId(), 1460647361000L, 1000*61*60, 80);
        Utils.getDBHelper().addWorkout(workoutIdD, PlanManager.getPlan("PlanA").planId(), 1460820161000L, 1000*60*60, 100);
        Utils.getDBHelper().addWorkout(workoutIdE, PlanManager.getPlan("PlanA").planId(), 1460992961000L, 1000*57*60, 100);
        Utils.getDBHelper().addWorkout(workoutIdF, PlanManager.getPlan("PlanA").planId(), 1461252161000L, 1000*70*60, 97);
        Utils.getDBHelper().addWorkout(workoutIdG, PlanManager.getPlan("PlanA").planId(), 1461424961000L, 1000 * 65 * 60, 98);
        Utils.getDBHelper().addWorkout(workoutIdH, PlanManager.getPlan("PlanA").planId(), 1461597761000L, 1000 * 60 * 60, 100);

        addExercisesExecution();
    }

    public static void addExercisesExecution() {
        Utils.getDBHelper().addExercise(workoutIdA, String.valueOf(exIdA), 40, 0, 100);
        Utils.getDBHelper().addExercise(workoutIdB, String.valueOf(exIdA), 40, 0, 80);
        Utils.getDBHelper().addExercise(workoutIdC, String.valueOf(exIdA), 46, 0, 100);
        Utils.getDBHelper().addExercise(workoutIdD, String.valueOf(exIdA), 46, 0, 90);
        Utils.getDBHelper().addExercise(workoutIdE, String.valueOf(exIdA), 49, 0, 100);
        Utils.getDBHelper().addExercise(workoutIdF, String.valueOf(exIdA), 49, 0, 85);
        Utils.getDBHelper().addExercise(workoutIdG, String.valueOf(exIdA), 53, 0, 100);
        Utils.getDBHelper().addExercise(workoutIdH, String.valueOf(exIdA), 53, 0, 100);

        Utils.getDBHelper().addExercise(workoutIdA, String.valueOf(exIdB), 40, 30, 80);
        Utils.getDBHelper().addExercise(workoutIdB, String.valueOf(exIdB), 40, 30, 100);
        Utils.getDBHelper().addExercise(workoutIdC, String.valueOf(exIdB), 46, 30, 90);
        Utils.getDBHelper().addExercise(workoutIdD, String.valueOf(exIdB), 46, 34, 100);
        Utils.getDBHelper().addExercise(workoutIdE, String.valueOf(exIdB), 49, 34, 90);
        Utils.getDBHelper().addExercise(workoutIdF, String.valueOf(exIdB), 49, 38, 85);
        Utils.getDBHelper().addExercise(workoutIdG, String.valueOf(exIdB), 53, 38, 100);
        Utils.getDBHelper().addExercise(workoutIdH, String.valueOf(exIdB), 53, 38, 100);
    }

    public static void addCardioExercises() {
        PlanManager.getCardios().clear();
        String exerciseID = UUID.randomUUID().toString();
        PlanManager.getCardios().put(exerciseID, "Running");

        Utils.getDBHelper().addCardio(exerciseID, 15, 2.3f, 200, 1460215361000L);
        Utils.getDBHelper().addCardio(exerciseID, 16, 2.35f, 208, 1460474561000L);
        Utils.getDBHelper().addCardio(exerciseID, 16, 2.37f, 220, 1460647361000L);
        Utils.getDBHelper().addCardio(exerciseID, 18, 2.36f, 240, 1460820161000L);
        Utils.getDBHelper().addCardio(exerciseID, 18, 2.39f, 250, 1460992961000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.0f, 240, 1461252161000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.05f, 230, 1461424961000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1461697761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1461797761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1461897761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1461997761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1462097761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1462197761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1462297761000L);
        Utils.getDBHelper().addCardio(exerciseID, 20, 3.1f, 270, 1462397761000L);
    }

    private static boolean checkForExistingRecords() {
        Cursor cursor = Utils.getDBHelper().getReadableDatabase().rawQuery("select * from workout_table", null);
        return cursor.getCount() > 0;
    }


}

