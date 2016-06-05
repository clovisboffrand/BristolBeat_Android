/**
 * HTTP File Uploader
 * <p>
 * Created by Vo Hoang San - hoangsan.762@gmail.com
 * Copyright (c) 2011 San Vo. All rights reserved.
 */

package com.radioserver.bristolbeat.helpers;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HttpFileUploader {
    private URL mConnectURL;
    private ArrayList<Data> mData;

    public HttpFileUploader(String url) {
        try {
            mConnectURL = new URL(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.mData = new ArrayList<>();
    }

    public void addData(Data item) {
        mData.add(item);
    }

    public void addData(String name, Object value) {
        Data temp = new Data(name, value, null);
        mData.add(temp);
    }

    public void addData(String name, Object value, String filename) {
        Data temp = new Data(name, value, filename);
        mData.add(temp);
    }

    @SuppressWarnings("deprecation")
    public String doUpload() {

        HttpURLConnection conn;
        DataOutputStream dos;
        DataInputStream inStream;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int maxBufferSize = 1024 * 1024;

        try {
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) mConnectURL.openConnection();
            conn.setReadTimeout(UserProfileSingleton.TIMEOUT);
            conn.setUseCaches(true);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //
            dos = new DataOutputStream(conn.getOutputStream());

            for (Data item : mData) {
                if (item.getValue() instanceof byte[]) {
                    byte[] uploadData = (byte[]) item.getValue();
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + item.getName() + "\";filename=\"" + item.getFilename() + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    int totalSize = uploadData.length;
                    int part = totalSize / maxBufferSize;
                    int odd = totalSize % maxBufferSize;

                    for (int i = 0; i < part; i++) {
                        dos.write(uploadData, i * maxBufferSize, maxBufferSize);
                        dos.flush();
                    }

                    if (odd > 0) {
                        dos.write(uploadData, part * maxBufferSize, odd);
                        dos.flush();
                    }


                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                } else {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + item.getName() + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(String.valueOf(item.getValue()));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }
            }

            mData.clear();
            // close streams
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            return ex.getMessage();
        } catch (IOException ioe) {
            return ioe.getMessage();
        }
        try {
            inStream = new DataInputStream(conn.getInputStream());
            StringBuilder b = new StringBuilder();
            String line;
            while ((line = inStream.readLine()) != null) {
                b.append(line);
            }
            inStream.close();
            Log.d("SAN", "Response: " + b.toString());
            return b.toString();
        } catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    //-----------------
    public class Data {
        private String mName;
        private Object mValue;
        private String mFilename;

        public Data(String name, Object value, String filename) {
            mName = name;
            mValue = value;
            mFilename = filename;
        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        public Object getValue() {
            return mValue;
        }

        public void setValue(Object mValue) {
            this.mValue = mValue;
        }

        public String getFilename() {
            return mFilename;
        }

        public void setFilename(String mFilename) {
            this.mFilename = mFilename;
        }
    }
}