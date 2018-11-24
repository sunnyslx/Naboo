package com.idx.naboo.takeout.data.item;

import java.util.Map;

/**
 * Created by danny on 4/20/18.
 */

public class MenuList {
    private String name;//商品名称
    private Map<Integer,Integer> map;
    public Map<Integer, Integer> getMap() {return map;}

    public void setMap(Map<Integer, Integer> map) {this.map = map;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @Override
    public String toString() {
        return "MenuList{" +
                ", name='" + name + '\'' +
                ", map='" + map + '\'' +
                '}';
    }
}
