package com.zqb.carnetwork.example.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zqb.carnetwork.R;

import java.util.List;

/**
 * Created by zqb on 2016/4/14.
 */
public class MusicListAdapter extends BaseAdapter {
    private List<MusicInfo>mData=null;
    private Context mcontext;
    private Holder holder;
    public MusicListAdapter(List<MusicInfo>mData, Context context)
    {
        this.mData=mData;
        this.mcontext=context;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(mcontext)
                    .inflate(R.layout.music_list_item, parent, false);
            holder=new Holder();
            //holder.album= (ImageView) convertView.findViewById(R.id.album);
            holder.music_name= (TextView) convertView.findViewById(R.id.music_name);
            holder.duration= (TextView) convertView.findViewById(R.id.duration);
            convertView.setTag(holder);
        }
        else
        {
            holder= (Holder) convertView.getTag();
        }
//        String path="content://media/external/audio/albums/"+mData.get(position).getAlbum()+".jpg";
//        Bitmap imageBitmap = BitmapFactory.decodeFile(path);
//        if(imageBitmap!=null)
//            holder.album.setImageBitmap(imageBitmap);
        int musicTime = mData.get(position).getDuration() / 1000;//计算时长，最初以毫秒为单位
        String time= musicTime / 60 + ":" + musicTime % 60;
        holder.duration.setText(time);
        holder.music_name.setText(mData.get(position).getTitle());
        return convertView;
    }

    static class Holder
    {
        //ImageView album;
        TextView music_name;
        TextView duration;
    }
}
