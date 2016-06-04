package com.radioserver.wqme.screens;

import org.san.iphonestyle.CustomTab;

import android.os.Bundle;

import com.radioserver.wqme.R;

public class AlarmTab extends CustomTab {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_tab);
    }

    @Override
    protected void setRootScreen() {
        AlarmScreen screen = new AlarmScreen();
        this.setScreen(getSupportFragmentManager(), screen);
    }

}
