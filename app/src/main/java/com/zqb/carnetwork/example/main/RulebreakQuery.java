package com.zqb.carnetwork.example.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.WeizhangIntentService;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.WeizhangResponseJson;
import com.zqb.carnetwork.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
public class RulebreakQuery extends AppCompatActivity {

    private CarInfo carInfo = null;
    private RequestQueue queue;
    WeizhangResponseJson info = null;

    private LinearLayout iv_choose_city;
    private TextView tv_plate_number;
    private TextView tv_engine_number;
    private TextView tv_car_frame_number;
    private TextView tv_city;
    private Button btn_query;
    private String province=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rulebreak_query);
        // //启动service
        // Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        // weizhangIntent.putExtra("appId", 1840);// 您的appId
        // weizhangIntent.putExtra("appKey", "abbc558c57d1f2dc161a245e287794b9");// 您的appKey
        // startService(weizhangIntent);

        tv_car_frame_number= (TextView) findViewById(R.id.car_frame_number);
        tv_engine_number= (TextView) findViewById(R.id.engine_number);
        tv_plate_number= (TextView) findViewById(R.id.plate_number);
        iv_choose_city= (LinearLayout) findViewById(R.id.choose_city);
        tv_city= (TextView) findViewById(R.id.final_city);
        btn_query= (Button) findViewById(R.id.begin_query);
        iv_choose_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(RulebreakQuery.this, ProvinceList.class);
                startActivityForResult(intent, 1);
            }
        });



        queue = Volley.newRequestQueue(RulebreakQuery.this);
        Bundle bundle = getIntent().getExtras();
        final int car_id = bundle.getInt("id");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetUrl.query_car_rulebreak, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String jsonStr = s; // 需要解析json格式的字符串
                Log.i("TAG",s);
                if (jsonStr != null && jsonStr.startsWith("\ufeff"))//以防有乱码
                {
                    jsonStr = jsonStr.substring(1);
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String plate_number = jsonObject.optString("plate_number");
                    String engine_number = jsonObject.optString("engine_number");
                    String car_frame_number = jsonObject.optString("car_frame_number");
                    tv_car_frame_number.setText(car_frame_number);
                    tv_plate_number.setText(plate_number);
                    tv_engine_number.setText(engine_number);
                    province=plate_number.substring(0,1);
                    carInfo = new CarInfo();
                    carInfo.setChejia_no(car_frame_number);
                    carInfo.setChepai_no(plate_number);
                    carInfo.setEngine_no(engine_number);
                    carInfo.setRegister_no("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("car_id", car_id + "");
                return map;
            }
        };
        queue.add(stringRequest);

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String city=tv_city.getText().toString();
                if(city==null||city.equals("请选择查询地")||city.length()<=0)
                {
                    Toast.makeText(RulebreakQuery.this,"请先选择查询地",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //开启线程
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                if(carInfo!=null)
                                {
                                    // 这里写入子线程需要做的工作
                                    info = WeizhangClient.getWeizhang(carInfo);
                                    Intent intent=new Intent(RulebreakQuery.this,RuleBreakResult.class);
                                    intent.putExtra("result",info.toJson());
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"carInfor为空",Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data==null)
        {
            //Toast.makeText(this,"province返回为空",Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle bundle=data.getExtras();
        String city_name=bundle.getString("city_name");
        String city_id=bundle.getString("city_id");
        //Toast.makeText(this,city_name+city_id,Toast.LENGTH_SHORT).show();
        tv_city.setText(city_name);
        carInfo.setCity_id(Integer.parseInt(city_id));
    }
}
