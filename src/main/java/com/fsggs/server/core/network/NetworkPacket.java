package com.fsggs.server.core.network;

import com.fsggs.server.core.FrameworkRegistry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetworkPacket {
    String value() default FrameworkRegistry.SYSTEM_UNKNOWN_PACKET;
}
