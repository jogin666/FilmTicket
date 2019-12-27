package com.zy.filmticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zy.filmticket.entity.CinemaEntity;
import com.zy.filmticket.entity.FilmEntity;

import java.util.Random;

public class FilmInfoActivity extends AppCompatActivity {
    private ImageView iv_back;
    private TextView tv_title;

    private ImageView iv_filmImg;
    private TextView tv_filmName;
    private TextView tv_filmType;
    private TextView tv_adivise;
    private TextView tv_filmTime;
    private TextView tv_indtroduction;

    private Button buyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_info);
        initView();
        viewAddEvent();
    }

    public void initView(){
        iv_filmImg=findViewById(R.id.filmImg);
        tv_filmType=findViewById(R.id.filmType);
        tv_filmName=findViewById(R.id.filmName);
        tv_adivise=findViewById(R.id.adviseText);
        tv_filmTime=findViewById(R.id.filmTime);
        tv_indtroduction=findViewById(R.id.introduction);

        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.titleText);
        tv_title.setText("电影信息");
        buyBtn=findViewById(R.id.buyTicket);

    }

    public void  viewAddEvent(){
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final FilmEntity filmEntity = (FilmEntity) bundle.getSerializable("filmEntity");
        final CinemaEntity cinemaEntity= (CinemaEntity) bundle.getSerializable("cinemaEntity");
//        final String cinemaId=cinemaEntity.getCinemaId();
        final String filmId=filmEntity.getFilmId();
        showFilmEnttity(filmEntity);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(FilmInfoActivity.this,ShowReleaseFilmPlayActivity.class);
                intent1.putExtra("cinemaEntity",cinemaEntity);
                intent1.putExtra("filmId",filmId);
                startActivity(intent1);
            }
        });

    }

    public void showFilmEnttity(FilmEntity entity) {
        Glide.with(this).load(entity.getCoverUrl()).into(iv_filmImg);  //图片
        tv_filmName.setText(entity.getFilmName());
        tv_filmTime.setText(entity.getReleaseDates());
        String filmTypeInfo=entity.getDurationMin()+"/"; //时长
        String type=entity.getGenres(); //类型
        type=type.replace("/"," ")+"/";
        String country=entity.getCountries(); //国家
        if (country.contains("/")){
            type=type+entity.getCountries().replace("/"," "); //国家
        }else{
            type=type+entity.getCountries();
        }
        type=type+"/"+entity.getLanguage();
        tv_filmType.setText(filmTypeInfo+type);
        tv_adivise.setText(adviseContent());
        tv_indtroduction.setText(entity.getDescription());
    }

    public String adviseContent(){
        Random random=new Random();
        int content=random.nextInt(100-80)+80;
        return  "推荐度:"+content+"%";
    }

}
