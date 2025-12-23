package com.fyb.networklib.api;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.base.Request;
import com.fyb.networklib.util.JsonCallback;
import com.fyb.networklib.util.TokenProvider;

import java.util.Map;

/**
 * 网络请求API封装类
 * 封装OkGo网络请求，提供统一的接口
 */
public class NetworkApi {
    
    private static NetworkApi instance;
    private TokenProvider tokenProvider;
    
    private NetworkApi() {
    }
    
    public static NetworkApi getInstance() {
        if (instance == null) {
            synchronized (NetworkApi.class) {
                if (instance == null) {
                    instance = new NetworkApi();
                }
            }
        }
        return instance;
    }
    
    /**
     * 设置Token提供者
     * @param tokenProvider Token提供者
     */
    public void setTokenProvider(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    
    /**
     * POST请求 - 使用JSON格式
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @param callback 回调
     * @param tag 请求标签（用于取消请求）
     * @param <T> 响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> postJson(String url, String jsonBody, 
                                                       JsonCallback<T> callback, Object tag) {
        JsonCallback<T> jsonCallback = createCallback(callback);
        Request<T, ? extends Request> request = OkGo.<T>post(url)
                .tag(tag)
                .upJson(jsonBody);
        request.execute(jsonCallback);
        return request;
    }
    
    /**
     * POST请求 - 使用参数
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 回调
     * @param tag 请求标签（用于取消请求）
     * @param <T> 响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> post(String url, Map<String, String> params,
                                                   JsonCallback<T> callback, Object tag) {
        JsonCallback<T> jsonCallback = createCallback(callback);
        Request<T, ? extends Request> request = OkGo.<T>post(url)
                .tag(tag)
                .params(params);
        request.execute(jsonCallback);
        return request;
    }
    
    /**
     * GET请求
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 回调
     * @param tag 请求标签（用于取消请求）
     * @param <T> 响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> get(String url, Map<String, String> params,
                                                 JsonCallback<T> callback, Object tag) {
        JsonCallback<T> jsonCallback = createCallback(callback);
        Request<T, ? extends Request> request = OkGo.<T>get(url).tag(tag);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }
        request.execute(jsonCallback);
        return request;
    }
    
    /**
     * 创建带Token的回调
     */
    private <T> JsonCallback<T> createCallback(JsonCallback<T> callback) {
        if (callback != null && tokenProvider != null) {
            callback.setTokenProvider((JsonCallback.TokenProvider) tokenProvider);
        }
        return callback;
    }
    
    /**
     * 取消指定标签的请求
     * @param tag 请求标签
     */
    public void cancel(Object tag) {
        OkGo.getInstance().cancelTag(tag);
    }
    
    /**
     * 取消所有请求
     */
    public void cancelAll() {
        OkGo.getInstance().cancelAll();
    }
}

