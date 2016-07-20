package com.hf.workit.components;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hanan on 12/09/15.
 */
public class DoubleExercise implements IExercise {
    private String mMuscle;
    private int numOfSets;
    private String breakTime;

    private String name1;
    private boolean isMachine1;
    private String machineNum1;
    private String weight1;
    private String repeats1;
    private boolean mIsRepeats1;

    private String name2;
    private boolean isMachine2;
    private String machineNum2;
    private String weight2;
    private String repeats2;
    private boolean mIsRepeats2;

    private int mId;
    private ArrayList<String> photos;


    public DoubleExercise(int id, String mMuscle, int numOfSets, String name1, boolean isMachine1,
                          String machineNum1, String weight1,  boolean isRepeats1, String repeats1, String name2, boolean isMachine2
                          , String machineNum2, String weight2,  boolean isRepeats2, String repeats2, String breakT, ArrayList<String> photos) {
        this.mId = id;
        this.mMuscle = mMuscle;
        this.numOfSets = numOfSets;
        this.name1 = name1;
        this.isMachine1 = isMachine1;
        this.machineNum1 = machineNum1;
        this.weight1 = weight1;
        this.repeats1 = repeats1;
        this.mIsRepeats1 = isRepeats1;
        this.name2 = name2;

        this.isMachine2 = isMachine2;
        this.machineNum2 = machineNum2;
        this.weight2 = weight2;
        this.repeats2 = repeats2;
        this.mIsRepeats2 = isRepeats2;
        this.breakTime = breakT;
        this.photos = photos;
    }

    @Override
    public String getTitle() {
        return mMuscle;
    }

    @Override
    public String getExercise() {
        return name1;
    }

    @Override
    public String getWeight() {
        return weight1;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public int getSets() {
        return numOfSets;
    }

    @Override
    public JSONObject toJson() {
        JSONObject exerciseJson = new JSONObject();
        try {
            exerciseJson.put(Constatnts.ExerciseJson.TYPE, "double");
            exerciseJson.put(Constatnts.ExerciseJson.ID, mId);
            exerciseJson.put(Constatnts.ExerciseJson.MUSCLE, mMuscle);
            exerciseJson.put(Constatnts.ExerciseJson.NUM_OF_SETS, numOfSets);
            exerciseJson.put(Constatnts.ExerciseJson.NAME, name1);
            exerciseJson.put(Constatnts.ExerciseJson.METHOD_DESCRIPTION, machineNum1);
            exerciseJson.put(Constatnts.ExerciseJson.WEIGHT, weight1);
            exerciseJson.put(Constatnts.ExerciseJson.IS_REPEATS, mIsRepeats1);
            exerciseJson.put(Constatnts.ExerciseJson.REPEATS, repeats1);

            exerciseJson.put(Constatnts.ExerciseJson.NAME2, name2);
            exerciseJson.put(Constatnts.ExerciseJson.METHOD_DESCRIPTION2, machineNum2);
            exerciseJson.put(Constatnts.ExerciseJson.WEIGHT2, weight2);
            exerciseJson.put(Constatnts.ExerciseJson.IS_REPEATS2, mIsRepeats2);
            exerciseJson.put(Constatnts.ExerciseJson.REPEATS2, repeats2);

            exerciseJson.put(Constatnts.ExerciseJson.BREAK, breakTime);
            exerciseJson.put(Constatnts.ExerciseJson.PHOTOS, new JSONArray(photos));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exerciseJson;
    }

    @Override
    public String getExercise2() {
        return name2;
    }

    @Override
    public String getWeight2() {
        return weight2;
    }

    @Override
    public ArrayList<String> getPhotos() {
        return photos;
    }

    public static DoubleExercise fromJson(JSONObject exerciseJson) throws JSONException {
        DoubleExercise ret = new DoubleExercise(exerciseJson.getInt(Constatnts.ExerciseJson.ID),
                exerciseJson.getString(Constatnts.ExerciseJson.MUSCLE),
                exerciseJson.getInt(Constatnts.ExerciseJson.NUM_OF_SETS),
                exerciseJson.getString(Constatnts.ExerciseJson.NAME),
                false,
                exerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION),
                exerciseJson.getString(Constatnts.ExerciseJson.WEIGHT),
                exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS),
                exerciseJson.getString(Constatnts.ExerciseJson.REPEATS),

                exerciseJson.getString(Constatnts.ExerciseJson.NAME2),
                false,
                exerciseJson.getString(Constatnts.ExerciseJson.METHOD_DESCRIPTION2),
                exerciseJson.getString(Constatnts.ExerciseJson.WEIGHT2),
                exerciseJson.getBoolean(Constatnts.ExerciseJson.IS_REPEATS2),
                exerciseJson.getString(Constatnts.ExerciseJson.REPEATS2),

                exerciseJson.getString(Constatnts.ExerciseJson.BREAK),
                getPhotosFromJson(exerciseJson.getJSONArray(Constatnts.ExerciseJson.PHOTOS))
        );
        return ret;
    }


    private static ArrayList<String> getPhotosFromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<String> listdata = new ArrayList<String>();
        if (jsonArray != null) {
            for (int i=0;i<jsonArray.length();i++){
                listdata.add(jsonArray.get(i).toString());
            }
        }
        return listdata;
    }
}
