package com.zy.filmticket.UI.fragment;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zy.filmticket.ChooseCityActivity;
import com.zy.filmticket.R;
import com.zy.filmticket.ShowCinemaAcitity;
import com.zy.filmticket.adaptor.FilmAdaptor;
import com.zy.filmticket.adaptor.ViewPagerAdapter;
import com.zy.filmticket.entity.FilmEntity;
import com.zy.filmticket.filmticketService.CinemaService;
import com.zy.filmticket.filmticketService.FilmService;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.ReleaseFilmService;
import com.zy.filmticket.util.HttpUtil;
import com.zy.filmticket.location.gps.LocationUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainFragment extends Fragment implements View.OnClickListener{

    private ViewPager viewPager;
    private List<ImageView> mImageList;
    private int  previousPosition=0;
    private List<View> mDots;
    private boolean isStop=false; //线程是否停止
    private static int PAGER_TIME=5000; //时间间隔

    private View rootView;
    private TextView tv_cityName;
    private ImageView iv_serach;
    private ImageView iv_location;

    private ListView releaseFilmListeView;


    private FilmAdaptor adaptor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(R.layout.main,container,false);
        }
        initData();
        initView();
        autoPlayView();//开启线程，自动播放

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //请求，获取电影院的id
    public void requestCinemaIdsRequest(){
        try {
            String cityName=tv_cityName.getText().toString();
            cityName= URLEncoder.encode(cityName,"Utf-8");  //获取该城市的电影院的id
            String address = HttpUrl.filmTicketUrl + HttpUrl.getCinemaIdsByCityName + "?cityName="+cityName;
            Log.d("url", address);
            HttpUtil.sendOKHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "请求失败！", Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String reposeText = response.body().string();
                    final String cinemaEntityIdStr=CinemaService.findCinemaIDByCityName(reposeText);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(cinemaEntityIdStr)) {
                                requestReleaseFilm(cinemaEntityIdStr);
                            } else {
                                Toast.makeText(getActivity(), "解释失败！", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }catch (UnsupportedEncodingException e){
            Log.d("ecoude","编码异常！");
            e.printStackTrace();
        }
    }

    //根据电影院的id 获取上映的电影的id
    public void requestReleaseFilm(final String cinemaEntityIdStr){
         String url=HttpUrl.filmTicketUrl+HttpUrl.findReleaseByFilmIds+"?cinemaIdStr="+cinemaEntityIdStr;
         Log.d("url",url);
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
                 final String filmIds = ReleaseFilmService.findFilmIds(reponseText);
                 getActivity().runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         if (!TextUtils.isEmpty(filmIds)) {
                                requestFilm(filmIds);
                         } else {
                             Toast.makeText(getActivity(), "解释失败！", Toast.LENGTH_LONG).show();
                         }
                     }
                 });
             }
         });
    }

    //请求电影信息
    public void requestFilm(String filmIds){
        String url=HttpUrl.filmTicketUrl+HttpUrl.findFilmById+"?filmIdStr="+filmIds;
        Log.d("url",url);

        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "请求失败！", Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                final List<FilmEntity> filmEntityList= FilmService.findFilmbyFilmId(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (filmEntityList!=null && filmEntityList.size()>0){
                            showReleaseFilm(filmEntityList);
                        }else{
                            Toast.makeText(getActivity(), "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //显示上映的电影
    public void showReleaseFilm(List<FilmEntity> filmEntityList){
        adaptor=new FilmAdaptor(filmEntityList,getActivity(),"mainFragement");
        releaseFilmListeView.setAdapter(adaptor);
    }

    //绑定视图 控件
    public void initView() {
        viewPager=rootView.findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(mImageList,viewPager);
        viewPager.setAdapter(viewPagerAdapter);

        iv_serach=rootView.findViewById(R.id.iv_serach);
        tv_cityName=rootView.findViewById(R.id.tv_cityName);
        iv_location=rootView.findViewById(R.id.iv_location);
        releaseFilmListeView=rootView.findViewById(R.id.showFilmList);
        viewAddEvent();
    }

    //增加监听器
    public void viewAddEvent(){
        //设置
        viewPager.setOnPageChangeListener(pageChangeListener);
        iv_serach.setOnClickListener(this);
        tv_cityName.setOnClickListener(this);
        iv_location.setOnClickListener(this);
        releaseFilmListeView.setOnItemClickListener(itemClickListener);
        releaseFilmListeView.requestFocus();
        tv_cityName.addTextChangedListener(textWatcher);
        setFirstLocation();
        showLocationCity();

    }

    //显示电影的信息
    private AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final FilmEntity filmEntity= (FilmEntity) adaptor.getItem(position);
            //自定义alertDialog
            LayoutInflater layoutInflater=getActivity().getLayoutInflater();
            View filmView=layoutInflater.inflate(R.layout.show_film_layout,null);
            AlertDialog.Builder builder=new android.app.AlertDialog.Builder(getActivity()).setTitle("电影信息");
            builder.setView(filmView);
            final AlertDialog alertDialog=builder.create();
            //图片
            ImageView iv_filmImg=filmView.findViewById(R.id.filmImg);
            //电影信息
            TextView tv_filmName=filmView.findViewById(R.id.filmName);
            TextView tv_filmType=filmView.findViewById(R.id.filmType);
            TextView tv_filmTime=filmView.findViewById(R.id.filmTime);
            Button   tv_Buy=filmView.findViewById(R.id.buyTicket);//购票
            Button   backBtn=filmView.findViewById(R.id.back); //返回

            Glide.with(getActivity()).load(filmEntity.getCoverUrl()).into(iv_filmImg);
            tv_filmName.setText(filmEntity.getFilmName());
            tv_filmName.setText(filmEntity.getFilmName());
            tv_filmType.setText(filmEntity.getGenres());
            tv_filmTime.setText(filmEntity.getReleaseDates());

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });

            tv_Buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String filmId=filmEntity.getFilmId();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestForCinemaIdByfilmId(filmId);
                        }
                    });
                    alertDialog.cancel();
                }
            });
            alertDialog.show();
        }
    };


    //通过上映电影的id 播放该电影的电影院id
    public void requestForCinemaIdByfilmId(String filmId){
        String url=HttpUrl.filmTicketUrl+HttpUrl.findCinemaIdByFilmId+"?filmId="+filmId;
        Log.d("url",url);
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
                String responseText=response.body().string();
                final String cinemIds=CinemaService.findCinemIdByFilmId(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(cinemIds)){
                           showMainFragement(cinemIds);
                        }else{
                            Toast.makeText(getActivity(), "解释失败！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //显示filmFragement
    public void showMainFragement(String cinemaId){
        Intent intent=new Intent(getActivity(),ShowCinemaAcitity.class);
        Log.d("cinemaId",cinemaId);
        intent.putExtra("cinemId",cinemaId);
        startActivity(intent);
//        FilmFragment filmFragment=new FilmFragment();
//        FragmentManager fm = getFragmentManager();
//        android.app.FragmentTransaction transaction = fm.beginTransaction();
//        transaction.hide(MainFragment.this);
//        transaction.add(R.id.frameLayout,filmFragment, "filmFragment");
//        transaction.show(filmFragment);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_serach:
                break;
            case R.id.tv_cityName:
                Intent intent=new Intent(getActivity(), ChooseCityActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.iv_location:
                showLocationCity();
                break;
        }
    }

    //
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

    //实现定位
    public void showLocationCity(){
        String cityName=LocationUtils.getCityName(getActivity());
        tv_cityName.setText(cityName);
    }

    //显示数据
    public void initData(){
        int imgs[]=new int[]{R.mipmap.a,R.mipmap.b,R.mipmap.c,R.mipmap.d};
        mImageList=new ArrayList<ImageView>();
        for (int i=0;i<imgs.length;i++){
            ImageView iv=new ImageView(getActivity());
            iv.setBackgroundResource(imgs[i]);
            mImageList.add(iv);
        }
        LinearLayout linearLayoutDots = rootView.findViewById(R.id.lineLayout_dot);
        //目的是将资源文件转成Drawable
        mDots = addDots(linearLayoutDots,fromResToDrawable(getActivity(),R.mipmap.ic_dot_normal),mImageList.size());
    }

    /**
     * 资源图片转Drawable
     * @param context
     * @param resId
     * @return
     */
    public Drawable fromResToDrawable(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }

    //实现小圆点变换的监听器
    private ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                int newPosition=i % mImageList.size();
                LinearLayout.LayoutParams newDotParams= (LinearLayout.LayoutParams) mDots.get(newPosition).getLayoutParams();
                newDotParams.width = 24;
                newDotParams.height = 24;

                LinearLayout.LayoutParams oldDotParams = (LinearLayout.LayoutParams) mDots.get(previousPosition).getLayoutParams();
                oldDotParams.width = 16;
                oldDotParams.height = 16;

                // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
                previousPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        };


    //设置刚打开app时显示的图片和文字
    private void setFirstLocation() {
        //实现无线循环
        int m = (Integer.MAX_VALUE / 2) % mImageList.size();
        int currentPosition = Integer.MAX_VALUE / 2 - m;
        viewPager.setCurrentItem(currentPosition);
    }

    //设置自动播放,每隔PAGER_TIOME秒换一张图片
    private void autoPlayView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                    SystemClock.sleep(PAGER_TIME);
                }
            }
        }).start();
    }

    /**
     * 动态添加一个点
     * @param linearLayout 添加到LinearLayout布局
     * @param backgount 设置
     * @return
     */
    @TargetApi(18)
    public int addDot(final LinearLayout linearLayout, Drawable backgount) {
        final View dot = new View(getActivity());
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.width = 16;
        dotParams.height = 16;
        dotParams.setMargins(4,0,4,0);
        dot.setLayoutParams(dotParams);
        try{
            dot.setBackground(backgount);
            dot.setId(View.generateViewId());
        }catch (Exception e){

        }
        linearLayout.addView(dot);
        return dot.getId();
    }

    /**
     * 添加多个轮播小点到横向线性布局
     * @param linearLayout
     * @param backgount
     * @param number
     * @return
     */
    public List<View> addDots(final LinearLayout linearLayout, Drawable backgount, int number){
        List<View> dots = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            int dotId = addDot(linearLayout,backgount);
            dots.add(rootView.findViewById(dotId));
        }
        return dots;
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            requestCinemaIdsRequest();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
