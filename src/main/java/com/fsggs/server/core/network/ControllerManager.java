package com.fsggs.server.core.network;

import io.netty.handler.codec.http.*;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpMethod.*;
import static org.reflections.ReflectionUtils.*;
import static org.reflections.ReflectionUtils.withAnnotation;

public class ControllerManager {
    private Map<String, RouteMethod> headRoutes = new HashMap<>();
    private Map<String, RouteMethod> optionsRoutes = new HashMap<>();

    private Map<String, RouteMethod> getRoutes = new HashMap<>();
    private Map<String, RouteMethod> postRoutes = new HashMap<>();
    private Map<String, RouteMethod> patchRoutes = new HashMap<>();
    private Map<String, RouteMethod> putRoutes = new HashMap<>();
    private Map<String, RouteMethod> deleteRoutes = new HashMap<>();
    private Map<String, RouteMethod> traceRoutes = new HashMap<>();
    private Map<String, RouteMethod> connectRoutes = new HashMap<>();

    private Map<String, RouteMethod> routes = new HashMap<>();

    private Map<String, String> contentTypes = new HashMap<>();

    public ControllerManager() {
        Reflections reflections = new Reflections("com.fsggs.server.controllers");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> controllerClass : classes) {
            @SuppressWarnings("unchecked")
            Set<Method> routesMethods = getAllMethods(
                    controllerClass,
                    withModifier(Modifier.PUBLIC),
                    withAnnotation(Route.class),
                    withReturnType(String.class)
            );

            for (Method method : routesMethods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Route) {
                        String[] routePath = ((Route) annotation).PATH();
                        String httpMethod = ((Route) annotation).METHOD();
                        String contentType = ((Route) annotation).TYPE();

                        for (String path : routePath) {
                            switch (httpMethod) {
                                case "HEAD":
                                    headRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "OPTIONS":
                                    optionsRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "GET":
                                    getRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "POST":
                                    postRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "PATCH":
                                    patchRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "PUT":
                                    putRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "DELETE":
                                    deleteRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "TRACE":
                                    traceRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                case "CONNECT":
                                    connectRoutes.put(path, new RouteMethod(path, controllerClass, method));
                                    break;
                                default:
                                    routes.put(path, new RouteMethod(path, controllerClass, method));
                            }
                            contentTypes.put(path, contentType);
                        }
                    }
                }
            }
        }
    }

    public boolean hasController(String path, HttpMethod httpMethod) {
        return httpMethod == HEAD && headRoutes.containsKey(path)
                || httpMethod == OPTIONS && optionsRoutes.containsKey(path)
                || httpMethod == GET && getRoutes.containsKey(path)
                || httpMethod == POST && postRoutes.containsKey(path)
                || httpMethod == PATCH && patchRoutes.containsKey(path)
                || httpMethod == PUT && putRoutes.containsKey(path)
                || httpMethod == DELETE && deleteRoutes.containsKey(path)
                || httpMethod == TRACE && traceRoutes.containsKey(path)
                || httpMethod == CONNECT && connectRoutes.containsKey(path)
                || routes.containsKey(path);

    }

    public BaseController getController(String path, HttpMethod httpMethod)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (httpMethod == HEAD && headRoutes.containsKey(path)) {
            return getController(headRoutes, path);
        }
        if (httpMethod == OPTIONS && optionsRoutes.containsKey(path)) {
            return getController(optionsRoutes, path);
        }
        if (httpMethod == GET && getRoutes.containsKey(path)) {
            return getController(getRoutes, path);
        }
        if (httpMethod == POST && postRoutes.containsKey(path)) {
            return getController(postRoutes, path);
        }
        if (httpMethod == PATCH && patchRoutes.containsKey(path)) {
            return getController(patchRoutes, path);
        }
        if (httpMethod == PUT && putRoutes.containsKey(path)) {
            return getController(putRoutes, path);
        }
        if (httpMethod == DELETE && deleteRoutes.containsKey(path)) {
            return getController(deleteRoutes, path);
        }
        if (httpMethod == TRACE && traceRoutes.containsKey(path)) {
            return getController(traceRoutes, path);
        }
        if (httpMethod == CONNECT && connectRoutes.containsKey(path)) {
            return getController(connectRoutes, path);
        }
        if (routes.containsKey(path)) {
            return getController(routes, path);
        }

        return null;
    }

    private BaseController getController(Map<String, RouteMethod> routesMap, String path)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        BaseController controller = (BaseController) (routesMap.get(path).controller)
                .getConstructor()
                .newInstance();

        controller.setAction(routesMap.get(path).method);
        return controller;
    }

    public String runController(BaseController controller)
            throws InvocationTargetException, IllegalAccessException {
        return (String) controller.getAction().invoke(controller);
    }

    public String getContentType(String uri) {
        return contentTypes.get(uri);
    }

    private class RouteMethod {
        String routePath;

        Class<?> controller;
        Method method;

        RouteMethod(String routePath, Class<?> controller, Method method) {
            this.routePath = routePath;
            this.controller = controller;
            this.method = method;
        }
    }
}
