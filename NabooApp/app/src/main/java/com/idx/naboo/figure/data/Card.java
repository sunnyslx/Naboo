package com.idx.naboo.figure.data;

/**
 * Created by darkmi on 4/12/18.
 */

public class Card {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Card{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
