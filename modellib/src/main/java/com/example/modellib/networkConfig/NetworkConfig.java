package com.example.modellib.networkConfig;

public class NetworkConfig {

    private String url = "";
    private int port = 0;

    static NetworkConfig instance;

    public static NetworkConfig newBuilder(){
        instance = new NetworkConfig();
        return instance;
    }

    public NetworkConfig setUrl(String url){
        instance.url = url;
        return instance;
    }

    public NetworkConfig setPort(int port){
        instance.port = port;
        return instance;
    }

    public String getUrl() {
        return instance == null ? "" : instance.url;
    }

    public int getPort() {
        return instance == null ? 0 : instance.port;
    }

    private NetworkConfig() {
    }
}
