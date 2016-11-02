package com.jimei.k3wise_mobile.Component;

import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jimei.k3wise_mobile.R;

/**
 * Created by lee on 2016/9/27.
 */

public abstract class HandledFragment extends Fragment implements View.OnTouchListener {

    private TextView mToolbarTitle;
    private TextView mToolbarSubTitle;
    private Toolbar mToolbar;

    private View rootView = null;

    protected ProgressDialog progressDialog = null;
    protected int currentLoaderId;

    public boolean onBackPressed() {return true;}

    public void popFragment() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);

            if (isShowToolBar()) {
                mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

                if (mToolbar != null) {
                    mToolbarTitle = (TextView) rootView.findViewById(R.id.toolbar_title);
                    mToolbarSubTitle = (TextView) rootView.findViewById(R.id.toolbar_subtitle);
                    setToolbarTitleText();
                    setToolbarSubTitleText();

                    //将Toolbar显示到界面
                    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                    if (isShowBacking()) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }
                }
            }
        }

        return rootView;
    }

    public TextView getToolbarTitle() {
        return mToolbarTitle;
    }

    public TextView getToolbarSubTitle() {
        return mToolbarSubTitle;
    }

    protected void setToolbarTitleText() {
        mToolbarTitle.setText("");
    }

    protected void setToolbarSubTitleText() {
        mToolbarSubTitle.setText("");
    }

    protected boolean isShowBacking() {
        return true;
    }

    protected boolean isShowToolBar() {
        return true;
    }

    protected abstract int getLayoutId();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 拦截触摸事件，防止泄露下去
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
