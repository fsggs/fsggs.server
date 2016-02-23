package com.fsggs.server.core.network;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetworkPacketRegister {

    private Map<String, Class<?>> networkPackets = new HashMap<>();

    public NetworkPacketRegister() {
        Reflections reflections = new Reflections("com.fsggs.server.packets");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(NetworkPacket.class);

        for (Class<?> networkPacketClass : classes) {
            for (Annotation annotation : networkPacketClass.getDeclaredAnnotations()) {
                if (annotation instanceof NetworkPacket
                        && networkPacketClass.getSuperclass().isAssignableFrom(BaseNetworkPacket.class)) {
                    String networkPacketName = ((NetworkPacket) annotation).name();
                    networkPackets.put(networkPacketName, networkPacketClass);
                }
            }
        }
    }

    public Map<String, Class<?>> getNetworkPackets() {
        return networkPackets;
    }
}
