package com.zqb.carnetwork.example.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2016/4/11.
 */
public class LeftFragment extends Fragment {
    private DrawerLayout drawer_layout;
    private ListView mylistview;
    private ArrayList<MyItem> menuLists;
    private MyItemAdapter myItemAdapter;
    private RequestQueue queue;
    private int cur_position=0;
    private boolean isplaying=true;
    private ImageLoader imageLoader;
    private NetworkImageView head_pic;
    private TextView tv_user;
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.left_fragment,container,false);
        System.out.println(cur_position);
        System.out.println(isplaying);
        mylistview= (ListView) view.findViewById(R.id.left_drawer);

        queue= Volley.newRequestQueue(getContext());
        imageLoader=new ImageLoader(queue, new BitmapCache());
        head_pic= (NetworkImageView) view.findViewById(R.id.head);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, NetUrl.query_head_pic,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Log.i("left",s);
                        if(!s.equals("null"))
                        {
                            String path=NetUrl.Base+s.substring(3);
                            if(path!=null&&path.length()>0)
                            {
                                head_pic.setDefaultImageResId(R.drawable.head);
                                head_pic.setErrorImageResId(R.drawable.loaderror);
                                head_pic.setImageUrl(path,imageLoader);
                            }
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
        tv_user= (TextView) view.findViewById(R.id.user);

        handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv_user.setText(Firstpage.getUsername());
            }
        });

        menuLists=new ArrayList<MyItem>();
        menuLists.add(new MyItem("预约加油",R.drawable.oil_appoint));
        menuLists.add(new MyItem("车载导航",R.drawable.navegation));
        menuLists.add(new MyItem("周围加油站",R.drawable.location));
        menuLists.add(new MyItem("违章查询",R.drawable.rule_break_query));
        menuLists.add(new MyItem("个人中心",R.drawable.person_center));
        myItemAdapter=new MyItemAdapter(menuLists,getContext());
        mylistview.setAdapter(myItemAdapter);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                    {
                        Intent intent=new Intent(getContext(), AppointmentOil.class);
                        intent.putExtra("from","appointment");
                        startActivity(intent);
                        break;
                    }
                    case 1:
                    {
                        Intent intent=new Intent(getContext(), Navigation.class);
                        startActivity(intent);
                        break;
                    }
                    case 2:
                    {
                        Intent intent=new Intent(getContext(), GasStationAround.class);
                        startActivity(intent);
                        break;
                    }
                    case 3:
                    {
                        Intent intent=new Intent(getContext(),AppointmentOil.class);
                        intent.putExtra("from","rulebreak");
                        startActivity(intent);
                        break;
                    }
                    case 4:
                    {
                        Intent intent=new Intent(getContext(), MyHome.class);
                        intent.putExtra("cur_position",cur_position);
                        intent.putExtra("isplaying",isplaying);
                        startActivity(intent);
                        break;
                    }
                    default:break;
                }
            }
        });
        return view;
    }


    protected void setValue(int cur_position, boolean isplaying)
    {
        this.isplaying=isplaying;
        this.cur_position=cur_position;
    }


    //暴露给Activity，用于传入DrawerLayout，因为点击后想关掉DrawerLayout
    public void setDrawerLayout(DrawerLayout drawer_layout){
        this.drawer_layout = drawer_layout;
    }
}
