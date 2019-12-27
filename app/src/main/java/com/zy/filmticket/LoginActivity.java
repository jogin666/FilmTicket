package com.zy.filmticket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.UserService;
import com.zy.filmticket.util.HttpUtil;
import com.zy.filmticket.util.MyCountDownTimer;
import com.zy.filmticket.message.SMSUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView titleView;//标题
    private TextView tv_forgetPassword;//忘记密码
    private TextView tv_register; //注册
    private TextView tv_verification; //短信验证码
    private EditText et_account; //账号
    private EditText et_password; //密码
    private ImageView iv_back;//返回图标
    //第三方登录
    private ImageView iv_QQ;
    private ImageView iv_WeChat;
    //登录按钮
    private Button loginBtn;

    private MyCountDownTimer myCountDownTimer;

    private boolean loginWayByPassword=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        viewAddEvent();
    }

    //绑定控件
    public void initView(){
        titleView=findViewById(R.id.titleText);
        titleView.setText(R.string.login);
        et_account=findViewById(R.id.tv_account);
        et_password=findViewById(R.id.tv_password);
        tv_forgetPassword=findViewById(R.id.forgetPass);
        tv_register=findViewById(R.id.tv_register);
        tv_verification=findViewById(R.id.verification);
        iv_QQ=findViewById(R.id.img_QQ);
        iv_WeChat=findViewById(R.id.img_WeChat);
        loginBtn=findViewById(R.id.loginBtn);
        iv_back=findViewById(R.id.iv_back);
    }

    //添加监听
    public void viewAddEvent(){
        loginBtn.setOnClickListener(this);
        tv_verification.setOnClickListener(this);
        iv_QQ.setOnClickListener(this);
        iv_WeChat.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forgetPassword.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        et_password.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn://登录
                if (!loginWayByPassword) {
                    loginByVerification(); //验证码登录
                }else{
                    login(); //账号密码登录
                }
                break;
            case R.id.tv_register: //注册
                Intent intent=new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.verification: //短信码登录
                changeText();
                break;
            case R.id.forgetPass: //找回密码
                if (!loginWayByPassword){
                    sendVerfication();
                }else{
                    //忘记密码
                }
                break;
            case R.id.iv_back: //返回一个界面
                Intent intent1=new Intent();
                intent1.putExtra("result","no");
                setResult(RESULT_OK,intent1);
                finish();
                break;
            case R.id.img_QQ: //QQ登录
                loginByQQ();
                break;
            case R.id.img_WeChat: //微信登录
                loginByWeChat();
                break;
        }
    }

    //信息变化
    public void  changeText(){
        if (!"密码登录".equals(tv_verification.getText().toString())) {
            tv_verification.setText("密码登录");
            tv_forgetPassword.setText("获取验证码");
            loginWayByPassword=false;
        }else{
            tv_verification.setText("短信验证码登录");
            tv_forgetPassword.setText("忘记密码");
            loginWayByPassword=true;
        }
    }


    //账号密码登陆事件
    private void login() {
        final String account=et_account.getText().toString();
        String password=et_password.getText().toString();
        if (accountIsExit()) {
            if (!TextUtils.isEmpty(password)) {
                String url = HttpUrl.filmTicketUrl + HttpUrl.login + "?useraccount=" + account + "&password=" + password;
                Log.d("url", url);
                check(url, account, "byPassword");
            }else {
                showErrorTip("密码不能为空！");
            }
        }
    }

    //发送验证码
    public void sendVerfication(){
        final String mobile=et_account.getText().toString();
        if (accountIsExit()) { //验证手机号码
            new Thread( new Runnable() {
                @Override
                public void run() {
                    SMSUtils.sendVerfication(mobile); //发送短信
                }
            }).start();
            myCountDownTimer=new MyCountDownTimer(60000,1000,tv_forgetPassword);
            myCountDownTimer.start();
        }
    }

    //验证码登录
    public void loginByVerification(){
        final String account=et_account.getText().toString();
        String verification=et_password.getText().toString();
        if(accountIsExit() && SMSUtils.isMobilePhoneNumber(account)){
            if (!TextUtils.isEmpty(verification)) {
                if (SMSUtils.verificationIsCorrect(verification)) {
                    String url = HttpUrl.filmTicketUrl + HttpUrl.isExitUser + "?useraccount=" + account;
                    Log.d("url", url);
                    check(url, account, "byVerfication");
                }else{
                    showErrorTip("验证码错误！");
                }
            }else{
                showErrorTip("验证码不能为空！");
            }
        }
    }

    //联网登录
    public void check(String url, final String account, final String type){
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                      showErrorTip("登录失败！");
                   }
               });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                String result=UserService.handleUserLogin(responseText);
                if ("true".equals(result)) {
                    markUserLogin(account);
                    //跳转到主页面
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("byVerfication".equals(type)) {
                                showErrorTip("账号不存在！");
                            }else if ("byPassword".equals(type)){
                               showErrorTip("账号或密码错误!");
                            }
                        }
                    });
                }
            }
        });
    }

    //将数据放入到缓存中
    public void markUserLogin(final String useraccount){
        HttpUrl.isLogin=true;
        HttpUrl.useraccount=useraccount;
        Intent intent=new Intent();
        intent.putExtra("result","ok");
        setResult(RESULT_OK,intent);
        finish();
//        SharedPreferences dataBase = getSharedPreferences( "SharedPreferences", Activity.MODE_PRIVATE);
//        dataBase.edit().putString("userdata",useraccount);
//        dataBase.edit().commit();
    }

    //是否符合登录条件
//    private boolean canLogin() {
//        if (accountIsExit()){ //密码是否为空
//            if ("".equals(et_password.getText().toString())) {
//                showErrorTip("密码不能为空！");
//                return false;
//            }
//        }
//        return true;
//    }

    //账号是否是手机号码
    public boolean accountIsExit(){
        String account=et_account.getText().toString();
        //判空
        if (TextUtils.isEmpty(account)){
            showErrorTip("账号不能为空！");
            return false;
        }
        //是否是mobile
        if (!SMSUtils.isMobilePhoneNumber(account)) {
            showErrorTip("账号不存在！");
            return false;
        }
        return true;
    }

    //错误提示
    public void showErrorTip(String context){
        TextView msg = new TextView(LoginActivity.this);		 //消息居中
        msg.setText(context);
        msg.setPadding(10, 20, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        final AlertDialog.Builder builder=
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("登录提示")
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


    //微信登录
    private void loginByWeChat() {
    }

    //QQ登录
    private void loginByQQ() {
    }


    //text文本的监听事件
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(myCountDownTimer!=null){
                myCountDownTimer.cancel();
                myCountDownTimer=null;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
