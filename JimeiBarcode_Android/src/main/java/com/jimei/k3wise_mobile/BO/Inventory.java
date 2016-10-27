package com.jimei.k3wise_mobile.BO;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by lee on 2016/9/24.
 */

public class Inventory implements Serializable {

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    String BatchNo;

    public String getStockPlaceName() {
        return StockPlaceName;
    }

    public void setStockPlaceName(String stockPlaceName) {
        StockPlaceName = stockPlaceName;
    }

    String StockPlaceName;

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }

    double Qty;

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    boolean Selected;

    String StockName;
    String PlaceName;
    int StockID;
    int PlaceID;
    String StockGroupName;

    public int getPlaceID() {
        return PlaceID;
    }

    public void setPlaceID(int placeID) {
        PlaceID = placeID;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public int getStockID() {
        return StockID;
    }

    public void setStockID(int stockID) {
        StockID = stockID;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public String getStockGroupName() {
        return StockGroupName;
    }

    public void setStockGroupName(String stockGroupName) {
        StockGroupName = stockGroupName;
    }
}
