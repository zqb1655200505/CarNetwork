package com.zqb.carnetwork.example.main;

import android.util.Log;

/**
 * Created by zqb on 2016/5/25.
 */
public class MyOrderItem {
    private String owner;
    private String brand;
    private String oil_type;
    private String money;
    private int  car_id;
    private String car_pic;
    private String oil_station;
    private String qrcode;
    protected MyOrderItem(String owner,String brand,String oil_type,String oil_station,String money,int car_id,String car_pic,String qrcode)
    {
        this.brand=brand;
        this.money=money;
        this.oil_type=oil_type;
        this.owner=owner;
        this.car_id=car_id;
        this.oil_station=oil_station;
        this.car_pic=car_pic;
        this.qrcode=qrcode;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public void setCar_pic(String car_pic) {
        this.car_pic = car_pic;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setOil_station(String oil_station) {
        this.oil_station = oil_station;
    }

    public void setOil_type(String oil_type) {
        this.oil_type = oil_type;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    protected String getOwner()
    {
        return owner;
    }

    public String getQrcode() {
        return qrcode;
    }

    public int getCar_id() {
        return car_id;
    }

    public String getBrand() {
        return brand;
    }

    public String getMoney() {
        return money;
    }

    public String getOil_type() {
        return oil_type;
    }

    public String getCar_pic() {
        return car_pic;
    }

    public String getOil_station() {
        return oil_station;
    }
}
