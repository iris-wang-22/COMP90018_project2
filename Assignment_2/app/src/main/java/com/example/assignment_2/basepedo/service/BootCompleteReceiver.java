package com.example.assignment_2.basepedo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StepService.class);
        context.startService(i);
    }
}
