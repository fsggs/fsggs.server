package com.fsggs.server.core;

import com.fsggs.server.core.network.*;
import com.fsggs.server.core.session.NeedAuthorization;
import com.fsggs.server.core.session.NeedPermission;
import io.netty.handler.codec.http.HttpMethod;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.reflections.ReflectionUtils.*;

public class FrameworkRegistry {
    static public final String SYSTEM_PACKET_NAME = "__@NetworkPacket#_";
    static public final String SYSTEM_UNKNOWN_PACKET = "__@UnknownNetworkPacket";

    final private String[] ControllerNamespaces = {
            "com.fsggs.server.controllers"
    };

    final private String[] PacketNamespaces = {
            "com.fsggs.server.packets"
    };

    private String[] METHODS = {"*", "HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE", "TRACE", "CONNECT"};

    private Map<String, FrameworkController> registeredControllers = new HashMap<>();
    private Map<String, Map<String, FrameworkRoute>> registeredRoutes = new HashMap<>();
    private Map<String, FrameworkPacket> registeredPackets = new HashMap<>();

    public FrameworkRegistry() {
        registerControllers();
        registerRoutes();
        registerNetworkPackets();
    }

    private void registerControllers() {
        for (String namespace : ControllerNamespaces) {
            Reflections reflections = new Reflections(namespace);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);

            for (Class<?> classDefinition : classes) {
                String className = classDefinition.toString().substring(6);
                FrameworkController controller = new FrameworkController(className, classDefinition);

                Annotation[] annotations = classDefinition.getDeclaredAnnotations();
                authAnnotation(annotations, controller);
                if (Objects.equals(controller.getName(), "")) controller.setName(className);

                @SuppressWarnings("unchecked")
                Set<Method> routeMethods = getAllMethods(
                        classDefinition,
                        withModifier(Modifier.PUBLIC),
                        withAnnotation(Route.class),
                        withReturnType(String.class)
                );

                for (Method method : routeMethods) {
                    FrameworkRoute route = new FrameworkRoute(method, classDefinition);
                    annotations = method.getDeclaredAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Route) {
                            String path = ((Route) annotation).PATH();
                            String httpMethod = ((Route) annotation).METHOD();
                            String contentType = ((Route) annotation).TYPE();

                            if (!Objects.equals(path, "")) route.setPath(path);
                            if (!Objects.equals(httpMethod, "")) route.setMethod(httpMethod);
                            if (!Objects.equals(path, "")) route.setResponseType(contentType);
                        }
                        if (annotation instanceof NeedAuthorization) {
                            boolean authorization = ((NeedAuthorization) annotation).value();
                            if (authorization) route.setAuthorized(true);
                        }
                        if (annotation instanceof NeedPermission) {
                            String permission = ((NeedPermission) annotation).value();
                            if (!Objects.equals(permission, "")) route.setPermission(permission);
                        }
                    }
                    if (route.isValid()) {
                        controller.addRoute(route);
                    }
                }
                if (controller.size() > 0) registeredControllers.put(className, controller);
            }
        }
    }

    private void registerRoutes() {
        for (String methodType : METHODS) registeredRoutes.put(methodType, new HashMap<>());

        for (Map.Entry<String, FrameworkController> controller : registeredControllers.entrySet()) {
            Map<String, FrameworkRoute> routes = controller.getValue().getRoutes("*");
            for (Map.Entry<String, FrameworkRoute> route : routes.entrySet()) {
                String method = route.getValue().getMethod();
                if(Objects.equals(method, "*")) {
                    for (String methodType : METHODS) {
                        registeredRoutes.get(methodType).put(route.getValue().getPath(), route.getValue());
                    }
                } else {
                    registeredRoutes.get(method).put(route.getValue().getPath(), route.getValue());
                }
            }
        }
    }

    private void registerNetworkPackets() {
        for (String namespace : PacketNamespaces) {
            Reflections reflections = new Reflections(namespace);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(NetworkPacket.class);

            for (Class<?> classDefinition : classes) {
                String className = classDefinition.toString().substring(6);
                FrameworkPacket packet = new FrameworkPacket(className, classDefinition);

                Annotation[] annotations = classDefinition.getDeclaredAnnotations();
                authAnnotation(annotations, packet);

                if (Objects.equals(packet.getName(), "")) packet.setName(className);

                @SuppressWarnings("unchecked")
                Set<Method> routeMethods = getAllMethods(
                        classDefinition,
                        withModifier(Modifier.PUBLIC),
                        withPrefix("set"),
                        withParametersCount(1),
                        withAnnotation(NetworkPacketParam.class)
                );
                for (Method method : routeMethods) {
                    annotations = method.getDeclaredAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof NetworkPacketParam) {
                            String param = ((NetworkPacketParam) annotation).value();

                            if (!Objects.equals(param, "")) packet.addParam(param, method);
                        }
                    }
                }
                registeredPackets.put(packet.getName(), packet);
            }
        }
    }

    private void authAnnotation(Annotation[] annotations, FrameworkElement classDefinition) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof NetworkPacket) {
                String name = ((NetworkPacket) annotation).value();
                if (!Objects.equals(name, SYSTEM_UNKNOWN_PACKET)) classDefinition.setName(name);
            }
            if (annotation instanceof NetworkPacketId) {
                int id = ((NetworkPacketId) annotation).value();
                if (id >= 0) classDefinition.setName(SYSTEM_PACKET_NAME + id);
            }
            if (annotation instanceof Controller) {
                String name = ((Controller) annotation).value();
                if (!Objects.equals(name, "")) classDefinition.setName(name);
            }
            if (annotation instanceof NeedAuthorization) {
                boolean authorization = ((NeedAuthorization) annotation).value();
                if (authorization) classDefinition.setAuthorized(true);
            }
            if (annotation instanceof NeedPermission) {
                String permission = ((NeedPermission) annotation).value();
                if (!Objects.equals(permission, "")) classDefinition.setPermission(permission);
            }
        }
    }

    public Map<String, FrameworkRoute> getRoutes(String httpMethod) {
        Map<String, FrameworkRoute> results = new HashMap<>();
        for (Map.Entry<String, FrameworkController> entry : registeredControllers.entrySet()) {
            Map<String, FrameworkRoute> route = entry.getValue().getRoutes(httpMethod);
            results.putAll(route);
        }
        return results;
    }

    public FrameworkPacket getPacket(String name) {
        if (registeredPackets.containsKey(name)) return registeredPackets.get(name);
        return null;
    }

    public boolean hasPacket(String name) {
        return registeredPackets.containsKey(name);
    }

    public BaseController getController(String path, HttpMethod method) {
        Map<String, FrameworkRoute> routes = registeredRoutes.get(method.toString());
        if (routes.containsKey(path)) {
            try {
                BaseController controller = (BaseController) (routes.get(path).getClassController())
                        .getConstructor()
                        .newInstance();
                controller.setAction(routes.get(path).getClassMethod());
                return controller;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getRouteContentType(String path, HttpMethod method) {
        Map<String, FrameworkRoute> routes = registeredRoutes.get(method.toString());
        if (routes.containsKey(path)) {
            return routes.get(path).getResponseType();
        }
        return "text/html; charset=UTF-8";
    }

    public boolean hasController(String path, HttpMethod method) {
        Map<String, FrameworkRoute> routes = registeredRoutes.get(method.toString());
        return routes.containsKey(path);
    }

    interface FrameworkElement {
        void setName(String name);

        void setAuthorized(boolean authorized);

        void setPermission(String permission);
    }

    private class FrameworkController implements FrameworkElement {
        private String name = "";
        private String permission;
        private boolean isAuthorized = false;

        private List<FrameworkRoute> routeList = new LinkedList<>();

        private Class<?> className;

        FrameworkController(String name, Class<?> className) {
            this.name = name;
            this.className = className;
        }

        int size() {
            return routeList.size();
        }

        void addRoute(FrameworkRoute route) {
            routeList.add(route);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAuthorized(boolean authorized) {
            isAuthorized = authorized;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        Map<String, FrameworkRoute> getRoutes(String httpMethod) {
            Map<String, FrameworkRoute> results = new HashMap<>();
            for (FrameworkRoute route : routeList) {
                if (Objects.equals(httpMethod, "*") || Objects.equals(httpMethod, route.method)) {
                    results.put(route.path, route);
                }
            }
            return results;
        }

        public String getName() {
            return name;
        }
    }

    public class FrameworkRoute {
        private String path = "";
        private String method = "*";
        private String responseType = "text/html; charset=UTF-8";
        private String permission;
        private boolean isAuthorized = false;

        private Class<?> classController;
        private Method classMethod;

        FrameworkRoute(Method classMethod, Class<?> classController) {
            this.classMethod = classMethod;
            this.classController = classController;
        }

        void setPath(String path) {
            this.path = path;
        }

        void setMethod(String method) {
            this.method = method;
        }

        void setResponseType(String responseType) {
            this.responseType = responseType;
        }

        boolean isValid() {
            return !Objects.equals(path, "");
        }

        void setAuthorized(boolean authorized) {
            isAuthorized = authorized;
        }

        void setPermission(String permission) {
            this.permission = permission;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public String getResponseType() {
            return responseType;
        }

        public boolean isAuthorized() {
            return isAuthorized;
        }

        public Method getClassMethod() {
            return classMethod;
        }

        public String getPermission() {
            return permission;
        }

        public Class<?> getClassController() {
            return classController;
        }
    }

    public class FrameworkPacket implements FrameworkElement {
        private String name = "";
        private String permission;
        private boolean isAuthorized;

        private Map<String, Method> params = new HashMap<>();

        private Class<?> className;

        FrameworkPacket(String name, Class<?> className) {
            this.name = name;
            this.className = className;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAuthorized(boolean authorized) {
            isAuthorized = authorized;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        void addParam(String param, Method method) {
            params.put(param, method);
        }

        public String getName() {
            return name;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isAuthorized() {
            return isAuthorized;
        }

        public Class<?> getClassName() {
            return className;
        }

        public Map<String, Method> getParams() {
            return params;
        }
    }
}




