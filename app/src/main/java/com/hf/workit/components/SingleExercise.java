package com.hf.workit.components;


import org.json.JSONException;
import org.json.JSONObject;

public class SingleExercise implements IExercise {
    private String mMuscle;
    private int numOfSets;
    private String name;
    private boolean isMachine;
    private String machineNum;
    private String weight;
    private String repeats;
    private boolean mIsRepeats;
    private String breakTime;
    private int mId;



    public SingleExercise(int id, String mMuscle, int numOfSets, String name, boolean isMachine, String machineNum, String weight, boolean isRepeats, String repeats, String breakT) {
        this.mId = id;
        this.mMuscle = mMuscle;
        this.numOfSets = numOfSets;
        this.name = name;
        this.isMachine = isMachine;
        this.machineNum = machineNum;
        this.weight = weight;
        this.mIsRepeats = isRepeats;
        this.repeats = repeats;
        this.breakTime = breakT;
    }

    @Override
    public String getTitle() {
        return mMuscle;
    }

    @Override
    public String getExercise() {
        return name;
    }

    @Override
    public String getWeight() {
        return weight;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public JSONObject toJson() {
        JSONObject exerciseJson = new JSONObject();
        try {
            exerciseJson.put(Constatnts.ExerciseJson.TYPE, "single");
            exerciseJson.put(Constatnts.ExerciseJson.ID, mId);
            exerciseJson.put(Constatnts.ExerciseJson.MUSCLE, mMuscle);
            exerciseJson.put(Constatnts.ExerciseJson.NUM_OF_SETS, numOfSets);
            exerciseJson.put(Constatnts.ExerciseJson.NAME, name);
            exerciseJson.put(Constatnts.ExerciseJson.METHOD_DESCRIPTION, machineNum);
            exerciseJson.put(Constatnts.ExerciseJson.WEIGHT, weight);
            exerciseJson.put(Constatnts.ExerciseJson.IS_REPEATS, mIsRepeats);
            exerciseJson.put(Constatnts.ExerciseJson.REPEATS, repeats);
            exerciseJson.put(Constatnts.ExerciseJson.BREAK, breakTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exerciseJson;
    }

    @Override
    public String getExercise2() {
        return "";
    }

    @Override
    public String getWeight2() {
        return "0";
    }

    public static SingleExercise fromJson(JSONObject exerciseJson) throws JSONException {
        SingleExercise ret = new SingleExercise(exerciseJson.getInt(Constatnts.ExerciseJson.ID),
                exerciseJson.getString(Constatnts.ExerciseJson.MUSCLE),
                exerciseJson.getInt(Constatnts.ExerciseJson.NUM_OF_SETS),
                exerciseJson.getString(Constatnts.ExerciseJson.NAME),
                false,
                exerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION),
                exerciseJson.getString(Constatnts.ExerciseJson.WEIGHT),
                exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS),
                exerciseJson.getString(Constatnts.ExerciseJson.REPEATS),
                exerciseJson.getString(Constatnts.ExerciseJson.BREAK)
                );
        return ret;
    }

    @Override
    public int getSets() {
        return numOfSets;
    }
}

