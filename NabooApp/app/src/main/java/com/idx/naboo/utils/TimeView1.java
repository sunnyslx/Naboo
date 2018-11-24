//package com.idx.naboo.utils;
//
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.os.Handler;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.view.Gravity;
//import android.widget.TextClock;
//import android.widget.TextView;
//
//import java.util.Calendar;
//
///**
// * Created by Franck on 12/15/17.
// */
//
//public class TimeView1 extends android.support.v7.widget.AppCompatTextView {
//    private TextView textView;
//    private String timeString;
//    private  TimeHandler mTimehandler=new TimeHandler();
//
//    public TimeView1(Context context) {
//        super(context);
//        this.textView=this;
//        Init(context);
//    }
//
//    public TimeView1(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.textView=this;
//        Init(context);
//    }
//
//    public TimeView1(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.textView=this;
//        Init(context);
//    }
//    //初始化方法
//    private void Init(Context context) {
//        try {
//            //初始化textview显示时间
//            updateClock();
//            //更新进程开始
//            new TimeThread().start();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    //更新子线程
//    private class TimeThread extends Thread{
//        @Override
//        public void run() {
//            mTimehandler.startScheduleUpdate();
//        }
//    }
//
//    //重要的更新Handler
//    private class   TimeHandler  extends Handler {
//        private boolean mStopped;
//        private void post(){
//            sendMessageDelayed(obtainMessage(0),1000*(60- Calendar.getInstance().get(Calendar.SECOND)));
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (!mStopped){
//                updateClock();
//                post();
//            }
//        }
//        //开始更新
//        public void startScheduleUpdate(){
//            mStopped = false;
//            post();
//        }
//        //停止更新
//        public void stopScheduleUpdate(){
//            mStopped = true;
//            removeMessages(0);
//        }
//    }
//
//    private void updateClock() {
//        //更新时间
//        Calendar calendar=Calendar.getInstance();
//        int hour=calendar.get(Calendar.HOUR_OF_DAY);
//        int minute=calendar.get(Calendar.MINUTE);
//        if (hour >= 10){
//            if (minute>=10){
//            timeString=hour+":"+minute;
//            }else {
//                timeString=hour+":"+ "0" + minute;
//            }
//
//        }else {
//            if (minute>10){
//                timeString = "0"+ hour + ":"+minute;
//            }else {
//                timeString = "0"+ hour + ":"+ "0" + minute;
//
//            }
//        }
//        textView.setText(timeString);
//    }
//
//
//}