package com.fsggs.server.core.network;

import com.fsggs.server.server.HttpServerHandler;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.*;

import static io.netty.handler.codec.http.HttpVersion.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

abstract public class BaseController {
    protected final String EOL = "\r\n";
    protected final String BR = "<br />";

    private Map<AsciiString, String> headers = new HashMap<>();
    protected Map<String, String> params = new HashMap<>();
    protected Map<String, String> data = new HashMap<>();
    protected Set<Cookie> cookies;

    private Method action;

    private HttpVersion httpVersion = HTTP_1_1;
    private HttpResponseStatus httpResponseStatus = OK;
    private String httpContentType = null;

    private ChannelHandlerContext context;
    private HttpRequest request;
    private String URI;

    public BaseController setContext(ChannelHandlerContext context) {
        this.context = context;
        return this;
    }

    public BaseController setURI(String URI) {
        this.URI = URI;
        return this;
    }

    protected String getURI() {
        return URI;
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

    public HttpRequest getRequest() {
        return request;
    }

    public BaseController setRequest(HttpRequest request) {
        this.request = request;
        return this;
    }

    public BaseController setAction(Method action) {
        this.action = action;
        return this;
    }

    public Method getAction() {
        return action;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public BaseController setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }

    public BaseController setHttpResponseStatus(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
        return this;
    }

    public String getHttpContentType() {
        return httpContentType;
    }

    public BaseController setHttpContentType(String httpContentType) {
        this.httpContentType = httpContentType;
        return this;
    }

    public Map<AsciiString, String> getHeaders() {
        return headers;
    }

    public BaseController setHeaders(Map<AsciiString, String> headers) {
        this.headers = headers;
        return this;
    }

    protected BaseController header(AsciiString key, String value) {
        headers.put(key, value);
        return this;
    }

    protected String render(String template) {
        return render(template, new HashMap<String, Object>());
    }

    protected String render(String template, Map<String, Object> data) {
        PebbleEngine engine = new PebbleEngine.Builder().build();
        String result = "";

        try {
            PebbleTemplate cTemplate = engine.getTemplate(template + ".peb");

            Writer writer = new StringWriter();
            cTemplate.evaluate(writer, data);
            result = writer.toString();
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected String redirect(String url) {
        HttpServerHandler.redirect(context, url);
        return url;
    }
}
