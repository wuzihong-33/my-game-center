package com.mygame.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 对Apache HttpClient组件进行封装
 * 用于发送HTTP请求，比如从游戏服务中心获取游戏网关信息
 */
public class GameHttpClient {
    private static Logger logger = LoggerFactory.getLogger(GameHttpClient.class);
    private static PoolingHttpClientConnectionManager poolConnManager = null; // 池化管理
    private static CloseableHttpClient httpClient; // 线程安全的，因此所有线程可以一块使用它发送http请求
    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            // 配置同时支持 HTTP 和 HTTPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", new SSLConnectionSocketFactory(builder.build()))
                    .build();
            // 初始化连接管理器
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolConnManager.setMaxTotal(640);// 同时最多连接数
            poolConnManager.setDefaultMaxPerRoute(320); // 最大路由
            httpClient = buildHttpClient();
            logger.debug("GameHttpClient初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("GameHttpClient初始化失败",e);
        }
    }

    // 创建httpClient实例
    public static CloseableHttpClient buildHttpClient() {
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        CloseableHttpClient httpClient = HttpClients
                .custom()
                // 设置连接池管理
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(config)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(2, false))
                .build();
        
        return httpClient;
    }

    /**
     * 发送get请求
     * @param url
     * @return
     */
    public static String get(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity());
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                logger.error("请求{}返回错误码：{},{}", url, code, result);
                return null;
            }
        } catch (IOException e) {
            logger.error("http请求异常，{}",url,e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发送post请求
     * @param uri
     * @param params
     * @param heads
     * @return
     */
    public static String post(String uri, Object params, Header... heads) {
        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpResponse response = null;
        try {
            StringEntity paramEntity = new StringEntity(JSON.toJSONString(params));
            paramEntity.setContentEncoding("UTF-8");
            paramEntity.setContentType("application/json");
            httpPost.setEntity(paramEntity);
            if (heads != null) {
                httpPost.setHeaders(heads);
            }
            response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (code == HttpStatus.SC_OK) {
                return result;
            } else {
                logger.error("请求{}返回错误码:{},请求参数:{},{}", uri, code, params,result);
                return null;
            }
        } catch (IOException e) {
            logger.error("收集服务配置http请求异常", e);
        } finally {
            try {
                if(response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
