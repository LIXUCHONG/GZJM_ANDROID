package com.jimei.k3wise_mobile.BO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2016/9/26.
 */

public class StockGroupList {
    public static List<StockGroup> Value = new ArrayList<>();

    public static void addStockGroup(int id, String name) {
        Value.add(new StockGroup(id, name));
    }

    public static void clear() {
        Value.clear();
    }

    public static StockGroup getStockGroupByName(String name){
        StockGroup stockGroup=null;

        for (StockGroup sg:Value) {
            if(sg.getName().equals(name)){
                stockGroup = sg;
            }
        }

        return stockGroup;
    }

    public static StockGroup getStockGroupById(int id){
        StockGroup stockGroup=null;

        for (StockGroup sg:Value) {
            if(sg.getID()==id){
                stockGroup = sg;
            }
        }

        return stockGroup;
    }
}