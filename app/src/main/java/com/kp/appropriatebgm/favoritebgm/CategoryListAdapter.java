package com.kp.appropriatebgm.favoritebgm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.TextView;

import com.kp.appropriatebgm.DBController.Category;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;
import java.util.HashMap;

//카테고리 목록을 받아오는 Adapter
public class CategoryListAdapter extends BaseAdapter {

    Context mContex=null;
    ArrayList<Category> categoryArrayList=null;
    LayoutInflater layoutInflater=null;
    boolean isCheckBoxVisible=false;
    HashMap<String, Integer> ctgcheckHashList;

    public CategoryListAdapter(Context mContex, ArrayList<Category> categoryArrayList) {
        this.mContex = mContex;
        this.categoryArrayList = categoryArrayList;
        layoutInflater= LayoutInflater.from(mContex);
        ctgcheckHashList = new HashMap<>();
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

    public String getlistName(int position){ return categoryArrayList.get(position).getCateName(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        View itemLayout=convertView;
        ViewHolder viewHolder=null;
        String categoryListCheckName;

        if(itemLayout==null){
            itemLayout=layoutInflater.inflate(R.layout.categoryadapter_layout_item,null);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)itemLayout.findViewById(R.id.category_item);
            viewHolder.chkbox=(CheckBox)itemLayout.findViewById(R.id.category_checkBox_categoryCheck);
            itemLayout.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)itemLayout.getTag();
        }
        viewHolder.name.setText(categoryArrayList.get(position).getCateName());
        categoryListCheckName = categoryArrayList.get(position).getCateName();

        if(isCheckBoxVisible){
            if(categoryArrayList.get(position).getCateId() != 1 && categoryArrayList.get(position).getCateId() != 2) {
                viewHolder.chkbox.setVisibility(CheckBox.VISIBLE);
                viewHolder.chkbox.setEnabled(true);
                viewHolder.chkbox.setChecked(ctgcheckHashList.containsKey(categoryListCheckName));
            } else {
                viewHolder.chkbox.setVisibility(View.INVISIBLE);
                viewHolder.chkbox.setEnabled(false);
                viewHolder.chkbox.setChecked(ctgcheckHashList.containsKey(categoryListCheckName));
            }
        }else{
            viewHolder.chkbox.setVisibility(CheckBox.INVISIBLE);
            viewHolder.chkbox.setEnabled(false);
            viewHolder.chkbox.setChecked(false);
            ctgcheckHashList.clear();
        }
        viewHolder.chkbox.setFocusable(false);
        viewHolder.chkbox.setClickable(false);

        viewHolder.chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("position", pos + "");
                Log.e("isChecked", isChecked + "");
                Log.e("name", categoryArrayList.get(pos).getCateName());
                if (isChecked) {
                    ctgcheckHashList.put(categoryArrayList.get(pos).getCateName(), pos);
                } else if (!isChecked) {
                    ctgcheckHashList.remove(categoryArrayList.get(pos).getCateName());
                }
            }
        });


        return itemLayout;

    }


    class ViewHolder{
        TextView name;
        CheckBox chkbox;
    }

    public void setCheckBoxEnable(boolean checking)
    {
        isCheckBoxVisible=checking;
    }
    public HashMap<String, Integer> addCheckData()
    {
        return ctgcheckHashList;
    }

    public boolean getCheckBoxVisibility()
    {
        return isCheckBoxVisible;
    }

    public void setCheckBoxChecked(int position)
    {
        String category_name = categoryArrayList.get(position).getCateName();
        if(ctgcheckHashList.containsKey(category_name))
        {
            ctgcheckHashList.remove(category_name);
        }
        else
        {
            ctgcheckHashList.put(category_name, position);
        }
    }

}

