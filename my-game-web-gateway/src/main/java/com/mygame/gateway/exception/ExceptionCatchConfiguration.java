package com.mygame.gateway.exception;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;

@Configuration
public class ExceptionCatchConfiguration {
//    private final ServerProperties serverProperties;
//    private final ApplicationContext applicationContext;
//    private final ResourceProperties resourceProperties;
//    private final List<ViewResolver> viewResolvers;
//    private final ServerCodecConfigurer serverCodecConfigurer;
//
//    public ExceptionCatchConfiguration(ServerProperties serverProperties, ResourceProperties resourceProperties, ObjectProvider<List<ViewResolver>> viewResolversProvider, ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
//        this.serverProperties = serverProperties;
//        this.applicationContext = applicationContext;
//        this.resouGlobalExceptionCatchHandler.javarceProperties = resourceProperties;
//        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
//        this.serverCodecConfigurer = serverCodecConfigurer;
//    }
//
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)//把Bean初始化的优先级设置为最高
//    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
//        //构造返回的bean类。
//        GlobalExceptionCatchHandler exceptionHandler = new GlobalExceptionCatchHandler(errorAttributes, this.resourceProperties, this.serverProperties.getError(), this.applicationContext);
//        exceptionHandler.setViewResolvers(this.viewResolvers);
//        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
//        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
//        return exceptionHandler;
//    }
}
