package com.fsggs.server.core.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String PATH() default "";

    String METHOD() default "*";

    String TYPE() default "text/html; charset=UTF-8";
}
