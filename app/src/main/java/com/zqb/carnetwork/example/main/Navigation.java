package com.zqb.carnetwork.example.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.zqb.carnetwork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示MapView的基本用法
 */
public class Navigation extends Activity implements OnGetSuggestionResultListener {
    /**
     * 搜索关键字输入窗口
     */
    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggest;
    private AutoCompleteTextView et_start_area = null;
    private AutoCompleteTextView et_end_area = null;
    private ArrayAdapter<String> sugAdapter = null;
    private TextView tv_strat_city;
    private TextView tv_end_city;
    private CheckBox start_check;
    private CheckBox end_check;
    private RadioGroup radioGroup;
    private RadioButton radio_dis;
    private Button btn_begin;
    private RadioButton radio_time;
    private ImageButton exchange;
    private LinearLayout ll_choose_start_city;
    private LinearLayout ll_choose_end_city;
    private int flag1=0;
    private int flag=0;//标记出行方案，0为距离最短，1为避免拥堵
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        initView();
        start_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(start_check.isChecked())
                {
                    start_check.setChecked(true);
                    end_check.setClickable(false);
                    et_start_area.setFocusable(false);
                    et_start_area.setFocusableInTouchMode(false);
                    et_start_area.setText("当前位置");
                }
                else
                {
                    start_check.setChecked(false);
                    end_check.setClickable(true);
                    et_start_area.setFocusable(true);
                    et_start_area.setFocusableInTouchMode(true);
                    et_start_area.setText("");
                    et_start_area.setHint("请输入起点");
                }
            }
        });
        end_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(end_check.isChecked())
                {
                    end_check.setChecked(true);
                    start_check.setClickable(false);
                    et_end_area.setFocusable(false);
                    et_end_area.setFocusableInTouchMode(false);
                    et_end_area.setText("当前位置");
                }
                else
                {
                    end_check.setChecked(false);
                    start_check.setClickable(true);
                    et_end_area.setFocusable(true);
                    et_end_area.setFocusableInTouchMode(true);
                    et_end_area.setText("");
                    et_end_area.setHint("请输入终点");
                }
            }
        });
        ll_choose_start_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag1=0;
                Intent intent=new Intent();
                intent.setClass(Navigation.this,ProvinceList.class);
                startActivityForResult(intent,1);
            }
        });
        ll_choose_end_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag1=1;
                Intent intent=new Intent();
                intent.setClass(Navigation.this,ProvinceList.class);
                startActivityForResult(intent,1);
            }
        });
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        et_start_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {}
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                String city = ((TextView) findViewById(R.id.start_city)).getText().toString();
                //Log.i("city",city);
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(city));
            }
        });
        et_end_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {}
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                String city = ((TextView) findViewById(R.id.end_city)).getText().toString();
                //Log.i("city",city);
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(city));
            }
        });
        //切换起始点和终点
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取个控件初始状态
                Boolean start_check_state=start_check.isChecked();
                Boolean start_clickale_state=start_check.isClickable();
                Boolean end_check_state=end_check.isChecked();
                Boolean end_clickale_state=end_check.isClickable();
                String end_area=et_end_area.getText().toString();
                String start_area=et_start_area.getText().toString();
                String end_city=tv_end_city.getText().toString();
                String start_city=tv_strat_city.getText().toString();
                start_check.setChecked(end_check_state);
                start_check.setClickable(end_clickale_state);
                end_check.setChecked(start_check_state);
                end_check.setClickable(start_clickale_state);
                et_start_area.setText(end_area);
                et_end_area.setText(start_area);
                tv_strat_city.setText(end_city);
                tv_end_city.setText(start_city);
                if(start_check.isChecked())
                {
                    et_end_area.setFocusable(true);
                    et_end_area.setFocusableInTouchMode(true);
                }
                if(end_check.isChecked())
                {
                    et_start_area.setFocusable(true);
                    et_start_area.setFocusableInTouchMode(true);
                }
            }
        });

        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String end_area=et_end_area.getText().toString();
                String start_area=et_start_area.getText().toString();
                String end_city=tv_end_city.getText().toString();
                String start_city=tv_strat_city.getText().toString();

                if(start_city==null||start_city.length()<=0||start_city.equals("请选择起始城市"))
                {
                    Toast.makeText(Navigation.this,"请选择起点城市",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(end_city==null||end_city.length()<=0||end_city.equals("请选择终点城市"))
                {
                    Toast.makeText(Navigation.this,"请选择终点城市",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(start_area==null||start_area.length()<=0)
                {
                    Toast.makeText(Navigation.this,"请输入起始地点",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(end_area==null||end_area.length()<=0)
                {
                    Toast.makeText(Navigation.this,"请输入终点地点",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(Navigation.this,RoutePlan.class);
                intent.putExtra("code",0);//标记从哪个activity传值过去
                intent.putExtra("start_city",start_city);
                intent.putExtra("start_area",start_area);
                intent.putExtra("end_city",end_city);
                intent.putExtra("end_area",end_area);
                startActivity(intent);
            }
        });
    }
    private void initView()
    {
        tv_strat_city= (TextView) findViewById(R.id.start_city);
        et_start_area= (AutoCompleteTextView) findViewById(R.id.start_area);
        tv_end_city= (TextView) findViewById(R.id.end_city);
        et_end_area= (AutoCompleteTextView) findViewById(R.id.end_area);
        start_check= (CheckBox) findViewById(R.id.start_check);
        end_check= (CheckBox) findViewById(R.id.end_check);
        end_check.setClickable(false);
        exchange= (ImageButton) findViewById(R.id.exchange);
        btn_begin= (Button) findViewById(R.id.begin_nav);
        ll_choose_end_city= (LinearLayout) findViewById(R.id.choose_end_city);
        ll_choose_start_city= (LinearLayout) findViewById(R.id.choose_start_city);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        et_start_area.setAdapter(sugAdapter);
        et_start_area.setThreshold(1);
        et_end_area.setAdapter(sugAdapter);
        et_end_area.setThreshold(1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null)
        {
            return;
        }
        Bundle bundle=data.getExtras();
        String city=bundle.getString("city_name");
        if(flag1==0)
        {
            tv_strat_city.setText(city);
        }
        else
        {
            tv_end_city.setText(city);
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<String>(Navigation.this, android.R.layout.simple_dropdown_item_1line, suggest);
        et_end_area.setAdapter(sugAdapter);
        et_start_area.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }
}
