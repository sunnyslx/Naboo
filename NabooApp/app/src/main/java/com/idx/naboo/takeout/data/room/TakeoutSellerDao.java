package com.idx.naboo.takeout.data.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * 外卖表操作接口
 * Created by danny on 4/21/18.
 */
@Dao
public interface TakeoutSellerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTakeoutSeller(TakeoutSeller seller);

    @Query("SELECT * From seller WHERE seller_name= :sellerName")
    List<TakeoutSeller> queryAllFood(String sellerName);

    @Query("SELECT * From seller WHERE food_name= :foodName")
    TakeoutSeller findFood(String foodName);

    @Query("UPDATE seller SET count= :i+count WHERE food_name= :foodName")
    void updateFoodCount(int i, String foodName);

    @Query("UPDATE seller SET count= :i WHERE food_name= :foodName")
    void modifyFoodCount(int i, String foodName);

    @Query("DELETE FROM seller WHERE seller_name= :sellerName")
    void deleteTakeoutSeller(String sellerName);

    @Query("DELETE FROM seller WHERE food_name= :foodName")
    void deleteFood(String foodName);

    @Query("DELETE FROM seller")
    void deleteTakeoutSeller();
}
