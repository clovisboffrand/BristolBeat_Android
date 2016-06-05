package com.radioserver.bristolbeat.helpers;

import com.radioserver.bristolbeat.services.AlarmGoOffReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmHelper {

    AlarmManager alarm;
    Context context;

    public AlarmHelper(Context context) {
        this.context = context;
        this.alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(int hour, int min, long miliseconds) {
        Intent intent = new Intent(context, AlarmGoOffReceiver.class);
        intent.putExtra("HOUR", hour);
        intent.putExtra("MIN", min);
        intent.putExtra("MILI", miliseconds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarm.set(AlarmManager.RTC_WAKEUP, miliseconds, pendingIntent);
    }

    public void unsetAlarm() {
        Intent intent = new Intent(context, AlarmGoOffReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarm.cancel(pendingIntent);
    }
}
