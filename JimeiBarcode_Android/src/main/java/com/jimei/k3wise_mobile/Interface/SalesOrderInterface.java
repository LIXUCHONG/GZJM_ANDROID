package com.jimei.k3wise_mobile.Interface;

import android.app.Fragment;
import android.os.Bundle;

import com.jimei.k3wise_mobile.BO.Client;
import com.jimei.k3wise_mobile.BO.Goods;
import com.jimei.k3wise_mobile.BO.Inventory;
import com.jimei.k3wise_mobile.BO.SalesOrder;
import com.jimei.k3wise_mobile.SaleGoodsListFragment;
import com.jimei.k3wise_mobile.SaleOrderInfoFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lee on 2016/9/18.
 */
public interface SalesOrderInterface {
//    void delGoods(HashMap<String,Object> goods);
//    void addGoods(HashMap<String,Object> goods);
//    void editGoods(HashMap<String,Object> goods);

    void showEditGoodsFragment(int operation);
    void showSelectInventoryFragment();
    void showSelectClientFragment();
    void setSaleGoodsListFragment(SaleGoodsListFragment fragment);
    void setSaleOrderInfoFragment(SaleOrderInfoFragment fragment);
    void setCurrentGoods(Goods currentGoods);
    void setEditCurrentGoods(Goods edit_currentGoods);
    void addCurrentGoods();
    void editCurrentGoods(Goods realGoods);
    void delCurrentGoods(Goods currentGoods);
    List<Goods> returnSaleGoodsList();
    List<?> returnSaleOrderInfoList(Class<?> currentClass);
    SalesOrder getSalesOrder();
    void setSaleOrderInfo(Class<?> currentClass,int selectedIndex);
    void setSalesOrderClient(Client client);
    boolean verifySalesOrderInfo();
    void submitSalesOrder();
}
