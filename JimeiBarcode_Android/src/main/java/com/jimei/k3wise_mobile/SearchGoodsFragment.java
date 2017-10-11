package com.jimei.k3wise_mobile;

//import android.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jimei.k3wise_mobile.BO.Client;
import com.jimei.k3wise_mobile.BO.SaleGoods;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Component.ProgressView;
import com.jimei.k3wise_mobile.Component.WebserviceTask;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by lee on 2016/9/14.
 */
public class SearchGoodsFragment extends HandledFragment {
    private EditText keyInputView;
    private SearchGoodsTask mTask;
    private ProgressView mProgressView;
    private View mSearchGoodsView;
    private ListView mSearchGoodsListView;

    private SalesOrderInterface salesOrderInterface;
    private ArrayList<SaleGoods> goodsList = new ArrayList<>();
    private GetGoodsClientPriceTask getGoodsClientPriceTask;
    private SaleGoods selectedSaleGoods;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_goods;
    }

    @Override
    protected boolean isShowToolBar() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mSearchGoodsView = view.findViewById(R.id.search_goods_form);
        keyInputView = (EditText) view.findViewById(R.id.key_input_edit_text);
        keyInputView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) (getActivity().getSystemService(INPUT_METHOD_SERVICE));
                    imm.hideSoftInputFromWindow(keyInputView.getWindowToken(), 0);
                }
            }
        });

        mProgressView = (ProgressView) view.findViewById(R.id.search_goods_progress);
        mSearchGoodsListView = (ListView) view.findViewById(R.id.searched_goods_lv);

        Button searchBtn = (Button) view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName_ke = keyInputView.getText().toString();
                showProgress(true);
                JSONObject jParas = new JSONObject();
                try {
                    jParas.put("itemName_key", itemName_ke);
                } catch (Exception ex) {
                    ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
                    showProgress(false);
                    return;
                }

                mTask = new SearchGoodsTask(getActivity(), "GetItemInfo", jParas);
                mTask.execute();
            }
        });

        return view;
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
    public void onDetach() {
        if (mTask != null) {
            if (!mTask.isCancelled()) {
                mTask.cancel(true);
            }
        }

        super.onDetach();
    }

    private void showProgress(boolean show) {
        mProgressView.Show(mSearchGoodsView, show);
    }

    private class SearchGoodsTask extends WebserviceTask {
        SearchGoodsTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPostExecute(Message msg) {
            mTask = null;
            showProgress(false);

            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        List<HashMap<String, Object>> goodsListMap = ConvertToGoodsMap((String) msg.obj);

                        if (goodsListMap.size() == 0) {
                            ShowDialog.WarningDialog(getActivity(), "无此编号的相关物料");
                        }
                        bindSearchGoodsList(goodsListMap);
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
                        return;
                    }
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(getActivity(), "读取数据失败");
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_BUSINESS_EXCEPTION:
                    ShowDialog.WarningDialog(getActivity(), msg.obj.toString());
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_EXCEPTION:
                    ShowDialog.ExceptionDialog(getActivity(), msg.obj.toString());
                    break;
            }

            super.onPostExecute(msg);
        }

        private List<HashMap<String, Object>> ConvertToGoodsMap(String jsonStr) throws Exception {
            goodsList.clear();
            List<HashMap<String, Object>> hashMaplist = new ArrayList<HashMap<String, Object>>();

            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                SaleGoods g = new SaleGoods();
                HashMap<String, Object> item = new HashMap<String, Object>();

                g.setId(jsonArray.getJSONObject(i).getInt("itemid"));
                g.setNumber(jsonArray.getJSONObject(i).getString("number"));
                g.setName(jsonArray.getJSONObject(i).getString("name"));
                g.setModel(jsonArray.getJSONObject(i).getString("model"));
                g.setPrice(new BigDecimal(jsonArray.getJSONObject(i).getString("price")));
                goodsList.add(g);

                item.put("Number", g.getNumber());
                item.put("Name", g.getName());
                item.put("Model", g.getModel());
                hashMaplist.add(item);
            }

            return hashMaplist;
        }

        private void bindSearchGoodsList(final List<HashMap<String, Object>> goodsListMap) {
            SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), goodsListMap, R.layout.lv_searched_goods_item,
                    new String[]{"Name", "Model", "Number"},
                    new int[]{R.id.item_name, R.id.item_model, R.id.item_no});

            mSearchGoodsListView.setAdapter(mAdapter);
            mSearchGoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedSaleGoods = new SaleGoods();
                    selectedSaleGoods.setId(goodsList.get(i).getId());
                    selectedSaleGoods.setNumber(goodsList.get(i).getNumber());
                    selectedSaleGoods.setName(goodsList.get(i).getName());
                    selectedSaleGoods.setModel(goodsList.get(i).getModel());

                    /**
                     * TODO:
                     * 是否读取物料默认价格，
                     * 是：读取默认价格
                     * 否：读取最近订单价格
                     */
                    Client client = salesOrderInterface.getSalesOrder().Client;
                    if (salesOrderInterface.getSalesOrder().isGetClientGoodsPrice()) {
                        if (client != null && client.getNumber() != null && !client.getNumber().equals("")) {
                            showEditGoodsFragment(selectedSaleGoods);
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("itemid", selectedGoods.getItemID());
//                                jsonObject.put("custNum", client.getNumber());
//                                getGoodsClientPriceTask=new GetGoodsClientPriceTask(getActivity(),"GetGoodsClientPrice",jsonObject);
//                                getGoodsClientPriceTask.execute();
//                            }catch (Exception ex){
//                                ShowDialog.ExceptionDialog(getActivity(),ex.getMessage());
//                            }
                        } else {
                            selectedSaleGoods.setPrice(BigDecimal.ZERO);
                            showEditGoodsFragment(selectedSaleGoods);
                        }
                    } else {
                        selectedSaleGoods.setPrice(goodsList.get(i).getPrice());
                        showEditGoodsFragment(selectedSaleGoods);
                    }
                }
            });
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }

    private void showEditGoodsFragment(SaleGoods selectedGoods) {
        salesOrderInterface.setCurrentGoods(selectedGoods);
        salesOrderInterface.showEditGoodsFragment(EditGoodsFragment.ADD);
    }

    public class GetGoodsClientPriceTask extends WebserviceTask {

        GetGoodsClientPriceTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected void onPostExecute(final Message msg) {

            getGoodsClientPriceTask = null;
            showProgress(false);

            super.onPostExecute(msg);
            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        getGoodsClientPrice(msg.obj.toString());
                        showEditGoodsFragment(selectedSaleGoods);
                    } catch (Exception ex) {
                        ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
                    }
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                    ShowDialog.WarningDialog(getActivity(), "读取客户价格失败");
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_BUSINESS_EXCEPTION:
                    ShowDialog.WarningDialog(getActivity(), msg.obj.toString());
                    break;
                case KingdeeK3WiseWebServiceHelper.INVOKE_EXCEPTION:
                    ShowDialog.ExceptionDialog(getActivity(), msg.obj.toString());
                    break;
            }

            super.onPostExecute(msg);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            getGoodsClientPriceTask = null;
        }

        private void getGoodsClientPrice(String jsonStr) throws Exception {
            selectedSaleGoods.setPrice(new BigDecimal(jsonStr));
        }
    }
}
