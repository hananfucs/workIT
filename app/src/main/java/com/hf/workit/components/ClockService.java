package com.hf.workit.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;

import com.hf.workit.R;
import com.hf.workit.activities.ExecuteExercise;


/**
 * Created by hanan on 29/06/16.
 */
public class ClockService extends Service {

    private boolean threadRun = true;
    private int notificationTime;
    private int mCurrentPhase;
    private String mTitle;
    private static final int notificationID = 232;
    private String mPlanName;
    private int mExercise;
    private float mPreCountDown = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        threadRun = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        int length = intent.getIntExtra("timer_seconds", 0);
        int target = intent.getIntExtra("target", 1);
        mPlanName = intent.getStringExtra(IPlan.PLAN_NAME);
        mExercise = intent.getIntExtra(IExercise.EXERCISE_ID, 0);
        mCurrentPhase = target;
        mTitle = intent.getStringExtra("title");
        mPreCountDown = intent.getIntExtra("pre_countdown", 0);
        startTimerThread(length, target);

        return START_STICKY;
        }

    private void startTimerThread(final int time, final int target) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPreCountDown != 0) {
                    while (mPreCountDown > 0) {
                        if (!threadRun)
                            return;
                        Intent intent = new Intent("timer_update");
                        intent.putExtra("time_value", mPreCountDown);
                        intent.putExtra("target", target);
                        intent.putExtra("change_color", Color.YELLOW);
                        sendBroadcast(intent);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mPreCountDown--;
                    }
                    if (!threadRun)
                        return;
                    Intent intent = new Intent("timer_update");
                    intent.putExtra("time_value", 0);
                    intent.putExtra("target", target);
                    intent.putExtra("change_color", Color.RED);
                    sendBroadcast(intent);
                }



                float i = time;
                while (i > 0) {
                    if (!threadRun)
                        return;
                    Intent intent = new Intent("timer_update");
                    intent.putExtra("time_value", i);
                    intent.putExtra("target", target);
                    sendBroadcast(intent);
                    updateNotification(i);
                    i = (float) (i - 0.1);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }

                }
                Intent intent = new Intent("timer_update");
                intent.putExtra("time_value", 0);
                intent.putExtra("target", target);
                sendBroadcast(intent);
            }
        });
        thread.start();
    }


    private void updateNotification(float dTime) {
        if ((int)dTime == notificationTime)
            return;
        notificationTime = (int)dTime;
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(this);
        String notificationTitle = null;
        switch (mCurrentPhase){
            case ExecuteExercise.PHASE_EX_1:
            case ExecuteExercise.PHASE_EX_2:
                notificationTitle = getResources().getString(R.string.time_left) + mTitle;
                break;
            case ExecuteExercise.PHASE_BREAK:
                notificationTitle = getResources().getString(R.string.time_left_break);
                break;
        }

        Intent notificationIntent = new Intent(this, ExecuteExercise.class);
        notificationIntent.putExtra(IPlan.PLAN_NAME, mPlanName);
        notificationIntent.putExtra(IExercise.EXERCISE_ID, mExercise);
        notificationIntent.putExtra("last_phase", mCurrentPhase);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        notification.setContentTitle(notificationTitle)
                .setContentText(String.valueOf(notificationTime))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo4))
                .setSmallIcon(R.drawable.ic_fitness_center_white_48dp);
        nm.notify(notificationID, notification.build());
    }
}
