package com.idx.naboo.takeout.data.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * 外卖表
 * Created by danny on 4/21/18.
 */
@Entity(tableName = "seller")
public class TakeoutSeller {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public String seller_name;
    public String food_name;
    public String food_image;
    public int count;
    public double price;
    public double old_price;
    public double agent_fee;//配送费
    public double packing_fee;//打包费
    public double no_agent_fee_total;//满多少免配送费
    public int activity_max_quantity;//最大购买份数

    @Override
    public String toString() {
        return "TakeoutSeller{" +
                "id=" + id +
                ", seller_name='" + seller_name + '\'' +
                ", food_name='" + food_name + '\'' +
                ", count=" + count +
                ", price=" + price +
                ", old_price=" + old_price +
                ", agent_fee=" + agent_fee +
                ", packing_fee=" + packing_fee +
                ", no_agent_fee_total=" + no_agent_fee_total +
                ", activity_max_quantity=" + activity_max_quantity +
                '}';
    }
}
