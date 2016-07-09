package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zqb.carnetwork.R;

import java.util.List;

public class ProvinceListAdapter extends BaseAdapter {
	
	private List<ListModel> mDate;
	private Context mContext;

	public ProvinceListAdapter(Context mContext, List mDate){
		this.mContext=mContext;
		this.mDate=mDate;		
	}

	@Override
	public int getCount() {
		return mDate.size();
	}

	@Override
	public Object getItem(int position) {
		return mDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = View.inflate(mContext, R.layout.province_list_item, null);
		ListModel model=mDate.get(position) ;
		TextView txt_name =(TextView) view.findViewById(R.id.txt_name);
		txt_name.setText(model.getTextName());
		txt_name.setTag(model.getNameId());
		return view;
	}
}
