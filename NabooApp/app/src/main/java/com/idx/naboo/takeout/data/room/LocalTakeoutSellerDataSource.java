package com.idx.naboo.takeout.data.room;

import com.idx.naboo.videocall.utils.NabooExecutors;

import java.util.List;

/**
 * Created by danny on 4/21/18.
 */

public class LocalTakeoutSellerDataSource implements TakeoutDataSource {
    private static volatile LocalTakeoutSellerDataSource sInstance = null;
    private NabooExecutors mNabooExecutors;
    private TakeoutSellerDao mSellerDao;
    private TakeoutSeller mTakeoutSeller;
    private List<TakeoutSeller> mSellers;

    private LocalTakeoutSellerDataSource(NabooExecutors nabooExecutors, TakeoutSellerDao takeoutSellerDao) {
        this.mNabooExecutors = nabooExecutors;
        this.mSellerDao = takeoutSellerDao;
    }


    public static LocalTakeoutSellerDataSource getInstance(NabooExecutors nabooExecutors, TakeoutSellerDao takeoutSellerDao) {
        if (sInstance == null) {
            synchronized (LocalTakeoutSellerDataSource.class) {
                if (sInstance == null) {
                    sInstance = new LocalTakeoutSellerDataSource(nabooExecutors, takeoutSellerDao);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void insertTakeoutSeller(final TakeoutSeller seller, final SaveOverCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.insertTakeoutSeller(seller);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){callback.onSuccess();}
                    }
                });
            }
        });
    }

    @Override
    public void queryAllFood(final String sellerName, final LoadAllSellerCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellers = mSellerDao.queryAllFood(sellerName);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!mSellers.isEmpty()) {
                            if (callback != null) {callback.onSuccess(mSellers);}
                        } else {
                            if (callback != null) {callback.onError();}
                        }
                    }
                });
            }
        });
    }

    @Override
    public void findFood(final String foodName, final LoadSellerCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mTakeoutSeller=mSellerDao.findFood(foodName);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mTakeoutSeller!=null){
                            if (callback!=null){callback.onSuccess(mTakeoutSeller);}
                        }else {
                            if (callback!=null){callback.onError();}
                        }
                    }
                });
            }
        });
    }

    @Override
    public void updateFoodCount(final int i, final String foodName, final SaveOverCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.updateFoodCount(i,foodName);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){callback.onSuccess();}
                    }
                });
            }
        });
    }

    @Override
    public void modifyFoodCount(final int i, final String foodName, final SaveOverCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.modifyFoodCount(i,foodName);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){callback.onSuccess();}
                    }
                });
            }
        });
    }

    @Override
    public void deleteTakeoutSeller(final String sellerName) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {mSellerDao.deleteTakeoutSeller(sellerName);}
        });
    }

    @Override
    public void deleteFood(final String foodName, final SaveOverCallback callback) {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mSellerDao.deleteFood(foodName);
                mNabooExecutors.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {if (callback!=null){callback.onSuccess();}}
                });
            }
        });
    }

    @Override
    public void deleteTakeoutSeller() {
        mNabooExecutors.getIoExecutor().execute(new Runnable() {
            @Override
            public void run() {mSellerDao.deleteTakeoutSeller();}
        });
    }
}
