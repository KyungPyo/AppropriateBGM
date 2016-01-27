package com.kp.appropriatebgm.favoritebgm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.kp.appropriatebgm.R;
/**
 * Created by LG on 2015-12-29.
 */
public class FavoriteListAdapter extends BaseAdapter {

    Context mContex=null;
    ArrayList<Music> aMusicList=null;
    LayoutInflater layoutInflater=null;

    public FavoriteListAdapter(Context mContex, ArrayList<Music> musicList) {
        this.mContex = mContex;
        this.aMusicList = musicList;
        layoutInflater= LayoutInflater.from(mContex);
    }

    @Override
    public int getCount() {
        return aMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return aMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout=convertView;
        ViewHolder viewHolder=null;

        if(itemLayout==null){
            itemLayout=layoutInflater.inflate(R.layout.music_list_layout,null);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)itemLayout.findViewById(R.id.musicName);
            itemLayout.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)itemLayout.getTag();
        }

        viewHolder.name.setText(aMusicList.get(position).getMusicName());

        return itemLayout;
    }


    class ViewHolder{
        TextView name;

    }
}

