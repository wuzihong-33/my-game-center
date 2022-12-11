package com.mygame.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import com.google.common.cache.LoadingCache;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.RateLimiter;
import com.mygame.common.utils.CommonField;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class RequestRateLimiterFilter implements GlobalFilter, Ordered {
    @Autowired
    private FilterConfig filterConfig;
    
    private RateLimiter globalRateLimiter;
    private LoadingCache<String, RateLimiter> userRateLimiterCache;
    private Logger logger = LoggerFactory.getLogger(RequestRateLimiterFilter.class);
    
    
    @PostConstruct
    public void init() {
        double permitsPerSecond = filterConfig.getGlobalRequestRateCount();
        globalRateLimiter = RateLimiter.create(permitsPerSecond);
        // 创建用户cache
        long maximumSize = filterConfig.getCacheUserMaxCount();
        long duration = filterConfig.getCacheUserTimeout();
        userRateLimiterCache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterAccess(duration, TimeUnit.MILLISECONDS).build(new CacheLoader<String, RateLimiter>() {
            @Override
            public RateLimiter load(String key) throws Exception {
                double permitsPerSecond = filterConfig.getUserRequestRateCount();
                RateLimiter newRateLimiter = RateLimiter.create(permitsPerSecond);
                return newRateLimiter;
            }
        });
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String openId = exchange.getRequest().getHeaders().getFirst(CommonField.OPEN_ID);
        if (!StringUtils.isEmpty(openId)) {
            try {
                RateLimiter userRateLimiter = userRateLimiterCache.get(openId);
                if (!userRateLimiter.tryAcquire()) {// 尝试获取令牌失败，被限流
                    this.tooManyRequest(exchange, chain);
                }
            } catch (ExecutionException e) {
                logger.error("限流器异常", e);
                return this.tooManyRequest(exchange, chain);
            }
        }
        if (!globalRateLimiter.tryAcquire()) {
            return this.tooManyRequest(exchange, chain);
        }
        return chain.filter(exchange);// 成功获取令牌，放行
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
    
    private Mono<Void> tooManyRequest(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("请求太多，触发限流");
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);// 请求失败，返回请求太多
        return exchange.getResponse().setComplete();
    }
    

}
