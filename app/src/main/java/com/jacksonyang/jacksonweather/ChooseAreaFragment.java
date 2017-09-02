package com.jacksonyang.jacksonweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.utils.Utils;
import com.jacksonyang.jacksonweather.Web.HttpUtil;
import com.jacksonyang.jacksonweather.Web.JsonCommand;
import com.jacksonyang.jacksonweather.database.City;
import com.jacksonyang.jacksonweather.database.County;
import com.jacksonyang.jacksonweather.database.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooseAreaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChooseAreaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseAreaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final int LEVEL_PROVINCE=1;
    public static final int LEVEL_CITY=2;
    public static final int LEVEL_COUNTY=3;
    private ProgressDialog progressDialog;
    private TextView Title;
    private Button Back;
    private ListView chooseArea;
    private ArrayAdapter<String> adapter;//适配器
    private List<String> data=new ArrayList<>();//一个列表
    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表
    private Province chooseProvince;//选中的省份
    private City chooseCity;//选中的城市
    private County chooseCounty;//选中的县
    private int currentLevel;//当前的级，先选省然后市然后县

    private OnFragmentInteractionListener mListener;

    public ChooseAreaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseAreaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseAreaFragment newInstance(String param1, String param2) {
        ChooseAreaFragment fragment = new ChooseAreaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.activity_choose_area, container, false);
        Title=(TextView) view.findViewById(R.id.title);
        Back=(Button) view.findViewById(R.id.back);
        chooseArea=(ListView) view.findViewById(R.id.choose);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,data);
        chooseArea.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chooseArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    chooseProvince=provinceList.get(position);
                    queryCities();
                }
                else if(currentLevel==LEVEL_CITY){
                    chooseCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();
                }
                else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /* 查询所有省的数据，优先从数据库查询，没有找到则去服务器查询
    */
    private void queryProvinces(){
        Title.setText("中国");
        Back.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            data.clear();
            for(Province province:provinceList){
                data.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            chooseArea.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        } else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCities(){
        Title.setText(chooseProvince.getProvinceName());
        Back.setVisibility(View.VISIBLE);
        cityList= DataSupport.where("provinceid=?",String.valueOf(chooseProvince.getId())).find(City.class);
        if(cityList.size()>0){
            data.clear();
            for(City city:cityList){
                data.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            chooseArea.setSelection(0);
            currentLevel=LEVEL_CITY;
        } else {
            int provinceCode=chooseProvince.getProvinceCode();
            String address="http://guolin.tech/api/china"+provinceCode;
            queryFromServer(address,"city");
        }

    }

    private void queryCounties(){
        Title.setText(chooseCity.getCityName());
        Back.setVisibility(View.VISIBLE);
        countyList= DataSupport.where("cityid=?",String.valueOf(chooseCity.getId())).find(County.class);
        if(countyList.size()>0){
            data.clear();
            for(County county:countyList){
                data.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            chooseArea.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        } else {
            int provinceCode=chooseProvince.getProvinceCode();
            int cityCode=chooseCity.getCityCode();
            String address="http://guolin.tech/api/china"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= JsonCommand.handleProvinceResponse(responseText);
                }
                else if("city".equals(type)){
                    result=JsonCommand.handleCityResponse(responseText,chooseProvince.getId());
                }
                else if("county".equals(type)){
                    result=JsonCommand.handleCountyResponse(responseText,chooseCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }
                            else if("city".equals(type)){
                                queryCities();
                            }
                            else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
