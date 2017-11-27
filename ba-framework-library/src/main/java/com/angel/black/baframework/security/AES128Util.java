package com.angel.black.baframework.security;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES128Util {
	
    public static SecretKeySpec setKey(String key)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
 /*
        byte[] key1 = null;
        byte[] keyDigest;
        MessageDigest sha = null;
         
        // Set Key
        key1 = key.getBytes("UTF-8");
        //System.out.println("setKey.key1.length = " + key1.length );
        // SHA-256
        sha = MessageDigest.getInstance("SHA-256");
        keyDigest = sha.digest(key1);
        
        SecretKeySpec keySpec = new SecretKeySpec(keyDigest,"AES") ;
        
      */  
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(key.getBytes());
           
        SecretKeySpec keySpec = new SecretKeySpec(md5.digest(), "AES");
        
        return keySpec;
    }

	
	public static String encrypt(String key, String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			final SecretKeySpec secretKey = setKey(key);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			final String encryptedString = Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes()), 0);
			return encryptedString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(String key, String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			final SecretKeySpec secretKey = setKey(key);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			final String decryptedString = new String(cipher.doFinal(Base64.decode(strToDecrypt, 0)));
			return decryptedString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getEncryptedString(String key, String str) {
		final String strToEncrypt = str;
		final String encryptedStr = AES128Util.encrypt(key, strToEncrypt.trim());
		return encryptedStr;
	}
	
	public static String getDecriyptedString(String key, String str) {
		final String strToDecrypt = str;
		final String decryptedStr = AES128Util.decrypt(key, strToDecrypt.trim());
		return decryptedStr;
	}
}
