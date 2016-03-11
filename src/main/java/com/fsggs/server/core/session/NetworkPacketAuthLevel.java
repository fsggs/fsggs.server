package com.fsggs.server.core.session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.fsggs.server.core.session.AuthLevel.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetworkPacketAuthLevel {
    AuthLevel value() default GUEST;
}
