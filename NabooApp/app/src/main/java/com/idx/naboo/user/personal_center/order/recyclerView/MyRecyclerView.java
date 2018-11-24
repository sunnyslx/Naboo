package com.idx.naboo.user.personal_center.order.recyclerView;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idx.naboo.NabooApplication;
import com.idx.naboo.R;
import com.idx.naboo.user.personal_center.order.orderbean.Items;
import com.idx.naboo.user.personal_center.order.orderbean.Orders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by ryan on 18-4-26.
 * Email: Ryan_chan01212@yeah.net
 */

public class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.MyHolder> {

    private static final String TAG = "MyRecyclerView";
    private List<Orders> order;
    private MyHolder holder;
    private long order_time;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MyRecyclerView(Context context,List<Orders> orders){
        mContext = context;
        order = orders;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }

    //OnCreateViewHolder用来给rv创建缓存的
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(NabooApplication.getInstance().getBaseContext()).inflate(R.layout.order_item,parent,false);
        holder = new MyHolder(view);
        return holder;
    }


    //给缓存控件设置数据
    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
         final Orders orders = order.get(position);
        holder.text_id.setText(String.valueOf(position + 1));

        holder.business_name.setText(orders.getOrderName());     // 商家名
        //holder.business_photo.(orders.seller_image); //商家图片

        //获得年月日
        String payData = orders.getCreateTime().substring(0,10);
        Log.d(TAG, "当前的日期是" +  payData);

        //时分秒
        String payTime = orders.getCreateTime().substring(11);
        Log.d(TAG, "当前的日期是" +  payTime);

        //总时间
        String payDataTime = payData+ " " + payTime;
        try {
            order_time = stringToLong(payDataTime,"yyyy-MM-dd HH:mm:ss");
            Log.d(TAG, "下订单的时间: " + String.valueOf(order_time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.pay_time.setText(payData + getWeekByDateStr(payData) + " "+ payTime); //下订单时间
        double total = orders.getTotalPrice() / 100;
        holder.pay_money.setText("¥"+String.valueOf(total)); //商品总价
        List<Items> items = orders.getItems();

        //选择列表第一个商品名
        int sum = 0;  //获取购买商品的总数量
        for (Items item: items) {
            int count = item.getCount();
            sum = sum + count;
        }
        holder.business_number.setText(items.get(0).getName()+"等,共" + String.valueOf(sum) + "件商品");

        Glide.with(mContext)
                .load(orders.getAttributes().get(2).getString().get(0))
                .placeholder(R.mipmap.img_seize)
                .override(150, 150)
                .into(holder.business_photo);




        //获取现在的时间 long
        long currte_time = System.currentTimeMillis();

        if (orders.getStatusName().equals("支付超时关闭")){
            holder.pay_money_time.setVisibility(View.GONE);
            Log.d(TAG, "隐藏倒计时");
        }else {

            if (currte_time - (order_time) < 14 * 60 *1000 ){
                holder.pay_money_time.setVisibility(View.VISIBLE);
                //在这里写倒计时
                long remaining_time = (14*1000*60)-(currte_time - order_time);
                Log.d(TAG, "显示倒计时");
                holder.countDownTimer = new CountDownTimer(remaining_time, 1000) {
                    /**
                     * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
                     * @param millisUntilFinished
                     */
                    @Override
                    public void onTick(long millisUntilFinished) {
                        holder.pay_money_time.setText(formatTime(millisUntilFinished));
                    }

                    /**
                     * 倒计时完成时被调用
                     */
                    @Override
                    public void onFinish() {
                        holder.pay_money_time.setVisibility(View.GONE);
                    }
                }.start();


            }else {
                holder.pay_money_time.setVisibility(View.GONE);
                Log.d(TAG, "隐藏倒计时");
            }


        }

        if( mOnItemClickListener!= null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return order.size();
    }
    //在这里初始化 控件
    class MyHolder extends RecyclerView.ViewHolder{
        TextView text_id;
        TextView business_name;     //商家名
        TextView order_pay;
        ImageView business_photo;
        TextView pay_time;
        TextView business_number;
        TextView pay_money;
        TextView pay_money_time;
        public CountDownTimer countDownTimer;

        public MyHolder(View itemView) {
            super(itemView);
            text_id = itemView.findViewById(R.id.text_id);
            business_name = itemView.findViewById(R.id.business);
            order_pay = itemView.findViewById(R.id.order_pay);
            business_photo = itemView.findViewById(R.id.business_photo);
            business_number = itemView.findViewById(R.id.business_number);
            pay_money = itemView.findViewById(R.id.pay_money);
            pay_money_time = itemView.findViewById(R.id.pay_money_time);
            pay_time = itemView.findViewById(R.id.pay_time);
        }
    }





    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "支付（剩余"+ "0" + minute + ":" + "0" + second + ")";
            } else {
                return "支付（剩余"+ "0" + minute + ":" + second + ")";
            }
        }else {
            if (second < 10) {
                return "支付（剩余" + minute + ":" + "0" + second + ")";
            } else {
                return "支付（剩余" + minute + ":" + second + ")";
            }
        }
    }


    //string类型转换为long类型
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date.getTime();
    }


    /**
     * <pre>
     * 根据指定的日期字符串获取星期几
     * </pre>
                *
                * @param strDate 指定的日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @return week
     *         星期几(MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY)
     */
    public static String getWeekByDateStr(String strDate)
    {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(5, 7));
        int day = Integer.parseInt(strDate.substring(8, 10));

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);

        String week = "";
        int weekIndex = c.get(Calendar.DAY_OF_WEEK);

        switch (weekIndex)
        {
            case 1:
                week = "(日)";
                break;
            case 2:
                week = "(一)";
                break;
            case 3:
                week = "(二)";
                break;
            case 4:
                week = "(三)";
                break;
            case 5:
                week = "(四)";
                break;
            case 6:
                week = "(五)";
                break;
            case 7:
                week = "(六)";
                break;
        }
        return week;
    }


}
