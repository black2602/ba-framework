package com.blackangel.baframework.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Base64;

import com.blackangel.baframework.logger.MyLog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Finger-kjh on 2017-07-18.
 */

public class DebugUtil {
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void printLine() {
        MyLog.i("==================================================================");
    }

    public String printTakenTime(long time1, long time2) {
        MyLog.i("startTime = " + getTimeString(time1));
        MyLog.i("endTime = " + getTimeString(time2));

        long interval = time2 - time1;

        int sec = (int) (interval / 1000);

        return getTakenTimeString(sec);
    }

    private String getTakenTimeString(int sec) {
        if( sec < 60 ) {
            return sec + "초";
        } else {

            int min = sec / 60;
            int remainSec = sec % 60;

            return min + "분 " + remainSec + "초";
        }
    }

    private String getTimeString(long timeMillis) {
        return mSimpleDateFormat.format(new Date(timeMillis));
    }


    public static void debugString(String s) {
        printLine();

        for(int i=0; i < s.length(); i++) {
            char c = s.charAt(i);

            MyLog.d("String.valueOf = " + String.valueOf(c));
            MyLog.d("Character.toString = " + Character.toString(c));
            MyLog.d("Character.getNumericValue = " + Character.getNumericValue(c));
        }

        printLine();
    }

    public static void debugHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                MyLog.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void debugMap(Map map) {
        Set set = map.keySet();

        Iterator iterator = set.iterator();

        printLine();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            MyLog.d(key + " = " + value);
        }
        printLine();
    }

    public static void debugList(List list) {
        printLine();

        if (list == null) {
            MyLog.d("list is null");

        } else {
            MyLog.i("list = " + list);

            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                if(obj != null) {
                    MyLog.d("[" + i + "] = " + obj.toString());
                }
            }
        }
        printLine();
    }

    public static void printLine(String tag) {
        MyLog.i("========================" + tag + " ===========================");
    }

    public static void debugFragment(Fragment fragment) {
        printLine();
        String fragName = fragment.getClass().getSimpleName();
        MyLog.i(fragName, "isAdded = " + fragment.isAdded());
        MyLog.i(fragName, "isInLayout = " + fragment.isInLayout());
        MyLog.i(fragName, "isHidden = " + fragment.isHidden());
        MyLog.i(fragName, "isVisible = " + fragment.isVisible());
        MyLog.i(fragName, "isDetached = " + fragment.isDetached());
        MyLog.i(fragName, "isRemoving = " + fragment.isRemoving());
        MyLog.i(fragName, "isResumed = " + fragment.isResumed());
        printLine();
    }

    /**
     * 앱이 디버그 가능한 상태인지 여부를 조사한다. (안티 디버깅에 사용)
     *
     * @param context
     * @return
     */
    public static boolean isDebuggable(Context context) {
        return ((context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    /**
     * 디버거를 감지한다. (안티 디버깅에 사용)
     *
     * @return
     */
    public static boolean detectDebugger() {
        return Debug.isDebuggerConnected();
    }

    /**
     * 스레드가 코드를 소비하는데 걸린 시간을 체크한다.
     * @return
     */
    public static boolean detect_threadCpuTimeNanos() {
        long start = Debug.threadCpuTimeNanos();

        for (int i = 0; i < 1000000; ++i)
            continue;

        long stop = Debug.threadCpuTimeNanos();

        if (stop - start < 10000000) {
            return false;
        } else {
            return true;
        }
    }

    private static String tracerpid = "TracerPid";
    /**
     * 추적하고 있는 native debugger 가 있는지를 탐지한다.
     * @return
     * @throws IOException
     */
    public static boolean hasTracerPid() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/self/status")), 1000);
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.length() > tracerpid.length()) {
                    if (line.substring(0, tracerpid.length()).equalsIgnoreCase(tracerpid)) {
                        if (Integer.decode(line.substring(tracerpid.length() + 1).trim()) > 0) {
                            return true;
                        }
                        break;
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            reader.close();
        }
        return false;
    }

    public static void debugBundle(Bundle bundle) {
        Set<String> keySet = bundle.keySet();

        printLine("debugBundle");
        for (String key : keySet) {
            Object value = bundle.get(key);
            MyLog.d(key + " = " + value);
        }
        printLine("debugBundle");
    }
}
