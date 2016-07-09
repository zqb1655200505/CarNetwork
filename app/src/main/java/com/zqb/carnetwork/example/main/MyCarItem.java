package com.zqb.carnetwork.example.main;

/**
 * Created by zqb on 2016/5/22.
 */
public class MyCarItem {
    private String brand;
    private String type;
    private String pic;
    private int id;
    protected MyCarItem(String brand,String type,String pic,int id)
    {
        this.brand=brand;
        this.pic=pic;
        this.type=type;
        this.id=id;
    }

    protected void setBrand(String brand)
    {
        this.brand=brand;
    }
    protected void setPic(String pic)
    {
        this.pic=pic;
    }
    protected void setType(String type)
    {
        this.type=type;
    }
    protected String getBrand()
    {
        return brand;
    }
    protected String getType()
    {
        return type;
    }
    protected String getPic()
    {
        return pic;
    }
    protected int getId(){return id;}
}
