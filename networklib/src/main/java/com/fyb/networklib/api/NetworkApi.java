package com.fyb.networklib.api;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.DeleteRequest;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.HeadRequest;
import com.lzy.okgo.request.OptionsRequest;
import com.lzy.okgo.request.PatchRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.PutRequest;
import com.lzy.okgo.request.TraceRequest;
import com.lzy.okgo.request.base.Request;
import com.fyb.networklib.util.JsonCallback;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * 网络请求API封装类
 * 封装OkGo网络请求，提供统一的接口
 */
public class NetworkApi {

    private static NetworkApi instance;

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
     * 初始化NetworkApi（必须在Application中调用）
     * 内部自动初始化OkGo并设置默认配置
     * @param app Application实例
     * @return NetworkApi实例
     */
    public NetworkApi init(Application app) {
        initOkGo(app);
        return this;
    }

    /**
     * 初始化OkGo并设置默认配置
     */
    private void initOkGo(Application application) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        // Global timeout
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        // Cookie management
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(application)));

        OkGo.getInstance().init(application)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);
    }

    /**
     * 设置OkHttpClient
     * @param okHttpClient OkHttpClient实例
     * @return NetworkApi实例
     */
    public NetworkApi setOkHttpClient(OkHttpClient okHttpClient) {
        OkGo.getInstance().setOkHttpClient(okHttpClient);
        return this;
    }

    /**
     * 获取OkHttpClient
     * @return OkHttpClient实例
     */
    public OkHttpClient getOkHttpClient() {
        return OkGo.getInstance().getOkHttpClient();
    }

    /**
     * 获取全局上下文
     * @return Context实例
     */
    public Context getContext() {
        return OkGo.getInstance().getContext();
    }

    /**
     * 获取主线程调度器
     * @return Handler实例
     */
    public Handler getDelivery() {
        return OkGo.getInstance().getDelivery();
    }

    /**
     * 获取Cookie管理器
     * @return CookieJarImpl实例
     */
    public CookieJarImpl getCookieJar() {
        return OkGo.getInstance().getCookieJar();
    }

    /**
     * 设置超时重试次数
     * @param retryCount 重试次数
     * @return NetworkApi实例
     */
    public NetworkApi setRetryCount(int retryCount) {
        OkGo.getInstance().setRetryCount(retryCount);
        return this;
    }

    /**
     * 获取超时重试次数
     * @return 重试次数
     */
    public int getRetryCount() {
        return OkGo.getInstance().getRetryCount();
    }

    /**
     * 设置全局缓存模式
     * @param cacheMode 缓存模式
     * @return NetworkApi实例
     */
    public NetworkApi setCacheMode(CacheMode cacheMode) {
        OkGo.getInstance().setCacheMode(cacheMode);
        return this;
    }

    /**
     * 获取全局缓存模式
     * @return 缓存模式
     */
    public CacheMode getCacheMode() {
        return OkGo.getInstance().getCacheMode();
    }

    /**
     * 设置全局缓存过期时间
     * @param cacheTime 缓存时间（毫秒）
     * @return NetworkApi实例
     */
    public NetworkApi setCacheTime(long cacheTime) {
        OkGo.getInstance().setCacheTime(cacheTime);
        return this;
    }

    /**
     * 获取全局缓存过期时间
     * @return 缓存时间（毫秒）
     */
    public long getCacheTime() {
        return OkGo.getInstance().getCacheTime();
    }

    /**
     * 添加全局公共请求参数
     * @param commonParams 请求参数
     * @return NetworkApi实例
     */
    public NetworkApi addCommonParams(HttpParams commonParams) {
        OkGo.getInstance().addCommonParams(commonParams);
        return this;
    }

    /**
     * 获取全局公共请求参数
     * @return HttpParams实例
     */
    public HttpParams getCommonParams() {
        return OkGo.getInstance().getCommonParams();
    }

    /**
     * 添加全局公共请求头
     * @param commonHeaders 请求头
     * @return NetworkApi实例
     */
    public NetworkApi addCommonHeaders(HttpHeaders commonHeaders) {
        OkGo.getInstance().addCommonHeaders(commonHeaders);
        return this;
    }

    /**
     * 获取全局公共请求头
     * @return HttpHeaders实例
     */
    public HttpHeaders getCommonHeaders() {
        return OkGo.getInstance().getCommonHeaders();
    }
    
    // ==================== HTTP 请求方法 ====================

    /**
     * GET请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return GetRequest对象，可用于进一步配置
     */
    public static <T> GetRequest<T> get(String url) {
        return OkGo.get(url);
    }

    /**
     * POST请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return PostRequest对象，可用于进一步配置
     */
    public static <T> PostRequest<T> post(String url) {
        return OkGo.post(url);
    }

    /**
     * PUT请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return PutRequest对象，可用于进一步配置
     */
    public static <T> PutRequest<T> put(String url) {
        return OkGo.put(url);
    }

    /**
     * HEAD请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return HeadRequest对象，可用于进一步配置
     */
    public static <T> HeadRequest<T> head(String url) {
        return OkGo.head(url);
    }

    /**
     * DELETE请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return DeleteRequest对象，可用于进一步配置
     */
    public static <T> DeleteRequest<T> delete(String url) {
        return OkGo.delete(url);
    }

    /**
     * OPTIONS请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return OptionsRequest对象，可用于进一步配置
     */
    public static <T> OptionsRequest<T> options(String url) {
        return OkGo.options(url);
    }

    /**
     * PATCH请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return PatchRequest对象，可用于进一步配置
     */
    public static <T> PatchRequest<T> patch(String url) {
        return OkGo.patch(url);
    }

    /**
     * TRACE请求
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return TraceRequest对象，可用于进一步配置
     */
    public static <T> TraceRequest<T> trace(String url) {
        return OkGo.trace(url);
    }

    // ==================== 兼容性方法 ====================

    // ==================== 兼容性方法 ====================

    /**
     * POST请求 - 使用JSON格式（兼容旧版本）
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @param callback 回调
     * @param tag 请求标签（用于取消请求）
     * @param <T> 响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> postJson(String url, String jsonBody,
                                                       JsonCallback<T> callback, Object tag) {
        Request<T, ? extends Request> request = OkGo.<T>post(url)
                .tag(tag)
                .upJson(jsonBody);
        request.execute(callback);
        return request;
    }

    /**
     * POST请求 - 使用参数（兼容旧版本）
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 回调
     * @param tag 请求标签（用于取消请求）
     * @param <T> 响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> post(String url, Map<String, String> params,
                                                   JsonCallback<T> callback, Object tag) {
        Request<T, ? extends Request> request = OkGo.<T>post(url)
                .tag(tag)
                .params(params);
        request.execute(callback);
        return request;
    }

    /**
     * GET请求（兼容旧版本）
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 回调
     * @param tag 请求标签（用于取消请求）
     * @param <T> 响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> get(String url, Map<String, String> params,
                                                 JsonCallback<T> callback, Object tag) {
        Request<T, ? extends Request> request = OkGo.<T>get(url).tag(tag);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }
        request.execute(callback);
        return request;
    }
    
    // ==================== 请求取消方法 ====================

    /**
     * 根据Tag取消请求
     * @param tag 请求标签
     */
    public void cancelTag(Object tag) {
        OkGo.getInstance().cancelTag(tag);
    }

    /**
     * 根据Tag取消请求（静态方法）
     * @param client OkHttpClient实例
     * @param tag 请求标签
     */
    public static void cancelTag(OkHttpClient client, Object tag) {
        OkGo.cancelTag(client, tag);
    }

    /**
     * 取消所有请求
     */
    public void cancelAll() {
        OkGo.getInstance().cancelAll();
    }

    /**
     * 取消所有请求（静态方法）
     * @param client OkHttpClient实例
     */
    public static void cancelAll(OkHttpClient client) {
        OkGo.cancelAll(client);
    }

    // ==================== 兼容性方法 ====================

    /**
     * 取消指定标签的请求（兼容旧版本方法名）
     * @param tag 请求标签
     */
    public void cancel(Object tag) {
        cancelTag(tag);
    }

}

