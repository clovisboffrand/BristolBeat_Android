package com.radioserver.bristolbeat.screens;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.helpers.UserProfileSingleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {

    protected int _splashTime = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        showSplash();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void showSplash() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(_splashTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    finish();
                } finally {
                    Intent i = new Intent(getBaseContext(), Main.class);
                    startActivity(i);

                    UserProfileSingleton.getConfig(getApplicationContext()).setShowSplashScreen(false);
                    finish();

                    SplashScreen.this.overridePendingTransition(R.anim.appear, R.anim.disappear);
                }
            }
        }.start();
    }
}