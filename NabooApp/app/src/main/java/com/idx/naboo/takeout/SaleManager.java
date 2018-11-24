package com.idx.naboo.takeout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.idx.naboo.takeout.data.item.TMSummary;
import com.idx.naboo.takeout.utils.Constant;
import com.idx.naboo.user.iom.login.LoginActivity;
import com.idx.naboo.user.personal_center.address.AddressActivity;
import com.idx.naboo.user.personal_center.address.AllAddress;
import com.idx.naboo.user.personal_center.address.ApiRequestUtils_1;
import com.idx.naboo.user.personal_center.address.DataBean;
import com.idx.naboo.user.personal_center.address.NewAddAddressActivity;
import com.idx.naboo.user.personal_center.address.UpdateActivity;
import com.idx.naboo.user.personal_center.address.bean.GetAdderssList;
import com.idx.naboo.utils.SharedPreferencesUtil;
import com.idx.naboo.videocall.utils.SpUtils;
import com.mor.sale.Isale;
import com.mor.sale.SaleClient;
import com.mor.sale.entity.AddressEntity;
import com.mor.sale.entity.TakeOutSummary;
import com.mor.sale.request.ElemeCreateCartRequest;
import com.xiaomor.mor.app.common.utils.LogUtils;

import net.imoran.sdk.bean.bean.TakeoutmenuBean;
import net.imoran.tv.sdk.network.callback.NetRequestCallback;
import net.imoran.tv.sdk.network.requestdata.FProtocol;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单管理类
 */

public class SaleManager {
    private static final String TAG = SaleManager.class.getSimpleName();
    private static SharedPreferencesUtil sharedPreferencesUtil;
//    private static Activity mContext;
    private static List<DataBean> mDataBean = null;


    public static void buyTakeOut(final List<TakeoutmenuBean.TakeoutmenuEntity> takeoutmenuEntityList, final TMSummary summary, final Activity context, final String where) {
//        mContext = context;
        sharedPreferencesUtil = new SharedPreferencesUtil(context);
        final ArrayList<ElemeCreateCartRequest.ElemeCreateCart.FoodBean.FoodDetailBean> foodDetailBeanList = new ArrayList<>();
        final AddressEntity addressEntity = new AddressEntity();

        String mobile = sharedPreferencesUtil.getUUID("mobile");
        if (!TextUtils.isEmpty(mobile)) {
            final GetAdderssList getAdderssList = new GetAdderssList();
            getAdderssList.setMo(mobile);//通过当前登录账号拿地址
            Log.d(TAG, "searchAddress: 当前手机号码");
            ApiRequestUtils_1.getAddressList(context, getAdderssList, 0x08, new NetRequestCallback() {
                @Override
                public void success(int requestCode, String data) {
                    String json = data.substring(data.indexOf("data") + 6, data.indexOf("msg") - 2);

                    Gson gson = new Gson();
                    AllAddress allAddress = gson.fromJson(data, AllAddress.class);
                    if (allAddress.getRet() != 200) {
                        //服务器无地址返回
                        Log.d(TAG, "success: 无地址返回");
                    } else {
                        //有地址
                        mDataBean = allAddress.getData();

                        Log.d(TAG, "isLoginAndHaveAddress: " + sharedPreferencesUtil.getUUID("uuid"));

                        //addressEntity
                        if (!TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
                            if (mDataBean != null && mDataBean.size() > 0) {
                                addressEntity.setLoginPhone(mDataBean.get(0).getConsigneeMobile());
                                addressEntity.setNotifyPhone(mDataBean.get(0).getConsigneeMobile());
                                addressEntity.setName(mDataBean.get(0).getConsignee());
                                addressEntity.setLatitude((String) mDataBean.get(0).getLatitude());
                                addressEntity.setLongitude((String) mDataBean.get(0).getLongitude());
                                LogUtils.i(TAG, "地址：" + mDataBean.get(0).getAddressDistrict()
                                        + "--" + mDataBean.get(0).getAddressRegion() + "--" + mDataBean.get(0).getAddressDetail());
                                addressEntity.setAddress(mDataBean.get(0).getAddressDistrict() + mDataBean.get(0).getAddressRegion() + mDataBean.get(0).getAddressDetail());
//                            addressEntity.setAddressJson("[{\"consignee\":\"\\u9093\",\"consigneeMobile\":\"15512345678\",\"addressProvince\":\"\",\"addressCity\":\"1525844734779\",\"addressDistrict\":\"\",\"addressDetail\":\"23\\u53f7\",\"addressAlias\":\"\\u516c\\u53f8\",\"addressType\":\"1\",\"email\":null,\"adddressid\":2820,\"addressRegion\":\"\\u5bcc\\u58eb\\u5eb7\\u5317\\u95e8(\\u516c\\u4ea4\\u7ad9)\",\"longitude\":\"114.052933\",\"latitude\":\"22.668533\"},{\"consignee\":\"\\u5218\",\"consigneeMobile\":\"15212345678\",\"addressProvince\":\"\",\"addressCity\":\"1525844771321\",\"addressDistrict\":\"\",\"addressDetail\":\"45\\u53f7\",\"addressAlias\":\"\\u516c\\u53f8\",\"addressType\":\"0\",\"email\":null,\"adddressid\":2821,\"addressRegion\":\"\\u6e05\\u6e56(\\u5730\\u94c1\\u7ad9)\",\"longitude\":\"114.036538\",\"latitude\":\"22.664085\"}]".replace(" ","").trim());

                                addressEntity.setAddressJson(json.replace(" ", "").trim());
                                Log.d(TAG, "success: " + json.trim());
//                            json.replace("\"email\":null","\"email\":\"\"")
                                addressEntity.setUserid(sharedPreferencesUtil.getUUID("uuid"));
                                if (addressEntity.checkAddress()) {
                                    Log.d(TAG, "success: 地址完整");
                                    addressNext(takeoutmenuEntityList, summary, addressEntity, context, foodDetailBeanList);
                                } else {
                                    Log.d(TAG, "success: 地址不完整");
                                    Toast.makeText(context, "请完善地址信息", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, AddressActivity.class);
                                    SpUtils.put(context, Constant.TAKEOUT_SELLER, where);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            } else {
                                Log.d(TAG, "success: 地址不完整");
                                Toast.makeText(context, "请完善地址信息", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, AddressActivity.class);
                                SpUtils.put(context, Constant.TAKEOUT_SELLER, where);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        } else {
                            //请登录
                            Log.d(TAG, "isLoginAndHaveAddress: 请登录");
                            login(context, where);
                        }
                    }
                }

                @Override
                public void mistake(int requestCode, FProtocol.
                        NetDataProtocol.ResponseStatus status, String errorMessage) {
                    Log.d(TAG, "fail: AllAddress " + requestCode);
                    login(context, where);
                }
            });
        }else {
            login(context,where);
        }
    }

    private static void addressNext(List<TakeoutmenuBean.TakeoutmenuEntity> takeoutmenuEntityList, TMSummary summary, AddressEntity addressEntity, final Context context, ArrayList<ElemeCreateCartRequest.ElemeCreateCart.FoodBean.FoodDetailBean> foodDetailBeanList) {
        if (addressEntity == null) {return;}

        //takeOutSummary
        TakeOutSummary takeOutSummary = initSummary(summary);
        if (takeoutmenuEntityList == null) return;

        //foodDetailBeanList
        for (int i = 0; i < takeoutmenuEntityList.size(); i++) {
            ElemeCreateCartRequest.ElemeCreateCart.FoodBean.FoodDetailBean foodDetailBean = new ElemeCreateCartRequest.ElemeCreateCart.FoodBean.FoodDetailBean();
            foodDetailBean.setAttrs(null);
            foodDetailBean.setGarnish(null);
            foodDetailBean.setNew_specs(null);
            foodDetailBean.setId(Long.parseLong(takeoutmenuEntityList.get(i).getId()));
            foodDetailBean.setName(takeoutmenuEntityList.get(i).getName());
            foodDetailBean.setPrice(takeoutmenuEntityList.get(i).getPrice());
            foodDetailBean.setOriginalPrice(takeoutmenuEntityList.get(i).getOriginal_price() == 0
                    ? takeoutmenuEntityList.get(i).getPrice() : takeoutmenuEntityList.get(i).getOriginal_price());
            foodDetailBean.setQuantity(Integer.parseInt(takeoutmenuEntityList.get(i).getTakeoutNum()));
            foodDetailBean.setPacking_fee(takeoutmenuEntityList.get(i).getPacking_fee());
            foodDetailBeanList.add(foodDetailBean);
        }
        SaleClient.getInstance().buyTakeOut(context, "C7484C880C551D33", addressEntity.getUserid(), foodDetailBeanList, addressEntity, takeOutSummary, new Isale() {
            @Override
            public void startSale() {}

            @Override
            public void onSaleDestroy() {}

            @Override
            public void onBackMain() {}

            @Override
            public void onAddress(boolean isAdd, AddressEntity addressEntity) {
                Log.d(TAG, "onAddress: "+isAdd);
                if (isAdd) {//新增地址
                    addAddress(context);
                }else {//修改地址-地址管理界面(本地地址修改封装bean和返回地址bean不一致,所以不能直接跳转修改地址界面)
                    modifyAddress(context);
                }
            }

            @Override
            public void onNextPart(boolean b) {}

            @Override
            public void changeTTS(String s) {}

            @Override
            public void onHomeClick() {}

            @Override
            public void onlyASR(boolean b) {}
        });
    }

    private static void addAddress(Context context) {
        Intent intent=new Intent(context,NewAddAddressActivity.class);
        SpUtils.put(context, Constant.COMMIT_ORDER_ADDRESS,Constant.COMMIT_ORDER_ADDRESS_VALUE);
        context.startActivity(intent);
    }

    private static void modifyAddress(Context context) {
        Intent intent=new Intent(context,AddressActivity.class);
        SpUtils.put(context, Constant.COMMIT_ORDER_ADDRESS,Constant.COMMIT_ORDER_ADDRESS_VALUE);
        context.startActivity(intent);
    }

    private static TakeOutSummary initSummary(TMSummary summary) {
        TakeOutSummary takeOutSummary = new TakeOutSummary();
        takeOutSummary.setAgent_fee(Integer.parseInt((new BigDecimal(summary.getAgent_fee()).setScale(0, BigDecimal.ROUND_HALF_UP)).toString()));
        takeOutSummary.setDeliver_amount(Integer.parseInt((new BigDecimal(summary.getDeliver_amount()).setScale(0, BigDecimal.ROUND_HALF_UP)).toString()));
        takeOutSummary.setDeliver_date(null);
        takeOutSummary.setIs_bookable(summary.getIs_bookable());
        takeOutSummary.setIs_dist_rst(summary.getIs_dist_rst());
        takeOutSummary.setLatitude(summary.getLatitude());
        takeOutSummary.setLongitude(summary.getLongitude());
        takeOutSummary.setNo_agent_fee_total(Integer.parseInt((new BigDecimal(summary.getNo_agent_fee_total()).setScale(0, BigDecimal.ROUND_HALF_UP)).toString()));
        takeOutSummary.setRestaurant_address(summary.getRestaurant_address());
        takeOutSummary.setRestaurant_name(summary.getRestaurant_name());
        takeOutSummary.setDeliver_times(summary.getDeliver_times());
        takeOutSummary.setPhone_list(summary.getPhone_list());
        takeOutSummary.setServing_time(summary.getServing_time());
        return takeOutSummary;
    }

    private static void login(Context context, String where) {
        Toast.makeText(context, "请先登录账户！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, LoginActivity.class);
        SpUtils.put(context,Constant.TAKEOUT_SELLER,where);
//        intent.putExtra("type", "takeout");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
