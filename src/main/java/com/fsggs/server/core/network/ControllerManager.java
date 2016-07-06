package com.fsggs.server.core.network;

import com.fsggs.server.Application;
import com.fsggs.server.core.FrameworkRegistry;
import io.netty.handler.codec.http.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ControllerManager {

    private Map<String, Map<String, FrameworkRegistry.FrameworkRoute>> cacheRoutes = new HashMap<>();

    public ControllerManager() {
        String[] methods = {"*", "HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE", "TRACE", "CONNECT"};
        for (String method : methods) {
            Map<String, FrameworkRegistry.FrameworkRoute> routes = Application.registry.getRoutes(method);
            if (routes != null) cacheRoutes.put(method, routes);
        }
    }

    public boolean hasController(String path, HttpMethod httpMethod) {
        String method = httpMethod.toString().toUpperCase();
        return cacheRoutes.containsKey(method) && cacheRoutes.get(method).containsKey(path);
    }

    public BaseController getController(String path, HttpMethod httpMethod)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!hasController(path, httpMethod)) return null;
        FrameworkRegistry.FrameworkRoute route = cacheRoutes.get(httpMethod.toString().toUpperCase()).get(path);
        if (route != null) return getController(route);
        return null;
    }

    private BaseController getController(FrameworkRegistry.FrameworkRoute route)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        BaseController controller = (BaseController) (route.getClassController())
                .getConstructor()
                .newInstance();

        controller.setAction(route.getClassMethod());
        return controller;
    }

    public String runController(BaseController controller)
            throws InvocationTargetException, IllegalAccessException {
        return (String) controller.getAction().invoke(controller);
    }

    public String getContentType(String path, HttpMethod httpMethod) {
        if (!hasController(path, httpMethod)) return "text/html; charset=UTF-8";
        FrameworkRegistry.FrameworkRoute route = cacheRoutes.get(httpMethod.toString().toUpperCase()).get(path);
        if (route != null) return route.getResponseType();
        return "text/html; charset=UTF-8";
    }
}
