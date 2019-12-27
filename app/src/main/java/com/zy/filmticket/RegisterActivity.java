package com.zy.filmticket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.UserService;
import com.zy.filmticket.util.HttpUtil;
import com.zy.filmticket.util.MyCountDownTimer;
import com.zy.filmticket.message.SMSUtils;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView titleView;
    private EditText et_mobile;
    private EditText et_verification;
    private Button registerBtn;
    private TextView tv_verification;
    private ImageView iv_back;

    private MyCountDownTimer myCountDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        addViewEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.resigterBtn: //登录
                login();
                break;
            case R.id.tv_getVerfication:  //获取验证码
                final String mobile=et_mobile.getText().toString();
                if (!TextUtils.isEmpty(mobile)) { //验证手机号码
                    if (SMSUtils.isMobilePhoneNumber(mobile)) {
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               SMSUtils.sendVerfication(mobile); //发送短信
                           }
                       }).start();
                        myCountDownTimer=new MyCountDownTimer(60000,1000,tv_verification);
                        myCountDownTimer.start();
                    }else{ //手机号码错误
                       showErrorTip("手机号码不合法！");
                    }
                }else{//手机号为空
                   showErrorTip("手机号码不能为空！");
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public void login(){
        String phone=et_mobile.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            String verification = et_verification.getText().toString();
            if (!TextUtils.isEmpty(verification)) {
                if (SMSUtils.verificationIsCorrect(verification)) {  //验证码是否正确
                    addUser();
                } else {
                    showErrorTip("验证码错误！");
                }
            } else {
                showErrorTip("验证码不能为空！");
            }
        }else{
            showErrorTip("手机号码不能为空！");
        }
    }

    public void addUser(){
        final String account=et_mobile.getText().toString();
        String url= HttpUrl.filmTicketUrl+HttpUrl.addUser+"?useraccount="+account;
        //存入数据库
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showErrorTip("注册失败！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responeseText=response.body().string();
                final String result=UserService.handleAddUser(responeseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("true".equals(result)) {
                            Toast.makeText(RegisterActivity.this,"注册成功！",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            HttpUrl.isLogin=true;
                            HttpUrl.useraccount=account;
                        }else{
                            showErrorTip("注册失败！");
                        }
                    }
                });
            }
        });
    }

    //错误提示
    public void showErrorTip(String context){
        TextView msg = new TextView(RegisterActivity.this);		 //消息居中
        msg.setText(context);
        msg.setPadding(10, 20, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        final AlertDialog.Builder builder=
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("注册提示")
                        .setView(msg);
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    //绑定控件
    public void initView(){
        titleView=findViewById(R.id.titleText);
        titleView.setText("注册");
        et_mobile=findViewById(R.id.et_regAccount);
        et_verification=findViewById(R.id.et_regPassward);
        registerBtn=findViewById(R.id.resigterBtn);
        tv_verification=findViewById(R.id.tv_getVerfication);
        iv_back=findViewById(R.id.iv_back);
    }

    //添加监听器
    public void addViewEvent(){
        registerBtn.setOnClickListener(this);
        tv_verification.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        et_verification.addTextChangedListener(textWatcher);
    }

    //editText的监听事件
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(myCountDownTimer!=null){
                myCountDownTimer.cancel();
                myCountDownTimer=null;
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };


}
