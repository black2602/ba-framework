package com.blackangel.baframework.sns;

/**
 * Created by Finger-kjh on 2017-11-28.
 */

public class SnsLoginResult {

    private String _id;
    private String userId;
    private String userName;

    public SnsLoginResult(String _id, String userId, String userName) {
        this._id = _id;
        this.userId = userId;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "SnsLoginResult{" +
                "_id='" + _id + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
