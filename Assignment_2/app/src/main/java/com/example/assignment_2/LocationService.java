package com.example.assignment_2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.Provider;

public class LocationService extends Service {
    private static final String TAG = "1234";
    private String update = "update";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        Toast.makeText(this,"create",Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        Toast.makeText(this,"startcommand",Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }


//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("this","test");
//        return super.onStartCommand(intent, flags, startId);
//    }
}
