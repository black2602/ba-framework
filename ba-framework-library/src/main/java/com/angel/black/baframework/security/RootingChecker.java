package com.angel.black.baframework.security;

import android.os.Environment;

import com.angel.black.baframework.logger.BaLog;

import java.io.File;

/**
 * Created by Finger-kjh on 2017-08-09.
 */

public class RootingChecker {

    /** 체크해야할 파일, 디렉토리 목록
     * /system/bin/su
     /system/xbin/su
     /system/app/superuser.apk
     /data/data/com.noshufou.android.su
     */
    public static final String ROOT_PATH = Environment. getExternalStorageDirectory() + "";

    public static final String ROOTING_PATH_1 = "/system/bin/su";
    public static final String ROOTING_PATH_2 = "/system/xbin/su";
    public static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    public static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";

    public String[] RootFilesPath = new String[]{
            ROOT_PATH + ROOTING_PATH_1 ,
            ROOT_PATH + ROOTING_PATH_2 ,
            ROOT_PATH + ROOTING_PATH_3 ,
            ROOT_PATH + ROOTING_PATH_4 };

    public boolean isRooting() {
        boolean isRootingFlag = false;
        try {
            Runtime.getRuntime().exec("su");
            isRootingFlag = true;
        } catch ( Exception e) {
            // Exception 나면 루팅 false;
            isRootingFlag = false;
        }

        if(!isRootingFlag){
            isRootingFlag = checkRootingFiles(createFiles(RootFilesPath));
        }

        BaLog.d("isRootingFlag = " + isRootingFlag);

        return isRootingFlag;
    }

    /** * 루팅파일 의심 Path를 가진 파일들을 생성 한다. */
    private File[] createFiles(String[] sfiles) {
        File[] rootingFiles = new File[sfiles.length];
        for(int i = 0; i < sfiles.length; i++){
            rootingFiles[i] = new File(sfiles[i]);
        }

        return rootingFiles;
    }

    /** * 루팅파일 여부를 확인 한다. */
    private boolean checkRootingFiles(File... file){
        boolean result = false;
        for(File f : file){
            if(f != null && f.exists() && f.isFile()){
                result = true;
                break;
            }else{
                result = false;
            }
        }
        return result;
    }
}
