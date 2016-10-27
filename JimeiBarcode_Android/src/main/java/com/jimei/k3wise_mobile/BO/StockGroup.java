package com.jimei.k3wise_mobile.BO;

/**
 * Created by lee on 2016/9/26.
 */

public class StockGroup {
    public StockGroup(int id, String name) {
        ID = id;
        Name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private int ID;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;
}
