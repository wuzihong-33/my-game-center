package com.mygame.gateway.filter;

import com.mygame.common.error.TokenException;
import com.mygame.common.utils.CommonField;
import com.mygame.common.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 权限验证全局过滤器
 * 当某个uri存在于白名单中或token验证成功，放行
 */
@Service
public class TokenVerifyFilter implements GlobalFilter, Ordered {
    private Logger logger = LoggerFactory.getLogger(TokenVerifyFilter.class);
    @Autowired
    private FilterConfig filterConfig;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUri = exchange.getRequest().getURI().getPath();
        List<String> whiteRequestUris = filterConfig.getWhiteRequestUri();
        if (whiteRequestUris.contains(requestUri)) {
            return chain.filter(exchange); // 通过验证
        }
        String token = exchange.getRequest().getHeaders().getFirst(CommonField.TOKEN);
        if (StringUtils.isEmpty(token)) {
            logger.info("{} 请求验证失败,token为空", requestUri);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            JWTUtil.TokenBody tokenBody = JWTUtil.getTokenBody(token);
            // 把token中的openId和userId添加到Header中，转发到后面的服务
            ServerHttpRequest request = exchange.getRequest().mutate().header(CommonField.OPEN_ID, tokenBody.getOpenId()).header(CommonField.USER_ID, String.valueOf(tokenBody.getUserId())).build();
            // exchange内部变量都是不变类型，因此需要使用mutate新建一个实例出来
            ServerWebExchange newExchange = exchange.mutate().request(request).build(); 
            return chain.filter(newExchange);
        } catch (TokenException e) {
            logger.debug("{} 请求验证失败,token非法", requestUri);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
