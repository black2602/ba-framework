package com.blackangel.baframework.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpCookie;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public class SerializableHttpCookie implements Serializable {
    private static final long serialVersionUID = 6374381323722046732L;

    private transient final HttpCookie cookie;
    private transient HttpCookie clientCookie;

    public SerializableHttpCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    public HttpCookie getCookie() {
        HttpCookie bestCookie = cookie;
        if (clientCookie != null) {
            bestCookie = clientCookie;
        }
        return bestCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getCommentURL());
        out.writeObject(cookie.getDomain());
        out.writeLong(cookie.getMaxAge());
        out.writeObject(cookie.getPath());
        out.writeObject(cookie.getPortlist());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.getSecure());
        out.writeBoolean(cookie.getDiscard());
    }


}