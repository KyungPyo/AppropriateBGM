package com.kp.appropriatebgm.favoritebgm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.Category;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;

//카테고리 목록을 받아오는 Adapter
public class CategoryListAdapter extends BaseAdapter {

    Context mContex=null;
    ArrayList<Category> categoryArrayList=null;
    LayoutInflater layoutInflater=null;
    CheckBox ctg_list_Checkbox;

    public CategoryListAdapter(Context mContex, ArrayList<Category> categoryArrayList) {
        this.mContex = mContex;
        this.categoryArrayList = categoryArrayList;
        layoutInflater= LayoutInflater.from(mContex);
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getlistId(int position){ return categoryArrayList.get(position).getCateId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout=convertView;
        ViewHolder viewHolder=null;

        if(itemLayout==null){
            itemLayout=layoutInflater.inflate(R.layout.spinner_item_layout,null);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)itemLayout.findViewById(R.id.category_item);
            viewHolder.chkbox=(CheckBox)itemLayout.findViewById(R.id.category_checkBox_categoryCheck);
            itemLayout.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)itemLayout.getTag();
        }
        viewHolder.name.setText(categoryArrayList.get(position).getCateName());
        viewHolder.chkbox.setVisibility(View.INVISIBLE);
        viewHolder.chkbox.setEnabled(false);
        return itemLayout;
    }


    class ViewHolder{
        TextView name;
        CheckBox chkbox;
    }
}

