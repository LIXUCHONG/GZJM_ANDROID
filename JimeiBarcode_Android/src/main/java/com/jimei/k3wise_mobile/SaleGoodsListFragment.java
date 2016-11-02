package com.jimei.k3wise_mobile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.Goods;
import com.jimei.k3wise_mobile.Component.HandledFragment;
import com.jimei.k3wise_mobile.Interface.SalesOrderInterface;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaleGoodsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaleGoodsListFragment extends HandledFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GOODS_LIST = "goods_list";

    // TODO: Rename and change types of parameters
    private List<Goods> goodsList;

    // TODO: 保留View引用
    View rootView = null;
    ListView goodsListView;
    TextView sumQtyView;
    TextView sumPriceView;

    // TODO: 变量
    private SalesOrderInterface salesOrderInterface;

    public SaleGoodsListFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sale_goods_list;
    }

    @Override
    protected boolean isShowToolBar() {
        return false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param goodsList Parameter 1.
     * @return A new instance of fragment SaleGoodsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaleGoodsListFragment newInstance(ArrayList<Goods> goodsList) {
        SaleGoodsListFragment fragment = new SaleGoodsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(GOODS_LIST, goodsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SalesOrderInterface) {
            salesOrderInterface = (SalesOrderInterface) context;
            salesOrderInterface.setSaleGoodsListFragment(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SalesOrderInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goodsList = (List<Goods>) getArguments().getSerializable(GOODS_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = super.onCreateView(inflater,container,savedInstanceState);

            goodsListView = (ListView) rootView.findViewById(R.id.sale_goods_list_lv);
            sumQtyView = (TextView) rootView.findViewById(R.id.sale_goods_list_sum_qty);
            sumPriceView = (TextView) rootView.findViewById(R.id.sale_goods_list_sum_price);
        }

        goodsList = salesOrderInterface.returnSaleGoodsList();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindGoodsList(goodsList);
    }

    private List<Map<String, Object>> conventGoodsListToMapList(List<Goods> goodsList) {

        List<Map<String, Object>> mapList = new ArrayList<>();

        if (goodsList != null) {
            for (Goods goods : goodsList) {
                Map<String, Object> map = CommonHelper.getObjMap(goods);
                map.put("sumPrice", goods.amountPrice());
                mapList.add(map);
            }
        }
        return mapList;
    }

    private void bindGoodsList(final List<Goods> goodsList) {

        List<Map<String, Object>> goodsMapList = conventGoodsListToMapList(goodsList);

        SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), goodsMapList, R.layout.lv_sale_goods_list_item,
                new String[]{"Number", "Name", "Model", "Price", "Qty", "sumPrice"},
                new int[]{R.id.item_sale_goods_list_no, R.id.item_sale_goods_list_name, R.id.item_sale_goods_list_model, R.id.item_sale_goods_list_price, R.id.item_sale_goods_list_qty, R.id.item_sale_goods_list_amount});

        goodsListView.setAdapter(mAdapter);
        goodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final TextView delView = (TextView) view.findViewById(R.id.item_sale_goods_list_del);
                final TextView editView = (TextView) view.findViewById(R.id.item_sale_goods_list_edit);
                final TextView cancelView = (TextView) view.findViewById(R.id.item_sale_goods_list_cancel_edit);

                if (delView.getVisibility() == View.GONE) {
                    delView.setVisibility(View.VISIBLE);
                    editView.setVisibility(View.VISIBLE);
                    cancelView.setVisibility(View.VISIBLE);

                    delView.setOnClickListener(new View.OnClickListener() {
                        Goods goods=goodsList.get(i);

                        @Override
                        public void onClick(View v) {
//                            ShowDialog.MessageDialog(getActivity(), "删除");
                            String dlgMessage = String.format("是否删除选定物料？\n名称：%s\n编号：%s\n颜色：%s\n单价：%.2f\n数量：%.2f\n合计：%.2f",
                                    goods.getName(), goods.getNumber(),goods.getModel(),goods.getPrice(),goods.getQty(),goods.amountPrice());
                            ShowDialog.YesNoDialog(getActivity(), dlgMessage, delPressed(), null);
                        }

                        Runnable delPressed(){
                            return new Runnable() {
                                @Override
                                public void run() {
                                    salesOrderInterface.delCurrentGoods(goods);
                                    onResume();
                                }
                            };
                        }
                    });

                    editView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            salesOrderInterface.setCurrentGoods(goodsList.get(i));
                            salesOrderInterface.showEditGoodsFragment(EditGoodsFragment.EDIT);
                            onResume();
                        }
                    });

                    cancelView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delView.setVisibility(View.GONE);
                            editView.setVisibility(View.GONE);
                            cancelView.setVisibility(View.GONE);
                        }
                    });

                }
//                else {
//                    delView.setVisibility(View.GONE);
//                    editView.setVisibility(View.GONE);
//                    cancelView.setVisibility(View.GONE);
//                }
            }
        });

        sumQtyView.setText(Double.toString(sumQty(goodsList)));
        sumPriceView.setText(Double.toString(sumAmount(goodsList)));
    }

    private double sumQty(List<Goods> goodsList) {
        double sum = 0;

        if (goodsList != null) {
            for (Goods goods : goodsList) {
                sum += goods.getQty();
            }
        }

        return sum;
    }

    private double sumAmount(List<Goods> goodsList) {
        double sum = 0;

        if (goodsList != null) {
            for (Goods goods : goodsList) {
                sum += goods.amountPrice();
            }
        }

        return sum;
    }
}
