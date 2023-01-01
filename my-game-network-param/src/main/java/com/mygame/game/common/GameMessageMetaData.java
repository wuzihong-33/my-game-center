package com.mygame.game.common;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameMessageMetadata {
    int messageId(); // 消息请求id：用于区分每个请求对应的业务处理。比如，1001表示登录；1002表示创建角色等。
    int serviceId(); // 服务id：消息所要到达的服务id，用于消息分发和负载均衡。
    EnumMessageType messageType();
}
