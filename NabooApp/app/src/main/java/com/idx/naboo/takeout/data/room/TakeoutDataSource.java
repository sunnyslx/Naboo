package com.idx.naboo.takeout.data.room;

import java.util.List;

/**
 * Created by danny on 4/21/18.
 */

public interface TakeoutDataSource {
    //加载单个用户信息
    interface LoadSellerCallback{
        void onSuccess(TakeoutSeller seller);
        void onError();
    }

    //加载所有用户信息
    interface LoadAllSellerCallback{
        void onSuccess(List<TakeoutSeller> sellers);
        void onError();
    }

    interface SaveOverCallback{void onSuccess();}

    void insertTakeoutSeller(TakeoutSeller seller,SaveOverCallback callback);

    void queryAllFood(String sellerName, LoadAllSellerCallback callback);

    void findFood(String foodName,LoadSellerCallback callback);

    void updateFoodCount(int i, String foodName, SaveOverCallback callback);

    void modifyFoodCount(int i, String foodName, SaveOverCallback callback);

    void deleteTakeoutSeller(String sellerName);

    void deleteFood(String foodName, SaveOverCallback callback);

    void deleteTakeoutSeller();
}
