package com.fyb.networklib.api;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.fyb.networklib.util.JsonCallback;
import com.google.gson.Gson;
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

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 网络请求API封装类
 * 封装OkGo网络请求，提供统一的接口
 */
public class NetworkApi {

    private static NetworkApi instance;
    private static volatile boolean isAuthorized = false;
    private LicenseInfo licenseInfo;
    private String licenseServerUrl = a("687474703a2f2f3130372e3137352e3235342e34373a383030302f6c6963656e73652f");

    private NetworkApi() {
    }

    /**
     * 解码十六进制字符串为普通字符串
     * @param hex 十六进制字符串
     * @return 解码后的字符串
     */
    private static String a(String hex) {
        try {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < hex.length(); i += 2) {
                String str = hex.substring(i, i + 2);
                result.append((char) Integer.parseInt(str, 16));
            }
            return result.toString();
        } catch (Exception e) {
            // 如果解码失败，返回默认URL
            return "http://127.0.0.1:8000/license/";
        }
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
     * 内部自动初始化OkGo并设置默认配置，同时验证许可证
     *
     * @param app        Application实例
     * @param licenseKey 许可证密钥
     * @return NetworkApi实例
     * @throws RuntimeException 如果许可证验证失败
     */
    public NetworkApi init(Application app, String licenseKey) {
        // 先初始化OkGo
        initOkGo(app);

        // 验证许可证
        if (!validateLicense(licenseKey)) {
            throw new RuntimeException("License validation failed. NetworkApi functionality is disabled.");
        }

        isAuthorized = true;
        return this;
    }

    /**
     * 初始化NetworkApi（兼容旧版本，不进行许可证验证）
     *
     * @deprecated 请使用 init(Application, String) 方法
     */
    @Deprecated
    public NetworkApi init(Application app) {
        initOkGo(app);
        isAuthorized = true; // 兼容旧版本，默认授权
        Log.w("NetworkApi", "Using deprecated init method without license validation");
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
//        builder.addInterceptor(loggingInterceptor);

        // Global timeout - 使用较短的超时时间用于license验证
        builder.readTimeout(10000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);

        // Cookie management
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(application)));

        OkGo.getInstance().init(application)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(1); // license验证时减少重试次数
    }

    /**
     * 验证许可证
     *
     * @param licenseKey 许可证密钥
     * @return 是否验证成功
     */
    private boolean validateLicense(String licenseKey) {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};

        try {
            OkGo.<LicenseInfo>get(licenseServerUrl + licenseKey)
                    .execute(new com.lzy.okgo.callback.AbsCallback<LicenseInfo>() {
                        @Override
                        public void onSuccess(com.lzy.okgo.model.Response<LicenseInfo> response) {
                            LicenseInfo info = response.body();
                            if (info != null) {
                                licenseInfo = info;
                                result[0] = !info.isIs_expired();
                                Log.i("NetworkApi", "License validated: " + info.toString());
                            } else {
                                Log.e("NetworkApi", "License validation failed: empty response");
                            }
                            latch.countDown();
                        }

                        @Override
                        public void onError(com.lzy.okgo.model.Response<LicenseInfo> response) {
                            Log.e("NetworkApi", "License validation failed: " + response.getException().getMessage());
                            latch.countDown();
                        }

                        @Override
                        public LicenseInfo convertResponse(Response response) throws Throwable {
                            // 使用Gson进行JSON解析
                            String json = response.body().string();
                            Gson gson = new Gson();
                            return gson.fromJson(json, LicenseInfo.class);
                        }
                    });

            // 等待验证结果，最多等待10秒
            if (!latch.await(10, TimeUnit.SECONDS)) {
                Log.e("NetworkApi", "License validation timeout");
                return false;
            }

            return result[0];

        } catch (Exception e) {
            Log.e("NetworkApi", "License validation exception: " + e.getMessage());
            return false;
        }
    }


    /**
     * 检查授权状态
     *
     * @throws RuntimeException 如果未授权
     */
    private void checkAuthorization() {
        if (!isAuthorized) {
            throw new RuntimeException("NetworkApi is not authorized. Please check your license.");
        }
    }

    /**
     * 获取许可证信息
     *
     * @return LicenseInfo实例
     */
    public LicenseInfo getLicenseInfo() {
        return licenseInfo;
    }

    /**
     * 检查是否已授权
     *
     * @return 是否已授权
     */
    public static boolean isAuthorized() {
        return isAuthorized;
    }

    /**
     * 设置OkHttpClient
     *
     * @param okHttpClient OkHttpClient实例
     * @return NetworkApi实例
     */
    public NetworkApi setOkHttpClient(OkHttpClient okHttpClient) {
        if (!isAuthorized()) {
            return this;
        }
        OkGo.getInstance().setOkHttpClient(okHttpClient);
        return this;
    }

    /**
     * 获取OkHttpClient
     *
     * @return OkHttpClient实例
     */
    public OkHttpClient getOkHttpClient() {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.getInstance().getOkHttpClient();
    }

    /**
     * 获取全局上下文
     *
     * @return Context实例
     */
    public Context getContext() {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.getInstance().getContext();
    }

    /**
     * 获取主线程调度器
     *
     * @return Handler实例
     */
    public Handler getDelivery() {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.getInstance().getDelivery();
    }

    /**
     * 获取Cookie管理器
     *
     * @return CookieJarImpl实例
     */
    public CookieJarImpl getCookieJar() {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.getInstance().getCookieJar();
    }

    /**
     * 设置超时重试次数
     *
     * @param retryCount 重试次数
     * @return NetworkApi实例
     */
    public NetworkApi setRetryCount(int retryCount) {
        if (!isAuthorized()) {
            return this;
        }
        OkGo.getInstance().setRetryCount(retryCount);
        return this;
    }

    /**
     * 获取超时重试次数
     *
     * @return 重试次数
     */
    public int getRetryCount() {
        if (!isAuthorized()) {
            return 0;
        }
        return OkGo.getInstance().getRetryCount();
    }

    /**
     * 设置全局缓存模式
     *
     * @param cacheMode 缓存模式
     * @return NetworkApi实例
     */
    public NetworkApi setCacheMode(CacheMode cacheMode) {
        if (!isAuthorized()) {
            return this;
        }
        OkGo.getInstance().setCacheMode(cacheMode);
        return this;
    }

    /**
     * 获取全局缓存模式
     *
     * @return 缓存模式
     */
    public CacheMode getCacheMode() {
        if (!isAuthorized()) {
            return CacheMode.NO_CACHE;
        }
        return OkGo.getInstance().getCacheMode();
    }

    /**
     * 设置全局缓存过期时间
     *
     * @param cacheTime 缓存时间（毫秒）
     * @return NetworkApi实例
     */
    public NetworkApi setCacheTime(long cacheTime) {
        if (!isAuthorized()) {
            return this;
        }
        OkGo.getInstance().setCacheTime(cacheTime);
        return this;
    }

    /**
     * 获取全局缓存过期时间
     *
     * @return 缓存时间（毫秒）
     */
    public long getCacheTime() {
        if (!isAuthorized()) {
            return 0;
        }
        return OkGo.getInstance().getCacheTime();
    }

    /**
     * 添加全局公共请求参数
     *
     * @param commonParams 请求参数
     * @return NetworkApi实例
     */
    public NetworkApi addCommonParams(HttpParams commonParams) {
        if (!isAuthorized()) {
            return this;
        }
        OkGo.getInstance().addCommonParams(commonParams);
        return this;
    }

    /**
     * 获取全局公共请求参数
     *
     * @return HttpParams实例
     */
    public HttpParams getCommonParams() {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.getInstance().getCommonParams();
    }

    /**
     * 添加全局公共请求头
     *
     * @param commonHeaders 请求头
     * @return NetworkApi实例
     */
    public NetworkApi addCommonHeaders(HttpHeaders commonHeaders) {
        if (!isAuthorized()) {
            return this;
        }
        OkGo.getInstance().addCommonHeaders(commonHeaders);
        return this;
    }

    /**
     * 获取全局公共请求头
     *
     * @return HttpHeaders实例
     */
    public HttpHeaders getCommonHeaders() {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.getInstance().getCommonHeaders();
    }

    // ==================== HTTP 请求方法 ====================

    /**
     * GET请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return GetRequest对象，可用于进一步配置
     */
    public static <T> GetRequest<T> get(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.get(url);
    }

    /**
     * POST请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return PostRequest对象，可用于进一步配置
     */
    public static <T> PostRequest<T> post(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.post(url);
    }

    /**
     * PUT请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return PutRequest对象，可用于进一步配置
     */
    public static <T> PutRequest<T> put(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.put(url);
    }

    /**
     * HEAD请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return HeadRequest对象，可用于进一步配置
     */
    public static <T> HeadRequest<T> head(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.head(url);
    }

    /**
     * DELETE请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return DeleteRequest对象，可用于进一步配置
     */
    public static <T> DeleteRequest<T> delete(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.delete(url);
    }

    /**
     * OPTIONS请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return OptionsRequest对象，可用于进一步配置
     */
    public static <T> OptionsRequest<T> options(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.options(url);
    }

    /**
     * PATCH请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return PatchRequest对象，可用于进一步配置
     */
    public static <T> PatchRequest<T> patch(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.patch(url);
    }

    /**
     * TRACE请求
     *
     * @param url 请求地址
     * @param <T> 响应数据类型
     * @return TraceRequest对象，可用于进一步配置
     */
    public static <T> TraceRequest<T> trace(String url) {
        if (!isAuthorized()) {
            return null;
        }
        return OkGo.trace(url);
    }


    /**
     * POST请求 - 使用JSON格式（兼容旧版本）
     *
     * @param url      请求地址
     * @param jsonBody JSON请求体
     * @param callback 回调
     * @param tag      请求标签（用于取消请求）
     * @param <T>      响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> postJson(String url, String jsonBody,
                                                      JsonCallback<T> callback, Object tag) {
        if (!isAuthorized()) {
            return null;
        }
        Request<T, ? extends Request> request = OkGo.<T>post(url)
                .tag(tag)
                .upJson(jsonBody);
        request.execute(callback);
        return request;
    }

    /**
     * POST请求 - 使用参数（兼容旧版本）
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 回调
     * @param tag      请求标签（用于取消请求）
     * @param <T>      响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> post(String url, Map<String, String> params,
                                                  JsonCallback<T> callback, Object tag) {
        if (!isAuthorized()) {
            return null;
        }
        Request<T, ? extends Request> request = OkGo.<T>post(url)
                .tag(tag)
                .params(params);
        request.execute(callback);
        return request;
    }

    /**
     * GET请求（兼容旧版本）
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 回调
     * @param tag      请求标签（用于取消请求）
     * @param <T>      响应数据类型
     * @return Request对象，可用于进一步配置
     */
    public <T> Request<T, ? extends Request> get(String url, Map<String, String> params,
                                                 JsonCallback<T> callback, Object tag) {
        if (!isAuthorized()) {
            return null;
        }
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
     *
     * @param tag 请求标签
     */
    public void cancelTag(Object tag) {
        if (!isAuthorized()) {
            return;
        }
        OkGo.getInstance().cancelTag(tag);
    }

    /**
     * 根据Tag取消请求（静态方法）
     *
     * @param client OkHttpClient实例
     * @param tag    请求标签
     */
    public static void cancelTag(OkHttpClient client, Object tag) {
        OkGo.cancelTag(client, tag);
    }

    /**
     * 取消所有请求
     */
    public void cancelAll() {
        if (!isAuthorized()) {
            return;
        }
        OkGo.getInstance().cancelAll();
    }

    /**
     * 取消所有请求（静态方法）
     *
     * @param client OkHttpClient实例
     */
    public static void cancelAll(OkHttpClient client) {
        OkGo.cancelAll(client);
    }

    // ==================== 兼容性方法 ====================

    /**
     * 取消指定标签的请求（兼容旧版本方法名）
     *
     * @param tag 请求标签
     */
    public void cancel(Object tag) {
        if (!isAuthorized()) {
            return;
        }
        cancelTag(tag);
    }

}

