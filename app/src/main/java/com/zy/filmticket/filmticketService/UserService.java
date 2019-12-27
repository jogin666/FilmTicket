package com.zy.filmticket.filmticketService;

import android.text.TextUtils;
import android.util.Log;

import com.zy.filmticket.entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;


public class UserService {

    //用户登录
    public static String handleUserLogin(String responseText){
        return showResult(responseText);
    }

    //更改密码
    public static String handleUserUpdatePass(String responseText){
        return showResult(responseText);
    }

    private static String showResult(String responseText){
        try{
            if (!TextUtils.isEmpty(responseText)) {
                JSONObject object = new JSONObject(responseText);
                String result=object.get("result").toString();
                return result;
            }
        }catch(JSONException e){
            Log.d("tip","解释失败！");
            e.printStackTrace();
        }
        return "false";
    }

    //更新用户信息
    public static String handleUserUpateUserInfo(String responseText){
        return showResult(responseText);
    }

    //增加用户
    public static String handleAddUser(String responseText){
        return showResult(responseText);
    }

    //查询用户信息
    public static UserEntity handleFindUserByAccount(String responeText){
        try{
            if (!TextUtils.isEmpty(responeText)) {
                Log.d("userInfo",responeText);
                JSONObject object = new JSONObject(responeText);
                String useraccount=object.getString("useraccount");
                String username=object.getString("username");
                String gender=object.getString("gender");
                String headImg=object.getString("headImg");
                String brithday=object.getString("brithday");
                String seleIntroduction="";
                Object o=object.get("selfIntroduction");
                if (object.getString("selfIntroduction")!=null){
                   seleIntroduction=object.getString("selfIntroduction");
                }
                UserEntity userEntity=new UserEntity(useraccount,username,gender,headImg,brithday,seleIntroduction);
                return userEntity;
            }
        }catch(JSONException e){
            Log.d("tip","解释失败！");
            e.printStackTrace();
        }
        return null;
    }

}
