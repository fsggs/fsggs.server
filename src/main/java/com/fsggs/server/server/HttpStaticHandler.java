package com.fsggs.server.server;

import com.fsggs.server.Application;
import com.fsggs.server.utils.FileUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

//TODO
class HttpStaticHandler {
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private boolean status = false;

    HttpStaticHandler(ChannelHandlerContext context, HttpRequest request, String uri) {
        final String separator = FileSystems.getDefault().getSeparator();
        final String rootPath = FileUtils.getApplicationPath() + separator + Application.PUBLIC_DIR;

        String uriPath = uri.split("\\?")[0].replaceAll("([/])\\1+", "$1");

        Path path = Paths.get(rootPath + sanitizeUri(uriPath));
        if (!Files.exists(path)) {
            try {
                URL resourceURL = ClassLoader.getSystemResource(Application.PUBLIC_DIR + sanitizeUri(uriPath));
                if (resourceURL == null) {
                    HttpServerHandler.sendErrorPage(NOT_FOUND, context, request);
                    Application.logger.error("Invalid URL [" + request.method() + "]: " + uri);
                    status = true;
                    return;
                }

                if (FileUtils.isRunnedInJar()) {
                    File file;
                    InputStream input = ClassLoader.getSystemResourceAsStream(Application.PUBLIC_DIR + sanitizeUri(uriPath));

                    String fileName = resourceURL.toString().substring(resourceURL.toString().lastIndexOf('/') + 1, resourceURL.toString().length());

                    file = File.createTempFile(fileName + '.', ".tmp");

                    OutputStream out = new FileOutputStream(file);
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = input.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }

                    HttpServerHandler.sendFile(file, fileName, context, request);

                    file.deleteOnExit();
                } else {
                    path = Paths.get(resourceURL.toURI());
                    if (!Files.exists(path)) throw new IOException("Invalid URL [" + request.method() + "]: " + uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
                HttpServerHandler.sendErrorPage(NOT_FOUND, context, request);
                Application.logger.error("Invalid URL [" + request.method() + "]: " + uri);
                status = true;
                return;
            }
        }

        try {
            if (Files.isHidden(path)) {
                HttpServerHandler.sendErrorPage(NOT_FOUND, context, request);
                status = true;
                return;
            }

            if (Files.isDirectory(path)) {
                if (uriPath.endsWith("/")) {
                    sendListingPage(context, path);
                } else {
                    HttpServerHandler.redirect(context, uriPath + '/');
                }
                sendListingPage(context, path);
                status = true;
                return;
            }

            if (Files.isRegularFile(path)) {
                HttpServerHandler.sendFile(path, path.getFileName().toString(), context, request);
                status = true;
            }
        } catch (IOException e) {
            HttpServerHandler.sendErrorPage(INTERNAL_SERVER_ERROR, context, request);
            status = true;
        }
    }

    boolean isMatch() {
        return status;
    }

    static void sendListingPage(ChannelHandlerContext context, Path path) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        StringBuilder buf = new StringBuilder()
                .append("<!DOCTYPE html>\r\n")
                .append("<html><head><title>")
                .append("Listing of: ")
                .append(path.toAbsolutePath().toString())
                .append("</title></head><body>\r\n")

                .append("<h3>Listing of: ")
                .append(path.toAbsolutePath().toString())
                .append("</h3>\r\n")

                .append("<ul>")
                .append("<li><a href=\"../\">..</a></li>\r\n");

        try {
            for (Path p : FileUtils.fileList(path)) {
                if (Files.isHidden(p) || !Files.isReadable(p)) {
                    continue;
                }

                String name = p.getFileName().toString();
                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                    continue;
                }

                buf.append("<li><a href=\"")
                        .append(name)
                        .append("\">")
                        .append(name)
                        .append("</a></li>\r\n");
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();

        // Close the connection as soon as the error message is sent.
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        return uri;
    }
}
