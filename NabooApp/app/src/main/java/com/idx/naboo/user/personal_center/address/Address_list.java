package com.idx.naboo.user.personal_center.address;

/**
 * Created by ryan on 18-4-13.
 * Email: Ryan_chan01212@yeah.net
 */

public class Address_list {

    private int id;
    private String name;
    private String phone;
    private String select;
    private String detailed;
    private String labels;

    public Address_list( int id,String name, String phone, String select, String detailed,String labels) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.select = select;
        this.detailed = detailed;
        this.labels = labels;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }
}
