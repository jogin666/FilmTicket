package com.zy.filmticket.util;

import android.os.CountDownTimer;
import android.widget.TextView;

public class MyCountDownTimer extends CountDownTimer{

    private TextView tv_timer;

    public MyCountDownTimer(long millisInFuture, long countDownInterval,TextView tv_timer) {
        super(millisInFuture, countDownInterval);
        this.tv_timer=tv_timer;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        tv_timer.setClickable(false);
        tv_timer.setText(l/1000+"s");

    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        //重新给Button设置文字
        tv_timer.setText("重新获取");
        //设置可点击
        tv_timer.setClickable(true);
    }

}
