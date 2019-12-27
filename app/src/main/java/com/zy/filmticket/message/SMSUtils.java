package com.zy.filmticket.message;


import com.zy.filmticket.message.alicloud.SendSMS;
import com.zy.filmticket.util.MyCountDownTimer;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSUtils {

    private static String result=""; //验证码
    private static MyCountDownTimer myCountDownTimer;

    //检验手机号码
    public static boolean isMobilePhoneNumber(String mobilePhoneNumber){
        String checkPhoneNum="^((13[0-9])|(14[5,7])|(15[^4,\\D])|(18[0-9])|(17[0,6,7,8]))\\d{8}$";
        Pattern p = Pattern.compile(checkPhoneNum);
        Matcher m = p.matcher(mobilePhoneNumber);
        return m.matches();
    }

    //验证验证码是否正确
    public static boolean verificationIsCorrect(String verification){
        if(verification.equals(result)){
            return true;
        }
        return false;
    }

    //发送验证码
    public static String sendVerfication(String mobile){
        result="";
        Random random=new Random();
        for (int i=0;i<6;i++){
            result+=random.nextInt(10);
        }
        try {
            SendSMS.sendSmms(mobile,result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
