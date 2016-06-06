package com.radioserver.bristolbeat.screens;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.helpers.SharedAlgorithm;
import com.radioserver.bristolbeat.models.RadioSong;
import com.radioserver.bristolbeat.services.RadioPlayerService;
import com.radioserver.bristolbeat.helpers.CommonUtils;

import java.util.List;

public class RecentPlaylistActivity extends Activity {

    ListView mListSong;
    RecentListAdapter mRecentListAdapter;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RadioPlayerService.ACTION_SONG_LIST_READY.equalsIgnoreCase(intent.getAction())) {
                updateSongList();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_recent_songs);

        SharedAlgorithm.setupAdView((WebView) findViewById(R.id.adView), this);
        mListSong = (ListView) findViewById(R.id.list_recent_song);
    }

    private void updateSongList() {
        List<RadioSong> data = RadioPlayerService.getInstance().getRecentPlayList();
        if (data == null || data.size() == 0) return;
        if (mRecentListAdapter != null) {
            mRecentListAdapter.setData(data);
            mRecentListAdapter.notifyDataSetChanged();
        } else {
            findViewById(R.id.progress_loading).setVisibility(View.GONE);
            mRecentListAdapter = new RecentListAdapter(data);
            mListSong.setAdapter(mRecentListAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSongList();
        registerReceiver(mReceiver, new IntentFilter(RadioPlayerService.ACTION_SONG_LIST_READY));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private class RecentListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<RadioSong> mData;

        public RecentListAdapter(List<RadioSong> data) {
            mData = data;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<RadioSong> newData) {
            mData = newData;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_song_item, parent, false);
            }
            RadioSong item = mData.get(position);
            ((TextView) convertView.findViewById(R.id.song_title)).setText(item.getTitle());
            ((TextView) convertView.findViewById(R.id.song_description)).setText(item.getDescription());
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.song_thumbnail);
            String url = item.getThumbnailUrl();
            int logoId = R.drawable.img_logo;
            if (url != null && url.contains("http")) {
                CommonUtils.loadImage(thumbnail, url, logoId);
            } else {
                thumbnail.setImageResource(logoId);
            }
            return convertView;
        }
    }
}
