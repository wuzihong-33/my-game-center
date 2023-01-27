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
    private Logger logger = LoggerFactory.getLogger(RequestRateLimiterFilter.class);
    @Autowired
    private FilterConfig filterConfig;
    private RateLimiter globalRateLimiter;
    // 缓存 openId->用户的RateLimiter
    private LoadingCache<String, RateLimiter> userRateLimiterCache;
    
    @PostConstruct
    public void init() {
        double permitsPerSecond = filterConfig.getGlobalRequestRateCount();
        globalRateLimiter = RateLimiter.create(permitsPerSecond);
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
                if (!userRateLimiter.tryAcquire()) {// 尝试获取令牌失败
                    this.tooManyRequest(exchange);
                }
            } catch (ExecutionException e) {
                logger.error("限流器异常", e);
                return this.tooManyRequest(exchange);
            }
        }
        if (!globalRateLimiter.tryAcquire()) {
            return this.tooManyRequest(exchange);
        }
        return chain.filter(exchange);// 成功获取令牌，放行
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
    
    private Mono<Void> tooManyRequest(ServerWebExchange exchange) {
        logger.info("请求太多，触发限流");
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }
}
