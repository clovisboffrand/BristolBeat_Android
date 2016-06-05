package com.radioserver.bristolbeat.screens;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.san.iphonestyle.CustomScreen;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.helpers.AlarmHelper;
import com.radioserver.bristolbeat.helpers.UserProfileSingleton;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class AlarmScreen extends CustomScreen implements OnClickListener {

    private Button btnSet;
    private TextView tvCurH1, tvCurH2, tvCurM1, tvCurM2;
    private TimePicker timePicker;

    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_alarm, container, false);

        this.initComponents(view);
        this.setListeners();

        return view;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initComponents(View container) {

        btnSet = (Button) container.findViewById(R.id.btnSet);
        tvCurH1 = (TextView) container.findViewById(R.id.tvCurH1);
        tvCurH2 = (TextView) container.findViewById(R.id.tvCurH2);
        tvCurM1 = (TextView) container.findViewById(R.id.tvCurM1);
        tvCurM2 = (TextView) container.findViewById(R.id.tvCurM2);
        timePicker = (TimePicker) container.findViewById(R.id.timePicker);

        if (UserProfileSingleton.getConfig(getActivity()).isAutomatic()) {
            timePicker.setCurrentHour(UserProfileSingleton.getConfig(getActivity()).getHour());
            timePicker.setCurrentMinute(UserProfileSingleton.getConfig(getActivity()).getMin());

            btnSet.setTag(1);
            btnSet.setText(getResources().getString(R.string.Cancel_Alarm));
            timePicker.setEnabled(false);
        } else {
            Calendar c = Calendar.getInstance();
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        }
    }

    @Override
    public void onResume() {
        if (isHidden()) {
            return;
        }

        timer = new Timer();
        timer.schedule(new MyTask(), 0, 60000);
        super.onResume();
    }

    @Override
    public void onStop() {
        if (isHidden()) {
            return;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        super.onStop();
    }

    @Override
    protected void setListeners() {
        btnSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSet) {
            if (Integer.parseInt(btnSet.getTag().toString()) == 0) {
                btnSet.setTag(1);
                btnSet.setText(getResources().getString(R.string.Cancel_Alarm));
                timePicker.setEnabled(false);

                Calendar c = Calendar.getInstance();

                long currentTime = c.getTimeInMillis();

                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, min);

                long setTime = c.getTimeInMillis();
                if (setTime < currentTime) setTime += 24 * 60 * 60 * 1000;

                AlarmHelper alarm = new AlarmHelper(getActivity());
                alarm.setAlarm(hour, min, setTime);

                UserProfileSingleton.getConfig(getActivity()).setHour(hour);
                UserProfileSingleton.getConfig(getActivity()).setMin(min);
                UserProfileSingleton.getConfig(getActivity()).setAutomatic(true);
            } else {
                btnSet.setTag(0);
                btnSet.setText(getResources().getString(R.string.Set_Alarm));
                timePicker.setEnabled(true);

                AlarmHelper alarm = new AlarmHelper(getActivity());
                alarm.unsetAlarm();

                UserProfileSingleton.getConfig(getActivity()).setAutomatic(false);
            }
        }
    }

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
        }
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);

                int h1 = hour % 10;
                int h2 = hour / 10;
                int m1 = min % 10;
                int m2 = min / 10;

                tvCurH1.setText(String.valueOf(h2));
                tvCurH2.setText(String.valueOf(h1));
                tvCurM1.setText(String.valueOf(m2));
                tvCurM2.setText(String.valueOf(m1));
            }

            super.handleMessage(msg);
        }
    };
}