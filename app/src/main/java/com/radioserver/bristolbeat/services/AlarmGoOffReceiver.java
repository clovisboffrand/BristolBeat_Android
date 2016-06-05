package com.radioserver.bristolbeat.services;

import com.radioserver.bristolbeat.helpers.AlarmHelper;
import com.radioserver.bristolbeat.screens.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmGoOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {
        Intent notificationIntent = new Intent(context, Main.class);
        notificationIntent.putExtras(data);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notificationIntent);

        setRepeat(context, data);
    }

    private void setRepeat(Context context, Intent data) {
        int hour = data.getIntExtra("HOUR", 0);
        int min = data.getIntExtra("MIN", 0);
        long mili = data.getLongExtra("MILI", 0);
        mili += 24 * 60 * 60 * 1000;

        new AlarmHelper(context).setAlarm(hour, min, mili);
    }
}