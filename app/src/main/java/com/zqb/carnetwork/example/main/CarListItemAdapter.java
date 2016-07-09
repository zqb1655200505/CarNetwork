package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zqb.carnetwork.R;
import java.util.List;

/**
 * Created by zqb on 2016/5/22.
 */
public class CarListItemAdapter extends BaseAdapter {
    private List<MyCarItem>carItems;
    private Context mcontext;
    private RequestQueue queue;
    private ImageLoader imageLoader;
    public CarListItemAdapter(List<MyCarItem> carItems, Context mcontext)
    {
        this.carItems=carItems;
        this.mcontext=mcontext;
        queue= Volley.newRequestQueue(mcontext);
        imageLoader = new ImageLoader(queue, new BitmapCache());
    }
    @Override
    public int getCount() {
        return carItems.size();
    }

    @Override
    public Object getItem(int i) {
        return carItems.get(i);
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
            view= LayoutInflater.from(mcontext)
                    .inflate(R.layout.car_list_item,viewGroup,false);
            holder=new ViewHolder();
            holder.brand= (TextView) view.findViewById(R.id.brand);
            holder.pic= (NetworkImageView) view.findViewById(R.id.pic);
            holder.type= (TextView) view.findViewById(R.id.type);
            view.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) view.getTag();
        }
        String path=NetUrl.Base+carItems.get(i).getPic().substring(3);
        //Log.i("path",path);
        holder.type.setText("车型："+carItems.get(i).getType());
        holder.brand.setText("品牌："+carItems.get(i).getBrand());
        if(path!=null&&!path.equals(""))
        {
            holder.pic.setDefaultImageResId(R.drawable.wait);
            holder.pic.setErrorImageResId(R.drawable.loaderror);
            holder.pic.setImageUrl(path,imageLoader);
        }
        return view;
    }
    static class ViewHolder
    {
        NetworkImageView pic;
        TextView type;
        TextView brand;
    }
}
