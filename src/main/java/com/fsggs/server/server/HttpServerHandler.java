package com.fsggs.server.server;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.session.SessionManager;
import com.fsggs.server.utils.FileUtils;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpServerHandler {
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    private static final int HTTP_CACHE_SECONDS = 60;

    private HttpPostRequestDecoder decoder;
    private SocketServerHandler handler;

    HttpServerHandler(SocketServerHandler handler, ChannelHandlerContext context, HttpRequest msg) {
        this.handler = handler;
        handlerHttpRequest(context, msg);
    }

    private void handlerHttpRequest(ChannelHandlerContext context, HttpRequest request) {
        // Handle a bad request.
        if (!request.decoderResult().isSuccess()) {
            //Application.logger.error(request.decoderResult().toString());
            sendErrorPage(BAD_REQUEST, context, request);
            return;
        }

        String uri = request.uri();
        if (uri == null) {
            sendErrorPage(FORBIDDEN, context, request);
            return;
        }

        String uriPath = uri.split("\\?")[0].replaceAll("([/])\\1+", "$1");

        // Handshake
        if (request.method() == GET && "/".equals(uriPath)
                && request.headers().contains("Upgrade")
                && Objects.equals(request.headers().get("Upgrade").toLowerCase(), "websocket")) {

            tryHandshake(context, request);
            return;
        }

        // Controller
        if (Application.registry.hasController(uriPath, request.method())) {
            Map<String, List<String>> parameters = new QueryStringDecoder(request.uri()).parameters();
            Map<String, String> params = new HashMap<>();
            if (parameters.size() > 0) {
                for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                    String key = entry.getKey();
                    List<String> attribute = entry.getValue();
                    for (String value : attribute) {
                        params.put(key, value);
                    }
                }
            }

            CharSequence value = request.headers().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = (Objects.equals(value, null))
                    ? new TreeSet<>()
                    : ServerCookieDecoder.STRICT.decode(value.toString());

            Map<String, String> postData = new HashMap<>();
            Map<String, FileUpload> filesData = new HashMap<>();

            if (request.method() == HEAD || request.method() == POST || request.method() == PUT
                    || request.method() == PATCH || request.method() == DELETE) {
                decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);

                List<InterfaceHttpData> data = decoder.getBodyHttpDatas();

                for (InterfaceHttpData attributeData : data) {
                    if (attributeData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) attributeData;
                        try {
                            postData.put(attributeData.getName(), attribute.getValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (attributeData.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                        FileUpload fileUpload = (FileUpload) attributeData;
                        filesData.put(fileUpload.getName(), fileUpload);
                    }
                }
            }

            //TODO:: Framework registry, auth
            try {
                BaseController controller = Application.registry.getController(
                        uriPath,
                        request.method()
                );

                controller
                        .setContext(context)
                        .setRequest(request)
                        .setURI(uriPath)
                        .setCookies(cookies)
                        .setParams(params)
                        .setData(postData)
                        .setFiles(filesData);

                String result = (String) controller.getAction().invoke(controller);

                ByteBuf buffer = Unpooled.copiedBuffer(result.getBytes());

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);

                response.headers().set(CONTENT_TYPE, controller.getHttpContentType() != null
                        ? controller.getHttpContentType()
                        : Application.registry.getRouteContentType(uriPath, request.method()));

                for (Map.Entry<AsciiString, String> header : controller.getHeaders().entrySet()) {
                    response.headers().set(header.getKey(), header.getValue());
                }

                response.setProtocolVersion(controller.getHttpVersion());
                response.setStatus(controller.getHttpResponseStatus());

                HttpUtil.setContentLength(response, buffer.readableBytes());

                sendHttpResponse(context, request, response);
                resetDecoder();
                return;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        resetDecoder();

        // Allow only GET methods.
        if (request.method() != GET) {
            sendErrorPage(FORBIDDEN, context, request);
            return;
        }

        //Static files
        HttpStaticHandler staticHandler = new HttpStaticHandler(context, request, uri);
        if (staticHandler.isMatch()) return;

        sendErrorPage(NOT_FOUND, context, request);
    }

    private void resetDecoder() {
        if (decoder != null) {
            decoder.cleanFiles();
            decoder.destroy();
        }
    }

    /**
     * Send http response
     */
    static private void sendHttpResponse(ChannelHandlerContext context, HttpRequest req, FullHttpResponse res) {
        // Send the response and close the connection if necessary.
        ChannelFuture f = context.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Try websocket handshake
     */
    private void tryHandshake(ChannelHandlerContext context, HttpRequest request) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(request), null, true);
        handler.handshaker = wsFactory.newHandshaker(request);
        if (handler.handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        } else {
            Channel channel = context.channel();
            SocketServerHandler.channels.add(channel);
            SessionManager.add(channel);
            handler.handshaker.handshake(channel, request);
        }
    }

    /**
     * Return websocket path
     */
    static private String getWebSocketLocation(HttpRequest req) {
        String location = req.headers().get(HOST) + Application.WEBSOCKET_PATH;
        if (Application.SSL) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

    /**
     * Send page
     */
    static private void sendPage(File file, ChannelHandlerContext context, HttpRequest request) throws IOException {
        ByteBuf buffer = Unpooled.copiedBuffer(Files.readAllBytes(file.toPath()));
        sendPage(buffer, context, request);
    }

    static private void sendPage(String response, ChannelHandlerContext context, HttpRequest request, String type)
            throws IOException {
        ByteBuf buffer = Unpooled.copiedBuffer(response.getBytes());
        sendPage(buffer, context, request, type);
    }

    static private void sendPage(ByteBuf buffer, ChannelHandlerContext context, HttpRequest request) {
        sendPage(buffer, context, request, "text/html; charset=UTF-8");
    }

    static private void sendPage(ByteBuf buffer, ChannelHandlerContext context, HttpRequest request, String type) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);

        response.headers().set(CONTENT_TYPE, type);
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, Application.CLIENT_URL);
        HttpUtil.setContentLength(response, buffer.readableBytes());

        sendHttpResponse(context, request, response);
    }

    /**
     * Send parsed page
     */
    static private void sendParsedPage(String path, Map<String, Object> data, ChannelHandlerContext context, HttpRequest request) {
        data.put("serverTitle", Application.APPLICATION_NAME);
        data.put("serverVersion", Application.APPLICATION_VERSION);

        ByteBuf content;
        try {
            content = parsePage(path, data);
        } catch (FileNotFoundException e) {
            sendErrorPage(NOT_FOUND, context, request);
            return;
        }

        sendPage(content, context, request);
    }

    static private void sendParsedPage(File file, Map<String, Object> data, ChannelHandlerContext context, HttpRequest request) {
        sendParsedPage(file.toPath().toAbsolutePath().toString(), data, context, request);
    }

    /**
     * Send error page
     */
    static void sendErrorPage(HttpResponseStatus status, ChannelHandlerContext context, HttpRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("errorCode", status.code());
        data.put("errorMessage", status.reasonPhrase());

        ByteBuf content;
        try {
            content = parsePage("system/error.peb", data);
        } catch (FileNotFoundException e) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
            sendHttpResponse(context, request, response);
            return;
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, content);

        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, Application.CLIENT_URL);
        HttpUtil.setContentLength(response, content.readableBytes());

        sendHttpResponse(context, request, response);
    }

    /**
     * Send redirect
     */
    static public void redirect(ChannelHandlerContext context, String uri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, uri);

        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    static void sendFile(Path path, String fileName, ChannelHandlerContext context, HttpRequest request) throws IOException {
        File file = path.toFile();
        sendFile(file, fileName, context, request);
    }

    static void sendFile(File file, String fileName, ChannelHandlerContext context, HttpRequest request) throws IOException {
        RandomAccessFile raf;
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String fileMimeType = mimeTypesMap.getContentType(file.getPath());
        String fileExtension = FileUtils.getFileExtension(file);

        if (Objects.equals(fileExtension, "tmp")) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        if (Objects.equals(fileMimeType, "text/html") || Objects.equals(fileExtension, "html")) {
            sendPage(file, context, request);
            return;
        }

        // Add Pebble parser
        if (Objects.equals(fileExtension, "peb")) {
            // TODO:: HashMap to static Mapper
            sendParsedPage(file, new HashMap<>(), context, request);
            return;
        }

        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException ignore) {
            sendErrorPage(NOT_FOUND, context, request);
            return;
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);

        response.headers().set(CONTENT_TYPE, fileMimeType);

        setDateAndCacheHeaders(response, file);
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        context.write(response);

        // Write the content.
        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;
        if (context.pipeline().get(SslHandler.class) == null) {
            sendFileFuture = context.write(
                    new DefaultFileRegion(raf.getChannel(), 0, fileLength),
                    context.newProgressivePromise()
            );
            lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            sendFileFuture = context.write(
                    new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
                    context.newProgressivePromise()
            );
            lastContentFuture = sendFileFuture;
        }

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) {
                    Application.logger.info(future.channel() + " Transfer progress: " + progress);
                } else {
                    Application.logger.info(future.channel() + " Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) {
                //Application.logger.info(future.channel() + " Transfer complete.");
            }
        });

        if (!HttpUtil.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Parse pebble pages
     */
    private static ByteBuf parsePage(String template, Map<String, Object> data) throws FileNotFoundException {
        PebbleEngine engine = new PebbleEngine.Builder().build();
        try {
            PebbleTemplate cTemplate = engine.getTemplate(template);

            Writer writer = new StringWriter();

            try {
                cTemplate.evaluate(writer, data);
                return Unpooled.copiedBuffer(writer.toString(), CharsetUtil.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (PebbleException e) {
            e.printStackTrace();
        }

        throw new FileNotFoundException("Template file: " + template + " not found or corrupted.");
    }

    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }
}
