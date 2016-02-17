package com.kp.appropriatebgm.record;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.appropriatebgm.DBController.Category;
import com.kp.appropriatebgm.DBController.DBManager;
import com.kp.appropriatebgm.MusicPlayer;
import com.kp.appropriatebgm.PlaybackBarTask;
import com.kp.appropriatebgm.R;
import com.kp.appropriatebgm.favoritebgm.CategoryListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RecordActivity extends AppCompatActivity {
    // 녹음 관련
    private RecordManager recordManager = null;
    private RecordTask recordTask = null;
    // 재생 관련
    private MusicPlayer musicPlayer;

    private final int PROGRESS_INTERVAL = 50;     // 재생 progress바 갱신주기
    private int maxTime = 0;    // 현재 녹음된 파일 재생길이

    // 화면 출력 관련
    private PlaybackBarTask playbackBar ;

    private int currentRecordTimeMs = 0, currentPlayTimeMs = 0;
    private SeekBar recordProgressBar = null;
    private TextView recordMaxTimeText = null;
    private TextView recordPlayTimeText = null;
    private ImageView btnPlay = null;
    private ImageButton btnRecord = null;
    private ImageView btnRecordUp = null;
    private ImageView btnSave = null;
    //다이얼로그
    private Category selectedCategory;
    // 저장 관련(팝업)
    private View mPopupLayout = null;
    private PopupWindow mPopupWindow = null;
    private Button popSaveBtn = null;
    private Button popCancelBtn = null;
    private EditText filenameEt = null;
    private Spinner categorySel = null;

    DBManager dbManager = DBManager.getInstance(this);//DB
    ArrayList<Category> categoryList;
    CategoryListAdapter categoryAdapter;

    /******
     * 작업스레드 AsyncTask 상속받아서 클래스 생성 - 녹음 스레드
     *****/
    private class RecordTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {     // 작업스레드 사전 처리작업
            currentRecordTimeMs = 0;
            btnPlay.setEnabled(false);  // 녹음중엔 재생버튼을 누를 수 없다.
            btnSave.setEnabled(false);  // 저장버튼도
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {      // 실제 작업스레드 동작
            recordManager.start(); //레코드매니저 실행
            while (true) {
                /** 녹음시간제한 주고 싶다면  **
                 if (!recordManager.isRecording() || currentRecordTimeMs > 180*1000 )3분이 지나면
                 {
                 recordManager.stop();
                 return true;
                 }*/
                if (isCancelled()) {  // 작업이 취소되었으면
                    if (recordManager.isRecording())
                        recordManager.stop();
                    return null;
                }
                try {
                    Thread.sleep(PROGRESS_INTERVAL);// cancel되면 이부분에서 Exception이 발생해 catch로 넘어간다
                    currentRecordTimeMs += PROGRESS_INTERVAL;
                    publishProgress(currentRecordTimeMs);   // 현재 녹음시간 메인스레드로 전달
                } catch (InterruptedException e) {
                    if (recordManager.isRecording())
                        recordManager.stop();
                    return null;
                }
            }
        }
        @Override
        protected void onCancelled() { // 녹음이 취소 된다면
            if (recordManager.isRecording())// 녹음이였다면
                recordManager.stop(); //녹음 중지
            btnPlay.setEnabled(true);   // 재생버튼 클릭가능
            btnSave.setEnabled(true);   // 저장버튼 클릭가능
            btnRecord.setEnabled(true); // 다시 녹음하고 싶으면 녹음클릭시 재녹음 가능
            prepareRecordFileToPlay();  // 녹음한 파일을 Temp로 만들어 재생할 준비.
            super.onCancelled();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            int currentTime = values[0];

            // 현재 녹음 진행상황 화면에 표시
            // Seekbar 설정
            recordProgressBar.setMax(currentTime);
            recordProgressBar.setProgress(currentTime);

            setTimeText(recordMaxTimeText, currentTime);
            setTimeText(recordPlayTimeText, currentTime);

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnPlay.setEnabled(true);   // 재생버튼 클릭가능
            prepareRecordFileToPlay();
            super.onPostExecute(aVoid);
        }
    }
    /***** 녹음 스레드  *****/

    /******** 액티비티  *****/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // 저장될 디렉토리명을 app_name으로 설정
        recordManager = new RecordManager(getString(R.string.app_name));
        btnRecordUp = (ImageView) findViewById(R.id.recardActivity_btn_startRecordImg); // 녹음상태 상단 이미지
        recordProgressBar = (SeekBar) findViewById(R.id.recordActivity_seekbar_recordSeekBar);            // 재생 바
        recordMaxTimeText = (TextView) findViewById(R.id.recordActivity_textview_maxtimeAtvrecord);   // 재생할 파일 최대길이
        recordPlayTimeText = (TextView) findViewById(R.id.recordActivity_textview_playtimeAtvrecord); // 재생하는 파일 현재시간
        btnPlay = (ImageView) findViewById(R.id.recordActivity_btn_playAtvrecord);                // 재생버튼
        btnRecord = (ImageButton) findViewById(R.id.recordActivity_btn_startRecord);               // 녹음시작/중지 버튼
        btnSave = (ImageView) findViewById(R.id.recordActivity_btn_saveAtvrecord);                // 저장버튼
        btnPlay.setEnabled(false);  // 녹음하기전엔(녹음된 파일이 없으면) 재생버튼을 누를 수 없다.
        btnSave.setEnabled(false);  // 저장버튼도
        setPopupWindow();
        TempDelete();
    }

    // 화면에서 벗어나면 작업을 정지한다.
    // 재생중이면 정지, 녹음중이면 녹음중지
    @Override
    protected void onPause() {
        super.onPause();
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pauseBgm();
        }
        if (recordManager.isRecording()) {
            recordManager.stop();
            recordTask.cancel(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playbackBar != null)
            playbackBar.cancel(true);
    }

    // Method : 녹음이후 Temp폴더에서 임시저장된 음원을가지고와서 재생을 준비
    // Return Value : void
    // Parameter : void
    // Use : 녹음이 된 이후, Temp폴더에서 임시저장된 음원을 가지고와서 재생준비 //
    public void prepareRecordFileToPlay() {
        musicPlayer = new MusicPlayer(this,recordManager.getPath());
        playbackBar = new PlaybackBarTask(this,recordProgressBar,recordPlayTimeText,recordMaxTimeText);
        playbackBar.setMusic(musicPlayer);
        playbackBar.setPlayAndPauseBtn(btnPlay);
        playbackBar.execute();
    }

    // Method : 녹음하기 클릭
    // Return Value : void
    // Parameter : View
    // Use : 녹음 버튼 클릭을하면 각각의 뷰에 대한 상황에 맞는 처리.
    public void onClick_startRecord(View v) {
        switch (v.getId()) {
            case R.id.recordActivity_btn_startRecord: {
                if (v.getId() == R.id.recordActivity_btn_startRecord) {
                    // 재생중이면 재생중이던것을 정지하고 녹음
                    if (musicPlayer != null && (musicPlayer.isPlaying() || musicPlayer.isPaused()) ) {   // 재생중 또는 일시정지 상태이면
                        musicPlayer.stopBgm();  // 재생중이던거 정지하고
                        musicPlayer = null;
                    }

                    // 녹음중이 아니면
                    if (!recordManager.isRecording()) {
                        // 녹음이 된 임시파일이 있다면 다이얼로그를 호출
                        if (new File(recordManager.getPath()).isFile()) {
                            AlertDialog dialog;
                            dialog = new AlertDialog.Builder(this).setTitle("녹 음 확 인")
                                    .setMessage("현재 녹음된 파일을 삭제하고 다시 녹음을 시작 하시겠습니까?")
                                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            recordTask = new RecordTask();
                                            Log.i("RecordActivity","Accept : recordAgain");
                                            recordTask.execute();   // 녹음 시작
                                            dialog.dismiss();       //본래의 액티비티로 복귀
                                            btnRecordUp.setImageResource(R.drawable.btn_stoprecord_selector1);

                                        }
                                    })
                                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            Log.i("RecordActivity","Cancel : Remainrecord");
                                            dialog.cancel();
                                        }
                                    })
                                    .show();

                        }
                        // 녹음된 파일이 없다면 녹음을 시작한다.
                        else {
                            recordTask = new RecordTask();
                            recordTask.execute();  // 녹음 시작
                            v.setBackgroundResource(R.drawable.btn_stoprecord_selector);// 녹음버튼의 이미지를 녹음중으로 변경
                            btnRecordUp.setImageResource(R.drawable.btn_stoprecord_selector1);
                        }
                    }
                    // 녹음 중이라면 녹음을 중지한다.
                    else {
                        recordManager.stop();
                        recordTask.cancel(true);
                        v.setBackgroundResource(R.drawable.btn_startrecord_selector);// 녹음버튼의 이미지를 녹음 준비중으로 변경
                        btnRecordUp.setImageResource(R.drawable.btn_startrecord_selector1);// 녹음버튼의 이미지를 녹음 준비중으로 변경
                    }
                    break;
                }
            }
        }
    }

    // Method : 재생하기
    // Return Value : void
    // Parameter : View
    // Use : 재생 버튼 클릭을 했을때 각각의 뷰에 대한 상황에 맞는 처리.
    public void onClick_playRecord(View v) {
        switch (v.getId()) {
            // 재생버튼
            case R.id.recordActivity_btn_playAtvrecord: {
                // Use : 재생중과 녹음중이 아니라면 재생 시작 후 seekbar&textview 처리
                if (!musicPlayer.isPlaying() && !recordManager.isRecording()) {
                    musicPlayer.playBgm();
                    Log.i("RecordAcitivity","onClickPlayBtn");
                    playbackBar = new PlaybackBarTask(this,recordProgressBar,recordPlayTimeText,recordMaxTimeText);
                    playbackBar.setMusic(musicPlayer);
                    playbackBar.setPlayAndPauseBtn(btnPlay);
                    playbackBar.execute();
                }
                // Use : 재생중이거나 녹음중이라면 일시정지
                else {
                    musicPlayer.pauseBgm();
                    Log.i("RecordAcitivity", "onClickPauseBtn");
                    playbackBar.setPlayAndPauseBtn(btnPlay);
                }
                break;
            }

        }
    }




    // Method : 저장버튼 클릭시 Dlg 출력
    // Return Value : void
    // Parameter : View
    // Use : Record Activity에서 저장버튼 클릭 하면 다이얼로그가 출력된다
    public void onClick_save(View v) {
        if (v.getId() == R.id.recordActivity_btn_saveAtvrecord) {
            // 팝업윈도우 출력
            // 가운데에서 위로 120만큼 올라와서 팝업윈도우 등장.
            mPopupWindow.showAtLocation(mPopupLayout, Gravity.CENTER, 0, -120);
            Log.i("RecordActivity","OnClickSaveBtn With PopupWindow");
        }
    }

    // Method : 하단부의 재생가능시간
    // Return Value : void
    // Parameter : targetView - 해당뷰 , timeMs - 해당뷰의 재생시간
    // Use :  재생시간 표시하는 텍스트뷰 설정
    public void setTimeText(TextView targetView, int timeMs) {
        int sec = 0, min = 0;
        String timeText = "";
        // TextView 설정
        min = timeMs / 60000;
        timeMs = timeMs % 60000;
        sec = timeMs / 1000;

        if (min < 10)
            timeText = "0";
        timeText = timeText + min + ":";
        if (sec < 10)
            timeText = timeText + "0";
        timeText = timeText + sec;

        targetView.setText(timeText);
    }

    // Method : RecordTempFile 삭제
    // Return Value : void
    // Parameter : Void
    // Use : 녹음 액티비티 시작시 Temp파일이 있으면 삭제.
    public void TempDelete(){
        boolean isTempExist = new File(recordManager.getPath()).isFile();
        if(isTempExist = true)
        {
            new File(recordManager.getPath()).delete();
            Log.i("RecordAcitivity", "DeletedTempFile");
        }

    }

    // Method : 저장버튼 이벤트 리스너
    // Return Value : void
    // Parameter : void
    // Use : 저장버튼을 눌렀을 때 출력되는 팝업윈도우 설정(버튼 이벤트리스너 포함)
    public void setPopupWindow() {
        mPopupLayout = getLayoutInflater().inflate(R.layout.popup_saverecord, null);    // 팝업으로 띄울 xml 연결
        // 팝업윈도우
        popSaveBtn = (Button) mPopupLayout.findViewById(R.id.popupRecordActivity_btn_AcceptSave);
        popCancelBtn = (Button) mPopupLayout.findViewById(R.id.popupRecordActivity_btn_saveCancel);
        filenameEt = (EditText) mPopupLayout.findViewById(R.id.popupRecordActivity_editText_saveFilename);
        categorySel = (Spinner) mPopupLayout.findViewById(R.id.popupRecordActivity_spinner_selectSaveCategory);
        // 팝업 윈도우 생성 popup_saverecord.xml 파일
        mPopupWindow = new PopupWindow(mPopupLayout,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        categoryList = dbManager.getCategoryList();
        categoryList.remove(0);
        categoryAdapter = new CategoryListAdapter(this, categoryList);
        categorySel.setBackgroundColor(Color.WHITE);
        // 카테고리 선택 스피너 선택 이벤트
        categorySel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryList.get(position);
                Log.d("RecordAcitivity","CategoryId : "+ selectedCategory.getCateId() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        categorySel.setAdapter(categoryAdapter);

        // Method : Edittext 엔터 및 특수문자 입력 안되기
        // Return Value : void
        // Parameter : void
        // Use :  한글 영어 숫자말고는 아에 입력이 안됨.
        TextWatcher watcher = new TextWatcher() {
            String text;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                if (length > 0) {
                    Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-흐]+$");//영문, 숫자, 한글만 허용
                    if (!ps.matcher(s).matches()) {
                        filenameEt.setText(text);
                        filenameEt.setSelection(filenameEt.length());
                    }
                }
            }

        };
        filenameEt.addTextChangedListener(watcher);

        // Method : 팝업윈도우내 저장버튼 클릭
        // Return Value : void
        // Parameter : View
        // Use :  저장버튼을 눌렀을때의 동작들.
        popSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFileName = filenameEt.getText().toString();  // 입력한 파일명 받아오기
                filenameEt.setHint("파일명을 입력해주세요(10글자)");
                filenameEt.setSingleLine(true);//한줄입력
                filenameEt.setSelectAllOnFocus(false);
                // Use : 파일명이 입력안됬을때.
                if (filenameEt.length() == 0) {     // 파일명 입력확인
                    Toast.makeText(RecordActivity.this, "파일명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    Log.i("RecordAcitivity", "Error : MissingFileName");
                }
                // Use : 파일명이 8글자 초과했을시
                else if (filenameEt.length() > 10) {
                    Toast.makeText(RecordActivity.this, " 파일명은 10글자를 넘을수 없습니다", Toast.LENGTH_SHORT).show();
                    Log.i("RecordAcitivity", "Error : OverflowFileNameLength");
                }
                // Use : 카테고리를 선택안했을시
                else if (selectedCategory == null) {   // 카테고리 선택확인
                    Toast.makeText(RecordActivity.this, "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
                    Log.i("RecordActivity", "Error : MissingCategoryId");
                }
                // Use : 파일명이 중복일때
                else if (dbManager.isExistFileName(newFileName)) {
                    Toast.makeText(RecordActivity.this, "파일이름이 중복입니다.", Toast.LENGTH_SHORT).show();
                    Log.i("RecordActivity", "Error : DuplicationFileName");
                }
                // Use :  해당 예외처리사항이 아무것도 없을시 저장
                else {
                    Log.i("RecordActivity","SavedFilename :" + newFileName);
                    File file = new File(recordManager.getPath());
                    File renamedFile = new File(recordManager.getDirPath() + File.separator + newFileName + ".mp3");
                    file.renameTo(renamedFile);
                    dbManager.insertBGM(renamedFile.getPath(), newFileName, selectedCategory.getCateId());
                    setResult(RESULT_OK);
                    if (playbackBar != null)
                        playbackBar.cancel(true);
                    finish();
                }
            }
        });
        /***** (팝업윈도우 내 클릭이벤트) 팝업윈도우의 취소버튼을 클릭 ****/
        popCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }
    /***** 액티비티 *****/

    // Method : 뒤로버튼 이벤트 리스너
    // Return Value : void
    // Parameter : keycode
    // Use : 백키버튼을 눌렀을 때 출력되는 팝업윈도우 설정(버튼 이벤트리스너 포함)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(new File(recordManager.getPath()).isFile()) {
            if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
                AlertDialog dialog;
                dialog = new AlertDialog.Builder(this).setTitle("종료확인")
                        .setMessage(" 지금 종료하시면 녹음파일이 삭제됩니다. 종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                File files = new File(recordManager.getPath());
                                Log.i("RecordAcitivity","Deleted : " + recordManager.getPath());
                                files.delete();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}