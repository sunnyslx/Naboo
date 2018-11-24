package com.idx.naboo.takeout.data.room;

/**
 * Created by danny on 4/21/18.
 */

public class TakeoutRepository implements TakeoutDataSource{
    private static TakeoutRepository sInstance=null;
    private LocalTakeoutSellerDataSource mDataSource;

    private TakeoutRepository(LocalTakeoutSellerDataSource dataSource){this.mDataSource=dataSource;}

    public static TakeoutRepository getInstance(LocalTakeoutSellerDataSource dataSource){
        if (sInstance==null){
            synchronized (TakeoutRepository.class){
                if (sInstance==null){
                    sInstance=new TakeoutRepository(dataSource);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertTakeoutSeller(TakeoutSeller seller,SaveOverCallback callback) {mDataSource.insertTakeoutSeller(seller,callback);}

    @Override
    public void queryAllFood(String sellerName,LoadAllSellerCallback callback) {mDataSource.queryAllFood(sellerName,callback);}

    @Override
    public void findFood(String foodName, LoadSellerCallback callback) {mDataSource.findFood(foodName,callback);}

    @Override
    public void updateFoodCount(int i, String foodName, SaveOverCallback callback) {mDataSource.updateFoodCount(i,foodName,callback);}

    @Override
    public void modifyFoodCount(int i, String foodName, SaveOverCallback callback) {mDataSource.modifyFoodCount(i,foodName,callback);}

    @Override
    public void deleteTakeoutSeller(String sellerName) {mDataSource.deleteTakeoutSeller(sellerName);}

    @Override
    public void deleteFood(String foodName, SaveOverCallback callback) {mDataSource.deleteFood(foodName,callback);}

    @Override
    public void deleteTakeoutSeller() {mDataSource.deleteTakeoutSeller();}
}
