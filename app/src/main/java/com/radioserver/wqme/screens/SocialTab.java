package com.radioserver.wqme.screens;

import android.os.Bundle;

import com.radioserver.wqme.R;

import org.san.iphonestyle.CustomTab;

public class SocialTab extends CustomTab {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_tab);
    }

    @Override
    protected void setRootScreen() {
        SocialScreen screen = new SocialScreen();
        this.setScreen(getSupportFragmentManager(), screen);
    }
}
