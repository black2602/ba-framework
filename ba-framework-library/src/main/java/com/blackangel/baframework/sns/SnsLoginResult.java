package com.blackangel.baframework.sns;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public class SnsLoginResult {

    private String id;          // sns api에서 돌려준 유저 id
    private String userKey;     // email or 유저를 식별할 키
    private String userName;    // 유저 이름 or 닉네임

    public SnsLoginResult(String id, String userKey, String userName) {
        this.id = id;
        this.userKey = userKey;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "SnsLoginResult{" +
                "id='" + id + '\'' +
                ", userKey='" + userKey + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
