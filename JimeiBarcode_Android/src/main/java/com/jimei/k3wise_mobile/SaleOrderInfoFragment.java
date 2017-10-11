package com.jimei.k3wise_mobile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.Properties.PayType;
import com.jimei.k3wise_mobile.BO.Properties.SaleType;
import com.jimei.k3wise_mobile.BO.Properties.ShippingType;
import com.jimei.k3wise_mobile.BO.SalesOrder;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleOrderInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleOrderInfoFragment extends HandledFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SalesOrderInterface salesOrderInterface;

    private View rootView=null;

    private View formSaleOrderInfo;
    private ListView lvSelectOrderInfo;
    private TextView tvClient;

    private int currentViewId_SaleOrderInfo;

    public SaleOrderInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaleOrderInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaleOrderInfoFragment newInstance(String param1, String param2) {
        SaleOrderInfoFragment fragment = new SaleOrderInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SalesOrderInterface) {
            salesOrderInterface = (SalesOrderInterface) context;
            salesOrderInterface.setSaleOrderInfoFragment(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SalesOrderInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public int getLayoutId(){
        return R.layout.fragment_sale_order_info;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(rootView==null) {
            rootView = super.onCreateView(inflater,container,savedInstanceState);

            formSaleOrderInfo=rootView.findViewById(R.id.sales_order_info_form);
            lvSelectOrderInfo =(ListView) rootView.findViewById(R.id.sales_order_info_lv);
            tvClient=(TextView) rootView.findViewById(R.id.tv_sales_order_info_client);

            View clientView=rootView.findViewById(R.id.layout_sales_order_info_client);
            clientView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salesOrderInterface.showSelectClientFragment();
                }
            });

            View shippingTypeView=rootView.findViewById(R.id.layout_sales_order_info_shipping_type);
            shippingTypeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentViewId_SaleOrderInfo=R.id.tv_sales_order_info_shipping_type;
                    showSaleOrderInfoList(currentViewId_SaleOrderInfo);
                }
            });

            View payTypeView=rootView.findViewById(R.id.layout_sales_order_info_pay_type);
            payTypeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentViewId_SaleOrderInfo=R.id.tv_sales_order_info_pay_type;
                    showSaleOrderInfoList(currentViewId_SaleOrderInfo);
                }
            });

            View saleTypeView=rootView.findViewById(R.id.layout_sales_order_info_sale_type);
            saleTypeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentViewId_SaleOrderInfo=R.id.tv_sales_order_info_sale_type;
                    showSaleOrderInfoList(currentViewId_SaleOrderInfo);
                }
            });

            View viewSubmitOrder=rootView.findViewById(R.id.btn_sales_order_submit);
            viewSubmitOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(verifyOrderInfoView()) {
                        salesOrderInterface.submitSalesOrder();
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SalesOrder order=salesOrderInterface.getSalesOrder();
        if(order!=null&&order.Client!=null){
            tvClient.setText(order.Client.getName());
        }
    }

    @Override
    public boolean onBackPressed() {
        if (lvSelectOrderInfo.getVisibility() == View.VISIBLE) {
            formSaleOrderInfo.setVisibility(View.VISIBLE);
            lvSelectOrderInfo.setVisibility(View.GONE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isShowToolBar(){
        return false;
    }

    void showSaleOrderInfoList(int tv_Id_OrderInfo){
        try {
            bindSaleOrderInfoList(tv_Id_OrderInfo);
            formSaleOrderInfo.setVisibility(View.GONE);
            lvSelectOrderInfo.setVisibility(View.VISIBLE);
        }catch (Exception ex) {
            ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
        }
    }

    private List<?> getSaleOrderInfoList(int tv_Id_OrderInfo){
        switch (tv_Id_OrderInfo){
            case R.id.tv_sales_order_info_shipping_type:
                return salesOrderInterface.returnSaleOrderInfoList(ShippingType.class);
            case R.id.tv_sales_order_info_pay_type:
                return salesOrderInterface.returnSaleOrderInfoList(PayType.class);
            case R.id.tv_sales_order_info_sale_type:
                return salesOrderInterface.returnSaleOrderInfoList(SaleType.class);
            default:
                return null;
        }
    }

    private List<Map<String, Object>> ConvertStockGroupListToHashMap(int tv_Id_OrderInfo) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<?> saleOrderInfoList=getSaleOrderInfoList(tv_Id_OrderInfo);

        if(saleOrderInfoList!=null) {
            for (int i = 0; i < saleOrderInfoList.size(); i++) {
                Map<String, Object> item = CommonHelper.getObjMap(saleOrderInfoList.get(i));
                mapList.add(item);
            }
        }

        return mapList;
    }

    void setSaleOrderInfo(int tv_Id_OrderInfo,int selectedIndex){
        switch (tv_Id_OrderInfo){
            case R.id.tv_sales_order_info_shipping_type:
                salesOrderInterface.setSaleOrderInfo(ShippingType.class,selectedIndex);
                break;
            case R.id.tv_sales_order_info_pay_type:
                salesOrderInterface.setSaleOrderInfo(PayType.class,selectedIndex);
                break;
            case R.id.tv_sales_order_info_sale_type:
                salesOrderInterface.setSaleOrderInfo(SaleType.class,selectedIndex);
                break;
        }
    }

    void bindSaleOrderInfoList(final int tv_Id_OrderInfo) throws Exception {
        SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), ConvertStockGroupListToHashMap(tv_Id_OrderInfo), R.layout.lv_sale_order_info_item,
                new String[]{"Id","Name"},
                new int[]{R.id.item_sale_order_info_id,R.id.item_sale_order_info_name});

        lvSelectOrderInfo.setAdapter(mAdapter);
        lvSelectOrderInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setSaleOrderInfo(tv_Id_OrderInfo,i);

                TextView nameView = (TextView) view.findViewById(R.id.item_sale_order_info_name);
                TextView tvOrderInfo=(TextView)rootView.findViewById(tv_Id_OrderInfo);
                tvOrderInfo.setText(nameView.getText());

                formSaleOrderInfo.setVisibility(View.VISIBLE);
                lvSelectOrderInfo.setVisibility(View.GONE);

//                getInventoryList(StockGroupList.getStockGroupById(Integer.parseInt(idView.getText().toString())), currentGoods);
            }
        });
    }

    public boolean verifyOrderInfoView(){

        SalesOrder order=salesOrderInterface.getSalesOrder();
        if(order.Client==null||order.Client.getNumber()==null||order.Client.getNumber().equals("")){
            ShowDialog.WarningDialog(getActivity(), "请选择正确的【购货单位】");
            return false;
        }

        if(order.ShippingType==null||order.ShippingType.getId()==0){
            ShowDialog.WarningDialog(getActivity(), "请选择正确的【订单执行】方式");
            return false;
        }
        if(order.PayType==null||order.PayType.getId()==0){
            ShowDialog.WarningDialog(getActivity(), "请选择正确的【结算方式】");
            return false;
        }
        if(order.SaleType==null||order.SaleType.getId()==0){
            ShowDialog.WarningDialog(getActivity(), "请选择正确的【销售方式】");
            return false;
        }
        if(!salesOrderInterface.verifySalesOrderInfo()){
            return false;
        }

        salesOrderInterface.getSalesOrder().setSalesGoods(salesOrderInterface.returnSaleGoodsList());

        String remark=((AutoCompleteTextView) rootView.findViewById(R.id.et_sales_order_info_remark)).getText().toString();
        String amount1 = ((EditText) rootView.findViewById(R.id.et_sales_order_info_amount1)).getText().toString();
        String amount2 = ((EditText) rootView.findViewById(R.id.et_sales_order_info_amount2)).getText().toString();
        String amount3 = ((EditText) rootView.findViewById(R.id.et_sales_order_info_amount3)).getText().toString();
        salesOrderInterface.getSalesOrder().setRemark(remark);
        if (!amount1.equals("")) {
            salesOrderInterface.getSalesOrder().setDyeFee(Double.parseDouble(amount1));
        }
        if (!amount2.equals("")) {
            salesOrderInterface.getSalesOrder().setOtherFee(Double.parseDouble(amount2));
        }
        if (!amount3.equals("")) {
            salesOrderInterface.getSalesOrder().setEarnestMoney(Double.parseDouble(amount3));
        }

        return true;
    }


}
