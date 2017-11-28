package com.blackangel.baframework.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Created by Finger-kjh on 2017-06-13.
 */

public interface ICrypto {

    String encrypt(String key, String plainText) throws UnsupportedEncodingException, GeneralSecurityException;

    String decrypt(String key, String encrypedText) throws UnsupportedEncodingException, GeneralSecurityException;

}
