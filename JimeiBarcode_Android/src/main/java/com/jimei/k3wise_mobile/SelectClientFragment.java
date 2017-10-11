package com.jimei.k3wise_mobile;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.jimei.k3wise_mobile.BO.LoginUser;
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
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectClientFragment extends HandledFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View rootView=null;
    View formSelectedClient;
    View formClientList;
    ListView lvClientList;
    ListView lvSelectedClientList;
    ProgressView progressView;
    EditText editInputKey;

    SalesOrderInterface salesOrderInterface;
    WebserviceTask getClientTask;
    List<Client> clientList;
    List<Client> selectedClientList;
    int currentSelectedClientListIndex;


    public SelectClientFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_client;
    }

    @Override
    protected void setToolbarTitleText() {
        super.getToolbarTitle().setText("选择客户");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectClientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectClientFragment newInstance(String param1, String param2) {
        SelectClientFragment fragment = new SelectClientFragment();
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

        initSelectedClientList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView==null) {
            rootView = super.onCreateView(inflater,container,savedInstanceState);

            formSelectedClient=rootView.findViewById(R.id.form_select_client);
            formClientList=rootView.findViewById(R.id.form_client_list);
            progressView=(ProgressView) rootView.findViewById(R.id.progress_client_list);
            lvClientList=(ListView)rootView.findViewById(R.id.lv_client_list);
            lvSelectedClientList=(ListView)rootView.findViewById(R.id.lv_selected_client);

            final EditText etKey=(EditText)rootView.findViewById(R.id.et_select_client_input_key);
            etKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        InputMethodManager imm = (InputMethodManager) (getActivity().getSystemService(INPUT_METHOD_SERVICE));
                        imm.hideSoftInputFromWindow(etKey.getWindowToken(), 0);
                    }
                }
            });

            Button btnFindClientByKey =(Button) rootView.findViewById(R.id.btn_select_client);
            btnFindClientByKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputKey=etKey.getText().toString();
                    if(!inputKey.equals("")) {
                        try {
                            currentSelectedClientListIndex=0;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("userid", LoginUser.Id);
                            jsonObject.put("key", inputKey);

                            getClientTask = new GetClientTask(getActivity(), "GetClientByKey", jsonObject);
                            getClientTask.execute();
                        } catch (Exception ex) {
                            ShowDialog.ExceptionDialog(getActivity(), ex.getMessage());
                        }
                    }else {
                        ShowDialog.WarningDialog(getActivity(), "请输入客户信息");
                    }
                }
            });

            Button btnFindClientByParent=(Button)rootView.findViewById(R.id.btn_select_client_by_parent);
            btnFindClientByParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initSelectedClientList();
                    reBindSelectedClientList();
//                    try{
//                        JSONObject jsonObject=new JSONObject();
//                        jsonObject.put("userid", LoginUser.Id);
//                        jsonObject.put("parentid",0);
//
//                        getClientTask=new GetClientTask(getActivity(),"GetClientByParent",jsonObject);
//                        getClientTask.execute();
//                    }catch (Exception ex){
//                        ShowDialog.ExceptionDialog(getActivity(),ex.getMessage());
//                    }
                }
            });

            bindSelectedClientList(selectedClientList);
        }

        return rootView;
    }

    @Override
    public void onDetach() {
        if (getClientTask != null) {
            if (!getClientTask.isCancelled()) {
                getClientTask.cancel(true);
            }
        }

        super.onDetach();
    }

    public boolean onBackPressed() {
        if (formClientList.getVisibility() == View.VISIBLE) {
            if (getClientTask != null) {
                if (!getClientTask.isCancelled()) {
                    getClientTask.cancel(true);
                }
            }
            formSelectedClient.setVisibility(View.VISIBLE);
            formClientList.setVisibility(View.GONE);
            return false;
        } else {
            return true;
        }
    }

    private void initSelectedClientList(){
        if(selectedClientList==null){
            selectedClientList=new ArrayList<>();
        }

        selectedClientList.clear();
        Client emptyParent =new Client();
        emptyParent.setParentId(0);
        emptyParent.setDetail(false);
        selectedClientList.add(emptyParent);
    }

    private void addSelectedClient(Client client, int fillIndex) {
        if (selectedClientList == null) {
            selectedClientList = new ArrayList<>();
        }

//        for (int i=fillIndex;i<selectedClientList.size();i++) {
//            selectedClientList.remove(i);
//        }
        while (fillIndex<selectedClientList.size()){
            selectedClientList.remove(fillIndex);
        }

        //插入已选中客户列表
        selectedClientList.add(client);

        if(!client.isDetail()){
            Client emptyParent=new Client();
            emptyParent.setParentId(client.getId());
            emptyParent.setDetail(false);
            selectedClientList.add(emptyParent);
        }

        //刷新
        reBindSelectedClientList();
    }

    private void reBindSelectedClientList(){
        bindSelectedClientList(selectedClientList);
    }

    private List<Map<String, Object>> conventClientListToMapList(List<Client> clientList) {

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        if (clientList != null) {
            for (Client client : clientList) {
                mapList.add(CommonHelper.getObjMap(client));
            }
        }
        return mapList;
    }

    private void bindSelectedClientList(final List<Client> clientList) {

        List<Map<String, Object>> clientMapList = conventClientListToMapList(clientList);

        SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), clientMapList, R.layout.lv_selected_client_item,
                new String[]{"Name"},
                new int[]{R.id.tv_item_selected_client_name});

        lvSelectedClientList.setAdapter(mAdapter);
        lvSelectedClientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Client client=clientList.get(i);
                currentSelectedClientListIndex=i;
                if(client.isDetail()){
                    Button btnSelectClient=(Button)view.findViewById(R.id.btn_item_selected_client_detail);
                    btnSelectClient.setVisibility(View.VISIBLE);
                    btnSelectClient.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            salesOrderInterface.setSalesOrderClient(client);
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    });
                }else {
                    try{
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("userid", LoginUser.Id);
                        jsonObject.put("parentid",client.getParentId());

                        getClientTask=new GetClientTask(getActivity(),"GetClientByParent",jsonObject);
                        getClientTask.execute();
                    }catch (Exception ex){
                        ShowDialog.ExceptionDialog(getActivity(),ex.getMessage());
                    }
                }
            }
        });
    }

    private void showProgress(boolean show) {
        progressView.Show(lvClientList, show);
    }

    private class GetClientTask extends WebserviceTask {
        GetClientTask(Context context, String method, JSONObject Paras) {
            super(context, method, Paras);
        }

        @Override
        protected void onPreExecute() {
            formSelectedClient.setVisibility(View.GONE);
            formClientList.setVisibility(View.VISIBLE);
            showProgress(true);
        }

        @Override
        protected void onPostExecute(Message msg) {
            getClientTask = null;
            showProgress(false);

            switch (msg.what) {
                case KingdeeK3WiseWebServiceHelper.INVOKE_SUCCESS:
                    try {
                        clientList = ConvertToClientList((String) msg.obj);

                        if (clientList.size() == 0) {
                            formSelectedClient.setVisibility(View.VISIBLE);
                            formClientList.setVisibility(View.GONE);
                            ShowDialog.WarningDialog(getActivity(), "未找到符合的客户信息");
                        }else if(clientList.size()==1) {
                            formSelectedClient.setVisibility(View.VISIBLE);
                            formClientList.setVisibility(View.GONE);
                            addSelectedClient(clientList.get(0),currentSelectedClientListIndex);
                        } else {
                            bindClientList(clientList);
                        }
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

        @Override
        protected void onCancelled() {
            getClientTask = null;
            showProgress(false);
        }

        private List<Client> ConvertToClientList(String jsonStr) throws Exception {

            List<Client> clientList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonStr);

            for (int i = 0; i < jsonArray.length(); i++) {

                Client client = new Client();
                JSONObject clientJson = new JSONObject(jsonArray.getJSONObject(i).toString());
                client.setId(clientJson.getInt("Id"));
                client.setParentId(clientJson.getInt("ParentId"));
                client.setName(clientJson.getString("Name"));
                client.setNumber(clientJson.getString("Number"));
                client.setDetail(Boolean.parseBoolean(clientJson.getString("Detail")));

//                for (int j = 0; j < clientJson.length(); j++) {
//
//                    String key = clientJson.getJSONObject(j).getString("Key");
//                    switch (key) {
//                        case "Id":
//                            client.setId(clientJson.getJSONObject(j).getInt("Value"));
//                            break;
//                        case "ParentId":
//                            client.setParentId(clientJson.getJSONObject(j).getInt("Value"));
//                            break;
//                        case "Name":
//                            client.setName(clientJson.getJSONObject(j).getString("Value"));
//                            break;
//                        case "Number":
//                            client.setNumber(clientJson.getJSONObject(j).getString("Value"));
//                        case "Detail":
//                            client.setDetail(Boolean.parseBoolean(clientJson.getJSONObject(j).getString("Value")));
//                            break;
//                    }
//                }

                clientList.add(client);
            }

            return clientList;
        }

        private void bindClientList(final List<Client> clientList) {

            List<Map<String, Object>> clientMapList = conventClientListToMapList(clientList);

            SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), clientMapList, R.layout.lv_client_list_item,
                    new String[]{"Name"},
                    new int[]{R.id.tv_item_client_list_name});

            lvClientList.setAdapter(mAdapter);
            lvClientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    formSelectedClient.setVisibility(View.VISIBLE);
                    formClientList.setVisibility(View.GONE);
                    addSelectedClient(clientList.get(i),currentSelectedClientListIndex);
                }
            });
        }
    }
}
