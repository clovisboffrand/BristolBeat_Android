package com.radioserver.bristolbeat.services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.radioserver.bristolbeat.models.RadioSong;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class GetRecentPlaylist extends AsyncTask<Void, Void, List<RadioSong>> {

    List<RadioSong> mListSong = new ArrayList<>();

    public static final String ACTION_GET_LIST_RADIO_SONG_COMPLETE = "kxoj.com.getplaylist.webservice.GET_SONG_LIST_COMPLETE";

    private Context mContext;
    private String mApiUrl;

    public GetRecentPlaylist(Context context, String apiUrl) {
        mContext = context.getApplicationContext();
        mApiUrl = apiUrl;
    }

    @Override
    protected List<RadioSong> doInBackground(Void... params) {
        fecthXML(mApiUrl);
        return mListSong;
    }

    @Override
    protected void onPostExecute(List<RadioSong> radioSongs) {
        super.onPostExecute(radioSongs);
        if (mContext != null)
            mContext.sendBroadcast(new Intent(ACTION_GET_LIST_RADIO_SONG_COMPLETE));
    }

    private InputStream getDataStream(String url) {
        try {
            URL webURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) webURL.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            InputStream in = conn.getInputStream();
            if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) in = new GZIPInputStream(in);
            return in;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fecthXML(String apiURL) {
        try {
            InputStream inputXmlStream = getDataStream(apiURL);
            if (inputXmlStream != null) {
                XmlPullParserFactory xmlPullObject = XmlPullParserFactory.newInstance();
                XmlPullParser xmlParser = xmlPullObject.newPullParser();
                xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlParser.setInput(inputXmlStream, null);
                parseXml(xmlParser);
                inputXmlStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXml(XmlPullParser xmlPullParser) {
        mListSong.clear();
        int event;
        String name;
        try {
            event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                name = xmlPullParser.getName();
                if (event == XmlPullParser.START_TAG && "item".equalsIgnoreCase(name)) {
                    RadioSong radioSong = readSong(xmlPullParser);
                    if (radioSong != null) mListSong.add(radioSong);
                }
                event = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RadioSong readSong(XmlPullParser xmlParser) {
        String title = null, guide = null, description = null, thumbnailUrl = null;
        int event;
        String currentName, text = null;
        try {

            loop:
            while ((event = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
                currentName = xmlParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("media:thumbnail".equalsIgnoreCase(currentName)) {
                            thumbnailUrl = xmlParser.getAttributeValue(0);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = xmlParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (currentName.equalsIgnoreCase("item")) break loop;
                        if ("title".equalsIgnoreCase(currentName))
                            title = text;
                        if ("guid".equalsIgnoreCase(currentName))
                            guide = text;
                        if ("description".equalsIgnoreCase(currentName))
                            description = text;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (title != null && title.length() > 0)
            return (new RadioSong(title, guide, description, thumbnailUrl));
        else
            return null;
    }
}
