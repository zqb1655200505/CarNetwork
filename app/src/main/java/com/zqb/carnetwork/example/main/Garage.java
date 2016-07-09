package com.zqb.carnetwork.example.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Garage extends AppCompatActivity {

    private List<MyCarItem>myCarItems;
    private CarListItemAdapter carListItemAdapter;
    private ListView mListview;
    private RequestQueue mQueue;
    private ImageView addImg;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final int[] ID=new int[100];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);
        mQueue= Volley.newRequestQueue(Garage.this);
        myCarItems=new LinkedList<MyCarItem>();
        mListview= (ListView) findViewById(R.id.carList);
        mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                new AlertDialog.Builder(Garage.this).setTitle("系统提示")
                        .setMessage("是否从车库移除该车辆")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, NetUrl.delete_car, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        myCarItems.remove(i);
                                        mListview.setAdapter(carListItemAdapter);
                                        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Toast.makeText(getApplicationContext(),"访问出错",Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String,String>map=new HashMap<>();
                                        map.put("car_id",ID[position]+"");
                                        return map;
                                    }
                                };
                                mQueue.add(stringRequest);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"未选中汽车",Toast.LENGTH_SHORT).show();
                            }
                }).show();
                return true;
            }
        });
        addImg= (ImageView) findViewById(R.id.add);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"添加车辆",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(Garage.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }
        });

        /**
         * JsonArrayRequest默认为get方法
         * JsonArrayRequest要想传递参数只能通过 url的get方法
         */
        String username=Firstpage.getUsername();
        String Request=NetUrl.query_car+"?username="+username;
        JsonArrayRequest req = new JsonArrayRequest(Request, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++)
                {
                    JSONObject jsonObject=response.optJSONObject(i);
                    Log.i("jsonObject",jsonObject.toString());
                    String brand=jsonObject.optString("brand");
                    String type=jsonObject.optString("type");
                    String pic=jsonObject.optString("pic");
                    int id=jsonObject.optInt("id");
                    ID[i]=id;
                    myCarItems.add(new MyCarItem(brand,type,pic,id));
                }
                carListItemAdapter=new CarListItemAdapter(myCarItems,getApplicationContext());
                mListview.setAdapter(carListItemAdapter);
            }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });
        mQueue.add(req);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String result=bundle.getString("result");
                    Intent intent=new Intent(Garage.this,QRcodeResult.class);
                    intent.putExtra("qrcode_result",result);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"扫描结果出错",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
