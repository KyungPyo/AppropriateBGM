package com.kp.appropriatebgm.Category;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kp.appropriatebgm.R;

/**
 * Created by GD on 2016-01-28.
 */
// Class : CtgCursorAdapter (리스트에 설정할 커서 어댑터)
// Use : 리스트에 띄울 데이터를 설정하기 위한 클래스
public class CtgCursorAdapter extends CursorAdapter
{
    final static String KEY_ID = "_id";
    final static String KEY_NAME = "name";
    final static String TABLE_NAME = "ctgtable";

    CheckBox tvchk;

    // Method : 커서 어댑터 생성자
    // Use : context(액티비티). c(사용할 커서), flags( 어댑터의 동작을 결정 )
    public CtgCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    // Method : 각 아이템의 속성 지정
    // Return value : void
    // paremeter : view(띄울 뷰), context(액티비티), cursor(커서)
    // Use : 리스트의 아이템의 초기값 및 속성을 설정해주는 기능입니다. 객체가 있는 경우에 호출
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById( R.id.tv_name );
        tvchk = (CheckBox) view.findViewById( R.id.tv_chk );

        String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
        tvchk.setVisibility(View.INVISIBLE);


        tvName.setText(name);
    }

    // Method : 리스트 뷰에 표시될 뷰를 설정
    // Return value : View
    // parameter : context(액티비티), cursor(커서), parent(부모 뷴)
    // Use : 리스트 뷰에 표시될 뷰를 설정하는 기능입니다. 뷰 객체가 null인 경우에 호출
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( R.layout.list_item, parent, false );
        return v;
    }

}