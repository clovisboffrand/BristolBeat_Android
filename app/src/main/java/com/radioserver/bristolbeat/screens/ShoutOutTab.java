/**
 * Project    : iRadio
 * Author     : Hoang San
 **/
package com.radioserver.bristolbeat.screens;

/**
 * @author Hoang San
 */

import android.os.Bundle;

import com.radioserver.bristolbeat.R;

import org.san.iphonestyle.CustomTab;

public class ShoutOutTab extends CustomTab {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_tab);
    }

    @Override
    protected void setRootScreen() {
        ShoutOutScreen screen = new ShoutOutScreen();
        this.setScreen(getSupportFragmentManager(), screen);
    }
}
