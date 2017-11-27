package com.angel.black.baframework.util;

import com.angel.black.baframework.logger.BaLog;

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
    public static void debugMap(Map map) {
        Set set = map.keySet();

        Iterator iterator = set.iterator();

        printLine();
        while(iterator.hasNext()) {
            Object key = iterator.next();
            Object value = map.get(key);
            BaLog.d(key + " = " + value);
        }
        printLine();
    }

    public static void printLine() {
        BaLog.i("==================================================================");
    }

    public String printTakenTime(long time1, long time2) {
        BaLog.i("startTime = " + getTimeString(time1));
        BaLog.i("endTime = " + getTimeString(time2));

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

    public static void debugList(List list) {
        printLine();
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            BaLog.d("[" + i + "] = " + obj.toString());
        }
        printLine();
    }
}
