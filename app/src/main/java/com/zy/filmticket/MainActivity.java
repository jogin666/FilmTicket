package com.zy.filmticket;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.zy.filmticket.UI.fragment.FilmFragment;
import com.zy.filmticket.UI.fragment.MainFragment;
import com.zy.filmticket.UI.fragment.MineFragment;
import com.zy.filmticket.entity.CinemaEntity;
import com.zy.filmticket.filmticketService.CinemaService;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.util.HttpUtil;

import java.io.IOException;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //碎片
    private Fragment mainFragment;
    private Fragment filmFragment;
    private Fragment mineFragment;
    //底部栏
    private  BottomNavigationView navigation;

    private int fragementIndex=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        viewAddEvent();
        HeConfig.init("HE1903071012051316","66553a8d042c43b7b26c585d3fcd9a69");
        HeConfig.switchToFreeServerNode();
        setSelectedFragment(fragementIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setSelectedFragment(fragementIndex);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        setSelectedFragment(fragementIndex);
    }

    //
    public void test(){
//        String name= URLEncoder.encode("上海","utf-8");
        String address=HttpUrl.filmTicketUrl+HttpUrl.findCinemaByCityName+"?=";
        Log.d("url",address);
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"请求失败！",Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String reposeText=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<CinemaEntity> list= CinemaService.findCinemaByCityName(reposeText);
                        if (list!=null && list.size()>0){
                            Log.d("list.size()",list.size()+"");
                        }else{
                            Toast.makeText(MainActivity.this,"解释失败！",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    //监听对象
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragementIndex=0;
                    setSelectedFragment(fragementIndex);
                    return true;
                case R.id.navigation_dashboard:
                    fragementIndex=1;
                    setSelectedFragment(fragementIndex);
                    return true;
                case R.id.navigation_notifications:
                    fragementIndex=2;
                    setSelectedFragment(fragementIndex);
                    return true;
            }
            return false;
        }
    };

    //退出界面，后台运行
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN
                && event.getRepeatCount()==0){
            this.moveTaskToBack(true);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //绑定视图
    private void initView(){
         navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }

    //添加视图监听事件
    private void viewAddEvent(){
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    //加载碎片视图
    private  void setSelectedFragment(int selectIndex){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (selectIndex) {
            case 0:
                hideFragment(transaction, filmFragment);
                hideFragment(transaction, mineFragment);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.frameLayout, mainFragment, "oneFragment");
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                hideFragment(transaction, mainFragment);
                hideFragment(transaction, mineFragment);
                if (filmFragment == null) {
                    filmFragment = new FilmFragment();
                    transaction.add(R.id.frameLayout, filmFragment, "filmFragment");
                } else {
                    transaction.show(filmFragment);
                }
                break;
            case 2:
                hideFragment(transaction, mainFragment);
                hideFragment(transaction, filmFragment);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.frameLayout, mineFragment, "mineFragment");
                } else {
                    transaction.show(mineFragment);
                }
                break;
        }
        transaction.commit();
    }

    //把之前的碎片给隐藏
    private void hideFragment(FragmentTransaction transaction, Fragment fragment) {
        if (fragment != null) {
            transaction.hide(fragment);
        }
    }

    public static void showCinema(){

    }

}
