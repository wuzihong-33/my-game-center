package com.mygame.gateway.message.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.mygame.game.common.IGameMessage;
import com.mygame.game.messagedispatcher.DispatcherMapping;
import com.mygame.game.messagedispatcher.GameMessageHandler;

/**
 * 根据rpc请求，调用具体的rpc server instance处理
 */
@Service
public class DispatchRPCEventService {
    private Logger logger = LoggerFactory.getLogger(DispatchRPCEventService.class);
    private Map<String, DispatcherMapping> rpcEventMethodCache = new HashMap<>();
    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        Map<String, Object> beans = context.getBeansWithAnnotation(GameMessageHandler.class);
        beans.values().parallelStream().forEach(object -> {
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                RPCEvent userEvent = method.getAnnotation(RPCEvent.class);
                if (userEvent != null) {
                    String key = userEvent.value().getName();
                    DispatcherMapping dispatcherMapping = new DispatcherMapping(object, method);
                    rpcEventMethodCache.put(key, dispatcherMapping);
                }
            }
        });
    }
    
    public void callMethod(RPCEventContext<?> ctx, IGameMessage msg) {
        String key = msg.getClass().getName();
        DispatcherMapping dispatcherMapping = this.rpcEventMethodCache.get(key);
        if (dispatcherMapping != null) {
            try {
                dispatcherMapping.getTargetMethod().invoke(dispatcherMapping.getTargetObject(), ctx, msg);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("RPC处理调用失败，消息对象: {}, 处理对象: {}, 处理方法: {}", msg.getClass().getName(), dispatcherMapping.getTargetObject().getClass().getName(), dispatcherMapping.getTargetMethod().getName());
            }
        } else {
            logger.debug("RPC请求对象：{} 没有找到处理的方法", msg.getClass().getName());
        }
    }
    
}
