package com.kp.appropriatebgm.record;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kp.appropriatebgm.R;


public class CancelRecordDlg extends Dialog{
	/*
	 * Layout
	 */
	private TextView mTitleView;
	private TextView mContentView;
	private Button mLeftButton;
	private Button mRightButton;
	private String mTitle;
	private String mContent;


	private View.OnClickListener mLeftClickListener;
	private View.OnClickListener mRightClickListener;

	/*
	 * Layout
	 */
	private void setLayout(){
		mTitleView = (TextView) findViewById(R.id.cancelRecordDlg_textview_title);
		mContentView = (TextView) findViewById(R.id.concelRecordDlg_textview_content);
		mLeftButton = (Button) findViewById(R.id.cancelRecordDlg_btn_left);
		mRightButton = (Button) findViewById(R.id.cancelRecordDlg_btn_right);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		getWindow().setAttributes(lpWindow);
		
		setContentView(R.layout.dialog_cencalrecord);
		
		setLayout();
		setTitle(mTitle);
		setContent(mContent);
		setClickListener(mLeftClickListener , mRightClickListener);
	}

	// Method : 초기설정
	// Return Value : void
	// Parameter : context
	// Use : Dialog의 배경을 투명처리해줘서 뒷배경이 보이게 한다.
	public CancelRecordDlg(Context context) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
	}

	// Method : 초기설정
	// Return Value : void
	// Parameter : context, title, singleListener(저장버튼 선택한 파라미터)
	// Use : Dialog이기 때문에 타이틀바를 없에준다.
	public CancelRecordDlg(Context context, String title,
						   View.OnClickListener singleListener) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.mTitle = title;
		this.mLeftClickListener = singleListener;
	}

	// Method : 초기설정
	// Return Value : void
	// Parameter : context, title, content, leftListener, rightListener
	// Use : 제목, 내용, 하단의 좌우 버튼의 선언
	public CancelRecordDlg(Context context, String title, String content,
						   View.OnClickListener leftListener, View.OnClickListener rightListener) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		this.mTitle = title;
		this.mContent = content;
		this.mLeftClickListener = leftListener;
		this.mRightClickListener = rightListener;
	}
	
	private void setTitle(String title){
		mTitleView.setText(title);
	}
	
	private void setContent(String content){
		mContentView.setText(content);
	}
	
	private void setClickListener(View.OnClickListener left , View.OnClickListener right){
		if(left!=null && right!=null){
			mLeftButton.setOnClickListener(left);
			mRightButton.setOnClickListener(right);
		}else if(left!=null && right==null){
			mLeftButton.setOnClickListener(left);
		}else {
			
		}
	}
	

	
}









