package com.radioserver.bristolbeat.screens;

import android.os.Bundle;

import com.radioserver.bristolbeat.R;

import org.san.iphonestyle.CustomTab;

/**
 * Custom class which is the container of Web Screen
 *
 * @see WebScreen
 */
public class WebTab extends CustomTab {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_tab);
    }

    @Override
    protected void setRootScreen() {
        WebScreen webScreen = new WebScreen();
        Bundle bundle = new Bundle();
        String url = getResources().getString(R.string.website_url);
        bundle.putString("URL", url);
        webScreen.setArguments(bundle);
        this.setScreen(getSupportFragmentManager(), webScreen);
    }
}
