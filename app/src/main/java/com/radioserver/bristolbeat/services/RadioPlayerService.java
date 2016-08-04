package com.radioserver.bristolbeat.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.models.RadioSong;
import com.radioserver.bristolbeat.screens.Main;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

import java.util.List;

public class RadioPlayerService {

    private static final String LOG_TAG = "RadioPlayerService";
    public static final String ACTION_PLAYER_STATE_CHANGE = "com.radioserver.bristolbeat.RadioPlayerService.PLAYER_STATE_CHANGE";
    public static final String ACTION_SONG_LIST_READY = "com.radioserver.bristolbeat.RadioPlayerService.SONG_LIST_READY";
    public static final String ACTION_PLAY = "com.radioserver.bristolbeat.RadioPlayerService.ACTION_PLAY";
    public static final String ACTION_STOP = "com.radioserver.bristolbeat.RadioPlayerService.ACTION_STOP";

    private Context mContext;
    private MultiPlayer mPlayer;
    private String mRadioLink;
    private boolean mIsPlaying = false;
    private boolean mIsPlayNewStream = false;

    private Notification mNotification;
    private RemoteViews mNotificationView;

    private static final long TIME_TO_UPDATE_FROM_UI = 20000L;
    private static final long TIME_INTERVAL_TO_UPDATE_BACKGROUND = 40000L;
    private GetRecentPlaylist mGetRecentPlaylistTask;
    private List<RadioSong> mRecentPlayList;
    private boolean mIsGetRecentPlayList = false;
    private boolean mIsStopNotification = false;
    private long mLastTimeUpdate = 0;
    private Handler mHandler;

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
            if (GetRecentPlaylist.ACTION_GET_LIST_RADIO_SONG_COMPLETE.equalsIgnoreCase(intent.getAction())) {
                try {
                    List<RadioSong> data = mGetRecentPlaylistTask.get();
                    if (data != null && data.size() > 0) {
                        mRecentPlayList = data;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mIsGetRecentPlayList = false;
                mContext.sendBroadcast(new Intent(ACTION_SONG_LIST_READY));
                //Do not need update playlist in background when notification is stopped.
                if (!mIsStopNotification)
                    mHandler.postDelayed(mUpdatePlayListTask, TIME_INTERVAL_TO_UPDATE_BACKGROUND);
            }
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
                mIsStopNotification = true;
            }
        }
    };

    private PlayerCallback mPlayerCallback = new PlayerCallback() {
        @Override
        public void playerStarted() {
            mIsStopNotification = false;
            mIsPlaying = true;

            mContext.startService(new Intent(mContext, NotificationService.class));
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

        @Override
        public void playerAudioTrackCreated(AudioTrack audioTrack) {
            System.out.println("1234");
            bluetoothNotifyChange();
//            RadioSong currentSong = RadioPlayerService.getInstance().getCurrentSong();
//            System.out.println("anshul222"+currentSong.getTitle());
//            if (currentSong != null) {
//                System.out.println("anshul"+currentSong.getTitle());
//                Intent avrcp = new Intent("com.android.music.metachanged");
//                avrcp.putExtra("track", "L lag gye");
//                //avrcp.putExtra("artist", currentSong.getTitle());
//               // avrcp.putExtra("album", currentSong.getDescription());
//                mContext.sendBroadcast(avrcp);
//                //tvArtist.setText(currentSong.getDescription());
//                //CommonUtils.loadImage(ivSongLogo, currentSong.getThumbnailUrl(), R.drawable.img_logo_full);
//            }
//
        }
    };

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void initialService(Context context) {
        mContext = context.getApplicationContext();

        mPlayer = new MultiPlayer(mPlayerCallback);
        mRadioLink = mContext.getString(R.string.STREAM_URL);

        mHandler = new Handler();
        mContext.registerReceiver(mReceiver, makeIntentFilter());
        play();
    }

    private IntentFilter makeIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GetRecentPlaylist.ACTION_GET_LIST_RADIO_SONG_COMPLETE);
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
        mHandler.removeCallbacks(mUpdatePlayListTask);
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

    public List<RadioSong> getRecentPlayList() {
        return mRecentPlayList;
    }

    public RadioSong getCurrentSong() {
        if (mRecentPlayList != null && mRecentPlayList.size() > 0) return mRecentPlayList.get(0);
        else return null;
    }

    Runnable mUpdatePlayListTask = new Runnable() {
        @Override
        public void run() {
            executeGetRecentSongs();
        }
    };

    public synchronized void executeGetRecentSongs() {
        long timeUpdateInteval = System.currentTimeMillis() - mLastTimeUpdate;
        if (!mIsGetRecentPlayList && timeUpdateInteval > TIME_TO_UPDATE_FROM_UI) {
            mLastTimeUpdate = System.currentTimeMillis();
            String api = mContext.getString(R.string.feed_url);
            mGetRecentPlaylistTask = new GetRecentPlaylist(mContext, api);
            mGetRecentPlaylistTask.execute();
            mIsGetRecentPlayList = true;
        }
    }

    private void bluetoothNotifyChange() {
        RadioSong currentSong = RadioPlayerService.getInstance().getCurrentSong();
        Intent i = new Intent("com.android.music.playstatechanged");
        i.putExtra("track", currentSong.getTitle());
        System.out.println("12345" + currentSong.getTitle());
        mContext.sendBroadcast(i);
    }

}
