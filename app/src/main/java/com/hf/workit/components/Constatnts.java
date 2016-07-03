package com.hf.workit.components;

/**
 * Created by hanan on 31/03/16.
 */
public class Constatnts {

    public static final String PLANS_FILE_NAME = "data.dat";
    public static final String AC_FILE_NAME = "ac.dat";
    public static final String CARDIO_FILE_NAME = "cardio.dat";
    public static final String AC_SEPARATOR = ">>><<<";
    public static final String AC_TRAINER = "trainer";
    public static final String AC_CLUB = "club";
    public static final String AC_MUSCLE = "muscle";
    public static final String AC_NAME = "name";
    public static final String AC_DESCRIPTION = "desc";
    public static final long MINUTE_MILLIS = 1000 * 60;
    public static final long DAY_MILLIS = MINUTE_MILLIS * 60 * 24;
    public static final long MONTH_MILLIS = DAY_MILLIS * 30;


    public static class ExerciseJson {
        public static final String TYPE="type";
        public static final String ID="id";
        public static final String MUSCLE = "muscle";
        public static final String NUM_OF_SETS = "num_of_sets";
        public static final String BREAK = "break";

        public static final String NAME = "name";
        public static final String METHOD = "method";
        public static final String METHOD_DESCRIPTION = "method_description";
        public static final String WEIGHT = "weight";
        public static final String REPEATS = "repeats";
        public static final String IS_REPEATS = "is_repeats";

        public static final String NAME2 = "name2";
        public static final String METHOD2 = "method2";
        public static final String METHOD_DESCRIPTION2 = "method_description2";
        public static final String WEIGHT2 = "weight2";
        public static final String IS_REPEATS2 = "is_repeats2";
        public static final String REPEATS2 = "repeats2";
    }

    public static class PlanJson {
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String TRAINER = "trainer";
        public static final String CLUB = "club";
        public static final String TIMES_PER_WEEK = "times_per_week";
        public static final String EXERCISES = "exercises";
        public static final String CREATION_TIME = "creation_time";
        public static final String EXPIRATION_TIME = "expiration_time";
    }

}
