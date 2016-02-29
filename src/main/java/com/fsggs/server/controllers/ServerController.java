package com.fsggs.server.controllers;

import com.fsggs.server.Application;
import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.network.Controller;
import com.fsggs.server.core.network.Route;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

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
}
