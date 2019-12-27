package com.zy.filmticket.UI.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zy.filmticket.ChangeUserPasswordActivity;
import com.zy.filmticket.LoginActivity;
import com.zy.filmticket.R;
import com.zy.filmticket.UserInfoActivity;
import com.zy.filmticket.entity.UserEntity;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.UserService;
import com.zy.filmticket.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MineFragment extends Fragment implements View.OnClickListener{
    private View rootview;
    private LinearLayout userInfoHeadLayout;
    private ImageView headImg;
    private TextView nameText;
    private TextView accountText;
    private Button button;

    private RelativeLayout changePasswordLayout;

    private UserEntity user=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootview==null){
            rootview=inflater.inflate(R.layout.mine,container,false);
        }
        initView();;
        initViewEvent();
        if (!HttpUrl.isLogin){
            loginOut();
        }else{
            showUserInfo();
        }
        return rootview;
    }


    //绑定控件
    private void initView(){
        userInfoHeadLayout=rootview.findViewById(R.id.userInfoHeadImgLayout);
        headImg=userInfoHeadLayout.findViewById(R.id.userHeadImg);
        nameText=userInfoHeadLayout.findViewById(R.id.usename);
        accountText=userInfoHeadLayout.findViewById(R.id.useraccount);
        button=rootview.findViewById(R.id.operationBtn);
        button.setText("登录");

        changePasswordLayout=rootview.findViewById(R.id.changePassword);
    }


    //添加监听器
    private void initViewEvent(){
        userInfoHeadLayout.setOnClickListener(this);
        button.setOnClickListener(this);
        changePasswordLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userInfoHeadImgLayout:
                if (HttpUrl.isLogin){
                    Intent intent=new Intent(getActivity(),UserInfoActivity.class); //用户信息界面
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("userEntity",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else{
                    showTip("您未登录，请先登录");
                }
                break;
            case R.id.operationBtn:
                if ("登录".equals(button.getText().toString())){
                    login();
                }else{
                    loginOut();
                }
                break;
            case R.id.changePassword:
                if (!HttpUrl.isLogin){
                   showTip("您未登录，请先登录！");
                }else{
                    Intent intent=new Intent(getActivity(), ChangeUserPasswordActivity.class);
                    startActivityForResult(intent,1);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回结果集
        String result=data.getStringExtra("result");
        if ("ok".equals(result)){
            showUserInfo(); //显示用户的信息
        }
    }

    //登录
    private void login() {
        Intent intent=new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent,1);
    }


    //显示用户的信息
    public void showUserInfo(){
        if (!"".equals(HttpUrl.useraccount)) {
            String url= HttpUrl.filmTicketUrl+HttpUrl.findUserInfo+"?useraccount="+HttpUrl.useraccount;
            Log.d("url",url);
            HttpUtil.sendOKHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         showTip("显示用户信息失败！");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    final UserEntity userEntity= UserService.handleFindUserByAccount(responseText);
                    user=userEntity;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (userEntity!=null){
                                nameText.setText(userEntity.getUsername());
                                accountText.setText(userEntity.getUseraccount());
                                String headImgUrl=userEntity.getHeadImg();
                                if (!"null".equals(headImgUrl) && !TextUtils.isEmpty(headImgUrl)){
                                    String headImgStr=HttpUrl.filmTicketUrl+userEntity.getHeadImg();
                                    Glide.with(getActivity()).load(headImgStr).into(headImg);
                                }else{
                                    Glide.with(getActivity()).load(HttpUrl.defaultHeadImg).into(headImg);
                                }
                                button.setText("退出");
                            }else{
                                button.setText("登录");
                            }
                        }
                    });
                }
            });
        }
    }

    //退出登录
    public void loginOut(){
        button.setText("登录");
        nameText.setText("");
        accountText.setText("立即登录");
        HttpUrl.isLogin=false;
        HttpUrl.useraccount="";
        Glide.with(getActivity()).load(HttpUrl.defaultHeadImg).into(headImg);
    }


    public void showTip(String context){
        TextView msg = new TextView(getActivity());		 //消息居中
        msg.setText(context);
        msg.setPadding(10, 20, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        final AlertDialog.Builder builder=
                new AlertDialog.Builder(getActivity())
                        .setTitle("登录提示")
                        .setView(msg);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getActivity(),
                        LoginActivity.class);
                startActivityForResult(intent,1);
            }
        });
        builder.create().show();
    }



//    //用户是否登陆
//    public String getUserAccount(){
//        String useraccount="";
//        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("userdata",
//                Activity.MODE_PRIVATE);
//        useraccount =sharedPreferences.getString("userName","");
//        return useraccount;
//    }


}
