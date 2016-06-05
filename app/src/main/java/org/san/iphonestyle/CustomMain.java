package org.san.iphonestyle;

import com.radioserver.bristolbeat.R;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Custom TabActivity class, manage all tab widgets (CustomTab).
 *
 * @author Hoang San
 * @see TabActivity
 * @see CustomTab
 */
public abstract class CustomMain extends TabActivity {

    private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Add a new tab to tabbar.
     *
     * @param label      the tab label.
     * @param drawableId the Drawable Resource Id for the image on this tab.
     * @param c          the CustomTab class which belong to this tab.
     */
    protected void addTab(String label, int drawableId, Class<?> c) {
        tabHost = super.getTabHost();
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + label);
        View tabIndicator;

        tabIndicator = LayoutInflater.from(this)
                .inflate(R.layout.layout_tabbar_indicator, getTabWidget(), false);
        ImageView tabIcon = (ImageView) tabIndicator.findViewById(R.id.icon);
        tabIcon.setImageResource(drawableId);
        ((TextView) tabIndicator.findViewById(R.id.title)).setText(label);

        spec.setIndicator(tabIndicator);

        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    protected void addTab(String label, Bitmap bm, Class<?> c) {
        tabHost = super.getTabHost();
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + label);
        View tabIndicator;

        tabIndicator = LayoutInflater.from(this)
                .inflate(R.layout.layout_tabbar_indicator, getTabWidget(), false);
        ((ImageView) tabIndicator.findViewById(R.id.icon)).setImageBitmap(bm);
        ((TextView) tabIndicator.findViewById(R.id.title)).setText(label);

        spec.setIndicator(tabIndicator);

        spec.setContent(intent);
        tabHost.addTab(spec);
    }

    public TabHost getTabHost() {
        return tabHost;
    }
}