package com.zqb.carnetwork.example.main;

/**
 * Created by zqb on 2016/4/10.
 */
public class MyItem {
    private String func;
    private int pic;
    public MyItem(String func,int pic)
    {
        this.func=func;
        this.pic=pic;
    }
    public String getFunc()
    {
        return func;
    }
    public int getPic()
    {
        return pic;
    }
    public void setFunc(String func)
    {
        this.func=func;
    }
    public void setPic(int pic)
    {
        this.pic=pic;
    }
}
