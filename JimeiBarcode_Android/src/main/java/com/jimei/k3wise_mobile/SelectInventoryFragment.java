package com.jimei.k3wise_mobile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.Goods;
import com.jimei.k3wise_mobile.BO.Inventory;
import com.jimei.k3wise_mobile.BO.StockGroup;
import com.jimei.k3wise_mobile.BO.StockGroupList;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Component.ProgressView;
import com.jimei.k3wise_mobile.Component.WebserviceTask;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectInventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectInventoryFragment extends HandledFragment {
    // TODO: Rename parameter arguments, choose names that match

    WebserviceTask mTask;

    private View layoutSelectInventory;
    private ListView lvSelectStockGroup;
    private TextView tvSelectedStockGroup;
    private ProgressView mProgressView;
    private ListView lvSelectInventory;
    private TextView tvStockQty;
    private TextView tvSelectStockQty;
    private TextView tvAllSelectStockQty;

    Goods currentGoods;
    Goods edit_currentGoods;
    private List<Inventory> InventoryList;
    private boolean hasInventory;

    private SalesOrderInterface salesOrderInterface;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CURRENT_GOODS = "CurrentGoods";
    private static final String ARG_EDIT_CURRENT_GOODS = "EditCurrentGoods";

    // TODO: Rename and change types of parameters


    public SelectInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_inventory;
    }

    @Override
    protected void setToolbarTitleText() {
        super.getToolbarTitle().setText("选择库存");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentGoods Parameter 1.
     * @return A new instance of fragment SelectInventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectInventoryFragment newInstance(Goods currentGoods,Goods editCurrentGoods) {
        SelectInventoryFragment fragment = new SelectInventoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CURRENT_GOODS, currentGoods);
        args.putSerializable(ARG_EDIT_CURRENT_GOODS, editCurrentGoods);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SalesOrderInterface) {
            salesOrderInterface = (SalesOrderInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SalesOrderInterface");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                currentGoods = (Goods) getArguments().getSerializable(ARG_CURRENT_GOODS);
                edit_currentGoods=(Goods) getArguments().getSerializable(ARG_EDIT_CURRENT_GOODS);
                InventoryList = (List<Inventory>) (CommonHelper.deepClone(currentGoods.SelectedInventory));
            }
        }catch (Exception ex){
            ShowDialog.ExceptionDialog(getActivity(), ex.getMessage(), new Runnable() {
                @Override
                public void run() {
                    popFragment();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        layoutSelectInventory = view.findViewById(R.id.select_inventory_layout);
        lvSelectStockGroup = (ListView) view.findViewById(R.id.select_stock_group_lv);
        tvSelectedStockGroup = (TextView) view.findViewById(R.id.select_inventory_value_selected_stock);
        mProgressView = (ProgressView) view.findViewById(R.id.select_inventory_progress);
        lvSelectInventory = (ListView) view.findViewById(R.id.select_inventory_lv);
        tvStockQty = (TextView) view.findViewById(R.id.select_inventory_value_stock_qty);
        tvSelectStockQty = (TextView) view.findViewById(R.id.select_inventory_value_stock_qty_selected);
        tvAllSelectStockQty = (TextView) view.findViewById(R.id.select_inventory_value_stock_qty_all_select);

        View btnSelectStockGroup = view.findViewById(R.id.select_inventory_select_stock_btn);
        btnSelectStockGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bindStockGroupList();
                    layoutSelectInventory.setVisibility(View.GONE);
                    lvSelectStockGroup.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
                }
            }
        });

        View btnSaveSelectedInventory = view.findViewById(R.id.select_inventory_save_inventory_btn);
        btnSaveSelectedInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                currentGoods.SelectedInventory = new ArrayList<>();
//                if (InventoryList != null && InventoryList.size() > 0) {
//                    for (Inventory inv : InventoryList) {
//                        if (inv.isSelected()) {
//                            currentGoods.SelectedInventory.add(inv);
//                        }
//                    }
//                }
                currentGoods.SelectedInventory =new ArrayList<>(InventoryList);

                currentGoods.setQty(Double.parseDouble(tvAllSelectStockQty.getText().toString()));
                currentGoods.setPrice(0);

//                String selectedStockGroupName = tvSelectedStockGroup.getText().toString();
//                if (currentGoods.getQty() > 0) {
//                    if (StockGroupList.getStockGroupByName(selectedStockGroupName) != null) {
//                        currentGoods.setSelectedInventoryStockGroup(selectedStockGroupName);
//                    }
//                } else {
//                    currentGoods.setSelectedInventoryStockGroup("");
//                }

                salesOrderInterface.setCurrentGoods(currentGoods);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        bindInventoryList(InventoryList);
        tvAllSelectStockQty.setText(Double.toString(sumSelectedQty(InventoryList)));
    }

    @Override
    public void onDetach() {
        if (mTask != null) {
            if (!mTask.isCancelled()) {
                mTask.cancel(true);
            }
        }

        super.onDetach();
    }

    public boolean onBackPressed() {
        if (lvSelectStockGroup.getVisibility() == View.VISIBLE) {
            layoutSelectInventory.setVisibility(View.VISIBLE);
            lvSelectStockGroup.setVisibility(View.GONE);
            return false;
        } else {
            cancelInventorySelectedChange();
            return true;
        }
    }

    private void cancelInventorySelectedChange() {
        for (Inventory inv : currentGoods.SelectedInventory) {
            inv.setSelected(true);
        }
    }

    private void getInventoryList(StockGroup stockGroup, Goods goods) {
        JSONObject jParas = new JSONObject();
        try {
            jParas.put("itemid", currentGoods.getItemID());
            jParas.put("stockparentid", stockGroup.getID());
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
            return;
        }

        showProgress(true);
        mTask = new ShowGoodsInventoryTask(getActivity(), "GetItemInventoryInStock", jParas);
        mTask.execute();
    }

    private List<Inventory> getAllSelectedInventoryList() {
        List<Goods> goodsList = salesOrderInterface.returnSaleGoodsList();

        List<Inventory> allSelected = new ArrayList<>();

        if (goodsList != null) {
            for (Goods goods : goodsList) {
                if (goods != edit_currentGoods) {
                    allSelected.addAll(goods.SelectedInventory);
                }
            }
        }

        return allSelected;
    }

    private List<Inventory> exceptAllSelectedInventoryList(List<Inventory> invList) {
        List<Inventory> exceptedSelected = new ArrayList<>();
        List<Inventory> allSelected = getAllSelectedInventoryList();
        for (Inventory inv : invList) {
            boolean isSelected = false;
            for (Inventory invSelected : allSelected) {
                if (inv.getStockGroupName().equals(invSelected.getStockGroupName())
                        &&inv.getBatchNo().equals(invSelected.getBatchNo())
                        && inv.getStockName().equals(invSelected.getStockName())
                        && inv.getPlaceName().equals(invSelected.getPlaceName())) {
                    isSelected = true;
                    break;
                }
            }

            if (!isSelected) {
                exceptedSelected.add(inv);
            }
        }

        tvStockQty.setText(Double.toString(sumStockQty(exceptedSelected)));

        return exceptedSelected;
    }

    void bindStockGroupList() throws Exception {
        SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), ConvertStockGroupListToHashMap(), R.layout.lv_select_stock_group_item,
                new String[]{"SGId", "SGName","SGSelectedQty"},
                new int[]{R.id.item_stock_group_id, R.id.item_stock_group_name,R.id.item_stock_group_qty});

        lvSelectStockGroup.setAdapter(mAdapter);
        lvSelectStockGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView idView = (TextView) view.findViewById(R.id.item_stock_group_id);
                TextView nameView = (TextView) view.findViewById(R.id.item_stock_group_name);
                tvSelectedStockGroup.setText(nameView.getText());
                layoutSelectInventory.setVisibility(View.VISIBLE);
                lvSelectStockGroup.setVisibility(View.GONE);

                getInventoryList(StockGroupList.getStockGroupById(Integer.parseInt(idView.getText().toString())), currentGoods);
            }
        });
    }

    private List<HashMap<String, Object>> ConvertStockGroupListToHashMap() throws Exception {
        List<HashMap<String, Object>> hashMaplist = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < StockGroupList.Value.size(); i++) {
            int id=StockGroupList.Value.get(i).getID();
            String name=StockGroupList.Value.get(i).getName();
            HashMap<String, Object> item = new HashMap<>();
            item.put("SGId", id);
            item.put("SGName", name);
            item.put("SGSelectedQty", getStockGroupSelectedQty(name));
            hashMaplist.add(item);
        }

        return hashMaplist;
    }

    private double getStockGroupSelectedQty(String stockGroupName){
        double result=0;
        for (Inventory inv:InventoryList) {
            if(inv.getStockGroupName().equals(stockGroupName)){
                result+=inv.getQty();
            }
        }
        return result;
    }

    private List<Map<String, Object>> conventInvListToMapList(List<Inventory> invList) {

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        if (invList != null) {
            for (Inventory inv : invList) {
                mapList.add(CommonHelper.getObjMap(inv));
            }
        }
        return mapList;
    }

    private void bindInventoryList(final List<Inventory> invList) {

        matchSelectedInventory(invList);
        List<Map<String, Object>> invMapList = conventInvListToMapList(invList);

        SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), invMapList, R.layout.lv_select_inventory_item,
                new String[]{"Selected", "BatchNo", "StockName", "PlaceName", "Qty"},
                new int[]{R.id.inventory_selected, R.id.inventory_batchno, R.id.inventory_stock, R.id.inventory_place, R.id.inventory_qty});

        lvSelectInventory.setAdapter(mAdapter);
        lvSelectInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.inventory_selected);
                checkBox.setChecked(!checkBox.isChecked());
                invList.get(i).setSelected(!invList.get(i).isSelected());

                double invQty = invList.get(i).getQty();

                String selectedQtyStr = tvSelectStockQty.getText().toString();
                double selectedQty = (selectedQtyStr.equals("") ? 0 : Double.parseDouble(selectedQtyStr));
                String allSelectedQtyStr = tvAllSelectStockQty.getText().toString();
                double allSelectedQty = (allSelectedQtyStr.equals("") ? 0 : Double.parseDouble(allSelectedQtyStr));

                if (checkBox.isChecked()) {
                    selectedQty += invQty;
                    allSelectedQty+= invQty;
                    addSelectedInventory(invList.get(i));
                } else {
                    selectedQty -= invQty;
                    allSelectedQty-= invQty;
                    removeSelectedInventory(invList.get(i));
                }

                tvSelectStockQty.setText(Double.toString(selectedQty));
                tvAllSelectStockQty.setText(Double.toString(allSelectedQty));
            }
        });

        tvSelectStockQty.setText(Double.toString(sumSelectedQty(invList)));
    }

    private double sumSelectedQty(List<Inventory> invList) {
        double sumQty = 0;
        for (Inventory inv : invList) {
            if (inv.isSelected()) {
                sumQty += inv.getQty();
            }
        }
        return sumQty;
    }

    private double sumStockQty(List<Inventory> invList) {
        double sumQty = 0;
        for (Inventory inv : invList) {
            sumQty += inv.getQty();
        }
        return sumQty;
    }

    private void matchSelectedInventory(List<Inventory> invList) {
        for (Inventory inv : invList) {
            for (Inventory inv1 : InventoryList) {
                if (inv.getStockGroupName().equals(inv1.getStockGroupName())
                        &&inv.getBatchNo().equals(inv1.getBatchNo())
                        && inv.getStockName().equals(inv1.getStockName())
                        && inv.getPlaceName().equals(inv1.getPlaceName())) {
                    inv.setSelected(true);
                }
            }
        }
    }

    private void addSelectedInventory(Inventory inventory){
        InventoryList.add(inventory);
    }

    private void removeSelectedInventory(Inventory inventory){
        for (Inventory inv:InventoryList) {
            if(inv.getStockGroupName().equals(inventory.getStockGroupName())
                    &&inv.getBatchNo().equals(inventory.getBatchNo())
                    &&inv.getStockName().equals(inventory.getStockName())
                    &&inv.getPlaceName().equals(inventory.getPlaceName())){
                InventoryList.remove(inv);
                break;
            }
        }
    }

    private void showProgress(boolean show) {
        mProgressView.Show(lvSelectInventory, show);
    }

    private class ShowGoodsInventoryTask extends WebserviceTask {
        ShowGoodsInventoryTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPostExecute(Message msg) {
            mTask = null;
            showProgress(false);

            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        List<Inventory> getInventoryList=ConvertToInventoryList((String) msg.obj);
                        hasInventory = (getInventoryList.size() > 0);
                        getInventoryList = exceptAllSelectedInventoryList(getInventoryList);
//                        InventoryList = ConvertToInventoryList((String) msg.obj);
//                        hasInventory = (InventoryList.size() > 0);
//                        InventoryList = exceptAllSelectedInventoryList(InventoryList);

                        if (getInventoryList.size() == 0) {
                            if (!hasInventory) {
                                ShowDialog.WarningDialog(getActivity(), "仓库无此编号的物料库存");
                            } else {
                                ShowDialog.WarningDialog(getActivity(), "物料库存已被[销售清单]预选");
                            }
                        }

                        bindInventoryList(getInventoryList);
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
                        return;
                    }
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(getActivity(), "读取数据失败");
                    break;
            }

            super.onPostExecute(msg);
        }

        private List<Inventory> ConvertToInventoryList(String jsonStr) throws Exception {

            List<Inventory> invList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArray.length(); i++) {

                Inventory itemInv = new Inventory();
                JSONArray itemJsonArray = new JSONArray(jsonArray.getJSONArray(i).toString());

                for (int j = 0; j < itemJsonArray.length(); j++) {

                    String key = itemJsonArray.getJSONObject(j).getString("Key");
                    switch (key) {
                        case "BatchNo":
                            itemInv.setBatchNo(itemJsonArray.getJSONObject(j).getString("Value"));
                            break;
                        case "StockName":
                            itemInv.setStockName(itemJsonArray.getJSONObject(j).getString("Value"));
                            break;
                        case "PlaceName":
                            itemInv.setPlaceName(itemJsonArray.getJSONObject(j).getString("Value"));
                            break;
                        case "Qty":
                            itemInv.setQty(itemJsonArray.getJSONObject(j).getDouble("Value"));
                            break;
                    }
                    itemInv.setStockGroupName(tvSelectedStockGroup.getText().toString());
                }

                invList.add(itemInv);
            }

            return invList;
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }
}
