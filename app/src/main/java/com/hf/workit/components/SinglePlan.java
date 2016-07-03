package com.hf.workit.components;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by hanan on 12/09/15.
 */
public class SinglePlan implements IPlan {
    private String mPlanId;
    private String mPlanName;
    private String mCoachName;
    private String mClubName;
    private int mTimesPerWeek;
    String fileName;
    ArrayList<IExercise> mExerciseList;

    private String mFileName;
    private long mCreationTime;
    private long mExpirationTIme;


    public SinglePlan(String planID, String planName, String coachName, String clubName, int timesPerWeek, ArrayList<IExercise> exercises, String fileName, boolean isNew, long creationTime, long expirationTIme){
        mPlanName = planName;
        mCoachName = coachName;
        mClubName = clubName;
        mTimesPerWeek = timesPerWeek;
        mExerciseList = exercises;
        if (isNew) {
            mCreationTime = System.currentTimeMillis();
            mPlanId = UUID.randomUUID().toString();

        }
        else {
            mCreationTime = creationTime;
            mPlanId = planID;
        }
        mExpirationTIme = expirationTIme;
    }


    @Override
    public String planId() {
        return mPlanId;
    }

    @Override
    public String getTitle() {
        return mPlanName;
    }

    @Override
    public String getCoach() {
        return mCoachName;
    }

    @Override
    public String getClub() {
        return mClubName;
    }

    @Override
    public int getTimes() {
        return mTimesPerWeek;
    }

    @Override
    public String getFileName() {
        return mFileName;
    }

    @Override
    public long getmCreationTime() {
        return mCreationTime;
    }

    @Override
    public ArrayList<IExercise> getExercises() {
        return mExerciseList;
    }

    @Override
    public void setExercises(ArrayList<IExercise> exercises) {
        mExerciseList = exercises;
    }

    @Override
    public JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Constatnts.PlanJson.ID, mPlanId);
        json.put(Constatnts.PlanJson.CREATION_TIME, mCreationTime);
        json.put(Constatnts.PlanJson.TIMES_PER_WEEK, mTimesPerWeek);
        json.put(Constatnts.PlanJson.NAME, mPlanName);
        json.put(Constatnts.PlanJson.CLUB, mClubName);
        json.put(Constatnts.PlanJson.TRAINER, mCoachName);
        json.put(Constatnts.PlanJson.TYPE, "single");
        json.put(Constatnts.PlanJson.EXPIRATION_TIME, mExpirationTIme);

        return json;
    }

    @Override
    public long getExpirationTime() {
        return mExpirationTIme;
    }
}
