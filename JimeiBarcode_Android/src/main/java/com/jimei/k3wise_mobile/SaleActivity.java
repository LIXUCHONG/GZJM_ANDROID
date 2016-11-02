package com.jimei.k3wise_mobile;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;

import com.google.gson.Gson;
import com.jimei.k3wise_mobile.BO.Client;
import com.jimei.k3wise_mobile.BO.Goods;
import com.jimei.k3wise_mobile.BO.LoginUser;
import com.jimei.k3wise_mobile.BO.Properties.PayType;
import com.jimei.k3wise_mobile.BO.Properties.SaleType;
import com.jimei.k3wise_mobile.BO.Properties.ShippingType;
import com.jimei.k3wise_mobile.BO.SalesOrder;
import com.jimei.k3wise_mobile.BO.StockGroupList;
import com.jimei.k3wise_mobile.Component.BaseAppCompatActivity;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Component.ProgressView;
import com.jimei.k3wise_mobile.Component.WebserviceLoader;
import com.jimei.k3wise_mobile.Component.WebserviceTask;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class SaleActivity extends BaseAppCompatActivity implements SalesOrderInterface, LoaderManager.LoaderCallbacks<Message> {
    Stack<HandledFragment> currentFragmentStack = new Stack<>();
    EditGoodsFragment editGoodsFragment;
    SelectInventoryFragment selectInventoryFragment;
    SaleGoodsListFragment saleGoodsListFragment;
    SaleOrderInfoFragment saleOrderInfoFragment;

    private FragmentTabHost mTabHost = null;
    private ProgressView viewProgress;
    private View formTabHost;

    SalesOrder Order = new SalesOrder();
    Goods currentGoods = null;
    Goods edit_currentGoods = null;
    ArrayList<Goods> GoodsList = null;
    final String uuid = UUID.randomUUID().toString();

    List<ShippingType> shippingTypeList = new ArrayList<>();
    List<PayType> payTypeList = new ArrayList<>();
    List<SaleType> saleTypeList = new ArrayList<>();
//    Client defClient=new Client();

    LoadViewInitInfoTask mLoadViewInitInfoTask;
    WebserviceTask submitSalesOrderTask;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sale;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(0, null, this);
//        getLoaderManager().initLoader(0,null, this);

        setSubTitle("提交");
        getSubTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saleOrderInfoFragment.verifyOrderInfoView()) {
//                    submitSalesOrder();
                    getSupportLoaderManager().initLoader(1, null, SaleActivity.this);
                }
            }
        });

//        JSONObject jParas = new JSONObject();
//        try {
//            jParas.put("userNumber", LoginUser.Number);
//        } catch (Exception ex) {
//            ShowDialog.ExceptionDialog(this, ex.getMessage());
//            return;
//        }
//
//        mLoadViewInitInfoTask = new LoadViewInitInfoTask(this, "GeSalesOrderViewInitInfo", jParas);
//        mLoadViewInitInfoTask.execute();

        viewProgress = (ProgressView) findViewById(R.id.progress_sales_order);
        formTabHost = findViewById(R.id.tabhost_layout);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("0").setIndicator("订单提交"), SaleOrderInfoFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator("查找商品"), SearchGoodsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("2").setIndicator("销售清单"), SaleGoodsListFragment.class, null);

        IntentFilter filter = new IntentFilter();
        filter.addAction("exitApp");
        filter.addAction("finishedSubmitSalesOrder");//只有持有相同的action的接受者才能接收此广播
        registerReceiver(new ReceiveBroadCast(), filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onDestroy() {
        if (mLoadViewInitInfoTask != null) {
            if (!mLoadViewInitInfoTask.isCancelled()) {
                mLoadViewInitInfoTask.cancel(true);
            }
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

            boolean canBack = true;
            if (mTabHost.getCurrentTabTag().equals("0")) {
                if (!saleOrderInfoFragment.onBackPressed()) {
                    canBack = false;
                }
            }

            if (canBack) {
                String dlgMessage = String.format("是否退出\n【销售订单】作业？", LoginUser.Number, LoginUser.Name);
                ShowDialog.YesNoDialog(this, dlgMessage, new Runnable() {
                    @Override
                    public void run() {
                        SaleActivity.super.onBackPressed();
                    }
                }, null);
            }
        } else {
            if (currentFragmentStack.peek().onBackPressed()) {
                currentFragmentStack.pop();
                super.onBackPressed();
            }
        }
    }

    void showFragment(HandledFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.currentFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showEditGoodsFragment(int operation) {
        showFragment(currentFragmentStack.push(EditGoodsFragment.newInstance(operation, currentGoods)));
    }

    public void showSelectInventoryFragment() {
        showFragment(currentFragmentStack.push(SelectInventoryFragment.newInstance(currentGoods, edit_currentGoods)));
    }

    public void showSelectClientFragment() {
        showFragment(currentFragmentStack.push(new SelectClientFragment()));
    }

    public void setSaleGoodsListFragment(SaleGoodsListFragment fragment) {
        saleGoodsListFragment = fragment;
    }

    public void setSaleOrderInfoFragment(SaleOrderInfoFragment fragment) {
        saleOrderInfoFragment = fragment;
    }

    public void setCurrentGoods(Goods current) {
        currentGoods = current;
    }

    public void setEditCurrentGoods(Goods editCurrentGoods) {
        edit_currentGoods = editCurrentGoods;
    }

    public void addCurrentGoods() {
        if (GoodsList == null) {
            GoodsList = new ArrayList<>();
        }

        boolean isAdded = false;
        for (Goods goods : GoodsList) {
            if (goods.getItemID() == currentGoods.getItemID()
                    && goods.getPrice() == currentGoods.getPrice()) {
                goods.setQty(goods.getQty() + currentGoods.getQty());
                goods.SelectedInventory.addAll(currentGoods.SelectedInventory);
                isAdded = true;
                break;
            }
        }

        if (!isAdded) {
            GoodsList.add(currentGoods);
        }
    }

    public void editCurrentGoods(Goods realGoods) {
        realGoods.setPrice(currentGoods.getPrice());
        realGoods.setQty(currentGoods.getQty());

        realGoods.SelectedInventory.clear();
        realGoods.SelectedInventory.addAll(currentGoods.SelectedInventory);

        saleGoodsListFragment.onResume();
    }

    public void delCurrentGoods(Goods currentGoods) {
        GoodsList.remove(currentGoods);
//        saleGoodsListFragment.onResume();
    }

    public List<Goods> returnSaleGoodsList() {
        return GoodsList;
    }

    public List<?> returnSaleOrderInfoList(Class<?> currentClass) {
        if (currentClass == ShippingType.class) {
            return shippingTypeList;
        }

        if (currentClass == PayType.class) {
            return payTypeList;
        }

        if (currentClass == SaleType.class) {
            return saleTypeList;
        }

        return null;
    }

    public int matchSaleOrderInfo(Class<?> currentClass, String saleOrderInfoName) throws Exception {
        int matchIndex = -1;
        if (currentClass == ShippingType.class) {
            for (int i = 0; i < shippingTypeList.size(); i++) {
                if (shippingTypeList.get(i).getName().equals(saleOrderInfoName)) {
                    matchIndex = i;
                }
            }
        }

        if (currentClass == PayType.class) {
            for (int i = 0; i < payTypeList.size(); i++) {
                if (payTypeList.get(i).getName().equals(saleOrderInfoName)) {
                    matchIndex = i;
                }
            }
        }

        if (currentClass == SaleType.class) {
            for (int i = 0; i < saleTypeList.size(); i++) {
                if (saleTypeList.get(i).getName().equals(saleOrderInfoName)) {
                    matchIndex = i;
                }
            }
        }

        if (!(matchIndex < 0)) {
            return matchIndex;
        } else {
            throw new Exception(String.format("无效的字段值[%s]", saleOrderInfoName));
        }
    }

    public void setSaleOrderInfo(Class<?> currentClass, int selectedIndex) {
        if (currentClass == ShippingType.class) {
            Order.ShippingType = shippingTypeList.get(selectedIndex);
        }

        if (currentClass == PayType.class) {
            Order.PayType = payTypeList.get(selectedIndex);
        }

        if (currentClass == SaleType.class) {
            Order.SaleType = saleTypeList.get(selectedIndex);
        }
    }

    public void setSalesOrderClient(Client client) {
        Order.Client = client;
        saleOrderInfoFragment.onResume();
    }

    public void setSaleOrderInfo(Class<?> currentClass, String saleOrderInfoName) throws Exception {
        setSaleOrderInfo(currentClass, matchSaleOrderInfo(currentClass, saleOrderInfoName));
    }

    public SalesOrder getSalesOrder() {
        return Order;
    }

    private void setDefaultSaleOrderInfo() throws Exception {
        setSaleOrderInfo(ShippingType.class, getString(R.string.sales_order_info_shipping_type_name));
        setSaleOrderInfo(PayType.class, getString(R.string.sales_order_info_pay_type_name));
        setSaleOrderInfo(SaleType.class, getString(R.string.sales_order_info_sale_type_name));
    }

    public class LoadViewInitInfoTask extends WebserviceTask {

        LoadViewInitInfoTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPostExecute(final Message msg) {

            mLoadViewInitInfoTask = null;
            super.onPostExecute(msg);
            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        loadViewInitInfo(msg.obj.toString());
                        setDefaultSaleOrderInfo();
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(SaleActivity.this, ex.getMessage());
                        return;
                    }
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(SaleActivity.this, "基础数据初始化失败");
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mLoadViewInitInfoTask = null;
        }
    }

    private void loadViewInitInfo(String jsonStr) throws Exception {
        JSONArray jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            String key = jsonArray.getJSONObject(i).getString("Key");
            switch (key) {
                case "DefaultClient":
                    setDefaultClient(new JSONArray(jsonArray.getJSONObject(i).getString("Value")));
                    break;
                case "isGetGoodsClientPrice":
                    Order.setGetClientGoodsPrice(jsonArray.getJSONObject(i).getBoolean("Value"));
                    break;
                case "StockGroupList":
                    loadStockGroupList(new JSONArray(jsonArray.getJSONObject(i).getString("Value")));
                    break;
                case "ShippingTypeList":
                    loadShippingTypeList(new JSONArray(jsonArray.getJSONObject(i).getString("Value")));
                    break;
                case "PayTypeList":
                    loadPayTypeList(new JSONArray(jsonArray.getJSONObject(i).getString("Value")));
                    break;
                case "SaleTypeList":
                    loadSaleTypeList(new JSONArray(jsonArray.getJSONObject(i).getString("Value")));
                    break;
            }
        }
    }

    private void loadStockGroupList(JSONArray jsonArray) throws Exception {
        StockGroupList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            int id = jsonArray.getJSONObject(i).getInt("Key");
            String name = jsonArray.getJSONObject(i).getString("Value");
            StockGroupList.addStockGroup(id, name);
        }
    }

    private void loadShippingTypeList(JSONArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            int id = jsonArray.getJSONObject(i).getInt("interid");
            String name = jsonArray.getJSONObject(i).getString("name");
            ShippingType shippingType = new ShippingType();
            shippingType.setId(id);
            shippingType.setName(name);
            shippingTypeList.add(shippingType);
        }
    }

    private void loadPayTypeList(JSONArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            int id = jsonArray.getJSONObject(i).getInt("itemid");
            String name = jsonArray.getJSONObject(i).getString("name");
            PayType payType = new PayType();
            payType.setId(id);
            payType.setName(name);
            payTypeList.add(payType);
        }
    }

    private void loadSaleTypeList(JSONArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.length(); i++) {
            int id = jsonArray.getJSONObject(i).getInt("itemid");
            String name = jsonArray.getJSONObject(i).getString("name");
            SaleType saleType = new SaleType();
            saleType.setId(id);
            saleType.setName(name);
            saleTypeList.add(saleType);
        }
    }

    private void setDefaultClient(JSONArray jsonArray) throws Exception {
        Client defClient = new Client();
        for (int i = 0; i < jsonArray.length(); i++) {
            String key = jsonArray.getJSONObject(i).getString("Key");
            if (key.equals("Number")) {
                defClient.setNumber(jsonArray.getJSONObject(i).getString("Value"));
            } else {
                defClient.setName(jsonArray.getJSONObject(i).getString("Value"));
            }
        }

//            Order.Client=defClient;
        setSalesOrderClient(defClient);
    }

    public boolean verifySalesOrderInfo() {
        if (GoodsList == null || GoodsList.size() < 1) {
            ShowDialog.WarningDialog(this, "请添加出售商品");
            return false;
        }

        return true;
    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SaleActivity.this.finish();
        }
    }

    public void submitSalesOrder() {
        try {
            String json = new Gson().toJson(Order);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderJsonStr", json);
            jsonObject.put("userid", LoginUser.Id);
            jsonObject.put("usernumber", LoginUser.Number);
            jsonObject.put("username", LoginUser.Name);
            jsonObject.put("uuid", uuid);
            submitSalesOrderTask = new SubmitSalesOrderTask(this, "SubmitSalesOrder", jsonObject);
            submitSalesOrderTask.execute();
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(this, ex.getMessage());
        }
    }

    private void showProgress(boolean show) {
        viewProgress.Show(formTabHost, show);
    }

    public class SubmitSalesOrderTask extends WebserviceTask {
        SubmitSalesOrderTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected void onPostExecute(final Message msg) {

            submitSalesOrderTask = null;
            showProgress(false);

            super.onPostExecute(msg);
            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        showSubmitOrderResult(msg.obj.toString());
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(SaleActivity.this, ex.getMessage());
                    }
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(SaleActivity.this, "销售订单提交失败");
                    break;
            }

            super.onPostExecute(msg);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            submitSalesOrderTask = null;
        }
    }

    private void showSubmitOrderResult(String result) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
        String message = jsonObject.getString("message");

        if (jsonObject.getBoolean("sueecssed")) {
            ShowDialog.MessageDialog(this, message, finishedSubmit());
        } else {
            ShowDialog.WarningDialog(this, String.format("单据提交失败，%s", message));
        }
    }

    Runnable finishedSubmit() {
        return new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction("finishedSubmitSalesOrder");
                SaleActivity.this.sendBroadcast(intent);
            }
        };
    }

    @Override
    public Loader<Message> onCreateLoader(int id, Bundle args) {
        final WebserviceLoader loader = new WebserviceLoader(this);
        currentLoaderId = id;
        try {
            if (id == 0) {
                String method = "GetSalesOrderViewInitInfo";
                JSONObject jsonParas = new JSONObject();
                jsonParas.put("userNumber", LoginUser.Number);
                loader.setWebMethod(method);
                loader.setMethodParas(jsonParas);
                progressDialog = ShowDialog.showLoaderProgressDialog(this, loader);
            } else if (id == 1) {
                String method = "SubmitSalesOrder";
                String json = new Gson().toJson(Order);
                JSONObject jsonParas = new JSONObject();
                jsonParas.put("orderJsonStr", json);
                jsonParas.put("userid", LoginUser.Id);
                jsonParas.put("usernumber", LoginUser.Number);
                jsonParas.put("username", LoginUser.Name);
                jsonParas.put("uuid", uuid);
                loader.setWebMethod(method);
                loader.setMethodParas(jsonParas);
                progressDialog = ShowDialog.showLoaderProgressDialog(this, loader, false);
            }
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(this, ex.getMessage());
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
                        if (loader.getId() == 0) {
                            loadViewInitInfo(data.obj.toString());
                            setDefaultSaleOrderInfo();
                        } else if (loader.getId() == 1) {
                            showSubmitOrderResult(data.obj.toString());
                        }
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                        ShowDialog.WarningDialog(this, "读取数据失败");
                        break;
                    case KingdeeK3WiseWebServiceHelper.EXCEPTION:
                        ShowDialog.ExceptionDialog(this, data.obj.toString());
                        break;
                }
            }
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(SaleActivity.this, ex.getMessage());
        } finally {
            getSupportLoaderManager().destroyLoader(loader.getId());
        }
    }
}
