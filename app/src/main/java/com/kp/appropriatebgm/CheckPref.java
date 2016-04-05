package com.kp.appropriatebgm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class CheckPref {

    private SharedPreferences settingPref = null;

    public CheckPref(AppCompatActivity activity){

        settingPref = activity.getSharedPreferences("AppSetting", Context.MODE_PRIVATE);
    }

    // Method : 최초실행여부 저장
    // Return Value : void
    // Parameter : void
    // Use : 최초실행 여부를 '최초실행이 아님(false)' 로 저장한다.
    public void setFirstExcute(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("FirstExcute", false);
        prefEditor.apply();
    }

    // Method : 최초실행여부 받아오기
    // Return Value : boolean(첫실행이면 true, 아니면 false)
    // Parameter : void
    // Use : 최초실행여부 받아오기. 기본 설정값은 true.
    public boolean getFirstExcute(){
        return settingPref.getBoolean("FirstExcute", true);
    }

    // Method : 권한허용여부 저장
    // Return Value : void
    // Parameter : granted(권한 허용여부. 허용이면 true, 아니면 false)
    // Use : 권한허용여부를 받아온 값으로 저장한다.
    public void setPermissionsGrant(boolean granted){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("PermissionsGrant", granted);
        prefEditor.apply();
    }

    // Method : 권한허용여부 받아오기
    // Return Value : boolean(허용이면 true, 아니면 false)
    // Parameter : void
    // Use : 권한허용여부 받아오기. 기본값은 false.
    public boolean getPermissionsGrant(){
        return settingPref.getBoolean("PermissionsGrant", false);
    }

    // Method : 잠금화면기능 설정여부 변경
    // Return Value : void
    // Parameter : void
    // Use : 잠금화면기능이 사용 설정여부를 변경한다. true->false, false->true 로 변경한다.
    public void setLockerOnOff(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        if(settingPref.getBoolean("LockerOn", false)) {
            prefEditor.putBoolean("LockerOn", false);
        } else {
            prefEditor.putBoolean("LockerOn", true);
        }
        prefEditor.apply();
    }

    // Method : 잠금화면기능 설정여부 받아오기
    // Return Value : boolean(사용이면 true, 아니면 false)
    // Parameter : void
    // Use : 잠금화면기능 사용여부를 받아온다. 기본설정값은 false.
    public boolean getLockerOnOff(){
        return settingPref.getBoolean("LockerOn", false);
    }

    // Method : 마지막으로 선택된 카테고리를 저장
    // Return Value : void
    // Parameter : int (선택된 카테고리 id)
    // Use : 마지막으로 선택된 카테고리를 저장한다. category spinner 변경 이벤트 발생 시 호출
    public void setLastSelecedCategory(int categoryId){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putInt("lastSelectedCategory",categoryId);
        prefEditor.apply();
    }

    // Method : 마지막으로 선택된 카테고리를 가져온다.
    // Return Value : int (선택된 카테고리 id)
    // Parameter : void
    // Use : 마지막으로 선택된 카테고리를 저장가져온다. init 에서 호출 예정
    public int  getLastSelecedCategory(){
        return settingPref.getInt("lastSelectedCategory", 1);
    }

    // Method : 마지막으로 재생된 BGM을 저장
    // Return Value : void
    // Parameter : bgmPath(재생한 bgmPath)
    // Use : 마지막으로 재생된 파일의 경로를 저장한다. 리스트에서 선택해서 재생했을 때 호출
    public void setLastPlayedBgm(String bgmPath, boolean isInnerfile){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("lastPlayedBgmisInnerfile", isInnerfile);
        prefEditor.putString("lastPlayedBgm", bgmPath);
        prefEditor.apply();
    }

    // Method : 마지막으로 선택된 BGM을 가져온다
    // Return Value : String(재생했던 bgmPath)
    // Parameter : void
    // Use : 마지막으로 재생된 파일의 경로를 가져온다. mainActivity의 onCreate에서 호출
    //       내장파일이 아닌데도 파일이 존재하지 않으면 null로 리턴한다.
    public String getLastPlayedBgm(){
        String path = settingPref.getString("lastPlayedBgm", null);
        // 내장파일이 아니면
        if (path != null && !settingPref.getBoolean("lastPlayedBgmisInnerfile", false)) {
            File file = new File(path);
            if (file.isFile()){
                return path;
            } else {
                return null;
            }
        }
        // 내장파일이면 바로 값 리턴
        else if(settingPref.getBoolean("lastPlayedBgmisInnerfile", false)){
            return path;
        }
        // 등록된 이전 재생기록이 없으면 null 리턴
        else {
            return null;
        }
    }

    // Method : 마지막 재생내역의 내장파일 여부 가져오기
    // Return Value : boolean(내장파일이면 true 아니면 false)
    // Parameter : void
    // Use : 마지막으로 선택된 BGM이 내장파일인지 여부를 가져온다
    public boolean getLastPlayedBgmIsInnerfile(){
        return settingPref.getBoolean("lastPlayedBgmisInnerfile", false);
    }

    // Method : 반복재생 설정 저장
    // Return Value : void
    // Parameter : void
    // Use : 반복재생 여부를 토글한다.
    public void setLoopPlay(){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("loopPlay", !settingPref.getBoolean("loopPlay", false));
        prefEditor.apply();
    }

    // Method : 반복재생 여부 가져오기
    // Return Value : boolean(반복재생이면 true 아니면 false)
    // Parameter : void
    // Use : 마지막으로 설정된 반복재생 여부를 가져온다
    public boolean getLoopPlay(){
        return settingPref.getBoolean("loopPlay", false);
    }

    // Method : 알람의 on off 여부를 저장한다.
    // Return Value : void
    // Parameter : boolean (on이면 true, off 이면 false)
    // Use : 알람의 on off를 저장하여 락스크린 서비스에서 사용
    public void setAlarmOnOff(boolean isOn){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("alarmOnOff",isOn);
        prefEditor.apply();
    }

    // Method : 알람이 on인지 off인지 가져온다.
    // Return Value : boolean (on이면 true, off 이면 false)
    // Parameter : void
    // Use : 알람의 on off 여부를 받아 notification 보여줄지 말지 결정함.
    public boolean  getAlarmOnOff(){
        return settingPref.getBoolean("alarmOnOff", false);
    }

    // Method : 알람의 on off 여부를 저장한다.
    // Return Value : void
    // Parameter : boolean (on이면 true, off 이면 false)
    // Use : 알람의 on off를 저장하여 락스크린 서비스에서 사용
    public void setFirstTutorial(boolean isOn){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("firstTutorial",isOn);
        prefEditor.apply();
    }

    // Method : 알람이 on인지 off인지 가져온다.
    // Return Value : boolean (on이면 true, off 이면 false)
    // Parameter : void
    // Use : 알람의 on off 여부를 받아 notification 보여줄지 말지 결정함.
    public boolean  getFirstTutorial(){
        return settingPref.getBoolean("firstTutorial", true);
    }

    // Method : 알람이 on인지 off인지 저장한다.
    // Return Value : boolean (on이면 true, off 이면 false)
    // Parameter : void
    // Use : 알람의 on off를 저장하여 메인액티비티 재생에서 사용
    public void setScreenOffPlayOnOff(boolean isOn){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("screenOffPlay",isOn);
        prefEditor.apply();
    }

    // Method : 알람이 on인지 off인지 가져온다.
    // Return Value : boolean (on이면 true, off 이면 false)
    // Parameter : void
    // Use : 알람의 on off 여부를 받아 화면 꺼졌을 시에 재생할지 말지 결정함.
    public boolean getScreenOffPlayOnOff(){
        return settingPref.getBoolean("screenOffPlay", true);
    }

    // Method : 알람이 on인지 off인지 저장한다.
    // Return Value : boolean (on이면 true, off 이면 false)
    // Parameter : void
    // Use : 알람의 on off를 저장하여 메인액티비티 재생 다른 작업 수행 시에 사용
    public void setDifferentTaskPlayOnOff(boolean isOn){
        SharedPreferences.Editor prefEditor = settingPref.edit();
        prefEditor.putBoolean("differentTaskPlay",isOn);
        prefEditor.apply();
    }

    // Method : 알람이 on인지 off인지 가져온다.
    // Return Value : boolean (on이면 true, off 이면 false)
    // Parameter : void
    // Use : 알람의 on off 여부를 받아 다른 작업 수행 시(다른 앱 사용, 인텐트 전환)에 재생할지 말지 결정함.
    public boolean getDifferentTaskPlayOnOff(){
        return settingPref.getBoolean("differentTaskPlay", true);
    }
}
