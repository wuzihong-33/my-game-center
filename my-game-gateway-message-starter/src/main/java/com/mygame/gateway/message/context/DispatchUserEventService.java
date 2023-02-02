package com.mygame.gateway.message.context;

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
import com.mygame.game.messagedispatcher.DispatcherMapping;
import com.mygame.game.messagedispatcher.GameMessageHandler;
import io.netty.util.concurrent.Promise;

@Service
public class DispatchUserEventService {
//    private Logger logger = LoggerFactory.getLogger(DispatchUserEventService.class);
//    private Map<String, DispatcherMapping> userEventMethodCache = new HashMap<>();//数据缓存
//    @Autowired
//    private ApplicationContext context;//注入spring 上下文类
//
//    @PostConstruct
//    public void init() {//项目启动之后，调用此初始化方法
//        Map<String, Object> beans = context.getBeansWithAnnotation(GameMessageHandler.class);//从spring 容器中获取所有被@GameMessageHandler标记的所有的类实例
//        beans.values().parallelStream().forEach(c -> {//使用stream并行处理遍历这些对象
//            Method[] methods = c.getClass().getMethods();
//            for (Method method : methods) {//遍历每个类中的方法
//                UserEvent userEvent = method.getAnnotation(UserEvent.class);
//                if (userEvent != null) {//如果这个方法被@UserEvent注解标记了，缓存下所有的数据
//                    String key = userEvent.value().getName();
//                    DispatcherMapping dispatcherMapping = new DispatcherMapping(c, method);
//                    userEventMethodCache.put(key, dispatcherMapping);
//                }
//            }
//        });
//    }
//    //通过反射调用处理相应事件的方法
//    public void callMethod(UserEventContext<?> ctx,Object event, Promise<Object> promise) {
//        String key = event.getClass().getName();
//        DispatcherMapping dispatcherMapping = this.userEventMethodCache.get(key);
//        if (dispatcherMapping != null) {
//            try {
//                dispatcherMapping.getTargetMethod().invoke(dispatcherMapping.getTargetObj(), ctx,event, promise);
//            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//                logger.error("事件处理调用失败，事件对象:{},处理对象：{}，处理方法：{}", event.getClass().getName(), dispatcherMapping.getTargetObj().getClass().getName(), dispatcherMapping.getTargetMethod().getName());
//            }
//        } else {
//            logger.debug("事件：{} 没有找到处理的方法", event.getClass().getName());
//        }
//    }
}
