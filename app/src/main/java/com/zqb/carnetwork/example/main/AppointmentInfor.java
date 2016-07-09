package com.zqb.carnetwork.example.main;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zqb.carnetwork.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppointmentInfor extends AppCompatActivity {

    private TextView tv_name;
    private TextView tv_car;
    private EditText et_station;
    private TextView tv_time;
    private RadioGroup gas_radioGroup;
    private RadioGroup num_radioGroup;
    private EditText et_num;
    private LinearLayout LL_time;
    private Button btn_appoint;
    private RequestQueue queue;
    private String gas_type="92#";
    private String num_type="升";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_infor);
        init();
        final Bundle bundle=getIntent().getExtras();
        int flag=bundle.getInt("flag");//标记从哪个activity进入
        if (flag==1)
        {
            String station_name=bundle.getString("station_name");
            et_station.setText(station_name);
        }
        final String car=bundle.getString("car");
        tv_car.setText(car);
        final String name=Firstpage.getUsername();
        tv_name.setText(name);
        LL_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new AlertDialog.Builder(AppointmentInfor.this).create();
                dialog.show();
                Window window=dialog.getWindow();
                window.setContentView(R.layout.calendar_dialog);
                Date date=new Date();
                int year1=date.getYear()+100;
                int month1=date.getMonth()+1;
                int day1=date.getDate();
                tv_time.setText(year1+"-"+month1+"-"+day1);
                CalendarView calendarView= (CalendarView) window.findViewById(R.id.calendar);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                        month=month+1;
                        tv_time.setText(year+"-"+month+"-"+day);
                        dialog.dismiss();
                    }
                });

            }
        });
        gas_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int position) {
                if (position==0)
                {
                    gas_type="92#";
                }
                else
                {
                    gas_type="95#";
                }
            }
        });
        num_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int position) {
                if (position==0)
                {
                    num_type="升";
                }
                else
                {
                    num_type="元";
                }
            }
        });
        btn_appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_station.getText().toString()==null||et_station.getText().equals("")||tv_time.getText().toString()==null
                        ||tv_time.getText().equals("")||et_num.getText().toString()==null||et_num.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"请先完善信息",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Log.i("TAG",name+car+gas_type+bundle.getInt("car_id"));
                    StringRequest stringRequest=new StringRequest(Request.Method.POST, NetUrl.creat_appointment, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Toast.makeText(getApplicationContext(),"预约成功",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(AppointmentInfor.this,MyOrder.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String,String>map=new HashMap<String, String>();
                            map.put("username",name);
                            map.put("brand",car);
                            map.put("oil_station",et_station.getText().toString());
                            map.put("time",tv_time.getText().toString());
                            map.put("oil_type",gas_type);
                            Log.i("oil_type",gas_type);
                            map.put("cost",et_num.getText().toString()+num_type);
                            map.put("car_id",bundle.getInt("car_id")+"");

                            return map;
                        }
                    };
                    queue.add(stringRequest);
                }
            }
        });
    }
    private void init()
    {
        tv_name= (TextView) findViewById(R.id.name);
        tv_car= (TextView) findViewById(R.id.car);
        tv_time= (TextView) findViewById(R.id.time);
        et_station= (EditText) findViewById(R.id.station);
        gas_radioGroup= (RadioGroup) findViewById(R.id.gas_type);
        num_radioGroup= (RadioGroup) findViewById(R.id.num_type);
        et_num= (EditText) findViewById(R.id.num);
        LL_time= (LinearLayout) findViewById(R.id.choose_time);
        btn_appoint= (Button) findViewById(R.id.appoint);
        queue= Volley.newRequestQueue(AppointmentInfor.this);
    }
}
