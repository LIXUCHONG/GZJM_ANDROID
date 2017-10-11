package com.jimei.k3wise_mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.SaleGoods;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Component.TextViewWatcher;
import com.jimei.k3wise_mobile.Component.WebserviceLoader;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONObject;

import java.math.BigDecimal;

import uk.co.senab.photoview.PhotoView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SalesOrderInterface} interface
 * to handle interaction events.
 * Use the {@link EditGoodsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGoodsFragment extends HandledFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Message> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String Operation = "param1";
    private static final String CurrentGoods = "param2";
    public static final int ADD = 1;
    public static final int EDIT = 2;
    public static final int DEL = 3;

    // TODO: Rename and change types of parameters
    private int currentOperation;
    private SaleGoods currentSaleGoods;
    private SaleGoods realSaleGoods;

    //    View view =null;
    private TextView viewNumber;
    private TextView viewName;
    private TextView viewColor;
    private EditText viewPrice;
    private TextView viewPriceAmount;
    private EditText viewBrokerage;
    private EditText viewBrokerageAmount;
    private EditText viewQty;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDel;
    private PhotoView viewImage;

    private boolean canCalculateBrokerage;
    private boolean canLoadCurrentGoodsQty;

    private SalesOrderInterface salesOrderInterface;

    public EditGoodsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_goods;
    }

    @Override
    protected void setToolbarTitleText() {
        super.getToolbarTitle().setText("确认商品");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param operation    Parameter 1.
     * @param currentGoods Parameter 2.
     * @return A new instance of fragment EditGoodsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditGoodsFragment newInstance(int operation, SaleGoods currentGoods) {
        EditGoodsFragment fragment = new EditGoodsFragment();
        Bundle args = new Bundle();
        args.putInt(Operation, operation);
        args.putSerializable(CurrentGoods, currentGoods);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentOperation = getArguments().getInt(Operation);
            currentSaleGoods = (SaleGoods) getArguments().getSerializable(CurrentGoods);

            if (currentOperation == this.EDIT) {
                try {
                    SaleGoods tmpGoods = (SaleGoods) CommonHelper.deepClone(currentSaleGoods);
                    realSaleGoods = currentSaleGoods;
                    currentSaleGoods = tmpGoods;
                    salesOrderInterface.setCurrentGoods(currentSaleGoods);
                    salesOrderInterface.setEditCurrentGoods(realSaleGoods);
                } catch (Exception ex) {
                    return;
                }
            } else {
                salesOrderInterface.setEditCurrentGoods(null);
            }
        }

        super.onCreate(savedInstanceState);
//        getActivity().findViewById(R.id.tabhostlayout).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);

        viewPriceAmount = (TextView) view.findViewById(R.id.edit_goods_value_priceAmount);

        viewNumber = (TextView) view.findViewById(R.id.edit_goods_value_number);
        viewName = (TextView) view.findViewById(R.id.edit_goods_value_name);
        viewColor = (TextView) view.findViewById(R.id.edit_goods_value_color);
        viewPrice = (EditText) view.findViewById(R.id.edit_goods_value_price);
        viewBrokerage = (EditText) view.findViewById(R.id.edit_goods_value_brokerage);
        viewBrokerageAmount = (EditText) view.findViewById(R.id.edit_goods_value_brokerageAmount);
        viewQty = (EditText) view.findViewById(R.id.edit_goods_value_qty);

        canCalculateBrokerage = true;
        canLoadCurrentGoodsQty = false;

        viewImage = (PhotoView) view.findViewById(R.id.edit_goods_image);
        getLoaderManager().initLoader(1, null, this);

        if (currentOperation == this.ADD) {
            getLoaderManager().initLoader(0, null, this);
        }

        Button btnSelectInventory = (Button) view.findViewById(R.id.select_inventory_select_inventory_btn);
        btnSelectInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salesOrderInterface.showSelectInventoryFragment();
            }
        });

        btnAdd = (Button) view.findViewById(R.id.edit_goods_add_btn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyViewInfo()) {
                    salesOrderInterface.addCurrentGoods();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        btnEdit = (Button) view.findViewById(R.id.edit_goods_edit_btn);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyViewInfo()) {
                    salesOrderInterface.editCurrentGoods(realSaleGoods);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        btnDel = (Button) view.findViewById(R.id.edit_goods_del_btn);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

//        setTextWatcher(true);

        setOperationView(currentOperation);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        setTextWatcher(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTextWatcher(true);
        bindGoodsInfo();
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
        super.onDetach();
        salesOrderInterface = null;
        setTextWatcher(false);
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public Loader<Message> onCreateLoader(int id, Bundle args) {
        final WebserviceLoader loader = new WebserviceLoader(getActivity());
        currentLoaderId = id;
        try {
            if (id == 0) {
                String method = "GetGoodsClientPrice";
                JSONObject jsonParas = new JSONObject();
                jsonParas.put("itemid", currentSaleGoods.getId());
                jsonParas.put("custNum", salesOrderInterface.getSalesOrder().Client.getNumber());
                loader.setWebMethod(method);
                loader.setMethodParas(jsonParas);
            }
            if (id == 1) {
                // TODO:Get good image
                String method = "GetGoodsImage";
                JSONObject jsonParas = new JSONObject();
                jsonParas.put("itemid", currentSaleGoods.getId());
                loader.setWebMethod(method);
                loader.setMethodParas(jsonParas);
            }
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
        }

        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ShowDialog.showLoaderProgressDialog(getActivity(), loader);
        }

        return loader;
    }

    @Override
    public void onLoaderReset(Loader<Message> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Message> loader, Message data) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        try {
            if (loader != null && data != null) {
                switch (data.what) {
                    case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                        int loaderId = loader.getId();
                        if (loaderId == 0) {
                            currentSaleGoods.setPrice(new BigDecimal(data.obj.toString()));
                            bindGoodsInfo();
                        } else if (loaderId == 1) {
                            String imgData = data.obj.toString();
                            if (!imgData.equals("")) {
                                viewImage.setImageBitmap(getBitmapFromHex(imgData));
                            }
                        }
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
                        ShowDialog.WarningDialog(getActivity(), "读取数据失败");
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_EXCEPTION:
                        ShowDialog.ExceptionDialog(getActivity(), data.obj.toString());
                        break;
                    case KingdeeK3WiseWebServiceHelper.INVOKE_BUSINESS_EXCEPTION:
                        ShowDialog.WarningDialog(getActivity(), data.obj.toString());
                        break;
                }
            }
        } catch (Exception ex) {
            ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
        } finally {
            getLoaderManager().destroyLoader(loader.getId());
        }
    }

    private Bitmap getBitmapFromHex(String imageStr) {
        byte[] bytes = Base64.decode(imageStr, Base64.NO_CLOSE);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    void bindGoodsInfo() {
        viewNumber.setText(currentSaleGoods.getNumber());
        viewName.setText(currentSaleGoods.getName());
        viewColor.setText(currentSaleGoods.getModel());

        if (viewPrice.getText().toString().equals("")) {
            viewPrice.setText(currentSaleGoods.getPrice().compareTo(BigDecimal.ZERO) == 0 ? "" : currentSaleGoods.getPrice().toString());
        }

        if (salesOrderInterface.getFragmentResult()) {
            salesOrderInterface.setFragmentResult(false);
            viewQty.setText(currentSaleGoods.getQty().compareTo(BigDecimal.ZERO) == 0 ? "" : currentSaleGoods.getQty().toString());
        }else {
            if (viewQty.getText().toString().equals("")) {
                viewQty.setText(currentSaleGoods.getQty().compareTo(BigDecimal.ZERO) == 0 ? "" : currentSaleGoods.getQty().toString());
            }
        }

        if (viewBrokerage.getText().toString().equals("")) {
            viewBrokerage.setText(currentSaleGoods.getBrokerage().compareTo(BigDecimal.ZERO) == 0 ? "" : currentSaleGoods.getBrokerage().toString());
        }

        canLoadCurrentGoodsQty = false;
    }

    void setOperationView(int operation) {
        switch (operation) {
            case ADD:
                btnAdd.setVisibility(View.VISIBLE);
                break;
            case EDIT:
                btnEdit.setVisibility(View.VISIBLE);
                break;
            case DEL:
                btnDel.setVisibility(View.VISIBLE);
                break;
        }
    }

    public boolean verifyViewInfo() {

        String priceStr = viewPrice.getText().toString();
        if (priceStr.equals("") || new BigDecimal(priceStr).compareTo(BigDecimal.ZERO) != 1) {
            ShowDialog.WarningDialog(getActivity(), "请输入正确的价格");
            return false;
        }

        String qtyStr = viewQty.getText().toString();
        if (qtyStr.equals("") || new BigDecimal(qtyStr).compareTo(BigDecimal.ZERO) != 1) {
            ShowDialog.WarningDialog(getActivity(), "请输入正确的数量");
            return false;
        }

        String brokerageStr = viewBrokerage.getText().toString();
        if (brokerageStr.equals("") || new BigDecimal(brokerageStr).compareTo(BigDecimal.ZERO) == -1) {
            brokerageStr = "0";
        }

        if (new BigDecimal(priceStr).compareTo(new BigDecimal(brokerageStr)) != 1) {
            ShowDialog.WarningDialog(getActivity(), "佣金单价必须小于商品单价");
            return false;
        }

        currentSaleGoods.setPrice(new BigDecimal(priceStr));
        currentSaleGoods.setBrokerage(new BigDecimal(brokerageStr));
        currentSaleGoods.setQty(new BigDecimal(qtyStr));
        return true;
    }

    private TextViewWatcher priceWatcher=new TextViewWatcher(){
        @Override
        public void afterTextChanged(Editable s) {
            try {
                if(s.toString().equals("")){
                    viewPriceAmount.setText("");
                }else {
                    BigDecimal price = new BigDecimal(s.toString());
                    BigDecimal qty = new BigDecimal(viewQty.getText().toString());
                    viewPriceAmount.setText(price.multiply(qty).toString());
                }
            } catch (Exception ex) {
                return;
            }
        }
    };

    private TextViewWatcher qtyWatcher=new TextViewWatcher(){
        @Override
        public void afterTextChanged(Editable s) {
            try {
                if(s.toString().equals("")){
                    viewPriceAmount.setText("");
                    viewBrokerageAmount.setText("");
                }else {
                    BigDecimal price = new BigDecimal(viewPrice.getText().toString());
                    BigDecimal qty = new BigDecimal(s.toString());
                    viewPriceAmount.setText(price.multiply(qty).toString());

                    BigDecimal brokerage = new BigDecimal(viewBrokerage.getText().toString());
                    viewBrokerageAmount.setText(brokerage.multiply(qty).toString());
                }
            } catch (Exception ex) {
                return;
            }
        }
    };

    private TextViewWatcher brokerageWatcher=new TextViewWatcher(){
        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (canCalculateBrokerage) {
                    canCalculateBrokerage = false;
                    if(s.toString().equals("")){
                        viewBrokerageAmount.setText("");
                    }else {
                        BigDecimal brokerage = new BigDecimal(s.toString());
                        BigDecimal qty = new BigDecimal(viewQty.getText().toString());
                        viewBrokerageAmount.setText(brokerage.multiply(qty).toString());
                    }
                }
            } catch (Exception ex) {
                return;
            } finally {
                canCalculateBrokerage = true;
            }
        }
    };

    private TextViewWatcher brokerageAmountWatcher=new TextViewWatcher(){
        @Override
        public void afterTextChanged(Editable s) {
            try {
                if (canCalculateBrokerage) {
                    canCalculateBrokerage = false;
                    BigDecimal qty = new BigDecimal(viewQty.getText().toString());
                    BigDecimal brokerageAmount = new BigDecimal(s.toString());
                    viewBrokerage.setText(brokerageAmount.divide(qty, 2, BigDecimal.ROUND_HALF_EVEN).toString());

                }
            } catch (Exception ex) {
                return;
            } finally {
                canCalculateBrokerage = true;
            }
        }
    };

    private void setTextWatcher(boolean isSet){
        if(isSet) {
            setTextWatcher(false);
            viewPrice.addTextChangedListener(priceWatcher);
            viewQty.addTextChangedListener(qtyWatcher);
            viewBrokerage.addTextChangedListener(brokerageWatcher);
            viewBrokerageAmount.addTextChangedListener(brokerageAmountWatcher);
        }
        else {
            viewPrice.removeTextChangedListener(priceWatcher);
            viewQty.removeTextChangedListener(qtyWatcher);
            viewBrokerage.removeTextChangedListener(brokerageWatcher);
            viewBrokerageAmount.removeTextChangedListener(brokerageAmountWatcher);
        }
    }
}
