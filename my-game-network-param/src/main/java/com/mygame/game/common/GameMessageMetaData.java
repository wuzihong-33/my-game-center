package com.mygame.game.common;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameMessageMetaData {
    int messageId();
    int serviceId();
    EnumMessageType messageType();
}
