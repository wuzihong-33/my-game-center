package com.mygame.http.response;

public class GameGatewayInfoMsg {
    private int id;
    private String ip;
    private int port;
    private String token;// 客户端连接网关需要携带token
    private String rsaPrivateKey;
    public GameGatewayInfoMsg() {}
    public GameGatewayInfoMsg(int id, String ip, int port) {
        super();
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }
}
