package com.fsggs.server.controllers;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.network.Controller;
import com.fsggs.server.core.network.Route;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.util.AsciiString;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ServerController extends BaseController {
    @Route(PATH = "/")
    public String indexPage() {
        Map<String, Object> data = new HashMap<>();

        data.put("serverTitle", Application.APPLICATION_NAME);
        data.put("serverVersion", Application.APPLICATION_VERSION);

        header(new AsciiString("Server".getBytes()),
                Application.APPLICATION_NAME + " (v." + Application.APPLICATION_VERSION + ")");

        return render("public/index", data);
    }

    @Route(PATH = "/test", METHOD = "POST")
    public String testPage() {
        String result = getURI() + BR;

        result += "Params: " + BR;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            result += key + ":" + value + BR;
        }

        result += BR + "POST:" + BR;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            result += key + ":" + value + BR;
        }

        result += BR + "COOKIE:" + BR;
        for (Cookie cookie : cookies) {
            result += cookie.toString() + BR;
        }

        return result + BR;
    }

    @Route(PATH = "/upload", METHOD = "POST")
    public String testUpload() {
        String result = getURI() + BR;
        result += BR + "FILES:" + BR;
        for (Map.Entry<String, FileUpload> entry : files.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            result += key + ":" + value + BR;
        }

        if (files.containsKey("file")) {
//            try {
//                FileUpload fileUpload = files.get("file");
//                File file = new File("E:\\test\\" + fileUpload.getFilename());
//                if (!file.exists()) {
//                    //noinspection ResultOfMethodCallIgnored
//                    file.createNewFile();
//                }
//                fileUpload("file", file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        return result;
    }
}
