package com.blackangel.baframework.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Finger-kjh on 2017-08-16.
 */

public class DigestUtil {
    /**
     * 문자열을 MD-5 방식으로 암호화
     * @param txt 암호화 하려하는 문자열
     * @return String
     * @throws Exception
     */
    public String getEncMD5(String txt) throws Exception {

        StringBuffer sbuf = new StringBuffer();

        MessageDigest mDigest = MessageDigest.getInstance("MD5");
        mDigest.update(txt.getBytes());

        byte[] msgStr = mDigest.digest() ;

        for(int i=0; i < msgStr.length; i++){
            String tmpEncTxt = Integer.toHexString((int)msgStr[i] & 0x00ff) ;
            sbuf.append(tmpEncTxt) ;
        }

        return sbuf.toString() ;
    }

    public String getEncSHA256(String str){
        String SHA = "";
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();

            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            SHA = sb.toString();
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();

            SHA = null;
        }

        return SHA;
    }
}
