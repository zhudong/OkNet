# 网络请求模块AAR打包说明

## 概述

`networklib` 模块已经配置完成，可以独立打包成AAR文件供其他项目使用。

## 快速打包

### 方法1：使用Gradle命令（推荐）

在项目根目录执行：

```bash
# Windows
gradlew :networklib:assembleRelease

# Linux/Mac
./gradlew :networklib:assembleRelease
```

生成的AAR文件位置：
```
networklib/build/outputs/aar/networklib-release.aar
```

### 方法2：在Android Studio中

1. 打开右侧的 `Gradle` 面板
2. 展开 `networklib` -> `Tasks` -> `build`
3. 双击 `assembleRelease`
4. AAR文件将生成在 `networklib/build/outputs/aar/` 目录

## 模块内容

AAR包含以下网络请求相关的类：

```
com.fyb.networklib
├── api/
│   ├── NetworkApi.java          # 核心网络请求API封装
│   └── TripManageApi.java       # 行程管理API示例
├── data/
│   └── BaseEntity.java          # 基础响应实体类
└── util/
    ├── JsonCallback.java        # JSON回调基类（支持Token注入）
    ├── JsonConvert.java         # JSON转换器
    ├── TokenProvider.java       # Token提供者接口
    ├── Convert.java             # JSON转换工具
    ├── LzyResponse.java         # 响应包装类
    └── SimpleResponse.java      # 简单响应类
```

## 在其他项目中使用AAR

### 步骤1：复制AAR文件

将生成的AAR文件复制到目标项目的 `libs` 目录：
```
your-project/app/libs/networklib-release.aar
```

### 步骤2：在build.gradle中添加依赖

在目标项目的 `app/build.gradle` 中添加：

```gradle
dependencies {
    // 添加AAR文件
    implementation files('libs/networklib-release.aar')
    
    // 必须添加以下依赖（AAR不包含这些依赖）
    implementation 'com.lzy.net:okgo:3.0.4'
    implementation 'com.lzy.net:okserver:2.0.5'
    implementation 'com.google.code.gson:gson:2.8.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
}
```

### 步骤3：同步项目

在Android Studio中点击 `Sync Now` 同步项目。

## 使用示例

### 1. 初始化（在Application中）

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化OkGo（必须）
        OkGo.getInstance().init(this);
        
        // 初始化NetworkApi，设置Token提供者
        NetworkApi.getInstance().setTokenProvider(new TokenProvider() {
            @Override
            public String getAccessToken() {
                // 返回你的访问令牌
                return AccountManager.getInstance().getAccessToken();
            }
        });
    }
}
```

### 2. 使用NetworkApi进行网络请求

```java
// POST请求 - JSON格式
String jsonBody = GsonUtils.toJson(yourBean);
NetworkApi.getInstance().postJson(
    "https://api.example.com/endpoint",
    jsonBody,
    new JsonCallback<BaseEntity<YourResponse>>() {
        @Override
        public void onSuccess(Response<BaseEntity<YourResponse>> response) {
            if (response.body().isSuccess()) {
                // 处理成功响应
                YourResponse data = response.body().getData();
            }
        }
        
        @Override
        public void onError(Response<BaseEntity<YourResponse>> response) {
            // 处理错误
        }
    },
    this  // tag，用于取消请求
);

// GET请求
Map<String, String> params = new HashMap<>();
params.put("key", "value");
NetworkApi.getInstance().get(
    "https://api.example.com/endpoint",
    params,
    new JsonCallback<BaseEntity<YourResponse>>() {
        @Override
        public void onSuccess(Response<BaseEntity<YourResponse>> response) {
            // 处理成功响应
        }
    },
    this
);
```

### 3. 取消请求

```java
// 取消指定tag的请求
NetworkApi.getInstance().cancel(this);

// 取消所有请求
NetworkApi.getInstance().cancelAll();
```

## 注意事项

1. **AAR不包含依赖**：生成的AAR只包含networklib模块的代码，不包含OkGo、Gson等依赖库。使用时需要单独添加这些依赖。

2. **必须初始化OkGo**：在使用NetworkApi之前，必须在Application中初始化OkGo。

3. **必须设置TokenProvider**：如果需要自动注入Token，必须设置TokenProvider。

4. **BaseEntity的success判断**：默认基于 `code == 2000`，可根据实际情况修改BaseEntity类。

5. **不修改原项目代码**：networklib模块是独立的，不会影响原项目的代码。

## 版本信息

- 模块名称：networklib
- 版本号：1.0.0
- 包名：com.fyb.networklib
- 最低SDK版本：24
- 目标SDK版本：34

## 依赖说明

使用AAR时需要添加以下依赖：

- `com.lzy.net:okgo:3.0.4` - OkGo网络请求库
- `com.lzy.net:okserver:2.0.5` - OkGo服务器库
- `com.google.code.gson:gson:2.8.1` - JSON解析库
- `androidx.appcompat:appcompat:1.6.1` - Android支持库

## 验证打包结果

打包完成后，可以：

1. 检查文件是否存在：`networklib/build/outputs/aar/networklib-release.aar`
2. 使用解压工具查看AAR内容，确认包含所需的类文件
3. 在测试项目中导入AAR，验证是否可以正常使用

