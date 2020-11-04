package com.example.assignment_2.Maps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

    private String username;
    private DatabaseReference databaseReference;
    private LocationCallback mLocationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        //Toast.makeText(this,"create",Toast.LENGTH_SHORT).show();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        databaseReference = FirebaseDatabase.getInstance().getReference("coordinates");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Toast.makeText(this,"testing",Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    private void getLocation() {
//        Log.e(TAG,"Get location.....");
        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
//        Log.d(TAG, "getLocation: getting location information.");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location location = locationResult.getLastLocation();

                if (location != null) {
                    LocationHelper helper = new LocationHelper(location.getLongitude(), location.getLatitude());

                    String msg = "update Location: " +
                            Double.toString(location.getLatitude()) + "," +
                            Double.toString(location.getLongitude());

                    Log.d(TAG, msg);

                    SharedPreferences sharedPreferences
                            = getSharedPreferences("Preferences", MODE_PRIVATE);
                    username = sharedPreferences.getString("username", "");
                    if(username!= null){
                        databaseReference.child(username).setValue(helper)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG, "Location Saved");
                                            Log.d(TAG, "username:"+username);
                                        }else {
                                            Log.d(TAG, "not Location data");
                                        }
                                    }
                                });
                    }
                    else {
                        mFusedLocationClient.removeLocationUpdates(this);
                        stopSelf();
                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, mLocationCallback,
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    @Override
    public void onDestroy() {
        if(mFusedLocationClient!=null && mLocationCallback!=null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();
        super.onDestroy();
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, Restarter.class);
//        this.sendBroadcast(broadcastIntent);
    }


//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("this","test");
//        return super.onStartCommand(intent, flags, startId);
//    }
}
