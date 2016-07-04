package com.hf.workit.components;

import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hanan on 06/06/16.
 */
public class LogManager {

    private static final long WEEK_MILLIS = 1000*60*60*24*7;

    private static ArrayList<Integer> sExerciseExecutionColors = new ArrayList<Integer>();
    private static SparseIntArray sExerciseExecution = new SparseIntArray();
    private static long sStartDate;
    private static long sEndDate;
    private static boolean sIsWorkoutInProgress;

    private static int sWorkoutPercentage;
    private static int sWorkoutLength;

    private static String currentPlanID;


    public static int getWorkoutPercentage() {
        return sWorkoutPercentage;
    }

    public static int getWorkoutLength() {
        return sWorkoutLength;
    }

    public static int getExerciseExecution(int exerciseId) {
        return sExerciseExecution.get(exerciseId);
    }

    public static void setExerciseExecution(int execiseId, int execution) {
        sExerciseExecution.put(execiseId, execution);
    }

    public static void initExerciseExecution(List<Integer> exercises, String planName) {
        if (sExerciseExecution.size() > 0)
            return;
        for (Integer ex : exercises) {
            sExerciseExecution.put(ex, PlanManager.getExerciseFromPlan(planName, ex).getSets());
        }
    }

    public static void resetExerciseExecution() {
        sExerciseExecution.clear();
    }

    public static long getStartDate() {
        return sStartDate;
    }

    public static void startWorkout() {
        sIsWorkoutInProgress = true;
        sStartDate = System.currentTimeMillis();
    }

    public static long getEndDate() {
        return sEndDate;
    }

    public static void endWorkout() {
        sEndDate = System.currentTimeMillis();
    }

    public static boolean isWorkoutInProgress() {
        return sIsWorkoutInProgress;
    }

    public static void setIsWorkoutInProgress(boolean value) {
        sIsWorkoutInProgress = value;
    }

    public static void setCurrentPlanID(String currentPlanID) {
        LogManager.currentPlanID = currentPlanID;
    }

    public static String getCurrentPlanID() {
        return LogManager.currentPlanID;
    }

    public static void endCurrentWorkout(){

    }

    public static void maybeResetExecutionProgress() {

    }

    public static String getCurrentPlanName() {
        String currentWorkoutName = null;
        String currentWorkoutId = getCurrentPlanID();
        for (IPlan plan : PlanManager.getPlans()) {
            if (plan.planId().equals(currentWorkoutId))
                currentWorkoutName = plan.getTitle();
        }
        return currentWorkoutName;
    }

    public static int getPlanAveragePerWeek(String planID) {
        long currentTime = System.currentTimeMillis();
        long planStartTime = PlanManager.getPlanById(planID).getmCreationTime();
        long totalWeeks = ((currentTime-planStartTime) / WEEK_MILLIS) + 1;
        int numOfWorkouts = Utils.getDBHelper().getWorkoutsCount(planID);
        return (int) (numOfWorkouts / totalWeeks);
    }

    public static long getPlanAverageLength(String planID) {
        return Utils.getDBHelper().getPlanAverageLength(planID);
    }

    public static int getPlanAverageCompleted(String planID) {
        return Utils.getDBHelper().getPlanAverageCompleted(planID);
    }

    public static int getExerciseAverageCompletion(String exerciseID) {
        return Utils.getDBHelper().getExerciseAverageCompletion(exerciseID);
    }

    public static void logWorkout() {
        float totalWorkoutSets = 0;
        float completedWorkoutSets = 0;

        String workoutID = UUID.randomUUID().toString();

        for(int i = 0; i < sExerciseExecution.size(); i++) {
            int exerciseKey = sExerciseExecution.keyAt(i);
            float setsDone = PlanManager.getExerciseFromPlan(getCurrentPlanName(), exerciseKey).getSets()
                        - sExerciseExecution.get(exerciseKey);
            completedWorkoutSets += setsDone;
            float totalSets = PlanManager.getExerciseFromPlan(getCurrentPlanName(), exerciseKey).getSets();
            totalWorkoutSets += totalSets;
            int percentage = (int)(100*(setsDone/totalSets));
            float weight = Float.parseFloat(PlanManager.getExerciseFromPlan(getCurrentPlanName(), exerciseKey).getWeight());
            float weight2 = Float.parseFloat(PlanManager.getExerciseFromPlan(getCurrentPlanName(), exerciseKey).getWeight2());
            Utils.getDBHelper().addExercise(workoutID, String.valueOf(exerciseKey), weight, weight2, percentage);
        }

        long endTIme = System.currentTimeMillis();
        int workoutLength = (int) ((endTIme - sStartDate) / Constatnts.MINUTE_MILLIS);
        sWorkoutLength = workoutLength;
        int workoutPercentage = (int)((completedWorkoutSets/totalWorkoutSets) * 100);
        sWorkoutPercentage = workoutPercentage;
        Utils.getDBHelper().addWorkout(workoutID, currentPlanID, sStartDate,
                workoutLength, workoutPercentage);

    }

    public static void resetCurrentWorkout() {
        sStartDate = 0;
        sEndDate = 0;
        sExerciseExecution.clear();
        sIsWorkoutInProgress = false;
        sWorkoutPercentage = 0;
        sWorkoutLength = 0;
        currentPlanID = null;
        sExerciseExecutionColors.clear();
    }

    public static ArrayList<Integer> getExerciseStateList() {
        return sExerciseExecutionColors;
    }
}
