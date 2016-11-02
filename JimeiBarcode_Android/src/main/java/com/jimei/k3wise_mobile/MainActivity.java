package com.jimei.k3wise_mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.LoginUser;
import com.jimei.k3wise_mobile.Component.BaseAppCompatActivity;
import com.jimei.k3wise_mobile.Util.ShowDialog;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View operation_menu_show = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isShowBacking() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getLayoutId());
        Toolbar toolbar = getToolbar();
//        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        TextView mUserNumberView = (TextView) navHeaderView.findViewById(R.id.textView_Number);
        TextView mUserNameView = (TextView) navHeaderView.findViewById(R.id.textView_Name);
        mUserNumberView.setText(LoginUser.Number);
        mUserNameView.setText(LoginUser.Name);

        IntentFilter filter = new IntentFilter();
        filter.addAction("exitApp");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(new ReceiveBroadCast(), filter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        String dlgMessage = String.format("当前用户 [%s:%s]\n是否退出登录", LoginUser.Number, LoginUser.Name);
        ShowDialog.YesNoDialog(this, dlgMessage, BackPressed(), null);
    }

    Runnable BackPressed() {
        return new Runnable() {
            @Override
            public void run() {
                MainActivity.super.onBackPressed();
            }
        };
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if ((operation_menu_show != null && operation_menu_show.getVisibility() == View.VISIBLE)) {
            operation_menu_show.setVisibility(View.GONE);
        }

        if (id == R.id.nav_stores) {
            operation_menu_show = findViewById(R.id.stores_operations_main);
            operation_menu_show.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_common) {
            operation_menu_show = findViewById(R.id.common_operations_main);
            operation_menu_show.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_quit) {
            onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void operationButtonClickHandler(View view) {
        if (view.getId() == R.id.sale_order_btn) {
            Intent intent = new Intent(this, SaleActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.inventory_btn) {
            ShowDialog.MessageDialog(MainActivity.this, "库存查询", null);
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.finish();
        }
    }
}
