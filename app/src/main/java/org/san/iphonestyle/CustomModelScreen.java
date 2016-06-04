/**
 * Project    : iPhone Style Framework
 * Author     : Hoang San
 * Date        : Dec 14, 2012 
 **/
package org.san.iphonestyle;

import android.os.Bundle;
import android.app.Activity;

/**
 * Custom Activity class, make a normal screen, not in Tab.
 * 
 * @author Hoang San
 * @see Activity
 */
public abstract class CustomModelScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    protected abstract void initComponents();
    protected abstract void setListener();
    protected abstract void initData();
    
}
