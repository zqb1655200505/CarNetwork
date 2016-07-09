package com.zqb.carnetwork.example.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zqb.carnetwork.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyOrder extends AppCompatActivity {

    private RequestQueue mQueue;
    private ListView mListview;
    private OrderItemAdapter orderadapter=null;
    private List<MyOrderItem> orderItem=null;
    private int[] ID=new int[100];
    private int[] id=new int[100];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        mQueue= Volley.newRequestQueue(MyOrder.this);
        mListview= (ListView) findViewById(R.id.order_list);
        orderItem=new LinkedList<MyOrderItem>();
        final String username=Firstpage.getUsername();
        String url=NetUrl.query_order+"?username="+username;
        Log.i("URL",url);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                if(jsonArray==null)
                {
                    Toast.makeText(MyOrder.this,"订单为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.optJSONObject(i);
                    String owner=jsonObject.optString("owner");
                    String brand=jsonObject.optString("brand");
                    String money=jsonObject.optString("money");
                    String oil_type=jsonObject.optString("oil_type");
                    String oil_station=jsonObject.optString("oil_station");
                    String car_pic=jsonObject.optString("car_pic");
                    int car_id=jsonObject.optInt("car_id");
                    id[i]=jsonObject.optInt("id");
                    ID[i]=car_id;
                    String qrcode=jsonObject.optString("qrcode");
                    Log.i("TAG",owner+" "+brand+" "+money+" "+oil_station+" "+oil_type+" "+car_id+" "+car_pic);
                    orderItem.add(new MyOrderItem(owner,brand,oil_type,oil_station,money,car_id,car_pic,qrcode));
                }
                orderadapter=new OrderItemAdapter(orderItem,getApplicationContext());
                mListview.setAdapter(orderadapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.e("Error: ", volleyError.getMessage());
                Toast.makeText(getApplicationContext(),"访问出错",Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonArrayRequest);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String qrcode=orderItem.get(i).getQrcode();
                Intent intent=new Intent(MyOrder.this,OrderQRcode.class);
                intent.putExtra("qrcode",qrcode);
                startActivity(intent);
            }
        });
        mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                new AlertDialog.Builder(MyOrder.this).setTitle("系统提示")
                        .setMessage("是否删除该订单")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int position) {
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, NetUrl.delete_order, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        orderItem.remove(i);
                                        mListview.setAdapter(orderadapter);
                                        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                                        //view.setVisibility(View.INVISIBLE);
                                }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Toast.makeText(getApplicationContext(),"访问出错",Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String,String>map=new HashMap<String, String>();
                                        map.put("car_id",id[i]+"");
                                        return map;
                                    }
                                };
                                mQueue.add(stringRequest);
                                view.setVisibility(View.INVISIBLE);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                    }).show();
                return true;
            }
        });
    }
}
