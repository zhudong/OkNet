package com.fyb.networklib.api;

import com.fyb.networklib.util.Convert;
import com.fyb.networklib.data.BaseEntity;
import com.fyb.networklib.util.JsonCallback;

/**
 * 行程管理API封装类
 * 封装行程管理相关的网络请求
 */
public class TripManageApi {
    
    private NetworkApi networkApi;
    private String baseUrl;
    
    public TripManageApi(NetworkApi networkApi, String baseUrl) {
        this.networkApi = networkApi;
        this.baseUrl = baseUrl;
    }
    
    /**
     * 添加行程管理
     * @param tripManageAddBean 行程管理数据Bean（需要实现序列化或提供toJson方法）
     * @param callback 回调
     * @param tag 请求标签
     * @param <T> 响应数据类型，通常是TripManageAddResponse
     */
    public <T> void addTripManage(Object tripManageAddBean, 
                                   JsonCallback<BaseEntity<T>> callback, 
                                   Object tag) {
        String url = baseUrl + "api/trip-manage/";
        String jsonBody = Convert.toJson(tripManageAddBean);
        networkApi.postJson(url, jsonBody, callback, tag);
    }
    
    /**
     * 添加行程管理（使用JSON字符串）
     * @param jsonBody JSON请求体
     * @param callback 回调
     * @param tag 请求标签
     * @param <T> 响应数据类型
     */
    public <T> void addTripManage(String jsonBody, 
                                   JsonCallback<BaseEntity<T>> callback, 
                                   Object tag) {
        String url = baseUrl + "api/trip-manage/";
        networkApi.postJson(url, jsonBody, callback, tag);
    }
}

