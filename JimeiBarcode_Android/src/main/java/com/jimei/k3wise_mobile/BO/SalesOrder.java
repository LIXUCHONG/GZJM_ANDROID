package com.jimei.k3wise_mobile.BO;

import java.util.ArrayList;
import java.util.List;

import com.jimei.k3wise_mobile.BO.Properties.ShippingType;
import com.jimei.k3wise_mobile.BO.Properties.PayType;
import com.jimei.k3wise_mobile.BO.Properties.SaleType;

/**
 * Created by lee on 2016/10/1.
 */

public class SalesOrder extends DocumentBill {

    public ShippingType ShippingType = null;
    public PayType PayType = null;
    public SaleType SaleType = null;
    public Client Client = null;
    private String Remark;
    private double DyeFee = 0;
    private double OtherFee = 0;
    private double EarnestMoney = 0;
    private boolean isGetClientGoodsPrice;

    private List<SaleGoods> SalesGoods = null;

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public double getDyeFee() {
        return DyeFee;
    }

    public void setDyeFee(double dyeFee) {
        DyeFee = dyeFee;
    }

    public double getEarnestMoney() {
        return EarnestMoney;
    }

    public void setEarnestMoney(double earnestMoney) {
        this.EarnestMoney = earnestMoney;
    }

    public double getOtherFee() {
        return OtherFee;
    }

    public void setOtherFee(double otherFee) {
        OtherFee = otherFee;
    }

    public List<SaleGoods> getSalesGoods() {
        return SalesGoods != null ? SalesGoods : (new ArrayList<SaleGoods>());
    }

    public void setSalesGoods(List salesGoods) {
        SalesGoods = salesGoods;
//        getGoodsRecords().clear();
//        for (SaleGoods i:SalesGoods) {
//            GoodsRecords.add(i);
//        }
    }

    public boolean isGetClientGoodsPrice() {
        return isGetClientGoodsPrice;
    }

    public void setGetClientGoodsPrice(boolean getClientGoodsPrice) {
        isGetClientGoodsPrice = getClientGoodsPrice;
    }
}
