package com.fsggs.server.controllers;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.network.Controller;
import com.fsggs.server.core.network.Route;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.netty.handler.codec.http.Cookie;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ServerController extends BaseController {

    @Route(PATH = "/")
    public String indexPage() {
        Map<String, Object> data = new HashMap<>();
        PebbleEngine engine = new PebbleEngine.Builder().build();

        data.put("serverTitle", Application.APPLICATION_NAME);
        data.put("serverVersion", Application.APPLICATION_VERSION);

        try {
            PebbleTemplate cTemplate = engine.getTemplate("public/index.peb");

            Writer writer = new StringWriter();
            cTemplate.evaluate(writer, data);
            return writer.toString();
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Route(PATH = "/test", METHOD = "POST")
    public String testPage() {
        String result = URI + BR;

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
}
