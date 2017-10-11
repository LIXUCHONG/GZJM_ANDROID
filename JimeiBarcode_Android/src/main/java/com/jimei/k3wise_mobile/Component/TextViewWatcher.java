package com.jimei.k3wise_mobile.Component;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by lee on 2017/8/20.
 */

public class TextViewWatcher implements TextWatcher {
    private TextView textView;

    public TextViewWatcher() {}

    public TextViewWatcher(TextView view) {
        textView = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
