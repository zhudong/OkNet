package com.fyb.networklib.util;

/**
 * Token提供者接口
 * 用于从外部获取访问令牌
 */
public interface TokenProvider {
    /**
     * 获取访问令牌
     * @return 访问令牌，如果未登录或令牌无效，返回null或空字符串
     */
    String getAccessToken();
}

