package com.jimei.k3wise_mobile.Component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.jimei.k3wise_mobile.R;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONObject;

/**
 * Created by lee on 2016/9/11.
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Message>{

    private TextView mToolbarTitle;
    private TextView mToolbarSubTitle;
    private Toolbar mToolbar;

    protected ProgressDialog progressDialog;

    boolean allowCancelLoader;
    boolean allowDestroyLoader;

    public void setAllowCancelLoader(boolean allowCancelLoader) {
        this.allowCancelLoader = allowCancelLoader;
    }

    public void setAllowDestroyLoader(boolean allowDestroyLoader) {
        this.allowDestroyLoader = allowDestroyLoader;
    }

    public void handleCreateLoader(int loaderId){

    }

    public void handleLoaderCallbacksMessage (int loaderId, String msg){

    }

    public Bundle setWebserviceArgs(int loaderId) throws Exception{
        return null;
    }

    @Override
    public Loader<Message> onCreateLoader(int id, Bundle args) {
        allowCancelLoader=false;
        allowDestroyLoader=true;
        final WebserviceLoader loader = new WebserviceLoader(this);
        try {
            Bundle bundle=setWebserviceArgs(id);
            if(bundle==null
                    ||bundle.getString("WebMethod")==null
                    ||bundle.getString("WebMethod").equals("")
                    ||bundle.getString("MethodParas")==null
                    ||bundle.getString("MethodParas").equals("")){
                throw new Exception(String.format("无效的请求参数，LoaderId:%d",id));
            }

            loader.setWebMethod(bundle.getString("WebMethod"));
            loader.setMethodParas(new JSONObject(bundle.getString("MethodParas")));
            handleCreateLoader(id);
            progressDialog = ShowDialog.showLoaderProgressDialog(this, loader, allowCancelLoader);
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(this, ex.getMessage());
            return null;
        }

        return loader;
    }

    @Override
    public void onLoaderReset(Loader<Message> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Message> loader, Message data) {
        progressDialog.dismiss();

        try {
            if (loader != null && data != null) {
                switch (data.what) {
                    case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                        handleLoaderCallbacksMessage(loader.getId(),data.obj.toString());
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                        ShowDialog.WarningDialog(this, "读取数据失败");
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_EXCEPTION:
                        ShowDialog.ExceptionDialog(this, data.obj.toString());
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_BUSINESS_EXCEPTION:
                        ShowDialog.WarningDialog(this, data.obj.toString());
                        break;
                }
            }
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(this, ex.getMessage());
        } finally {
            if(allowDestroyLoader){
                getSupportLoaderManager().destroyLoader(loader.getId());
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
       /*
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Title");
        toolbar.setSubtitle("Sub Title");
        */
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbarSubTitle = (TextView) findViewById(R.id.toolbar_subtitle);
        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar);
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            //设置默认的标题不显示
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 判断是否有Toolbar,并默认显示返回按钮
         */
        if(null != getToolbar() && isShowBacking()){
            showBack();
        }
    }

    /**
     * 设置头部标题
     * @param title
     */
    public void setToolBarTitle(CharSequence title) {
        if(mToolbarTitle != null){
            mToolbarTitle.setText(title);
        }else{
            getToolbar().setTitle(title);
            setSupportActionBar(getToolbar());
        }
    }

    /**
     * 获取头部标题的TextView
     * @return
     */
    public TextView getToolbarTitle(){
        return mToolbarTitle;
    }

    /**
     * 设置头部标题
     * @param title
     */
    public void setSubTitle(CharSequence title) {
        mToolbarSubTitle.setText(title);
    }

    /**
     * 获取头部标题的TextView
     * @return
     */
    public TextView getSubTitle(){
        return mToolbarSubTitle;
    }

    /**
     * this Activity of tool bar.
     * 获取头部.
     * @return support.v7.widget.Toolbar.
     */
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 版本号小于21的后退按钮图片
     */
    private void showBack(){
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
//        Drawable upArrow;
//        if(Build.VERSION.SDK_INT>=21) {
//            upArrow = getResources().getDrawable(R.drawable.ic_left_triangle, null);
//        }else {
//            upArrow = getResources().getDrawable(R.drawable.ic_left_triangle);
//        }
//
//        if(Build.VERSION.SDK_INT>=23){
//            upArrow.setColorFilter(getResources().getColor(R.color.whilt,null), PorterDuff.Mode.SRC_ATOP);
//        }else {
//            upArrow.setColorFilter(getResources().getColor(R.color.whilt), PorterDuff.Mode.SRC_ATOP);
//        }
//
//        getToolbar().setNavigationIcon(upArrow);
//        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     * @return
     */
    protected boolean isShowBacking(){
        return true;
    }

    /**
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     * @return res layout xml id
     */
    protected abstract int getLayoutId();
}
