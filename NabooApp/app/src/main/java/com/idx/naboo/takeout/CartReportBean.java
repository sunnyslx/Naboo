package com.idx.naboo.takeout;


import net.imoran.sdk.bean.base.BaseEntity;

import java.util.List;

/**
 * 购物车到服务器
 * Created by danny on 5/21/18.
 */

public class CartReportBean<T extends BaseEntity> extends BaseEntity {
    public static final String COFFEE = "coffeecart";
    public static final String TAKEOUT = "takeoutmenucart";
    private CartInfoBean<T> cart_info;

    public CartInfoBean<T> getCart_info() {return cart_info;}

    public void setCart_info(CartInfoBean<T> cart_info) {this.cart_info = cart_info;}

    public static class CartInfoBean<T extends BaseEntity> extends BaseEntity{
        private String type;
        private List<T> data;

        public String getType() {return type;}
        public void setType(String type) {this.type = type;}

        public List<T> getData() {return data;}
        public void setData(List<T> data) {this.data = data;}
    }
}
