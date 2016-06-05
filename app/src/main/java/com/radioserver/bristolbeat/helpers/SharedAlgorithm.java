package com.radioserver.bristolbeat.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SharedAlgorithm {

    private static final String adResource = "file:///android_asset/ads.html";
//    private static final String adContentUrl = "http://lakercountry.com";

    public static void setupAdView(WebView adview, final Context currentContext) {
        adview.setBackgroundColor(0x00000000);
        adview.getSettings().setBuiltInZoomControls(false);
        WebSettings webSettingsAds = adview.getSettings();
        webSettingsAds.setJavaScriptEnabled(true);
        adview.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.contains("__oadest")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    currentContext.getApplicationContext().startActivity(browserIntent);
                } else {
                    super.onLoadResource(view, adResource);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(adResource);
                return true;
            }
        });
        adview.loadUrl(adResource);
//        adview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(adContentUrl));
//                    currentContext.startActivity(i);
//                }
//                return true;
//            }
//        });
    }
}
