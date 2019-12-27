package com.zy.filmticket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.filmticket.util.HttpUtil;
import com.zy.filmticket.location.LocationService;
import com.zy.filmticket.location.pojo.City;
import com.zy.filmticket.location.pojo.Province;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseCityActivity extends AppCompatActivity {

    private final String url_province="http://guolin.tech/api/china";
    private String url_city="http://guolin.tech/api/china/";

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static int current_level=0;

    private ImageView iv_back;
    private TextView tv_cityName;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> dataList=new ArrayList<String>();
    private List<Province> provinceList;
    private List<City> cityList;

    private ProgressDialog progressDialog;

    private Province selectedProvince;
    private City selectedCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooes_city);
        initView();
        viewAddEvent();
    }

    //绑定视图 控件
    public void initView(){
        iv_back=findViewById(R.id.iv_back);
        tv_cityName=findViewById(R.id.titleText);
        listView=findViewById(R.id.cityListView);
        arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(arrayAdapter);
        tv_cityName.setText("中国");
    }

    //添加数据
    public void viewAddEvent(){
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_level==LEVEL_CITY){
                    queryFromServer(url_province,"province");
                }else{
                    finish();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(current_level==LEVEL_PROVINCE){
                     selectedProvince=provinceList.get(position);
                     int provinceCode=selectedProvince.getProvinceCode();
                     queryFromServer(url_city+provinceCode,"city"); //查询
                }else if(current_level==LEVEL_CITY){
                     selectedCity=cityList.get(position);
                     String cityName=selectedCity.getCityName();
                     Intent intent=new Intent();
                     intent.putExtra("result", "ok"); //数据回显
                     intent.putExtra("cityName",cityName);
                     setResult(RESULT_OK,intent);
                     finish();
                }
            }
        });
        queryFromServer(url_province,"province");
    }

    //从服务器中查询数据
    private void queryFromServer(String address,final String type){
        showProcessDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseCityActivity.this,"加载失败！",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                closeProgressDialog();
                final String reposeText=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if("province".equals(type)){
                            showProvince(LocationService.handleProvinceResponse(reposeText));
                        }else if("city".equals(type)){
                            showCity(LocationService.handleCityResponse(reposeText,selectedProvince.getId()));
                        }
                    }
                });
            }
        });
    }

    //显示省份
    public void showProvince(List<Province> provinceList){
        this.provinceList=provinceList;
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getPrivinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level=LEVEL_PROVINCE;
        }
    }

    //显示城市
    public void showCity(List<City> cityList){
        this.cityList=cityList;
        if(provinceList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level=LEVEL_CITY;
        }
    }

    //显示加载界面进度条
    public void showProcessDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭加载进度条
    public void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.cancel();
        }
    }
}
