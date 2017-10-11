package com.jimei.k3wise_mobile.BO;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2016/9/15.
 */
public class SaleGoods extends GoodsRecord {

    public SaleGoods() {
        Price = BigDecimal.ZERO;
        Brokerage = BigDecimal.ZERO;
        SelectedInventory = new ArrayList<>();
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        this.Model = model;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public void setPrice(BigDecimal price) {
        this.Price = price;
    }

    public BigDecimal getBrokerage() {
        return Brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.Brokerage = brokerage;
    }

    String Model;
    BigDecimal Price;
    BigDecimal Brokerage;
    Bitmap Image;
//    String SelectedInventoryStockGroup;
//
//    public String getSelectedInventoryStockGroup() {
//        return SelectedInventoryStockGroup;
//    }
//
//    public void setSelectedInventoryStockGroup(String selectedInventoryStockGroup) {
//        this.SelectedInventoryStockGroup = selectedInventoryStockGroup;
//    }


    public List<Inventory> SelectedInventory;

    public BigDecimal priceAmount() {
        return Price.multiply(Qty);
    }

    public BigDecimal brokerageAmount() {
        return Brokerage.multiply(Qty);
    }

    public BigDecimal noBrokerageAmount() {
        return Price.subtract(Brokerage).multiply(Qty);
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }
}
