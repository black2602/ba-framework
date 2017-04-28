package com.angel.black.baframework.util;

/**
 * Created by Finger-kjh on 2017-04-26.
 */

public class IntegerUtil {
    public static int getInt(String str, int defValue) {
        int value = defValue;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return value;
    }
}
