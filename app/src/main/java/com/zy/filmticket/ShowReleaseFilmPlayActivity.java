package com.zy.filmticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.filmticket.adaptor.ReleaseFilmAdapter;
import com.zy.filmticket.entity.CinemaEntity;
import com.zy.filmticket.entity.FilmReleaseEntity;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.ReleaseFilmService;
import com.zy.filmticket.util.HttpUtil;


import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowReleaseFilmPlayActivity extends AppCompatActivity {
    private List<FilmReleaseEntity> filmReleaseFilmEntityList;
    private ListView releaseFilmListView;
    private ReleaseFilmAdapter releaseFilmAdapter;

    private ImageView iv_back;

    private TextView tv_title;


    private TextView tv_cinamaName;
    private TextView tv_cinemaAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_release_film_play);
        initView();
        initData();
    }

    public void initView(){
        tv_title=findViewById(R.id.titleText); //标题
        tv_title.setText("上映电影信息");
        iv_back=findViewById(R.id.iv_back);
        tv_cinamaName=findViewById(R.id.cinemaName);
        tv_cinemaAddress=findViewById(R.id.cinemaPlace);
        releaseFilmListView=findViewById(R.id.showReleaseFilms);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initData(){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        CinemaEntity cinemaEntity= (CinemaEntity) bundle.getSerializable("cinemaEntity");
        tv_cinemaAddress.setText(cinemaEntity.getCinemaAddrss());
        tv_cinamaName.setText(cinemaEntity.getCinemaName());

        String filmId=intent.getStringExtra("filmId");
        String cinemaId=cinemaEntity.getCinemaId();
        requestReleaseFilmBycinemaIdAndFilmId(cinemaId,filmId);
    }

    public void requestReleaseFilmBycinemaIdAndFilmId(String cinemaId,String filmId){
        String url= HttpUrl.filmTicketUrl+HttpUrl.findReleaseFilmFyCinemaIdAndFilmId+"?cinemaId="+cinemaId+"&filmId="+filmId;
        Log.d("url",url);
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowReleaseFilmPlayActivity.this, "请求失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                final List<FilmReleaseEntity> filmReleaseEntityList= ReleaseFilmService.findReleaseFilmFyCinemaIdAndFilmId(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (filmReleaseEntityList!=null && filmReleaseEntityList.size()>0) {
                            showReleaseFilms(filmReleaseEntityList);
                        } else {
                            Toast.makeText(ShowReleaseFilmPlayActivity.this, "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void showReleaseFilms(List<FilmReleaseEntity> filmReleaseEntityList){
        filmReleaseFilmEntityList=filmReleaseEntityList;
        releaseFilmAdapter=new ReleaseFilmAdapter(this,filmReleaseEntityList);
        releaseFilmListView.setAdapter(releaseFilmAdapter);
        releaseFilmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
