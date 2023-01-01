package com.mygame.client.service;

import org.apache.logging.log4j.core.config.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

/**
 * 负责客户端启动后的一些初始化操作
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GameClientInitService {
}
