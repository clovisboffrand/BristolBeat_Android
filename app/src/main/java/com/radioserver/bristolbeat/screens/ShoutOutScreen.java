/**
 * Project    : iRadio
 * Author     : Hoang San
 **/
package com.radioserver.bristolbeat.screens;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.helpers.HttpFileUploader;
import com.radioserver.bristolbeat.helpers.RecordingHelper;

import org.san.iphonestyle.CustomScreen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ShoutOutScreen extends CustomScreen implements OnClickListener {

    ProgressBar prgLoading;
    Button btnListen, btnRecord, btnPlay, btnSend;

    RecordingHelper mRecordHelper;

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
        View view = inflater.inflate(R.layout.screen_shoutout, container, false);

        this.initComponents(view);
        this.setListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        mRecordHelper = new RecordingHelper();
    }

    @Override
    protected void initComponents(View container) {
        prgLoading = (ProgressBar) container.findViewById(R.id.prgLoading);
        btnListen = (Button) container.findViewById(R.id.btnListen);
        btnRecord = (Button) container.findViewById(R.id.btnRecord);
        btnPlay = (Button) container.findViewById(R.id.btnPlay);
        btnSend = (Button) container.findViewById(R.id.btnSend);
    }

    @Override
    protected void setListeners() {
        btnListen.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnListen) {
            startListen();
        } else if (v.getId() == R.id.btnRecord) {
            int isChecked = Integer.parseInt(v.getTag().toString());
            if (isChecked == 0) {
                v.setTag(1);
                v.setBackgroundResource(R.drawable.ic_btn_stop_record);
                startRecord();
            } else {
                v.setTag(0);
                v.setBackgroundResource(R.drawable.ic_btn_record);
                stopRecord();
            }
        } else if (v.getId() == R.id.btnPlay) {
            startPlay();
        } else if (v.getId() == R.id.btnSend) {
            new UploadTask().execute();
        }
    }

    private void startRecord() {
        mRecordHelper.startRecording();
    }

    private void stopRecord() {
        mRecordHelper.stopRecording();
    }

    private void startPlay() {
        new PlayTask().execute(mRecordHelper.getFilename());
    }

    private void startListen() {
        new PlayTask().execute(getResources().getString(R.string.sample_audio));
    }

    private MediaPlayer mp;

    private class PlayTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prgLoading.setVisibility(View.VISIBLE);

            mp = new MediaPlayer();
            mp.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    prgLoading.setVisibility(View.GONE);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                mp.setDataSource(params[0]);
                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class UploadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prgLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                HttpFileUploader uploader = new HttpFileUploader(getResources().getString(R.string.upload_audio));

                File file = new File(mRecordHelper.getFilename());
                InputStream inputStream = new FileInputStream(file);
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                inputStream.close();
                byte[] dataBytes = byteBuffer.toByteArray();

                uploader.addData("file", dataBytes, "upload.wav");

                return uploader.doUpload();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            prgLoading.setVisibility(View.GONE);

            Log.e("ShoutOutScreen", "uploading result: " + result);
            Toast.makeText(getActivity(), R.string.Thanks_for_sharing, Toast.LENGTH_SHORT).show();
        }
    }
}