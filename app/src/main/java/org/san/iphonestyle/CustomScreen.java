package org.san.iphonestyle;

import com.radioserver.bristolbeat.R;

import android.app.TabActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Custom Fragment class, make a screen which is in Tab.
 *
 * @see Fragment
 */
public abstract class CustomScreen extends Fragment {

    public enum Result {OK, CANCEL}

    private CustomScreen _caller;
    private int _requestId;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            this.onResume();
        }
    }

    /**
     * Go back in CustomScreen.
     *
     * @param fm        Current FragmentManager.
     * @param animation Set show/hide animation when go back.
     */
    protected void back(FragmentManager fm, boolean animation) {
        if (animation) {
            Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_right);
            this.getView().setAnimation(ani);
        }
        fm.popBackStack();
    }

    /**
     * Set a CustomScreen as root in current tab.
     *
     * @param fm     Current FragmentManager.
     * @param screen The screen which is set as root screen.
     */
    protected void setRoot(FragmentManager fm, CustomScreen screen) {
        fm.beginTransaction().replace(R.id.vgrContent, screen).commit();
    }

    /**
     * Start a new CustomScreen from current screen in current tab
     *
     * @param fm      Current FragmentManager.
     * @param current The screen request a new screen.
     * @param next    The new screen.
     */
    protected void startNewScreen(FragmentManager fm, CustomScreen current, CustomScreen next) {
        current.onPause();
        FragmentTransaction fs = fm.beginTransaction();
        fs.add(R.id.vgrContent, next);
        fs.hide(current);
        fs.addToBackStack(null);
        fs.commit();
    }

    /**
     * Start a new CustomScreen from current screen in current tab, response a result.
     *
     * @param fm        Current FragmentManager.
     * @param current   The screen request a new screen.
     * @param next      The new screen.
     * @param requestId The request Id.
     */
    protected void startNewScreenForResult(FragmentManager fm, CustomScreen current, CustomScreen next, int requestId) {
        next._caller = current;
        next._requestId = requestId;

        startNewScreen(fm, current, next);
    }

    /**
     * Start a new CustomScreen from current screen in other tab.
     *
     * @param new_screen The new screen.
     * @param tabId      The tab index for new screen.
     */
    protected void startNewScreenAtOtherTab(CustomScreen new_screen, int tabId) {
        TabActivity main = (CustomMain) this.getActivity().getParent();

        main.getTabHost().setCurrentTab(tabId);

        FragmentManager fm = ((CustomTab) main.getCurrentActivity()).getSupportFragmentManager();
        FragmentTransaction fs = fm.beginTransaction();
        fs.add(R.id.vgrContent, new_screen);
        fs.addToBackStack(null);
        fs.commit();
    }

    /**
     * Start a new CustomScreen from current screen in other tab with position in stack
     *
     * @param new_screen The new screen.
     * @param tabId      The tab index for new screen.
     * @param posInStack The postion in back-stack.
     */
    protected void startNewScreenAtOtherTabByPos(CustomScreen new_screen, int tabId, int posInStack) {
        TabActivity main = (CustomMain) this.getActivity().getParent();

        main.getTabHost().setCurrentTab(tabId);

        FragmentManager fm = ((CustomTab) main.getCurrentActivity()).getSupportFragmentManager();
        int count = fm.getBackStackEntryCount() + 1;
        for (int i = posInStack; i < count; i++)
            fm.popBackStack();

        FragmentTransaction fs = fm.beginTransaction();
        fs.add(R.id.vgrContent, new_screen);
        fs.addToBackStack(null);
        fs.commit();
    }

    /**
     * Change current tab shown.
     *
     * @param tabId The tab index which is shown.
     */
    protected void changeCurrentTab(int tabId) {
        CustomTab cus = (CustomTab) this.getActivity();
        if (cus != null) cus.changeCurrentTab(tabId);
    }

    /**
     * Show/hide the Loading dialog.
     *
     * @param isShow Set show/hide.
     */
    protected void setLoading(boolean isShow) {
        CustomTab cus = (CustomTab) this.getActivity();
        if (cus != null) cus.setLoading(isShow);
    }

    /**
     * Initialize data by derived class.
     */
    protected abstract void initData();

    /**
     * Initialize all components by derived class.
     *
     * @param container
     */
    protected abstract void initComponents(View container);

    /**
     * Set listeners for components by derived class.
     */
    protected abstract void setListeners();

    public void callForResult(FragmentManager fm, CustomScreen.Result result, Object data) {
        this.back(fm, false);
        if (_caller != null)
            _caller.doForResult(result, data, _requestId);
    }

    protected void doForResult(CustomScreen.Result result, Object data, int requestId) {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_to_left);
        view.setAnimation(ani);
        super.onViewCreated(view, savedInstanceState);
    }
}