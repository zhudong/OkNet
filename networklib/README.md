# NetworkLib - 网络请求封装库

这是一个封装了OkGo网络请求功能的Android Library模块，可以独立打包成AAR文件供其他项目使用。

## 功能特性

- **完全封装OkGo**：提供OkGo所有方法的统一接口，无需直接依赖OkGo
- **许可证授权验证**：初始化时自动验证许可证，确保功能安全可用
- **一键初始化**：在Application中传入许可证密钥完成所有初始化
- **完整的HTTP方法支持**：GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, TRACE
- **灵活的配置选项**：支持自定义OkHttpClient、缓存、重试、全局参数等
- **请求取消功能**：支持按标签取消请求或取消所有请求
- **代码混淆支持**：提供完整的ProGuard混淆配置
- **传递依赖**：核心依赖自动传递，无需手动添加

## 模块结构

```
networklib/
├── src/main/java/com/fyb/networklib/
│   ├── api/
│   │   ├── NetworkApi.java          # 核心网络请求API
│   │   └── TripManageApi.java       # 行程管理API示例
│   ├── data/
│   │   └── BaseEntity.java          # 基础响应实体
│   └── util/
│       ├── JsonCallback.java        # JSON回调基类
│       ├── JsonConvert.java         # JSON转换器
│       ├── TokenProvider.java       # Token提供者接口
│       ├── Convert.java             # JSON转换工具
│       ├── LzyResponse.java         # 响应包装类
│       └── SimpleResponse.java      # 简单响应类
└── build.gradle
```

## 使用方法

### 1. 在Application中初始化

```java
public class MyAPP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // 初始化NetworkApi并验证许可证
            // 许可证密钥需要从服务器获取或硬编码
            NetworkApi.getInstance()
                .setLicenseServerUrl("http://192.168.7.171:8000/license/") // 设置许可证服务器地址
                .init(this, "ABC123");
        } catch (RuntimeException e) {
            // 许可证验证失败，NetworkApi功能将被禁用
            Log.e("MyApp", "License validation failed", e);
            // 可以在这里处理许可证过期的情况
        }
    }
}
```

**许可证验证说明：**
- 初始化时会自动调用许可证服务器接口验证许可证
- 默认服务器地址：`http://127.0.0.1:8000/license/`
- 可以通过 `setLicenseServerUrl()` 方法自定义服务器地址
- 例如：传入密钥 "ABC123" 时，会调用 `{LICENSE_SERVER_URL}ABC123`
- 如果许可证过期或验证失败，所有NetworkApi功能将被禁用
- 验证失败时会抛出 `RuntimeException`

**许可证信息查询：**
```java
// 获取许可证信息
LicenseInfo license = NetworkApi.getInstance().getLicenseInfo();
if (license != null) {
    Log.i("License", "过期时间: " + license.getExpiry_date());
    Log.i("License", "剩余天数: " + license.getDays_remaining());
}

// 检查授权状态
boolean isAuthorized = NetworkApi.getInstance().isAuthorized();
```

### 2. 使用NetworkApi进行网络请求

NetworkApi完全封装了OkGo的所有功能，提供简洁统一的接口：

#### 基础HTTP请求
```java
// GET请求
NetworkApi.get("https://api.example.com/users")
    .params("page", "1")
    .execute(new JsonCallback<UserResponse>() {
        @Override
        public void onSuccess(Response<UserResponse> response) {
            // 处理成功响应
        }
    });

// POST请求 - JSON格式
NetworkApi.post("https://api.example.com/users")
    .upJson("{\"name\":\"张三\",\"age\":25}")
    .execute(new JsonCallback<UserResponse>() {
        @Override
        public void onSuccess(Response<UserResponse> response) {
            // 处理成功响应
        }
    });

// POST请求 - 表单参数
NetworkApi.post("https://api.example.com/login")
    .params("username", "user")
    .params("password", "pass")
    .execute(new JsonCallback<LoginResponse>() {
        @Override
        public void onSuccess(Response<LoginResponse> response) {
            // 处理成功响应
        }
    });
```

#### 其他HTTP方法
```java
// PUT请求
NetworkApi.put("https://api.example.com/users/1")
    .upJson("{\"name\":\"李四\"}")
    .execute(callback);

// DELETE请求
NetworkApi.delete("https://api.example.com/users/1")
    .execute(callback);

// HEAD请求
NetworkApi.head("https://api.example.com/status")
    .execute(callback);

// OPTIONS请求
NetworkApi.options("https://api.example.com/cors")
    .execute(callback);

// PATCH请求
NetworkApi.patch("https://api.example.com/users/1")
    .upJson("{\"email\":\"new@email.com\"}")
    .execute(callback);

// TRACE请求
NetworkApi.trace("https://api.example.com/debug")
    .execute(callback);
```

#### 高级配置
```java
try {
    // 自定义OkHttpClient
    OkHttpClient customClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build();

    NetworkApi.getInstance()
        .setOkHttpClient(customClient)
        .setRetryCount(5)
        .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
        .setCacheTime(3600000); // 1小时

    // 添加全局参数和请求头
    HttpParams commonParams = new HttpParams();
    commonParams.put("app_version", "1.0.0");
    commonParams.put("platform", "android");

    HttpHeaders commonHeaders = new HttpHeaders();
    commonHeaders.put("Authorization", "Bearer token");
    commonHeaders.put("User-Agent", "NetworkLib/1.0");

    NetworkApi.getInstance()
        .addCommonParams(commonParams)
        .addCommonHeaders(commonHeaders);

} catch (RuntimeException e) {
    // 许可证过期时配置方法会抛出异常
    Log.e("Config", "配置失败: " + e.getMessage());
}
```

#### 许可证状态查询
```java
// 检查授权状态
boolean isAuthorized = NetworkApi.getInstance().isAuthorized();

// 获取许可证详细信息
LicenseInfo license = NetworkApi.getInstance().getLicenseInfo();
if (license != null) {
    Log.i("License", "许可证: " + license.getName());
    Log.i("License", "到期时间: " + license.getExpiry_date());
    Log.i("License", "剩余天数: " + license.getDays_remaining());

    if (license.getDays_remaining() < 30) {
        // 许可证即将过期，提醒用户
        showLicenseWarning(license.getDays_remaining());
    }
}

private void showLicenseWarning(int daysRemaining) {
    // 显示许可证到期警告
    Log.w("License", "许可证还有 " + daysRemaining + " 天到期");
}
```

### 3. 使用业务API（如TripManageApi）

```java
TripManageApi tripManageApi = new TripManageApi(
    NetworkApi.getInstance(), 
    "https://your-api-base-url.com/"
);

TripManageAddBean bean = new TripManageAddBean(...);
tripManageApi.addTripManage(
    bean,
    new JsonCallback<BaseEntity<TripManageAddResponse>>() {
        @Override
        public void onSuccess(Response<BaseEntity<TripManageAddResponse>> response) {
            // 处理成功响应
        }
    },
    this
);
```

### 4. 取消请求

```java
// 取消指定tag的请求
NetworkApi.getInstance().cancelTag(this);

// 取消所有请求
NetworkApi.getInstance().cancelAll();

// 使用静态方法取消（需要OkHttpClient实例）
NetworkApi.cancelTag(okHttpClient, this);
NetworkApi.cancelAll(okHttpClient);
```

## 打包成AAR（推荐）

### 方法1：使用Gradle命令（推荐）

在项目根目录执行：

```bash
# Windows
gradlew :networklib:assembleRelease

# Linux/Mac
./gradlew :networklib:assembleRelease
```

生成的AAR文件位于：
```
networklib/build/outputs/aar/networklib-release.aar
```

### 方法2：在Android Studio中

1. 打开右侧的 `Gradle` 面板
2. 展开 `networklib` -> `Tasks` -> `build`
3. 双击 `assembleRelease`
4. AAR文件将生成在 `networklib/build/outputs/aar/` 目录

**AAR的优势**：
- 可以包含Android资源文件
- 可以包含AndroidManifest.xml
- 更适合Android Library项目
- 依赖管理更清晰

### 打包成JAR（可选）

如果需要JAR格式，可以执行：

```bash
# Windows
gradlew :networklib:jarRelease

# Linux/Mac
./gradlew :networklib:jarRelease
```

生成的JAR文件位于：
```
networklib/build/libs/networklib-1.0.0-release.jar
```

**注意**：JAR只包含类文件，不包含依赖库。使用时需要单独添加依赖。

## JitPack 集成（推荐）

NetworkLib 现在支持通过 JitPack 发布，可以直接在其他项目中引用，无需手动下载 AAR 文件。

### 添加 JitPack 仓库

在项目的根 `build.gradle` 或 `settings.gradle` 中添加 JitPack 仓库：

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

或在 `build.gradle` 中：

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 添加依赖

在模块的 `build.gradle` 中添加：

```gradle
dependencies {
    // 只需要这一行，所有传递依赖都会自动包含
    implementation 'com.github.zhudong:OkNet:1.0.0'
}
```

**重要**：NetworkLib 使用 `api` 配置，核心依赖（OkGo、Gson）会自动传递，无需手动添加！

### 版本说明

- 使用 GitHub 上发布的版本标签（Tag）作为版本号
- 例如：`1.0.0`, `1.1.0`, `v1.0.0` 等
- JitPack 会自动为每个 Tag 构建对应的版本

### 发布新版本

1. 在 GitHub 上创建新的版本标签（Tag）
2. JitPack 会自动检测并构建
3. 构建完成后就可以在其他项目中使用新版本

## 在其他项目中使用

### 使用AAR包（手动集成）

1. 将AAR文件复制到项目的 `libs` 目录
2. 在 `build.gradle` 中添加：

```gradle
dependencies {
    // 只需要这一行，所有依赖都会自动传递
    implementation files('libs/networklib-release.aar')
}
```

**重要**：networklib使用`api`配置，核心依赖（OkGo、Gson）会自动传递，无需手动添加！

### 使用JAR包（包含所有依赖）

如果需要JAR格式：

```bash
gradlew :networklib:fatJar
```

然后使用：
```gradle
dependencies {
    // JAR包含所有依赖，无需额外配置
    implementation files('libs/networklib-fat-1.0.0-all.jar')
}
```

### 使用Maven本地仓库

1. 将AAR发布到本地Maven仓库
2. 在项目的 `build.gradle` 中添加：

```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.fyb:networklib:1.0.0'
}
```

## 依赖说明

NetworkLib采用`api`配置声明核心依赖，确保所有依赖都会自动传递给使用此库的项目：

### 自动传递的依赖
- `com.lzy.net:okgo:3.0.4` - OkGo网络请求库
- `com.lzy.net:okserver:2.0.5` - OkGo服务器库
- `com.google.code.gson:gson:2.8.1` - JSON解析库

### 内部依赖（不传递）
- `androidx.appcompat:appcompat:1.6.1` - Android支持库（implementation配置）

**优势**：使用NetworkLib的项目无需手动添加OkGo、Gson等依赖，大大简化了依赖管理。

## 代码混淆

NetworkLib 已启用代码混淆（`minifyEnabled true`），并提供了完整的 ProGuard 混淆配置。

### 混淆配置内容

NetworkLib 的 `proguard-rules.pro` 文件包含以下配置：

#### OkGo 相关配置
```proguard
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# OkGo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}
```

#### NetworkLib 自身配置
```proguard
# 保留NetworkLib的主要API类 - 只保留public方法和字段
-keep class com.fyb.networklib.api.NetworkApi {
    public <methods>;
    public <fields>;
}
-keep class com.fyb.networklib.api.LicenseInfo {
    public <methods>;
    public <fields>;
}

# 保留NetworkLib工具类 - 只保留必要的方法签名
-keep class com.fyb.networklib.util.JsonCallback {
    public <init>(...);
    public <methods>;
}
-keep class com.fyb.networklib.util.TokenProvider {
    public <methods>;
}

# 保留LicenseInfo的字段名（用于Gson反序列化）
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowobfuscation class com.fyb.networklib.api.LicenseInfo {
    <fields>;
}
```

#### Consumer配置（consumer-rules.pro）
```proguard
# Consumer ProGuard rules for NetworkLib
# 这些规则会被使用NetworkLib的项目继承
-keep class com.fyb.networklib.api.NetworkApi { *; }
-keep class com.fyb.networklib.api.LicenseInfo { *; }
-keep class com.fyb.networklib.data.BaseEntity { *; }
```

#### Gson 配置
```proguard
# Gson specific classes used by OkGo
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Gson
-keep class com.google.gson.** {*;}
-dontwarn com.google.gson.**
```

### 使用说明

### 源码保护策略

NetworkLib 采用**商业级源码保护策略**，在保证API可用性的同时最大限度保护源码：

#### 已实施的商业保护措施

##### ✅ 移除Sources JAR发布
- **不发布源码包**：Maven仓库中不包含`souces.jar`文件
- **保护效果**：使用者无法在IDE中查看任何源码文件
- **优势**：完全隐藏实现细节，防止商业代码泄露

##### ✅ 深度代码混淆 + 重构
- **API签名保留**：保留所有public方法签名，保证开发体验
- **实现深度混淆**：内部方法名被替换为无意义字符
- **包结构重构**：使用`-repackageclasses`重新组织包结构
- **字符串加密**：敏感字符串（如服务器URL）使用十六进制编码保护
- **多层优化**：5次优化遍历，最大化混淆效果

##### ✅ 选择性保留策略
- **保留**：核心API类（NetworkApi, LicenseInfo）、数据模型（BaseEntity）
- **混淆**：所有内部实现类和方法
- **保护**：敏感业务逻辑和算法实现

#### 保护效果对比

| 保护等级 | API可用性 | 源码可见性 | 开发体验 | 商业保护 |
|---------|----------|-----------|---------|---------|
| 标准开源 | ✅ | ✅ | ⭐⭐⭐⭐⭐ | 🔓 |
| 基础混淆 | ✅ | ⚠️ (混淆) | ⭐⭐⭐⭐ | 🔒 |
| **商业保护** | ✅ | ❌ | ⭐⭐⭐ | 🔒🔒🔒 |

#### 使用体验

使用者现在会看到：
- ✅ **完整的API提示**：`NetworkApi.get()`, `NetworkApi.post()`等方法
- ✅ **清晰的文档**：所有方法的参数、返回值和注释信息
- ❌ **无源码查看**：无法在IDE中查看NetworkLib的任何实现代码
- ✅ **正常编译运行**：所有功能正常工作，无任何功能影响

### 使用说明

由于 NetworkLib 已启用混淆，使用此库的项目无需额外配置 ProGuard 规则，NetworkLib 会自动处理所有必要的混淆配置。

如果您的项目也需要自定义混淆规则，请参考 `networklib/proguard-rules.pro` 和 `consumer-rules.pro` 文件中的配置。

## 注意事项

1. **初始化与授权**：必须在Application中调用`NetworkApi.getInstance().init(this, "LICENSE_KEY")`，内部会自动初始化OkGo并验证许可证
2. **许可证要求**：需要有效的许可证密钥，过期许可证会导致所有功能不可用
3. **网络连接**：许可证验证需要网络连接到许可证服务器（默认 `http://127.0.0.1:8000/license/`）
4. **Token处理**：如果需要Token验证，可以在请求时手动添加headers或使用自定义回调
5. **请求标签**：tag用于取消请求，建议使用Activity或Fragment实例作为tag
6. **响应处理**：BaseEntity的success判断基于code == 2000，可根据实际情况修改
7. **线程安全**：所有NetworkApi方法都是线程安全的

## 扩展

可以根据业务需求创建更多的API类，参考 `TripManageApi` 的实现方式。

