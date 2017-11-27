package com.angel.black.baframework.security.keyboard;

import android.content.Context;

/**
 * Created by Finger-kjh on 2017-08-17.
 */

public class NativeSecurityKeyboard {
    static {
        System.loadLibrary("native-skb");
    }

    public native String encryptSecurityKbKey(String encryptKey, String value);

    public native String decryptSecurityKbKey(String encryptKey, String value);

    public native String newEncryptKey();

    public native static String getTwoDepthEncryptedValue(Context context, String[] encKeyArr, String[] encValueArr);

}
