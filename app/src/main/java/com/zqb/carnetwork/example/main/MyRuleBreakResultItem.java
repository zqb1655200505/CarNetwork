package com.zqb.carnetwork.example.main;

/**
 * Created by zqb on 2016/6/3.
 */
public class MyRuleBreakResultItem {
    private String money;
    private String address;
    private String time;
    private String main_infor;
    public MyRuleBreakResultItem(String money,String address,String time,String main_infor)
    {
        this.address=address;
        this.main_infor=main_infor;
        this.money=money;
        this.time=time;
    }

    public String getMoney() {
        return money;
    }

    public String getAddress() {
        return address;
    }

    public String getMain_infor() {
        return main_infor;
    }

    public String getTime() {
        return time;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMain_infor(String main_infor) {
        this.main_infor = main_infor;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
