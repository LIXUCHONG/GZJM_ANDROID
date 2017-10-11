package com.jimei.k3wise_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jimei.k3wise_mobile.BO.Client;
import com.jimei.k3wise_mobile.BO.DocumentBill;
import com.jimei.k3wise_mobile.BO.Inventory;
import com.jimei.k3wise_mobile.BO.LoginUser;
import com.jimei.k3wise_mobile.BO.Properties.PayType;
import com.jimei.k3wise_mobile.BO.Properties.SaleType;
import com.jimei.k3wise_mobile.BO.Properties.ShippingType;
import com.jimei.k3wise_mobile.BO.SaleGoods;
import com.jimei.k3wise_mobile.BO.SalesOrder;
import com.jimei.k3wise_mobile.Component.BaseAppCompatActivity;
import com.jimei.k3wise_mobile.Interface.DocumentsFinderInterface;
import com.jimei.k3wise_mobile.Util.ShowDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lee on 2017/9/1.
 */

public class FindDocumentActivity extends BaseAppCompatActivity implements DocumentsFinderInterface {

    ListView lvDocuments;
    Button btnPrePage;
    Button btnNextPage;
    TextView tvPageCount;

    DocumentBill currentDocument;
    DocumentType documentType;

    private static final int perPageCount = 8;
    int currentPageCount = 1;

    List<DocumentBill> documentList;

    @Override
    public Bundle setWebserviceArgs(int loaderId) throws Exception {
        Bundle bundle = new Bundle();
        if (loaderId == 0) {
            bundle.putString("WebMethod", "findSalesOrder");
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("key", "");
            jsonParams.put("userId", LoginUser.Id);
            jsonParams.put("perPageCount", perPageCount);
            jsonParams.put("currentPageIndex", currentPageCount);
            bundle.putString("MethodParas", jsonParams.toString());
        } else if (loaderId == 1) {
            bundle.putString("WebMethod", "GetSalesOrderArray");
            if (currentDocument != null) {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("billId", currentDocument.getId());
                bundle.putString("MethodParas", jsonParams.toString());
            }
        }
        return bundle;
    }

    @Override
    public void handleCreateLoader(int loaderId) {
        if (loaderId == 0) {
            setAllowDestroyLoader(false);
        } else if (loaderId == 1) {
            setAllowDestroyLoader(false);
        }
    }

    @Override
    public void handleLoaderCallbacksMessage (int loaderId, String msg) {
        if (loaderId == 0) {
            updatePageCountView(currentPageCount);
            try {
                List<HashMap<String, Object>> documentListMap = fillDocumentListAndGetHashList(msg);

                if (documentListMap.size() == 0) {
                    ShowDialog.WarningDialog(this, "未查询到相关订单");
                }

                bindDocumentList(documentListMap);
            } catch (Exception ex) {
                ShowDialog.ExceptionDialog(this, ex.getMessage());
                return;
            }
        } else if (loaderId == 1) {
            currentDocument = getDocument(msg);

            showDocumentPreview(documentType);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_document;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lvDocuments = (ListView) findViewById(R.id.found_document_lv);
        btnPrePage = (Button) findViewById(R.id.pre_page_btn);
        btnNextPage = (Button) findViewById(R.id.next_page_btn);
        tvPageCount = (TextView) findViewById(R.id.page_count_label);

        documentList = new ArrayList<>();
        Intent intent = getIntent();
        documentType = DocumentType.valueOf(intent.getStringExtra("DocumentType"));

        setSubTitle("刷新");
        getSubTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportLoaderManager().restartLoader(0, null, FindDocumentActivity.this);
            }
        });

        btnPrePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPageCount > 1) {
                    currentPageCount--;
                    getSupportLoaderManager().restartLoader(0, null, FindDocumentActivity.this);
                }
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPageCount++;
                getSupportLoaderManager().restartLoader(0, null, FindDocumentActivity.this);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private List<HashMap<String, Object>> fillDocumentListAndGetHashList(String jsonStr) throws Exception {
        documentList.clear();
        List<HashMap<String, Object>> hashMapList = new ArrayList<>();

        if (jsonStr.length() > 0) {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                DocumentBill d = new DocumentBill();
                HashMap<String, Object> item = new HashMap<>();

                d.setId(jsonArray.getJSONObject(i).getInt("Id"));
                d.setNumber(jsonArray.getJSONObject(i).getString("Number"));
                d.setOrganizationName(jsonArray.getJSONObject(i).getString("Client"));
                d.setOperateDate((new SimpleDateFormat("yyyy-MM-dd")).parse(jsonArray.getJSONObject(i).getString("Date")));
                documentList.add(d);

                item.put("Id", d.getId());
                item.put("Number", d.getNumber());
                item.put("OrganizationName", d.getOrganizationName());
                item.put("Date", (new SimpleDateFormat("yyyy-MM-dd")).format(d.getOperateDate()));
                hashMapList.add(item);
            }
        }

        return hashMapList;
    }

    private void bindDocumentList(final List<HashMap<String, Object>> documentListMap) {
        SimpleAdapter mAdapter = new SimpleAdapter(this, documentListMap, R.layout.lv_find_document_list_item,
                new String[]{"Date", "OrganizationName", "Number"},
                new int[]{R.id.document_date, R.id.document_organization, R.id.document_number});

        lvDocuments.setAdapter(mAdapter);
        lvDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentDocument = new SalesOrder();
                currentDocument.setId(documentList.get(position).getId());
                getSupportLoaderManager().initLoader(1, null, FindDocumentActivity.this);
            }
        });
    }

    private void updatePageCountView(int pageIndex) {
        tvPageCount.setText(String.format("第%d页", pageIndex));
    }

    private DocumentBill getDocument(String jsonStr) {
        DocumentBill result = null;

        try {
            switch (documentType) {
                case SalesOrder:
                    result = getSalesOrderPreview(jsonStr);
                    break;
                default:
                    break;
            }
        }catch (Exception ex){
            ShowDialog.ExceptionDialog(this,ex.getMessage());
        }


        return result;
    }

    private void showDocumentPreview(DocumentType documentType) {
        switch (documentType) {
            case SalesOrder:
                showSalesOrderPreview();
                break;
            default:
                break;
        }
    }

    private void showSalesOrderPreview() {
        // TODO: 2017/9/26
        DocumentPreviewDialog.Builder builder = new DocumentPreviewDialog.Builder(this);
        builder.setTitle("预览");
//        builder.s
    }

    private SalesOrder getSalesOrderPreview(String jsonStr) throws Exception {
        SalesOrder salesOrder = new SalesOrder();

        JSONObject jsonObject = new JSONObject(jsonStr);
        salesOrder.ShippingType = new ShippingType();
        salesOrder.ShippingType.setId(jsonObject.getInt("ShippingTypeId"));
        salesOrder.ShippingType.setName(jsonObject.getString("ShippingTypeName"));
        salesOrder.PayType = new PayType();
        salesOrder.PayType.setId(jsonObject.getInt("PayTypeId"));
        salesOrder.PayType.setName(jsonObject.getString("PayTypeName"));
        salesOrder.SaleType = new SaleType();
        salesOrder.SaleType.setId(jsonObject.getInt("SaleTypeId"));
        salesOrder.SaleType.setName(jsonObject.getString("SaleTypeName"));
        salesOrder.Client = new Client();
        salesOrder.Client.setName(jsonObject.getString("ClientNumber"));
        salesOrder.setRemark(jsonObject.getString("Remark"));
        salesOrder.setDyeFee(jsonObject.getDouble("DyeFee"));
        salesOrder.setOtherFee(jsonObject.getDouble("OtherFee"));
        salesOrder.setEarnestMoney(jsonObject.getDouble("EarnestMoney"));

        List<SaleGoods> saleGoodsList=new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("Entry");
        for (int i = 0; i < jsonArray.length(); i++) {
            SaleGoods saleGoods = new SaleGoods();

            JSONObject goodsObj =jsonArray.getJSONObject(i);
            saleGoods.setId(goodsObj.getInt("Id"));
            saleGoods.setNumber(goodsObj.getString("Number"));
            saleGoods.setName(goodsObj.getString("Name"));
            saleGoods.setModel(goodsObj.getString("Color"));
            saleGoods.setPrice(new BigDecimal(goodsObj.getDouble("Price")));
            saleGoods.setQty(new BigDecimal(goodsObj.getDouble("Qty")));
            saleGoods.setBrokerage(new BigDecimal(goodsObj.getDouble("Brokerage")));

            List<Inventory> selectedInventoryList=new ArrayList<>();
            JSONArray selectedInventoryArray=goodsObj.getJSONArray("PresaleBatch");
            for(int j=0;j<selectedInventoryArray.length();i++){
                Inventory inventory=new Inventory();

                JSONObject invObj=selectedInventoryArray.getJSONObject(i);
                // TODO: 2017/10/8
                /*
                 * 提交的订单预算库存仅保存仓库组，仓位，批号，数量，
                 * 没有仓库，需要读取仓库
                 */
            }
        }

        return null;
    }
}
