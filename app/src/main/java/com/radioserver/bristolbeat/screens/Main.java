package com.radioserver.bristolbeat.screens;

import org.san.iphonestyle.CustomMain;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.services.RadioPlayerService;
import com.radioserver.bristolbeat.helpers.UserProfileSingleton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class Main extends CustomMain {

    private TabHost tabHost;
    private View previousView;
    private View currentView;
    private int currentTab;

    public static final int ANIMATION_TIME = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init play radio service
        RadioPlayerService.getInstance().initialService(this);

        setContentView(R.layout.main);

        initTabs();
        tabHost = getTabHost();

        tabHost.setCurrentTab(0);
        previousView = tabHost.getCurrentView();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                currentView = tabHost.getCurrentView();
                if (tabHost.getCurrentTab() > currentTab) {
                    previousView.setAnimation(outToLeftAnimation());
                    currentView.setAnimation(inFromRightAnimation());
                } else {
                    previousView.setAnimation(outToRightAnimation());
                    currentView.setAnimation(inFromLeftAnimation());
                }
                previousView = currentView;
                currentTab = tabHost.getCurrentTab();
                setTabSelectorIconTint(tabHost.getTabWidget(), currentTab);
            }
        });
        setTabSelectorIconTint(tabHost.getTabWidget(), 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getLongExtra("MILI", 0) != 0) {
            this.getTabHost().setCurrentTab(0);
            UserProfileSingleton.getConfig(getApplicationContext()).setPlay(true);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RadioPlayerService.getInstance().destroy();
    }

    @Override
    protected void onResume() {
        TabWidget allTab = tabHost.getTabWidget();
        ColorStateList colorStateList = getColorStateList();
        for (int i = 0; i < allTab.getTabCount(); i++)
            setTabSelector(colorStateList, allTab.getChildAt(i));

        super.onResume();
    }

    private void setTabSelector(ColorStateList colorStateList, View view) {
        if (view != null) {
            TextView title = (TextView) view.findViewById(R.id.title);
            if (title != null) {
                title.setTextColor(colorStateList);
            }
        }
    }

    private ColorStateList getColorStateList() {
        return ContextCompat.getColorStateList(this, R.drawable.tab_title_selector);
    }

    private void setTabSelectorIconTint(TabWidget allTabs, int currentSelection) {
        int tintColor = ContextCompat.getColor(this, R.color.tab_bar_tint_color);
        int defaultColor = ContextCompat.getColor(this, R.color.grey);
        for (int i = 0; i < allTabs.getTabCount(); i++) {
            if (allTabs.getChildAt(i) != null) {
                ImageView icon = (ImageView) allTabs.getChildAt(i).findViewById(R.id.icon);
                if (icon != null) {
                    if (i == currentSelection) {
                        icon.setColorFilter(tintColor);
                    } else {
                        icon.setColorFilter(defaultColor);
                    }
                }
            }
        }
    }

    protected void initTabs() {
        addTab("Radio", R.mipmap.ic_radio, RadioTab.class);
        addTab("Web", R.mipmap.ic_web, WebTab.class);
        addTab("Alarm", R.mipmap.ic_alarm_clock, AlarmTab.class);
        addTab("Social", R.mipmap.ic_social, SocialTab.class);
        addTab("Shout Out", R.mipmap.ic_shoutout, ShoutOutTab.class);
    }

    /**
     * Custom animation that animates in from right
     *
     * @return Animation the Animation object
     */
    private Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(inFromRight);
    }

    /**
     * Custom animation that animates out to the right
     *
     * @return Animation the Animation object
     */
    private Animation outToRightAnimation() {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outToRight);
    }

    /**
     * Custom animation that animates in from left
     *
     * @return Animation the Animation object
     */
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(inFromLeft);
    }

    /**
     * Custom animation that animates out to the left
     *
     * @return Animation the Animation object
     */
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        return setProperties(outtoLeft);
    }

    /**
     * Helper method that sets some common properties
     *
     * @param animation the animation to give common properties
     * @return the animation with common properties
     */
    private Animation setProperties(Animation animation) {
        animation.setDuration(ANIMATION_TIME);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }
}
