package com.fsggs.server.controllers;

import com.fsggs.server.core.network.BaseController;
import com.fsggs.server.core.network.Controller;
import com.fsggs.server.core.network.Route;

@Controller
public class MasterController extends BaseController {

    @Route(PATH = "/getVersion.json", METHOD = "GET")
    public String getVersion() {
        return "";
    }
}
