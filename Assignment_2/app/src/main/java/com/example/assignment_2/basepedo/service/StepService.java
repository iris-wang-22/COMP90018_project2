package com.example.assignment_2.basepedo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.assignment_2.Login.MainActivity;
import com.example.assignment_2.R;
import com.example.assignment_2.basepedo.base.StepMode;
import com.example.assignment_2.basepedo.callback.StepCallBack;
import com.example.assignment_2.basepedo.config.Constant;
import com.example.assignment_2.basepedo.poj.StepData;
import com.example.assignment_2.basepedo.utils.CountDownTimer;
import com.example.assignment_2.basepedo.utils.DbUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class StepService extends Service implements /*SensorEventListener,*/ StepCallBack {
    private static final long SCREEN_OFF_RECEIVER_DELAY = 500l;
    private final String TAG = "StepService";
    private String DB_NAME = "Pedometer";

    private static int duration = 30000;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;
    private Messenger messenger = new Messenger(new MessenerHandler());
    private BroadcastReceiver mBatInfoReceiver;
    private WakeLock mWakeLock;
    private TimeCount time;
    private DatabaseReference databaseRef;

    private String CURRENTDATE = "";

    private static class MessenerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_CLIENT:
                    try {
                        Messenger messenger = msg.replyTo;
                        Message replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER);
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", StepMode.CURRENT_SETP);
                        replyMsg.setData(bundle);
                        messenger.send(replyMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        Log.v(TAG, "onCreate");
        initBroadcastReceiver();
        startStep();
        startTimeCount();
    }

    private void initBroadcastReceiver() {
        Log.v(TAG, "initBroadcastReceiver");
        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_SCREEN_OFF);

        filter.addAction(Intent.ACTION_DATE_CHANGED);

        filter.addAction(Intent.ACTION_SHUTDOWN);

        filter.addAction(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_USER_PRESENT);

        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    Log.v(TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    Log.v(TAG, "screen off");

                    duration = 60000;

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startStep();
                        }
                    };
                    new Handler().postDelayed(runnable,SCREEN_OFF_RECEIVER_DELAY);
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Log.v(TAG, "screen unlock");
                    save();

                    duration = 30000;
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.v(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");

                    save();
                } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_SHUTDOWN");
                    save();
                } else if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
                    Log.v(TAG, " receive ACTION_DATE_CHANGED");
                    initTodayData();
                    clearStepData();
                    Log.v(TAG, "Clear data："+StepMode.CURRENT_SETP);
                    Step(StepMode.CURRENT_SETP);
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    private void startStep() {
        StepMode mode = new StepInPedometer(this, this);
        boolean isAvailable = mode.getStep();
        if (!isAvailable) {
            mode = new StepInAcceleration(this, this);
            isAvailable = mode.getStep();
            if (isAvailable) {
                Log.v(TAG, "acceleration can execute!");
            }
        }
    }

    private void startTimeCount() {
        time = new TimeCount(duration, 1000);
        time.start();
    }

    @Override
    public void Step(int stepNum) {
        StepMode.CURRENT_SETP = stepNum;
        Log.v(TAG, "Step:" + stepNum);
        updateNotification("Today Step：" + stepNum + " step");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initTodayData();
        updateNotification("Today Step：" + StepMode.CURRENT_SETP + " step");
        return START_STICKY;
    }

    private void initTodayData() {
        CURRENTDATE = getTodayDate();
        DbUtils.createDb(this, DB_NAME);

        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepMode.CURRENT_SETP = 0;
        } else if (list.size() == 1) {
            StepMode.CURRENT_SETP = Integer.parseInt(list.get(0).getStep());
        } else {
            Log.v(TAG, "It's wrong！");
        }
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }



    private void updateNotification(String content) {
        builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher_01);
        builder.setTicker("Pedometer");
        builder.setContentTitle("Pedometer");
        builder.setOngoing(true);
        builder.setContentText(content);
        Notification notification = builder.build();

        startForeground(0, notification);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(R.string.app_name, notification);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            time.cancel();
            save();
            startTimeCount();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    private void save() {
        int tempStep = StepMode.CURRENT_SETP;
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENTDATE});
        if (list.size() == 0 || list.isEmpty()) {
            StepData data = new StepData();
            data.setToday(CURRENTDATE);
            data.setStep(tempStep + "");
            DbUtils.insert(data);
        } else if (list.size() == 1) {
            StepData data = list.get(0);
            data.setStep(tempStep + "");
            databaseRef.child("current_steps").child(username).setValue(tempStep);
            DbUtils.update(data);
        } else {
        }
    }

    private void clearStepData() {
        StepMode.CURRENT_SETP = 0;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        DbUtils.closeDb();
        unregisterReceiver(mBatInfoReceiver);
        Intent intent = new Intent(this, StepService.class);
        startService(intent);
        super.onDestroy();
    }


    synchronized private WakeLock getLock(Context context) {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
        }
        return (mWakeLock);
    }
}
