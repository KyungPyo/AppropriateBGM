package com.kp.appropriatebgm.favoritebgm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kp.appropriatebgm.DBController.BGMInfo;
import com.kp.appropriatebgm.R;

import java.util.ArrayList;
import java.util.Locale;

//***************************************
//Database에 있는 BGM을 받아오는 Adapter
//***************************************
public class BGMListAdapter extends BaseAdapter {

    //*Context 즉 띄워질 Activity 클래스를 받아온다고 생각하면 될듯.
    Context mContex=null;
    //*ListView에 띄워질 Music의 ArrayList
    ArrayList<BGMInfo> aMusicList = null;
    //*검색시 update될 리스트를 저장하는 ArrayList
    ArrayList<BGMInfo> searchingList=null;
    //*띄워질 뷰를 infate시켜줄 LayoutInflater
    LayoutInflater layoutInflater=null;
    boolean isCheckBoxVisible=false;
    boolean isChecked=false;


    //**생성자**
    public BGMListAdapter(Context mContex, ArrayList<BGMInfo> musicList) {
        this.mContex = mContex;
        this.aMusicList = musicList;
        layoutInflater= LayoutInflater.from(mContex);
        searchingList=new ArrayList<BGMInfo>();
        searchingList.addAll(aMusicList);
    }

    @Override
    public int getCount() {
        return aMusicList.size();
    }

    @Override
    public BGMInfo getItem(int position) {
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
            itemLayout=layoutInflater.inflate(R.layout.bgmadapter_layout_item,null);
            viewHolder=new ViewHolder();
            viewHolder.name=(TextView)itemLayout.findViewById(R.id.bgmListAdapter_textView_name);
            viewHolder.checkBox=(CheckBox)itemLayout.findViewById(R.id.bgmListAdapter_checkBox_musicCheck);
            itemLayout.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)itemLayout.getTag();
        }

        viewHolder.name.setText(aMusicList.get(position).getBgmName());

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

    //***Filter Method***
    // 검색 시 들어온 문자가 List에 존재하는지 탐색해서
    // 같은 문자가 있는 BGM으로 구성된 List로 갱신해줌
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        aMusicList.clear();
        if (charText.length() == 0) {
            aMusicList.addAll(searchingList);
        }
        else
        {
            for (BGMInfo m : searchingList)
            {
                if (m.getBgmName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    aMusicList.add(m);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSearchingList(){
        searchingList.clear();
        searchingList.addAll(aMusicList);
    }

    public void setCheckBoxVisible(boolean checked){
        isCheckBoxVisible=checked;
    }
}
