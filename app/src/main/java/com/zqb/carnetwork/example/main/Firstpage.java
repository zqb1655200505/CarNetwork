package com.zqb.carnetwork.example.main;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import com.cheshouye.api.client.WeizhangIntentService;
import com.zqb.carnetwork.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firstpage extends AppCompatActivity{

    private static String username;
    private DrawerLayout drawer_layout;
    private LeftFragment leftFragment;
    private FragmentManager fManager;
    private MusicLoader musicLoader;
    private static List<MusicInfo>list;
    private MusicListAdapter musicListAdapter;
    private ListView listView;
    private ContentResolver contentResolver;
    private ImageButton before;
    private ImageButton later;
    private ImageButton play_pause;
    private ImageButton show_list;
    private TextView singer;
    private TextView song;
    private boolean isplaying=true;
    private MusicBoxReceiver mReceiver;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private int cur_position=0;//记录当前播放歌曲
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private RequestQueue queue;
    private String car_pic=null;
    private ImageLoader imageLoader;
    private TextView tv_engine;
    private TextView tv_gas;
    private TextView tv_lamb;
    private TextView tv_speed;
    private TextView tv_mile_num;
    private TextView tv_level;
    private TextView tv_type;
    private NetworkImageView img_car_pic;
    private LinearLayout ll;
    private ImageButton img_btn;

    private int[] car_id_list=new int[50];
    private JSONArray car_jsonArray;

    private String body_level;
    private String mile_number;
    private String engine_performance;
    private String speed_shift;
    private String car_lamb;
    private String gas_sum;
    private String type;
    private String brand;
    private String plate_num;
    private String engine_num;
    private String car_frame;
    private JSONObject json;
    private JSONObject json1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);
        Bundle myBundle=getIntent().getExtras();
        username=myBundle.getString("username");


        tv_engine= (TextView) findViewById(R.id.engine);
        tv_gas= (TextView) findViewById(R.id.gas);
        tv_lamb= (TextView) findViewById(R.id.lamb);
        tv_level= (TextView) findViewById(R.id.body_level);
        tv_mile_num= (TextView) findViewById(R.id.mile_num);
        tv_speed= (TextView) findViewById(R.id.speed);
        img_car_pic= (NetworkImageView) findViewById(R.id.car_pic);
        ll= (LinearLayout) findViewById(R.id.infor);
        img_btn= (ImageButton) findViewById(R.id.change);
        tv_type= (TextView) findViewById(R.id.car_type);
        queue=Volley.newRequestQueue(Firstpage.this);
        imageLoader=new ImageLoader(queue, new BitmapCache());

        StringRequest stringRequest=new StringRequest(Request.Method.POST, NetUrl.query_performance, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("查询为空"))
                {
                    String[] str=new String[]{"从数据库导入","立即扫码添加"};
                    new AlertDialog.Builder(Firstpage.this).setTitle("暂无车辆信息")
                            .setItems(str, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(i==0)//从数据库导入
                                    {
                                        StringRequest stringRequest1=new StringRequest(Request.Method.POST, NetUrl.query_performance, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String s1) {
                                                try {
                                                    car_jsonArray=new JSONArray(s1);
                                                    JSONObject jsonObject=car_jsonArray.optJSONObject(0);
                                                    body_level=jsonObject.optString("body_level");
                                                    tv_level.setText(body_level);
                                                    mile_number=jsonObject.optString("mile_number");
                                                    tv_mile_num.setText(mile_number);
                                                    engine_performance=jsonObject.optString("engine_performance");
                                                    tv_engine.setText(engine_performance);
                                                    speed_shift=jsonObject.optString("speed_shift");
                                                    tv_speed.setText(speed_shift);
                                                    car_lamb=jsonObject.optString("car_lamb");
                                                    tv_lamb.setText(car_lamb);
                                                    gas_sum=jsonObject.optString("gas_sum");
                                                    tv_gas.setText(gas_sum);
                                                    car_pic=jsonObject.optString("car_pic");
                                                    ll.setVisibility(View.VISIBLE);

                                                    type=jsonObject.optString("type");
                                                    brand=jsonObject.optString("brand");

                                                    plate_num=jsonObject.optString("plate_num");
                                                    engine_num=jsonObject.optString("engine_num");
                                                    car_frame=jsonObject.optString("car_frame");
                                                    final String path=NetUrl.Base+car_pic.substring(3);
                                                    if(path!=null&&path.length()>0)
                                                    {
                                                        //img_car_pic.setDefaultImageResId(R.drawable.wait);
                                                        img_car_pic.setErrorImageResId(R.drawable.loaderror);
                                                        img_car_pic.setImageUrl(path,imageLoader);
                                                    }
                                                    tv_type.setText(type+brand);
                                                    json=car_jsonArray.optJSONObject(0);
                                                    StringRequest stringRequest3=new StringRequest(Request.Method.POST, NetUrl.insert, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String s) {
                                                            Toast.makeText(Firstpage.this,s,Toast.LENGTH_SHORT).show();
                                                            //Log.i("TAG---",s);
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError volleyError) {

                                                        }
                                                    }){
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            HashMap<String,String>map=new HashMap<String, String>();
                                                            map.put("body_level",json.optString("body_level"));
                                                            map.put("mile_number",json.optString("mile_number"));
                                                            map.put("engine_performance",json.optString("engine_performance"));
                                                            map.put("speed_shift",json.optString("speed_shift"));
                                                            map.put("car_lamb",json.optString("car_lamb"));
                                                            map.put("gas_sum",json.optString("gas_sum"));
                                                            map.put("username",username);
                                                            map.put("car_pic",json.optString("car_pic"));
                                                            map.put("type",json.optString("type"));
                                                            map.put("brand",json.optString("brand"));
                                                            map.put("plate_num",json.optString("plate_num"));
                                                            map.put("engine_num",json.optString("engine_num"));
                                                            map.put("car_frame",json.optString("car_frame"));
                                                            return map;
                                                        }
                                                    };
                                                    queue.add(stringRequest3);

                                                    json1=car_jsonArray.optJSONObject(1);
                                                    StringRequest string=new StringRequest(Request.Method.POST, NetUrl.insert, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String s) {
                                                            Toast.makeText(Firstpage.this,s,Toast.LENGTH_SHORT).show();
                                                            //Log.i("TAG---",s);
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError volleyError) {

                                                        }
                                                    }){
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            HashMap<String,String>map=new HashMap<String, String>();
                                                            map.put("body_level",json1.optString("body_level"));
                                                            map.put("mile_number",json1.optString("mile_number"));
                                                            map.put("engine_performance",json.optString("engine_performance"));
                                                            map.put("speed_shift",json1.optString("speed_shift"));
                                                            map.put("car_lamb",json1.optString("car_lamb"));
                                                            map.put("gas_sum",json1.optString("gas_sum"));
                                                            map.put("username",username);
                                                            map.put("car_pic",json1.optString("car_pic"));
                                                            map.put("type",json1.optString("type"));
                                                            map.put("brand",json1.optString("brand"));
                                                            map.put("plate_num",json1.optString("plate_num"));
                                                            map.put("engine_num",json1.optString("engine_num"));
                                                            map.put("car_frame",json1.optString("car_frame"));
                                                            return map;
                                                        }
                                                    };
                                                    queue.add(string);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
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
                                                map.put("username","zqb");
                                                return map;
                                            }
                                        };
                                        queue.add(stringRequest1);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent();
                                        intent.setClass(Firstpage.this, MipcaActivityCapture.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                                    }
                                }
                            }).show();
                    return;
                }
                try {
                    car_jsonArray=new JSONArray(s);
                    JSONObject jsonObject=car_jsonArray.optJSONObject(0);
                    String body_level=jsonObject.optString("body_level");
                    tv_level.setText(body_level);
                    String mile_number=jsonObject.optString("mile_number");
                    tv_mile_num.setText(mile_number);
                    String engine_performance=jsonObject.optString("engine_performance");
                    tv_engine.setText(engine_performance);
                    String speed_shift=jsonObject.optString("speed_shift");
                    tv_speed.setText(speed_shift);
                    String car_lamb=jsonObject.optString("car_lamb");
                    tv_lamb.setText(car_lamb);
                    String gas_sum=jsonObject.optString("gas_sum");
                    tv_gas.setText(gas_sum);
                    car_pic=jsonObject.optString("car_pic");
                    ll.setVisibility(View.VISIBLE);
                    type=jsonObject.optString("type");
                    brand=jsonObject.optString("brand");

                    String path=NetUrl.Base+car_pic.substring(3);
                    if(path!=null&&path.length()>0)
                    {
                        //img_car_pic.setDefaultImageResId(R.drawable.wait);
                        img_car_pic.setErrorImageResId(R.drawable.loaderror);
                        img_car_pic.setImageUrl(path,imageLoader);
                    }
                    tv_type.setText(type+brand);
                } catch (JSONException e) {
                    e.printStackTrace();
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
                map.put("username",username);
                return map;
            }
        };
        queue.add(stringRequest);

        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] car_list=new String[car_jsonArray.length()];//不能有多出来的空间，否则出错
                for(int i=0;i<car_jsonArray.length();i++)
                {
                    JSONObject jsonObject=car_jsonArray.optJSONObject(i);
                    car_id_list[i]=jsonObject.optInt("car_id");
                    car_list[i]=jsonObject.optString("brand")+jsonObject.optString("type");
                    //Log.i("ss",car_list[i]);
                }

                new AlertDialog.Builder(Firstpage.this).setTitle("请选择汽车")
                        .setItems(car_list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                JSONObject jsonObject=car_jsonArray.optJSONObject(i);
                                String body_level=jsonObject.optString("body_level");
                                tv_level.setText(body_level);
                                String mile_number=jsonObject.optString("mile_number");
                                tv_mile_num.setText(mile_number);
                                String engine_performance=jsonObject.optString("engine_performance");
                                tv_engine.setText(engine_performance);
                                String speed_shift=jsonObject.optString("speed_shift");
                                tv_speed.setText(speed_shift);
                                String car_lamb=jsonObject.optString("car_lamb");
                                tv_lamb.setText(car_lamb);
                                String gas_sum=jsonObject.optString("gas_sum");
                                tv_gas.setText(gas_sum);
                                car_pic=jsonObject.optString("car_pic");
                                type=jsonObject.optString("type");
                                brand=jsonObject.optString("brand");
                                ll.setVisibility(View.VISIBLE);
                                String path=NetUrl.Base+car_pic.substring(3);
                                if(path!=null&&path.length()>0)
                                {
                                    //img_car_pic.setDefaultImageResId(R.drawable.car);
                                    img_car_pic.setErrorImageResId(R.drawable.loaderror);
                                    img_car_pic.setImageUrl(path,imageLoader);
                                }
                                tv_type.setText(type+brand);
                            }
                        }).show();

            }
        });


        drawer_layout= (DrawerLayout) findViewById(R.id.drawer_layout);
        fManager=getSupportFragmentManager();
        leftFragment=(LeftFragment)fManager.findFragmentById(R.id.left_fg);
        leftFragment.setValue(cur_position,isplaying);
        leftFragment.setDrawerLayout(drawer_layout);
        toolbar= (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        drawer_layout.setDrawerListener(mDrawerToggle);
        //获取本机音乐
        contentResolver=this.getContentResolver();
        musicLoader=MusicLoader.instance(contentResolver);
        list=musicLoader.getMusicInfoList();
        musicListAdapter=new MusicListAdapter((List<MusicInfo>)list,getApplicationContext());
        before= (ImageButton) findViewById(R.id.before);
        later= (ImageButton) findViewById(R.id.later);
        play_pause= (ImageButton) findViewById(R.id.play_pause);
        show_list= (ImageButton) findViewById(R.id.show_list);
        singer= (TextView) findViewById(R.id.singer);
        song= (TextView) findViewById(R.id.song);
        //注册接收器
        mReceiver=new MusicBoxReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ConstUtil.MUSICBOX_ACTION);
        registerReceiver(mReceiver, filter);
        //启动后台音乐Service
        Intent intent=new Intent(this, MusicService.class);
        startService(intent);
        //启动后台违章service
        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId", 1840);// 您的appId
        weizhangIntent.putExtra("appKey", "abbc558c57d1f2dc161a245e287794b9");// 您的appKey
        startService(weizhangIntent);

        show_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                sendBroadcastToService(ConstUtil.STATE_PREVIOUS,cur_position);
                cur_position=cur_position-1;
                if(cur_position<0)
                {
                    cur_position=list.size()-1;
                }
                isplaying = true;
                leftFragment.setValue(cur_position,isplaying);
            }
        });
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                sendBroadcastToService(ConstUtil.STATE_NEXT,cur_position);
                cur_position=cur_position+1;
                if(cur_position>list.size())
                {
                    cur_position=0;
                }
                isplaying=true;
                leftFragment.setValue(cur_position,isplaying);
            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isplaying)//当前为播放中
                {
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    sendBroadcastToService(ConstUtil.STATE_PAUSE,cur_position);
                    isplaying=false;
                    leftFragment.setValue(cur_position,isplaying);
                }
                else
                {
                    play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    sendBroadcastToService(ConstUtil.STATE_PLAY,cur_position);
                    isplaying=true;
                    leftFragment.setValue(cur_position,isplaying);
                }
            }
        });

    }

    protected static String getUsername()//实现其他类调用该方法
    {
        return username;
    }
    protected static List<MusicInfo> getList()
    {
        return list;
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showPopupWindow()
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupWindowView=inflater.inflate(R.layout.popup_window,null);
        PopupWindow popupWindow=new PopupWindow(popupWindowView,
                getWindowManager().getDefaultDisplay().getWidth()-32,
                getWindowManager().getDefaultDisplay().getHeight()/2);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw=new ColorDrawable(0Xa0000000);
        popupWindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        popupWindow.showAtLocation(Firstpage.this.findViewById(R.id.show_list),
                Gravity.BOTTOM,0,140);
        listView= (ListView) popupWindowView.findViewById(R.id.mylist);
        listView.setAdapter(musicListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                sendBroadcastToService(ConstUtil.STATE_PLAY, position);
                isplaying=true;
                cur_position=position;
                leftFragment.setValue(cur_position,isplaying);
            }
        });
    }

    @Override
    protected void onStop() {
        drawer_layout.closeDrawers();
        super.onStop();
    }

    @Override
    protected void onPause(){
        drawer_layout.closeDrawers();
        super.onPause();
    }
//    @Override
//    protected void onRestart() {
//        drawer_layout.closeDrawers();
//        super.onRestart();
//    }

    /**
     *向后台Service发送控制广播
     *@param state int state 控制状态码
     * */

    protected void sendBroadcastToService(int state,int position) {
        //向后台Service发送播放控制的广播
        Intent intent=new Intent();
        intent.setAction(ConstUtil.MUSICSERVICE_ACTION);
        intent.putExtra("control", state);
        intent.putExtra("position", position);
        sendBroadcast(intent);
    }


//    @Override
    protected void onDestroy() {
        sendBroadcastToService(ConstUtil.STATE_STOP,cur_position);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private int flag;
    //创建一个广播接收器用于接收后台Service发出的广播
    class MusicBoxReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取Intent中的current消息，current代表当前正在播放的歌曲
            cur_position = intent.getIntExtra("current", -1);
            leftFragment.setValue(cur_position,isplaying);
            flag=intent.getIntExtra("isplaying",-1);
            //Toast.makeText(getApplicationContext(),flag+"",Toast.LENGTH_LONG).show();
            if(flag==1)
            {
                play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            }
            else
            {
                play_pause.setImageDrawable(getResources().getDrawable(R.drawable.play));
            }
            String Song = list.get(cur_position).getTitle();
            int i = Song.lastIndexOf("-");
            String Singer = Song.substring(0, i - 1);
            Song = Song.substring(i + 1, Song.length() - 4);
            singer.setText(Singer);
            song.setText(Song);
        }

    }
    //optionzqz
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.scan) {
            //Toast.makeText(getApplicationContext(),"点击了扫一扫",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(Firstpage.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String result=bundle.getString("result");
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        String brand=jsonObject.optString("brand");
                        String sign=jsonObject.optString("sign");
                        String type=jsonObject.optString("type");
                        String plate_number=jsonObject.optString("plate_number");
                        String engine_number=jsonObject.optString("engine_number");
                        String body_level=jsonObject.optString("body_level");
                        String mile_number=jsonObject.optString("mile_number");
                        String gas_num=jsonObject.optString("gas_num");
                        String engine_performance=jsonObject.optString("engine_performance");
                        String speed_shift=jsonObject.optString("speed_shift");
                        String car_lamb=jsonObject.optString("car_lamb");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(Firstpage.this,QRcodeResult.class);
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

