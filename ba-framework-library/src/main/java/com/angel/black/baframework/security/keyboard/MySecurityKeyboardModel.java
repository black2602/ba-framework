package com.angel.black.baframework.security.keyboard;

import android.content.Context;
import android.graphics.Rect;

/**
 * Created by Finger-kjh on 2017-08-10.
 */

public class MySecurityKeyboardModel {
    private int printKey;
    private String encryptKey;
    private String encryptValue;
    private Rect area;
    private static NativeSecurityKeyboard sNativeSecurityKeyboard;

    static {
        if(sNativeSecurityKeyboard == null) {
            sNativeSecurityKeyboard = new NativeSecurityKeyboard();
        }
    }

    public MySecurityKeyboardModel(int printKey) {
        this(printKey, new Rect());
    }

    public MySecurityKeyboardModel(int printKey, Rect area) {
        this.encryptKey = sNativeSecurityKeyboard.newEncryptKey();
        this.encryptValue = sNativeSecurityKeyboard.encryptSecurityKbKey(encryptKey, String.valueOf(printKey));
        this.printKey = -1;
        this.area = area;
    }

    public Rect getArea() {
        return area;
    }

    public int getPrintKey() {
        return printKey;
    }

    public String getDecryptedValue() {
//        try {
//            return new AES256Util(encryptKey).decrypt(encryptValue);
//        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//            throw new SecurityException("decrypt not working");
//        }

        return sNativeSecurityKeyboard.decryptSecurityKbKey(encryptKey, encryptValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printKey).append(", ")
                .append(encryptKey).append(", ")
                .append(encryptValue).append(", ")
                .append(area.toShortString());

        return sb.toString();
    }

    public String getEncryptedValue() {
        return encryptValue;
    }


    public static String getTwoDepthEncryptedValue(Context context, String[] encKeyArr, String[] encValueArr) {
        return NativeSecurityKeyboard.getTwoDepthEncryptedValue(context, encKeyArr, encValueArr);
    }

    public String getEncryptKey() {
        return encryptKey;
    }
}
