package com.zy.filmticket;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zy.filmticket.entity.UserEntity;
import com.zy.filmticket.filmticketService.HttpUrl;
import com.zy.filmticket.filmticketService.UserService;
import com.zy.filmticket.util.HttpUtil;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_title; //标题
    private TextView tv_editTitle; //编辑

    private ImageView imageView;
    private EditText tv_userName; //昵称
    private RadioGroup userGenderGroup; //性别
    private EditText tv_userBrithday; //生日
    private EditText tv_userIntroduction; //介绍
    private RelativeLayout layout;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        viewAddEvent();
        requestUserInfo();
    }

    public void requestUserInfo(){
        String url= HttpUrl.filmTicketUrl+HttpUrl.findUserInfo+"?useraccount="+HttpUrl.useraccount;
        Log.d("url",url);
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserInfoActivity.this,"网络异常！",Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                final UserEntity userEntity= UserService.handleFindUserByAccount(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUserInfo(userEntity);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_endTitle:
                String text=tv_editTitle.getText().toString();
                if (text.equals("保存")){
                    updateUserInfo();
                    tv_editTitle.setText("编辑");
                    changeState("编辑");
                }else{
                    tv_editTitle.setText(R.string.userInfoSave);
                    changeState("保存");
                }
                break;
            case R.id.headImgLayout:
                changeImge();
                break;
            case R.id.iv_back:
                finish();

        }
    }

    //绑定控件
    private void initView() {
        tv_title=findViewById(R.id.titleText); //标题
        tv_title.setText("修改个人信息");

        tv_editTitle=findViewById(R.id.tv_endTitle); //编辑
        tv_editTitle.setTextSize(16);
        tv_editTitle.setText(R.string.userInfoEdit);

        imageView=findViewById(R.id.userHeadUrl); //头像
        tv_userName=findViewById(R.id.tv_username); //用户名
        tv_userIntroduction=findViewById(R.id.et_introduction); //介绍
        userGenderGroup=findViewById(R.id.genderGroup); //性别组
        tv_userBrithday=findViewById(R.id.tv_brithday); //出生年月日
        layout=findViewById(R.id.headImgLayout); //头像布局

        iv_back=findViewById(R.id.iv_back);

    }

    //更换头像
    public void changeImge(){
        //自定义alertDialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this).setTitle("更新学生信息");
        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.change_head_img_layout,null);
        builder.setView(view);
        final AlertDialog alertDialog=builder.create();
        TextView textView=view.findViewById(R.id.tipFootView);
        ImageView imgFile=view.findViewById(R.id.img_img);
        ImageView phoneImg=view.findViewById(R.id.img_camera);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    //改变控件的状态
    private void changeState(String type){
        if (type.equals("保存")) {
            tv_userName.setEnabled(true);
            tv_userIntroduction.setEnabled(true);
            userGenderGroup.setEnabled(true);
            tv_userBrithday.setEnabled(true);
            layout.setEnabled(true);
        }else if ("编辑".equals(type)) {
            tv_userName.setEnabled(false);
            tv_userIntroduction.setEnabled(false);
            userGenderGroup.setEnabled(false);
            tv_userBrithday.setEnabled(false);
            layout.setEnabled(false);
        }
    }

    //添加监听事件
    private void viewAddEvent(){
        tv_editTitle.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    //保存修改后的用户
    private void updateUserInfo(){
        UserEntity userEntity=getUserInfo();
        if (userEntity!=null){
            StringBuffer userInfo=new StringBuffer();
            try {
                userInfo.append("useraccount=" + userEntity.getUseraccount()) //用户名
                        .append("&username=")
                        .append(URLEncoder.encode(userEntity.getUsername(), "utf-8")) //名称
                        .append("&gender=")
                        .append(URLEncoder.encode(userEntity.getAgender(), "utf-8")) //性别
                        .append("&headImg=")
                        .append("upload/head_img.jpg") //头像
                        .append("&selfIntroduction=")
                        .append(URLEncoder.encode(userEntity.getSelfIntroduction(),"utf-8"))//个人介绍
                        .append("&brithday=").append(URLEncoder.encode(userEntity.getBrithday(),"utf-8"));
            }catch(IOException e){
                System.out.println("编码异常！");
                e.printStackTrace();
            }
            String url = HttpUrl.filmTicketUrl + HttpUrl.updateUserInfo + "?" +userInfo.toString();
            Log.d("updateUserInfoUrl",url);
            HttpUtil.sendOKHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserInfoActivity.this,"请求失败！",Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    final String result=UserService.handleUserUpateUserInfo(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("true".equals(result)) {
                                Toast.makeText(UserInfoActivity.this,"保存成功！",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(UserInfoActivity.this,"保存失败！",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
    }

    //获取修改后的用户信息
    public UserEntity getUserInfo(){
        //昵称
        String username=tv_userName.getText().toString();
        //一句话介绍
        String seleIntroduction=tv_userIntroduction.getText().toString();
        //性别
        int id=userGenderGroup.getCheckedRadioButtonId();
        RadioButton radioButton=userGenderGroup.findViewById(id);
        String gender=radioButton.getText().toString();
        //头像
//        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream os=new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG,100,os);
//        String headImg=os.toByteArray().toString();
        //
        String brithday=tv_userBrithday.getText().toString();
        UserEntity userEntity=new UserEntity(HttpUrl.useraccount,username,gender,"",brithday,seleIntroduction);
        return userEntity;
    }

    //显示用户的信息
    private void showUserInfo(UserEntity userEntity){
        String headImgUrl=userEntity.getHeadImg();
        if (!"null".equals(headImgUrl) && !TextUtils.isEmpty(headImgUrl)){
            String headImg=HttpUrl.filmTicketUrl+userEntity.getHeadImg();
            Glide.with(this).load(headImg).into(imageView);
        }
        if (!"null".equals(userEntity.getUsername())) {
            tv_userName.setText(userEntity.getUsername()); //昵称
        }
        if (!"null".equals(userEntity.getSelfIntroduction())) {
            tv_userIntroduction.setText(userEntity.getSelfIntroduction()); //一句话介绍
        }
        if (!"null".equals(userEntity.getAgender())){ //性别
            if ("男".equals(userEntity.getAgender())){
                RadioButton radioButton=userGenderGroup.findViewById(R.id.man);
                radioButton.setChecked(true);
            }else{
                RadioButton radioButton=userGenderGroup.findViewById(R.id.women);
                radioButton.setChecked(true);
            }
        }
        if (!"null".equals(userEntity.getBrithday())) { //生日
            tv_userBrithday.setText(userEntity.getBrithday());
        }
    }

}
