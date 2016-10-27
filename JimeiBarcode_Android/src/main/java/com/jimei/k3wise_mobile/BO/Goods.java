package com.jimei.k3wise_mobile.BO;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lee on 2016/9/15.
 */
public class Goods implements Serializable {

    public Goods(){
        SelectedInventory = new ArrayList<>();
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        this.ItemID = itemID;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        this.Model = model;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        this.Number = number;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        this.Price = price;
    }

    int ItemID;
    String Model;
    String Name;
    String Number;
    double Price;
    double Qty;
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

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }

    public ArrayList<Inventory> SelectedInventory;

    public double amountPrice(){
        return Qty*Price;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }
}
