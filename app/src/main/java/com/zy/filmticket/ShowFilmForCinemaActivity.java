package com.zy.filmticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.filmticket.adaptor.FilmAdaptor;
import com.zy.filmticket.entity.CinemaEntity;
import com.zy.filmticket.entity.FilmEntity;
import com.zy.filmticket.filmticketService.FilmService;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.ReleaseFilmService;
import com.zy.filmticket.util.HttpUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ShowFilmForCinemaActivity extends AppCompatActivity {

    private ListView filmLsietView;
    private FilmAdaptor filmAdaptor;
    private TextView tv_cityName;
    private TextView tv_cinemaName;
    private TextView tv_cinemaAddress;
    private TextView tv_title;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_film_for_cinema);
        initView();
        viewAddEvent();
    }

    public void initView(){
        tv_cinemaName=findViewById(R.id.cinemaName);
        tv_cinemaAddress=findViewById(R.id.cinemaPlace);
        filmLsietView=findViewById(R.id.filmListView);
        tv_cityName=findViewById(R.id.tv_cityName);
        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.titleText);
        tv_title.setText("上映电影");
    }

    public void viewAddEvent(){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        final CinemaEntity cinemaEntity= (CinemaEntity) bundle.getSerializable("cinemaEntity");
        tv_cinemaAddress.setText(cinemaEntity.getCinemaAddrss());
        tv_cinemaName.setText(cinemaEntity.getCinemaName());
        tv_cityName.setText(cinemaEntity.getCity());
        requestReleaseFilm(cinemaEntity.getCinemaId());

        filmLsietView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilmEntity filmEntity= (FilmEntity) filmAdaptor.getItem(position);
                Intent intent=new Intent(ShowFilmForCinemaActivity.this,FilmInfoActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("cinemaEntity",cinemaEntity);
                bundle.putSerializable("filmEntity",filmEntity);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //根据电影院的id 获取上映的电影的id
    public void requestReleaseFilm(final String cinemaEntityIdStr){
        String url=HttpUrl.filmTicketUrl+HttpUrl.findReleaseByFilmIds+"?cinemaIdStr="+cinemaEntityIdStr;
        Log.d("url",url);
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowFilmForCinemaActivity.this, "请求失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reponseText = response.body().string();
                final String filmIds = ReleaseFilmService.findFilmIds(reponseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(filmIds)) {
                            requestFilm(filmIds);
                        } else {
                            Toast.makeText(ShowFilmForCinemaActivity.this, "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //根据电影的id 获取电影的信息
    public void requestFilm(String filmIds){
        String url=HttpUrl.filmTicketUrl+HttpUrl.findFilmById+"?filmIdStr="+filmIds;
        Log.d("url",url);

        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowFilmForCinemaActivity.this, "请求失败！", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                final List<FilmEntity> filmEntityList= FilmService.findFilmbyFilmId(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (filmEntityList!=null && filmEntityList.size()>0){
                            showReleaseFilm(filmEntityList);
                        }else{
                            Toast.makeText(ShowFilmForCinemaActivity.this, "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

//    public void requestReleaseFilmByCinemaId(String cinmaId){
//        String url= HttpUrl.filmTicketUrl+ HttpUrl.findReleaseFilmByCinemaId+"?cinmeId="+cinmaId;
//        Log.d("urlFilmByCinemaId",url);
//        HttpUtil.sendOKHttpRequest(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(ShowFilmForCinemaActivity.this, "请求失败！", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseText= response.body().string();
//                final List<FilmReleaseEntity> filmReleaseEntityList=ReleaseFilmService.findReleaseFilmByCinemaId(responseText);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (filmReleaseEntityList!=null && filmReleaseEntityList.size()>0){
//                                    showReleaseFilm(filmReleaseEntityList);
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        });
//    }

    public void showReleaseFilm(List<FilmEntity> filmEntityList){
        filmAdaptor=new FilmAdaptor(filmEntityList,this,"showFilmForCinemaActity");
        filmLsietView.setAdapter(filmAdaptor);
    }
}
