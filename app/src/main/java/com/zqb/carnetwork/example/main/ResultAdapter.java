package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zqb.carnetwork.R;

import java.util.List;

/**
 * Created by zqb on 2016/6/3.
 */
public class ResultAdapter extends BaseAdapter {
    private List<MyRuleBreakResultItem> mData;
    private Context mContext;
    public ResultAdapter(Context mContext,List<MyRuleBreakResultItem>mData)
    {
        this.mContext=mContext;
        this.mData=mData;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Holder holder;
        if(view==null)
        {
            view= LayoutInflater.from(mContext)
                    .inflate(R.layout.rule_break_result_listitem,viewGroup,false);
            holder=new Holder();
            holder.tv_address= (TextView) view.findViewById(R.id.fine_address);
            holder.tv_main_infor= (TextView) view.findViewById(R.id.fine_infor);
            holder.tv_money= (TextView) view.findViewById(R.id.fine_money);
            holder.tv_time= (TextView) view.findViewById(R.id.fine_time);
            holder.tv_money_fine= (TextView) view.findViewById(R.id.money_fine);
            view.setTag(holder);
        }
        else
        {
            holder= (Holder) view.getTag();
        }
        holder.tv_time.setText(mData.get(position).getTime());
        holder.tv_money.setText(mData.get(position).getMoney());
        holder.tv_main_infor.setText(mData.get(position).getMain_infor());
        holder.tv_address.setText(mData.get(position).getAddress());
        holder.tv_money_fine.setText("对违法驾驶员处以罚款"+mData.get(position).getMoney()+"(金额单位：元)");
        return view;
    }


    class Holder{
        TextView tv_money;
        TextView tv_time;
        TextView tv_address;
        TextView tv_main_infor;
        TextView tv_money_fine;
    }
}
