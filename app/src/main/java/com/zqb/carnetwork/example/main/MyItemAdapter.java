package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zqb.carnetwork.R;

import java.util.ArrayList;

/**
 * Created by zqb on 2016/4/10.
 */
public class MyItemAdapter extends BaseAdapter {
    private ArrayList<MyItem>myItems;
    private Context mcontext;
    public MyItemAdapter(ArrayList<MyItem>myItems,Context context)
    {
        this.myItems=myItems;
        this.mcontext=context;
    }
    @Override
    public int getCount() {
        return myItems.size();
    }

    @Override
    public Object getItem(int position) {
        return myItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(mcontext)
                    .inflate(R.layout.left_item_list, parent, false);
            holder=new ViewHolder();
            holder.pic= (ImageView) convertView.findViewById(R.id.pic);
            holder.func= (TextView) convertView.findViewById(R.id.theme);
            convertView.setTag(holder);
        }
        else
        {
            holder= (ViewHolder) convertView.getTag();
        }
        holder.func.setText(myItems.get(position).getFunc());
        holder.pic.setImageResource(myItems.get(position).getPic());
        return convertView;
    }
    static class ViewHolder
    {
        ImageView pic;
        TextView func;
    }
}
