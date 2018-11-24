package com.idx.naboo.videocall;

import java.util.Map;

/**
 * 未接电话封装bean
 * Created by danny on 6/1/18.
 */

public class Missed {
    public String account;
    public Map<Integer,Integer> flag;

    @Override
    public String toString() {
        return "Missed{" +
                "account='" + account + '\'' +
                ", flag=" + flag +
                '}';
    }
}
