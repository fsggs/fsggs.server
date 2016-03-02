package com.fsggs.server.core.network;

import io.netty.handler.codec.http.Cookie;

import java.util.*;

abstract public class BaseController {
    protected final String EOL = "\r\n";
    protected final String BR = "<br />";

    protected String URI;

    protected Map<String, String> params = new HashMap<>();
    protected Map<String, String> data = new HashMap<>();
    protected Set<Cookie> cookies;

    public BaseController setURI(String URI) {
        this.URI = URI;
        return this;
    }

    public BaseController setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public BaseController setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public BaseController setData(Map<String, String> data) {
        this.data = data;
        return this;
    }
}
