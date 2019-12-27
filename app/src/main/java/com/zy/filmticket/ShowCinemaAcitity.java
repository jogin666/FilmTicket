package com.zy.filmticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.filmticket.adaptor.CinemaAdaptor;
import com.zy.filmticket.entity.CinemaEntity;
import com.zy.filmticket.filmticketService.CinemaService;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.util.HttpUtil;
import com.zy.filmticket.location.gps.LocationUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowCinemaAcitity extends AppCompatActivity {
    private View rootView;
    private ListView cinemaListView;
    private TextView tv_cityName;
    private SearchView searchView;
    private ImageView iv_location;

    private CinemaAdaptor cinemaAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cinema_acitity);
        initView();
    }

    public void initView(){
        cinemaListView=findViewById(R.id.bb_cinemaListView);
        tv_cityName=findViewById(R.id.bb_position);
        searchView=findViewById(R.id.ivb_serach);
//        iv_location=rootView.findViewById(R.id.iv_location);
        //显示城市
        String cityName= LocationUtils.getCityName(ShowCinemaAcitity.this);
        tv_cityName.setText(cityName);
        viewAddEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null) {
            String result = data.getStringExtra("result");
            if ("ok".equals(result)) {
                String cityName = data.getStringExtra("cityName");
                tv_cityName.setText(cityName);
            }
        }
    }


    public void requestCinemaByCityName(){
        String cityName = tv_cityName.getText().toString();
        try {
            cityName = URLEncoder.encode(cityName, "utf-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String url= HttpUrl.filmTicketUrl+HttpUrl.findCinemaByCityName+"?cityName="+cityName;
        Log.d("urlCinmeByCityName",url);
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowCinemaAcitity.this, "请求失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reponseText = response.body().string();
                final List<CinemaEntity> cinemaEntityList = CinemaService.findCinemaByCityName(reponseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cinemaEntityList!=null && cinemaEntityList.size()>0) {
                            showCinema(cinemaEntityList);
                        } else {
                            Toast.makeText(ShowCinemaAcitity.this, "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //显示该城市的电影院
    public void showCinema(List<CinemaEntity> cinemaEntityList){
        cinemaAdaptor =new CinemaAdaptor(ShowCinemaAcitity.this,cinemaEntityList);
        cinemaListView.setAdapter(cinemaAdaptor);
        cinemaListView.setOnItemClickListener(itemClickListener);
    }


    public AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CinemaEntity cinemaEntity= (CinemaEntity) cinemaAdaptor.getItem(position);
            Bundle bundle=new Bundle();
            bundle.putSerializable("cinemaEntity",cinemaEntity);
            Intent intent=new Intent(ShowCinemaAcitity.this, ShowFilmForCinemaActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    public void viewAddEvent(){
//        searchView.setOnClickListener(onClickListener);
//        iv_location.setOnClickListener(onClickListener);
        tv_cityName.setOnClickListener(onClickListener);
        requestCinemaByCityName();
        tv_cityName.addTextChangedListener(textWatcher);
    }

    public View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.position){
                Intent intent=new Intent(ShowCinemaAcitity.this, ChooseCityActivity.class);
                startActivityForResult(intent,1);
            }else if (v.getId()==R.id.search){

            }else if (v.getId()==R.id.iv_location){
                //实现定图
                String cityName=LocationUtils.getCityName(ShowCinemaAcitity.this);
                tv_cityName.setText(cityName);
            }
        }
    };

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            requestCinemaByCityName();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };
}
