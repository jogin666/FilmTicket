package com.zy.filmticket.UI.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.filmticket.ChooseCityActivity;
import com.zy.filmticket.R;
import com.zy.filmticket.ShowFilmForCinemaActivity;
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

public class FilmFragment extends Fragment {
    private View rootView;
    private ListView cinemaListView;
    private TextView tv_cityName;
    private SearchView searchView;
    private ImageView iv_location;

    private CinemaAdaptor cinemaAdaptor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(R.layout.films,container,false);
        }
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "请求失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reponseText = response.body().string();
                final List<CinemaEntity> cinemaEntityList = CinemaService.findCinemaByCityName(reponseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cinemaEntityList!=null && cinemaEntityList.size()>0) {
                            showCinema(cinemaEntityList);
                        } else {
                            Toast.makeText(getActivity(), "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //显示该城市的电影院
    public void showCinema(List<CinemaEntity> cinemaEntityList){
        cinemaAdaptor =new CinemaAdaptor(getActivity(),cinemaEntityList);
        cinemaListView.setAdapter(cinemaAdaptor);
        cinemaListView.setOnItemClickListener(itemClickListener);
    }


    public AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CinemaEntity cinemaEntity= (CinemaEntity) cinemaAdaptor.getItem(position);
            Bundle bundle=new Bundle();
            bundle.putSerializable("cinemaEntity",cinemaEntity);
            Intent intent=new Intent(getActivity(), ShowFilmForCinemaActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };



    public void initView(){
        cinemaListView=rootView.findViewById(R.id.cinemaListView);
        tv_cityName=rootView.findViewById(R.id.position);
        searchView=rootView.findViewById(R.id.search);
        iv_location=rootView.findViewById(R.id.iv_location);
        //显示城市
        String cityName= LocationUtils.getCityName(getActivity());
        tv_cityName.setText(cityName);
        viewAddEvent();
    }

    public void viewAddEvent(){
        searchView.setOnClickListener(onClickListener);
        iv_location.setOnClickListener(onClickListener);
        tv_cityName.setOnClickListener(onClickListener);
        requestCinemaByCityName();
        tv_cityName.addTextChangedListener(textWatcher);
    }

    public View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.position){
                Intent intent=new Intent(getActivity(), ChooseCityActivity.class);
                startActivityForResult(intent,1);
            }else if (v.getId()==R.id.search){

            }else if (v.getId()==R.id.iv_location){
                //实现定图
                String cityName=LocationUtils.getCityName(getActivity());
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
