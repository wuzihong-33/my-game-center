package com.mygame.center.dataconfig;

public class GameGatewayInfo {
    private int id;       // 唯一id
    private String ip;    // 网关ip地址
    private int port;     // 网关端口
//    private int httpPort; //网关服务的Http的服务地址


    @Override
    public String toString() {
        return "GameGatewayInfo{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
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
}
