package com.jimei.k3wise_mobile.BO;

import java.math.BigDecimal;

/**
 * Created by lee on 2017/9/6.
 */

public class GoodsRecord extends Goods {
    protected BigDecimal Qty;

    public GoodsRecord(){
        Qty=BigDecimal.ZERO;
    }

    public BigDecimal getQty() {
        return Qty;
    }

    public void setQty(BigDecimal qty) {
        Qty = qty;
    }
}
