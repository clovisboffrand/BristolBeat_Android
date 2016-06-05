package org.san.iphonestyle;

import com.radioserver.bristolbeat.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

/**
 * Custom FragmentActivity class, make a container for CustomScreen.
 *
 * @see FragmentActivity
 * @see CustomScreen
 */
public abstract class CustomTab extends FragmentActivity {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_custom_tab);
        this.setRootScreen();

        progressDialog = new ProgressDialog(this);
    }

    /**
     * Require the derived class chooses a CustomScreen as root.
     */
    protected abstract void setRootScreen();

    /**
     * Set a CustomScreen as root.
     *
     * @param fm     Current FragmentManager.
     * @param screen the screen is set as root.
     */
    protected void setScreen(FragmentManager fm, CustomScreen screen) {
        fm.beginTransaction().replace(R.id.vgrContent, screen).commitAllowingStateLoss();
    }

    /**
     * Change current tab shown.
     *
     * @param tabId The tab index which is shown.
     */
    protected void changeCurrentTab(int tabId) {
        TabActivity main = (CustomMain) this.getParent();
        main.getTabHost().setCurrentTab(tabId);
    }

    /**
     * Show/hide the Loading dialog.
     *
     * @param isShow Set show/hide.
     */
    public void setLoading(boolean isShow) {
        if (isShow) {
            if (progressDialog != null)
                progressDialog.dismiss();
            progressDialog = ProgressDialog.show(this, "", getString(R.string.alert_loading));
        } else {
            if (progressDialog != null) progressDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (this.getSupportFragmentManager().getBackStackEntryCount() != 0) return false;

            AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
            builder.setMessage(getString(R.string.alert_ask_to_exit))
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
