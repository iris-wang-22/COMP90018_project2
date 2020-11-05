package com.example.assignment_2.basepedo.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.assignment_2.basepedo.base.StepMode;
import com.example.assignment_2.basepedo.callback.StepCallBack;


public class StepInPedometer extends StepMode {
    private final String TAG = "StepInPedometer";
    private int lastStep = -1;
    private int liveStep = 0;
    private int increment = 0;
    //0-TYPE_STEP_DETECTOR 1-TYPE_STEP_COUNTER
    private int sensorMode = 0;


    public StepInPedometer(Context context, StepCallBack stepCallBack) {
        super(context, stepCallBack);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void registerSensor() {
        addCountStepListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        liveStep = (int) event.values[0];
        if(sensorMode == 0){
            StepMode.CURRENT_SETP += liveStep;
        }else if(sensorMode == 1){
            StepMode.CURRENT_SETP = liveStep;
        }
        stepCallBack.Step(StepMode.CURRENT_SETP);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addCountStepListener() {
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (detectorSensor != null) {
            sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
            isAvailable = true;
            sensorMode = 0;
        } else if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            isAvailable = true;
            sensorMode = 1;
        } else {
            isAvailable = false;
            Log.v(TAG, "Count sensor not available!");
        }
    }
}
