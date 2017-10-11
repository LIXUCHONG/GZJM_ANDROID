package com.jimei.k3wise_mobile.Interface;

import com.jimei.k3wise_mobile.BO.Client;
import com.jimei.k3wise_mobile.BO.SaleGoods;
import com.jimei.k3wise_mobile.BO.SalesOrder;
import com.jimei.k3wise_mobile.SaleGoodsListFragment;
import com.jimei.k3wise_mobile.SaleOrderInfoFragment;

import java.util.List;

/**
 * Created by lee on 2016/9/18.
 */
public interface SalesOrderInterface {
//    void delGoods(HashMap<String,Object> goods);
//    void addGoods(HashMap<String,Object> goods);
//    void editGoods(HashMap<String,Object> goods);

    void showEditGoodsFragment(int operation);
    void setFragmentResult(boolean value);
    boolean getFragmentResult();
    void showSelectInventoryFragment();
    void showSelectClientFragment();
    void setSaleGoodsListFragment(SaleGoodsListFragment fragment);
    void setSaleOrderInfoFragment(SaleOrderInfoFragment fragment);
    void setCurrentGoods(SaleGoods currentGoods);
    void setEditCurrentGoods(SaleGoods edit_currentGoods);
    void addCurrentGoods();
    void editCurrentGoods(SaleGoods realGoods);
    void delCurrentGoods(SaleGoods currentGoods);
    List<SaleGoods> returnSaleGoodsList();
    List<?> returnSaleOrderInfoList(Class<?> currentClass);
    SalesOrder getSalesOrder();
    void setSaleOrderInfo(Class<?> currentClass,int selectedIndex);
    void setSalesOrderClient(Client client);
    boolean verifySalesOrderInfo();
    void submitSalesOrder();
}
