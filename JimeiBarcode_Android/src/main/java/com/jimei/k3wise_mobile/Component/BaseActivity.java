package com.jimei.k3wise_mobile.Component;

import android.app.Activity;
import android.view.MotionEvent;

import com.jimei.k3wise_mobile.Util.CommonHelper;

/**
 * Created by lee on 2016/9/11.
 */
public class BaseActivity extends Activity {

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (CommonHelper.isFastDoubleClick()) {
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
