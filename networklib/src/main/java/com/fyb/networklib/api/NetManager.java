package com.fyb.networklib.api;

import android.app.Application;
import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class NetManager {

    private static NetManager instance;

    private NetManager() {
        // Private constructor
    }

    public static NetManager getInstance() {
        if (instance == null) {
            synchronized (NetManager.class) {
                if (instance == null) {
                    instance = new NetManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context instanceof Application) {
            initOkGo((Application) context);
        } else {
            throw new IllegalArgumentException("Context must be Application instance");
        }
    }

    private void initOkGo(Application application) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);

        // Global timeout
        builder.readTimeout(10000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(10000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);

        // Cookie management
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(application)));

        OkGo.getInstance().init(application)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.NO_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);
    }
}