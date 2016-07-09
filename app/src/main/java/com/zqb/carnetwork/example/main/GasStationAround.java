package com.zqb.carnetwork.example.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.zqb.carnetwork.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zqb.carnetwork.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 演示poi搜索功能
 */
public class GasStationAround extends Activity implements
        OnGetPoiSearchResultListener {

    private PoiSearch mPoiSearch = null;
    private BaiduMap mBaiduMap = null;
    private MapView mapView;
    private int loadIndex = 0;
    private RequestQueue queue;
    private List<HashMap<String,String>>mList=null;
    // 定位相关
    private LocationClient mLocClient;
    public MyLocationListenner myListener;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    // UI相关
    boolean isFirstLoc = true; // 是否首次定位
    private SDKReceiver mReceiver;
    private BDLocation mLocation;
    //定位回调函数
    public class MyLocationListenner implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null||mapView==null)
            {
                return;
            }
            mLocation=location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            //Log.i("经度",location.getLatitude()+"");
            if (isFirstLoc)
            {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            searchButtonProcess(loadIndex);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR))
            {
                new AlertDialog.Builder(GasStationAround.this).setTitle("提示信息")
                        .setMessage("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置").show();
            }
            else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK))
            {
                Toast.makeText(GasStationAround.this,"key 验证成功! 功能可以正常使用",Toast.LENGTH_SHORT).show();
            }
            else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                new AlertDialog.Builder(GasStationAround.this)
                        .setTitle("提示信息").setMessage("网络出错").show();
            }
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_station_around);


        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);


        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mapView= (MapView) findViewById(R.id.map);
        mBaiduMap=mapView.getMap();
//        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
//                .findFragmentById(R.id.map))).getBaiduMap();


        // 开启定位图层,定位初始化,开启定位
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        myListener=new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
//        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//                        mCurrentMode, true, null));
    }




    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        mapView.onDestroy();
        mapView=null;
        unregisterReceiver(mReceiver);
        mLocClient.stop();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int result_sum=0;
    public void searchButtonProcess(int index) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
        nearbySearchOption.keyword("加油站");
        nearbySearchOption.radius(8000);// 检索半径，单位是米
        nearbySearchOption.pageNum(index);
        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }
    /**
     * 影响搜索按钮点击事件
     *
     * @param v
     */

    public void goToBeforePage(View v)
    {
        if(loadIndex<=0)
        {
            Toast.makeText(this,"已是首页",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadIndex--;
            searchButtonProcess(loadIndex);
        }
    }
    public void goToNextPage(View v) {
        loadIndex++;
        if(loadIndex>=result_sum)
        {
            loadIndex--;
            Toast.makeText(this,"无更多结果",Toast.LENGTH_SHORT).show();
        }
        else
        {
            searchButtonProcess(loadIndex);
        }
    }

    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND)
        {
            return;
        }
        result_sum=result.getTotalPageNum();
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }

    }

    public void onGetPoiDetailResult(final PoiDetailResult result) {
        queue= Volley.newRequestQueue(GasStationAround.this);
        if (result.error != SearchResult.ERRORNO.NO_ERROR)
        {
            Toast.makeText(GasStationAround.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        }
        else
        {
//            Toast.makeText(GasStationAround.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
//                    .show();
            double lat=result.getLocation().latitude;
            double lont=result.getLocation().longitude;
            //System.out.println("经度="+lat+"纬度="+lont);
            String url="http://apis.juhe.cn/oil/local?key=f3fa9bcafe27b3f1679706f4f416f430&" +
                    "lon="+lont+"&lat="+lat+"&r=200";
            final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONObject>() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(JSONObject jsonObject) {

                    int result_code=jsonObject.optInt("error_code");
                    if(result_code==0)
                    {
                        //Log.i("result_gas_station",jsonObject.toString());
                        try {
                            JSONObject jsonObject1=new JSONObject(jsonObject.optString("result"));
                            JSONArray jsonArray=new JSONArray(jsonObject1.optString("data"));
                            JSONObject jsonObject2=jsonArray.optJSONObject(0);
                            final String name=jsonObject2.optString("name");
                            String address=jsonObject2.optString("address");
                            String brand=jsonObject2.optString("brandname");
                            String type=jsonObject2.optString("type");
                            String discount=jsonObject2.optString("discount");
                            String exhaust=jsonObject2.optString("exhaust");
                            String server=jsonObject2.optString("fwlsmc");
                            String distance= jsonObject2.optString("distance");
                            final Dialog dialog=new AlertDialog.Builder(GasStationAround.this).create();
                            dialog.show();
                            Window window=dialog.getWindow();
                            window.setContentView(R.layout.gas_station_dialog);
                            JSONObject gas_price=jsonObject2.optJSONObject("gastprice");
                            String price1=gas_price.optString("92#");
                            String price2=gas_price.optString("95#");
                            final double lat=jsonObject2.optDouble("lat");
                            final double lon=jsonObject2.optDouble("lon");

                            TextView tv_name= (TextView) window.findViewById(R.id.station_name);
                            tv_name.setText(name);
                            TextView tv_brand= (TextView) window.findViewById(R.id.brandname);
                            tv_brand.setText(brand);
                            TextView tv_type= (TextView) window.findViewById(R.id.type);
                            tv_type.setText(type);
                            TextView tv_discount= (TextView) window.findViewById(R.id.discount);
                            tv_discount.setText(discount);
                            TextView tv_exhaust= (TextView) window.findViewById(R.id.exhaust);
                            tv_exhaust.setText(exhaust);
                            TextView tv_address= (TextView) window.findViewById(R.id.address);
                            tv_address.setText(address);
                            TextView tv_server= (TextView) window.findViewById(R.id.server);
                            tv_server.setText(server);
                            TextView tv_distance= (TextView) window.findViewById(R.id.distance);
                            tv_distance.setText(distance+"");
                            TextView tv_appointment= (TextView) window.findViewById(R.id.appointment);
                            TextView tv_navigation= (TextView) window.findViewById(R.id.navgation);
                            TextView tv_price1= (TextView) window.findViewById(R.id.price1);
                            tv_price1.setText(price1);
                            TextView tv_price2= (TextView) window.findViewById(R.id.price2);
                            tv_price2.setText(price2);
                            tv_appointment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(GasStationAround.this,AppointmentOil.class);
                                    intent.putExtra("from","gasstation");
                                    intent.putExtra("station_name",name);
                                    dialog.dismiss();
                                    startActivity(intent);
                                }
                            });
                            tv_navigation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(GasStationAround.this,RoutePlan.class);
                                    intent.putExtra("code",1);//标记从哪个activity传值过去
                                    intent.putExtra("st_lat",mLocation.getLatitude());
                                    intent.putExtra("st_lon",mLocation.getLongitude());
                                    intent.putExtra("end_lat",lat);
                                    intent.putExtra("end_lon",lon);
                                    dialog.dismiss();
                                    startActivity(intent);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(GasStationAround.this,"请求数据出错",Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            queue.add(jsonObjectRequest);
        }
    }



    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            return true;
        }
    }
}
