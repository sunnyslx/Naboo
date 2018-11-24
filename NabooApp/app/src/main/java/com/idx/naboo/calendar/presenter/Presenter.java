package com.idx.naboo.calendar.presenter;

import com.idx.calendarview.CalendarView;
import com.idx.naboo.calendar.Iview;

/**
 * Created by geno on 20/12/17.
 */

public class Presenter implements Ipresenter{
    Iview iview;
    CalendarView calendarView;
    int mYear,mMonth,mDay;
    int hour,minutes;
    private boolean flag = true;
    private static final int msgKey1 = 1;
    public Presenter(Iview iview, CalendarView calendarView) {
        super();
        this.iview = iview;
        this.calendarView = calendarView;
        init();
    }
    private void init(){
        mYear = calendarView.getCurYear();
        mMonth = calendarView.getCurMonth();
        mDay = calendarView.getCurDay();
    }
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case msgKey1:
//                    long time = System.currentTimeMillis();
//                    Date date = new Date(time);
//                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//                    iview.showtime(format.format(date));
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
    @Override
    public void selectyear() {
      iview.showyear(mYear);

    }

    @Override
    public void selectmonth() {
      iview.showmonth(mYear,mMonth,mDay);
    }

    public void Destroy(){
        //flag = false;
        if (calendarView != null){
            calendarView = null;
        }
    }
}
