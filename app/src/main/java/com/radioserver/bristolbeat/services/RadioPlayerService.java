package com.radioserver.bristolbeat.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.screens.Main;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

public class RadioPlayerService {

    private static final String LOG_TAG = "RadioPlayerService";
    public static final String ACTION_PLAYER_STATE_CHANGE = "com.radioserver.wqme.RadioPlayerService.PLAYER_STATE_CHANGE";
    public static final String ACTION_PLAY = "com.radioserver.wqme.RadioPlayerService.ACTION_PLAY";
    public static final String ACTION_STOP = "com.radioserver.wqme.RadioPlayerService.ACTION_STOP";

    private Context mContext;
    private MultiPlayer mPlayer;
    private String mRadioLink;
    private boolean mIsPlaying = false;
    private boolean mIsPlayNewStream = false;

    private Notification mNotification;
    private RemoteViews mNotificationView;

    private static RadioPlayerService mInstance;

    private RadioPlayerService() {
    }

    public static RadioPlayerService getInstance() {
        if (mInstance == null) {
            mInstance = new RadioPlayerService();
        }
        return mInstance;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_PLAY.equalsIgnoreCase(intent.getAction())) {
                if (mIsPlaying) {
                    stop();
                    changeNotificationPlayStatus(false);
                } else {
                    play();
                    changeNotificationPlayStatus(true);
                }
            } else if (ACTION_STOP.equalsIgnoreCase(intent.getAction())) {
                stop();
                mContext.stopService(new Intent(mContext, NotificationService.class));
            }
        }
    };

    private PlayerCallback mPlayerCallback = new PlayerCallback() {
        @Override
        public void playerStarted() {
            mContext.startService(new Intent(mContext, NotificationService.class));
            mIsPlaying = true;
            notifyPlayerStateChange();
        }

        @Override
        public void playerPCMFeedBuffer(boolean b, int i, int i1) {

        }

        @Override
        public void playerStopped(int i) {
            mIsPlaying = false;
            if (mIsPlayNewStream) {
                play();
                mIsPlayNewStream = false;
            } else {
                notifyPlayerStateChange();
            }
        }

        @Override
        public void playerException(Throwable throwable) {

        }

        @Override
        public void playerMetadata(String s, String s1) {

        }
    };

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void initialService(Context context) {
        mContext = context.getApplicationContext();
        mPlayer = new MultiPlayer(mPlayerCallback);
        mRadioLink = mContext.getString(R.string.STREAM_URL);
        mContext.registerReceiver(mReceiver, makeIntentFilter());
        play();
    }

    private IntentFilter makeIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_STOP);
        return intentFilter;
    }

    private void notifyPlayerStateChange() {
        if (mContext != null) {
            Intent i = new Intent(ACTION_PLAYER_STATE_CHANGE);
            mContext.sendBroadcast(i);
        }
    }

    public void play() {
        Log.d(LOG_TAG, "Play stream: " + mRadioLink);
        if (!mIsPlaying) {
            Log.d(LOG_TAG, "Start play");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPlayer.playAsync(mRadioLink);
                }
            }).start();
        }
    }

    public void stop() {
        mPlayer.stop();
    }

    public void destroy() {
        mPlayer.stop();
        mContext.stopService(new Intent(mContext, NotificationService.class));
    }

    public Notification createNotification() {
        mNotification = new Notification(R.mipmap.ic_notification, null, System.currentTimeMillis());
        mNotificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_layout);
        setOriginView();
        Intent playIntent = new Intent(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(mContext, 100, playIntent, 0);
        mNotificationView.setOnClickPendingIntent(R.id.noti_play_action, pendingPlayIntent);

        Intent stopIntent = new Intent(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getBroadcast(mContext, 100, stopIntent, 0);
        mNotificationView.setOnClickPendingIntent(R.id.noti_stop_action, pendingStopIntent);

        mNotification.contentView = mNotificationView;

        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(mContext, Main.class);
        mNotification.contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        return mNotification;
    }

    public void changeNotificationPlayStatus(boolean isPlaying) {
        if (mNotification != null && mNotificationView != null) {
            if (!isPlaying) {
                mNotificationView.setImageViewResource(R.id.noti_play_view, R.mipmap.ic_btn_play);
            } else {
                mNotificationView.setImageViewResource(R.id.noti_play_view, R.mipmap.ic_btn_pause);
            }
            mNotification.contentView = mNotificationView;
            mContext.startService(new Intent(mContext, NotificationService.class));
        }
    }

    private void setOriginView() {
        if (mNotificationView != null) {
            mNotificationView.setTextViewText(R.id.noti_title, mContext.getString(R.string.app_name));
            mNotificationView.setTextViewText(R.id.noti_sub_text, "");
            if (!mIsPlaying) {
                mNotificationView.setImageViewResource(R.id.noti_play_view, R.mipmap.ic_btn_play);
            } else {
                mNotificationView.setImageViewResource(R.id.noti_play_view, R.mipmap.ic_btn_pause);
            }
        }
    }

    public Notification getNotification() {
        if (mNotification == null)
            return createNotification();
        else return mNotification;
    }
}
