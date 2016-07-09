package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zqb.carnetwork.R;

import java.util.List;

/**
 * Created by zqb on 2016/5/31.
 */
public class CityListAdapter extends BaseAdapter {
    private List<ListModel> mData;
    private Context mContext;
    public CityListAdapter(Context mcontext,List<ListModel>mData)
    {
        this.mContext=mcontext;
        this.mData=mData;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = View.inflate(mContext, R.layout.city_listitem, null);
        ListModel model=mData.get(position) ;
        TextView txt_name =(TextView) view.findViewById(R.id.city_name);
        txt_name.setText(model.getTextName());
        txt_name.setTag(model.getNameId());
        return view;
    }
}
