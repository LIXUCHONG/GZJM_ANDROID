package com.jimei.k3wise_mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.Goods;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Component.WebserviceLoader;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.KingdeeK3WiseWebServiceHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SalesOrderInterface} interface
 * to handle interaction events.
 * Use the {@link EditGoodsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditGoodsFragment extends HandledFragment/* implements android.support.v4.app.LoaderManager.LoaderCallbacks<Message> */{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String Operation = "param1";
    private static final String CurrentGoods = "param2";
    public static final int ADD = 1;
    public static final int EDIT = 2;
    public static final int DEL = 3;

    // TODO: Rename and change types of parameters
    private int currentOperation;
    private Goods currentGoods;
    private Goods realGoods;

    //    View view =null;
    private TextView viewNumber;
    private TextView viewName;
    private TextView viewColor;
    private EditText viewPrice;
    private EditText viewQty;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDel;
    private ImageView viewImage;


    private SalesOrderInterface salesOrderInterface;

    public EditGoodsFragment() {
        // Required empty public constructor
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
    public static EditGoodsFragment newInstance(int operation, Goods currentGoods) {
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
            currentGoods = (Goods) getArguments().getSerializable(CurrentGoods);

            if (currentOperation == this.EDIT) {
                try {
                    Goods tmpGoods = (Goods) CommonHelper.deepClone(currentGoods);
                    realGoods = currentGoods;
                    currentGoods = tmpGoods;
                    salesOrderInterface.setCurrentGoods(currentGoods);
                    salesOrderInterface.setEditCurrentGoods(realGoods);
                } catch (Exception ex) {
                    return;
                }
            }else{
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
        View view = inflater.inflate(R.layout.fragment_edit_goods, container, false);

        viewNumber = (TextView) view.findViewById(R.id.edit_goods_value_number);
        viewName = (TextView) view.findViewById(R.id.edit_goods_value_name);
        viewColor = (TextView) view.findViewById(R.id.edit_goods_value_color);
        viewPrice = (EditText) view.findViewById(R.id.edit_goods_value_price);
        viewQty = (EditText) view.findViewById(R.id.edit_goods_value_qty);

        viewImage = (ImageView) view.findViewById(R.id.edit_goods_image);
//        getLoaderManager().initLoader(0,null,this);

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
                    salesOrderInterface.editCurrentGoods(realGoods);
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

        setOperationView(currentOperation);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }

//    @Override
//    public Loader<Message> onCreateLoader(int id, Bundle args) {
//        Loader<Message> result=null;
//
//        try {
//            if(id==0) {
//                String method = "GetGoodsImage";
//                JSONObject jsonParas = new JSONObject();
//                jsonParas.put("itemid", currentGoods.getItemID());
//                result = new WebserviceLoader(getActivity(), method, jsonParas);
//            }
//        }catch (Exception ex){
//            ShowDialog.ExceptionDialog(getActivity(),ex.getMessage());
//        }
//
//        return result;
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Message> loader) {
//
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Message> loader, Message data) {
//        if(loader!=null) {
//            switch (data.what) {
//                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
//                    try {
//                        JSONArray jsonArray = new JSONArray(data.obj.toString());
//                        String imageStr=jsonArray.getJSONObject(0).getString("Value");
//                        if (!imageStr.equals("")) {
//                            viewImage.setImageBitmap(getBitmapFromHex(imageStr));
//                        }
////                        viewImage.setImageBitmap(getBitmapFromHex(data.obj.toString()));
//                    } catch (Exception ex) {
//                        ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
//                        return;
//                    }
//                    break;
//                case KingdeeK3WiseWebServiceHelper.INVOKE_NULL:
//                    ShowDialog.WarningDialog(getActivity(), "读取数据失败");
//                    break;
//            }
//        }
//    }
//
//    private Bitmap getBitmapFromHex(String imageStr) {
//        byte[] bytes = Base64.decode(imageStr,Base64.NO_CLOSE);
////        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }

    void bindGoodsInfo() {
        viewNumber.setText(currentGoods.getNumber());
        viewName.setText(currentGoods.getName());
        viewColor.setText(currentGoods.getModel());

        viewPrice.setText(currentGoods.getPrice() == 0 ? "" : Double.toString(currentGoods.getPrice()));
        viewQty.setText(currentGoods.getQty() == 0 ? "" : Double.toString(currentGoods.getQty()));
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

        String price = viewPrice.getText().toString();
        if (price.equals("") || Double.parseDouble(price) <= 0) {
            ShowDialog.WarningDialog(getActivity(), "请输入正确的价格");
            return false;
        }

        String qty = viewQty.getText().toString();
        if (qty.equals("") || Double.parseDouble(qty) <= 0) {
            ShowDialog.WarningDialog(getActivity(), "请输入正确的数量");
            return false;
        }

        currentGoods.setPrice(Double.parseDouble(price));
        currentGoods.setQty(Double.parseDouble(qty));
        return true;
    }


}
