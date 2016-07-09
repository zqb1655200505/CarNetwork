package com.zqb.carnetwork.example.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zqb.carnetwork.R;

public class OrderQRcode extends AppCompatActivity {

    private RequestQueue queue;
    private ImageLoader imageLoader;
    private NetworkImageView networkImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_qrcode);
        Bundle bundle=getIntent().getExtras();
        String qrcode=NetUrl.Base+bundle.getString("qrcode").substring(3);
        queue= Volley.newRequestQueue(getApplicationContext());
        imageLoader=new ImageLoader(queue, new BitmapCache());
        networkImageView= (NetworkImageView) findViewById(R.id.qrcode);
        if(qrcode!=null&&qrcode.length()>0)
        {
            networkImageView.setDefaultImageResId(R.drawable.wait);
            networkImageView.setErrorImageResId(R.drawable.loaderror);
            networkImageView.setImageUrl(qrcode,imageLoader);
        }
    }
}
