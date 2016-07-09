package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2016/5/25.
 */
public class OrderItemAdapter extends BaseAdapter {
    private List<MyOrderItem>list;
    private Context context;
    private RequestQueue queue;
    private ImageLoader imageLoader;

    protected OrderItemAdapter(List<MyOrderItem>list,Context context)
    {
        this.context=context;
        this.list=list;
        queue= Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(queue, new BitmapCache());
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null)
        {
            holder=new ViewHolder();
            Log.i("TAG",list.get(i).getBrand());
            view= LayoutInflater.from(context)
                    .inflate(R.layout.order_list_item,viewGroup,false);
            holder.brand= (TextView) view.findViewById(R.id.brand);
            holder.car_pic= (NetworkImageView) view.findViewById(R.id.carPic);
            holder.money= (TextView) view.findViewById(R.id.money);
            holder.oil_type= (TextView) view.findViewById(R.id.oil_type);
            holder.owner= (TextView) view.findViewById(R.id.owner);
            holder.oil_station= (TextView) view.findViewById(R.id.oil_station);
            view.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) view.getTag();
        }
        String pic_url=NetUrl.Base+list.get(i).getCar_pic().substring(3);
        holder.brand.setText("汽车："+list.get(i).getBrand());
        holder.money.setText("金额："+list.get(i).getMoney());
        holder.oil_station.setText("加油站："+list.get(i).getOil_station());
        holder.oil_type.setText("油类别："+list.get(i).getOil_type());
        holder.owner.setText("车主："+list.get(i).getOwner());
        if(pic_url!=null&&pic_url.length()>0)
        {
            holder.car_pic.setDefaultImageResId(R.drawable.wait);
            holder.car_pic.setErrorImageResId(R.drawable.loaderror);
            holder.car_pic.setImageUrl(pic_url,imageLoader);
        }
        return view;
    }
    static class ViewHolder {
        NetworkImageView car_pic;
        TextView owner;
        TextView brand;
        TextView oil_type;
        TextView money;
        TextView oil_station;
    }
}
