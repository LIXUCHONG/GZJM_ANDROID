package com.jimei.k3wise_mobile.BO;

/**
 * Created by lee on 2016/10/2.
 */

public class Client {
    int Id;
    String Number;
    String Name;
    int ParentId;
    boolean Detail;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isDetail() {
        return Detail;
    }

    public void setDetail(boolean detail) {
        Detail = detail;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int parentId) {
        ParentId = parentId;
    }
}
