package com.kp.appropriatebgm.favoritebgm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.Favorite;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;

/**
 * Created by Choi on 2016-02-01.
 */
public class FavoriteListAdapter extends BaseAdapter {

    Context mContext=null;
    ArrayList<Favorite> favoriteArrayList = null;
    LayoutInflater layoutInflater=null;
    private boolean isCheckBoxVisible=false;

    public FavoriteListAdapter(Context mContext, ArrayList<Favorite> favoriteList) {
        this.mContext = mContext;
        this.favoriteArrayList = favoriteList;
        layoutInflater= LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return favoriteArrayList.size();
    }

    @Override
    public Favorite getItem(int position) {
        return favoriteArrayList.get(position);
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
            itemLayout=layoutInflater.inflate(R.layout.favoriteadapter_layout_item,null);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)itemLayout.findViewById(R.id.favoriteAdapter_textView_bgmName);
            viewHolder.checkBox=(CheckBox)itemLayout.findViewById(R.id.favoriteAdapter_checkBox_deleteCheck);
            itemLayout.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)itemLayout.getTag();
        }

        viewHolder.name.setText(favoriteArrayList.get(position).getBgmName());

        if(this.isCheckBoxVisible){
            viewHolder.checkBox.setVisibility(CheckBox.VISIBLE);
        }else{
            viewHolder.checkBox.setVisibility(CheckBox.GONE);
        }


        viewHolder.checkBox.setChecked(false);
        viewHolder.checkBox.setChecked(((ListView) parent).isItemChecked(position));
        viewHolder.checkBox.setFocusable(false);
        viewHolder.checkBox.setClickable(false);


        return itemLayout;
    }

    class ViewHolder{
        TextView name;
        CheckBox checkBox;
    }

    public void setCheckBoxVisible(boolean checked){
        isCheckBoxVisible=checked;
    }
}
