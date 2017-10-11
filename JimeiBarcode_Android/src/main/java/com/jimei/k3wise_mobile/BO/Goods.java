package com.jimei.k3wise_mobile.BO;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by lee on 2017/9/5.
 */

public class Goods implements Serializable {
    protected int Id;
    protected String Number;
    protected String Name;
    protected int ModelId;
    protected int UnitId;
    protected int ColorId;

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

    public int getModelId() {
        return ModelId;
    }

    public void setModelId(int modelId) {
        ModelId = modelId;
    }

    public int getUnitId() {
        return UnitId;
    }

    public void setUnitId(int unitId) {
        UnitId = unitId;
    }

    public int getColorId() {
        return ColorId;
    }

    public void setColorId(int colorId) {
        ColorId = colorId;
    }
}
