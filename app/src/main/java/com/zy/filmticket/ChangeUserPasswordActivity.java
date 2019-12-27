package com.zy.filmticket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.UserService;
import com.zy.filmticket.util.HttpUtil;
import com.zy.filmticket.message.SMSUtils;
import com.zy.filmticket.util.MyCountDownTimer;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangeUserPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private MyCountDownTimer myCountDownTimer;

    private EditText et_pass;
    private EditText et_sPass;
    private EditText et_vrif;
    private TextView tv_getVerif;
    private Button sumbitBtn;
    private ImageView iv_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_password);
        initView();
        viewAddEvent();
    }

    public void initView(){
        et_pass=findViewById(R.id.et_Pass);
        et_sPass=findViewById(R.id.et_sPass);
        et_vrif=findViewById(R.id.et_et_verif);
        tv_getVerif=findViewById(R.id.tv_getVerf);
        sumbitBtn=findViewById(R.id.sumbitBtn);

        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.titleText);

        tv_title.setText("修改密码");
    }

    public void viewAddEvent(){
        tv_getVerif.setOnClickListener(this);
        sumbitBtn.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        et_vrif.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                Intent intent=new Intent();
                intent.putExtra("result","ok");
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.tv_getVerf:
                if (HttpUrl.isLogin) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SMSUtils.sendVerfication(HttpUrl.useraccount); //发送短信
                        }
                    }).start();
                    myCountDownTimer=new MyCountDownTimer(60000,1000,tv_getVerif);
                    myCountDownTimer.start();
                }
                break;
            case R.id.sumbitBtn:
                sumbit();
                break;
        }
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

    public void sumbit(){
        String password=et_pass.getText().toString();
        String sPass=et_sPass.getText().toString();
        if (!TextUtils.isEmpty(password)){
            if (sPass.equals(password)) {
                String verification=et_vrif.getText().toString();
                Log.i("useraccount",HttpUrl.useraccount);
                if (SMSUtils.verificationIsCorrect("123456")){
                    String url=HttpUrl.filmTicketUrl+HttpUrl.updateUserPassword+"?useraccount="+HttpUrl.useraccount+"&password="+sPass;
                    Log.d("urlUpateUserPass",url);
                    HttpUtil.sendOKHttpRequest(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChangeUserPasswordActivity.this,"请求失败！",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String resopnseText=response.body().string();
                            String result= UserService.handleUserUpdatePass(resopnseText);
                            if ("true".equals(result)){
                                successfulTip();
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangeUserPasswordActivity.this,"修改密码失败！",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }else{
                showTip("密码不一致！");
            }
        }else{
            showTip("密码不能为空！");
        }
    }

    public void showTip(final String content){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView msg = new TextView(ChangeUserPasswordActivity.this);		 //消息居中
                msg.setText(content);
                msg.setPadding(10, 20, 10, 10);
                msg.setGravity(Gravity.CENTER);
                msg.setTextSize(18);

                final AlertDialog.Builder builder=
                        new AlertDialog.Builder(ChangeUserPasswordActivity.this)
                                .setTitle("修改密码提示！")
                                .setView(msg);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void successfulTip(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView msg = new TextView(ChangeUserPasswordActivity.this);		 //消息居中
                msg.setText("修改密码成功！");
                msg.setPadding(10, 20, 10, 10);
                msg.setGravity(Gravity.CENTER);
                msg.setTextSize(18);

                final AlertDialog.Builder builder=
                        new AlertDialog.Builder(ChangeUserPasswordActivity.this)
                                .setTitle("修改密码提示！")
                                .setView(msg);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent=new Intent();
                        intent.putExtra("result","ok");
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
                builder.create().show();
            }
        });
    }

}
