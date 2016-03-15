package com.fsggs.server.configs;

import com.fsggs.server.Application;

public class InitLogoServer {
    public InitLogoServer() {
        String v = Application.APPLICATION_VERSION_ID;

        System.out.println("Start " + Application.APPLICATION_NAME + " (v." + Application.APPLICATION_VERSION + ")");
        System.out.println("#################################### FSGGS ####################################");
        System.out.println("#                            Server Update 0 (" + v + ")                          #");
        System.out.println("#                                                                             #");
        System.out.println("#                      Author Dimitriy Kalugin @ DeVinterX                    #");
        System.out.println("#                      Copyright 2009-2016. License BSD-3.                    #");
        System.out.println("#################################### FSGGS ####################################");
        Application.logger.log(Application.FSGGS, "Server started");
    }
}
