package com.jimei.k3wise_mobile.BO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lee on 2017/9/4.
 */

public class DocumentBill {
    protected int Id;
    protected String Number;
    protected int OperatorId;
    protected String OperatorNumber;
    protected String OperatorName;
    protected Date OperateDate;
    protected int OrganizationId;
    protected String OrganizationNumber;
    protected String OrganizationName;
    protected List<GoodsRecord> GoodsRecords;

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
        this.Number = number;
    }

    public int getOperatorId() {
        return OperatorId;
    }

    public void setOperatorId(int operatorId) {
        OperatorId = operatorId;
    }

    public String getOperatorNumber() {
        return OperatorNumber;
    }

    public void setOperatorNumber(String operatorNumber) {
        OperatorNumber = operatorNumber;
    }

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public Date getOperateDate() {
        return OperateDate;
    }

    public void setOperateDate(Date operateDate) {
        OperateDate = operateDate;
    }

    public int getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(int organization) {
        OrganizationId = organization;
    }

    public String getOrganizationNumber() {
        return OrganizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        OrganizationNumber = organizationNumber;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public List<GoodsRecord> getGoodsRecords() {
        return GoodsRecords == null ? new ArrayList<GoodsRecord>() : GoodsRecords;
    }

    public void setGoodsRecords(List<GoodsRecord> goodsRecords) {
        GoodsRecords = goodsRecords;
    }
}
