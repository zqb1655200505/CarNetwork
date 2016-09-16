package com.zqb.carnetwork.example.main;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private RequestQueue mQueue;
    private EditText et_username;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;
    private String username;
    private String password;
    private CheckBox check_remember;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mQueue= Volley.newRequestQueue(Login.this);
        et_username= (EditText) findViewById(R.id.username);
        et_password= (EditText) findViewById(R.id.password);
        btn_login= (Button) findViewById(R.id.login);
        btn_register= (Button) findViewById(R.id.register);
        check_remember= (CheckBox) findViewById(R.id.remember);
        //存储信息到本地
        sp=getSharedPreferences("userInfer",0);
        final SharedPreferences.Editor editor=sp.edit();

        findViewById(R.id.tv_remember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_remember.isChecked())
                {
                    check_remember.setChecked(false);
                }
                else
                {
                    check_remember.setChecked(true);
                }
            }
        });
        String Name=sp.getString("username","");
        String Password=sp.getString("password","");
        Boolean isChecked=sp.getBoolean("remember",false);
//        Toast.makeText(getApplicationContext(),Name+"  "+
//                Password,Toast.LENGTH_LONG).show();
        if(isChecked)
        {
            et_username.setText(Name);
            et_password.setText(Password);
            check_remember.setChecked(true);
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                if (username == null || username.equals(""))//用户名缺失
                {
                    Toast.makeText(Login.this, "请填写用户名", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (password == null || password.equals(""))//密码缺失
                    {
                        Toast.makeText(Login.this, "请填写密码", Toast.LENGTH_SHORT).show();
                    }
                    else//用户名和密码完整
                    {
                        StringRequest stringRequest=new StringRequest(Request.Method.POST,NetUrl.login_check,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    Log.i("sssssssssssssss",s);
                                    String jsonStr=s; // 需要解析json格式的字符串
                                    if(jsonStr != null && jsonStr.startsWith("\ufeff"))
                                    {
                                        jsonStr =  jsonStr.substring(1);
                                    }
                                    JSONObject jsonObject = new JSONObject(jsonStr);
                                    Log.i("jsonObject",jsonObject.toString());
                                    String type=jsonObject.optString("type");
                                    if(type.equals("null"))
                                    {
                                        Toast.makeText(Login.this,"用户名不存在！",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(type.equals("true"))
                                    {
                                        editor.putString("username",username);
                                        editor.putString("password",password);
                                        editor.putBoolean("remember",check_remember.isChecked());
                                        editor.commit();
                                        Toast.makeText(Login.this,"登入成功！",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(Login.this,Firstpage.class);
                                        intent.putExtra("username",username);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(Login.this,"密码错误！",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.i("aaa", volleyError.getMessage(), volleyError);
                            if(volleyError.getMessage()==null)
                                Toast.makeText(getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
                        }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("username", username);
                                    map.put("password",password);
                                    return map;
                                }
                        };
                        mQueue.add(stringRequest);
                    }
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=et_username.getText().toString();
                password=et_password.getText().toString();
                if(username==null||username.equals(""))//用户名缺失
                {
                    Toast.makeText(Login.this,"请填写用户名",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(password==null||password.equals(""))//密码缺失
                    {
                        Toast.makeText(Login.this,"请填写密码",Toast.LENGTH_SHORT).show();
                    }
                    else//用户名和密码完整
                    {
                        if(password.length()<6)
                        {
                            Toast.makeText(Login.this,"密码过于简单！",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            StringRequest stringRequest=new StringRequest(Request.Method.POST,NetUrl.register_check,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            String jsonStr=s; // 需要解析json格式的字符串
                                            if(jsonStr != null && jsonStr.startsWith("\ufeff"))
                                            {
                                                jsonStr =  jsonStr.substring(1);
                                            }
                                            JSONObject jsonObject = new JSONObject(jsonStr);
                                            String type=jsonObject.optString("type");
                                            if(type.equals("true"))
                                            {
                                                Toast.makeText(Login.this,"注册成功！",Toast.LENGTH_SHORT).show();
                                            }
                                            else if(type.equals("false"))
                                            {
                                                Toast.makeText(Login.this,"用户名已存在！",Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i("aaa", volleyError.getMessage(), volleyError);
                            }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("username", username);
                                    map.put("password", password);
                                    return map;
                                }
                            };
                            mQueue.add(stringRequest);
                        }

                    }
                }
            }
        });
    }

}
