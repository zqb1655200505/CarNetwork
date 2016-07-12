package com.zqb.carnetwork.example.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zqb.carnetwork.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHome extends AppCompatActivity {

    private ImageLoader imageLoader;
    private NetworkImageView head_pic;
    private RequestQueue queue;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private TextView tv_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_home);
        queue= Volley.newRequestQueue(MyHome.this);
        imageLoader=new ImageLoader(queue, new BitmapCache());
        head_pic= (NetworkImageView) findViewById(R.id.head_pic);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, NetUrl.query_head_pic,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String path=NetUrl.Base+s.substring(3);
                        if(path!=null&&path.length()>0)
                        {
                            head_pic.setDefaultImageResId(R.drawable.head);
                            head_pic.setErrorImageResId(R.drawable.loaderror);
                            head_pic.setImageUrl(path,imageLoader);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String>map=new HashMap<String,String>();
                map.put("username",Firstpage.getUsername());
                return map;
            }
        };
        queue.add(stringRequest);
        tv_name= (TextView) findViewById(R.id.username);
        tv_name.setText(Firstpage.getUsername());
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        linearLayout1 = (LinearLayout) findViewById(R.id.myOrder);
        linearLayout2 = (LinearLayout) findViewById(R.id.myGarage);
        head_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFromAlbum();
            }
        });
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyHome.this,MyOrder.class);
                startActivity(intent);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "你点击了第2项", Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(MyHome.this,Garage.class);
                startActivity(intent);
                
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    //从手机上选择照片用作头像
    private void selectFromAlbum()
    {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try
        {
            Uri selectImage=data.getData();
            String[] filePathColumn= {MediaStore.Images.Media.DATA};//图片选取范围
            Cursor cursor=getContentResolver().query(selectImage,filePathColumn,
                    null,null,null);
            cursor.moveToFirst();
            int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
            String picPath=cursor.getString(columnIndex);
            //Toast.makeText(getApplicationContext(),picPath,Toast.LENGTH_LONG).show();
            upload_head_pic(picPath);
            cursor.close();
            Bitmap bitmap= BitmapFactory.decodeFile(picPath);
            head_pic.setImageBitmap(bitmap);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upload_head_pic(final String path)
    {
        Map<String,String> param=new HashMap<String, String>() ;
        String name=Firstpage.getUsername();
        param.put("username",name);
        ImageUploadUtil imageUploadUtil=ImageUploadUtil.getInstance();
        imageUploadUtil.uploadFile(path,"uploadedfile",NetUrl.requestURL,param);
    }
}
