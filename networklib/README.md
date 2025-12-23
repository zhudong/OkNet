# NetworkLib - 网络请求封装库

这是一个封装了OkGo网络请求功能的Android Library模块，可以独立打包成AAR文件供其他项目使用。

## 功能特性

- 封装OkGo网络请求，提供统一的API接口
- 支持Token自动注入
- 支持JSON格式的POST/GET请求
- 提供回调接口，方便处理请求结果
- 支持请求取消功能

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
        
        // 初始化OkGo（必须）
        OkGo.getInstance().init(this);
        
        // 初始化网络请求API，设置Token提供者
        NetworkApi.getInstance().setTokenProvider(new TokenProvider() {
            @Override
            public String getAccessToken() {
                return AccountManager.getInstance().getAccessToken();
            }
        });
    }
}
```

### 2. 使用NetworkApi进行网络请求

```java
// POST请求 - JSON格式
NetworkApi.getInstance().postJson(
    url,
    jsonBody,
    new JsonCallback<BaseEntity<YourResponse>>() {
        @Override
        public void onSuccess(Response<BaseEntity<YourResponse>> response) {
            // 处理成功响应
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
    url,
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
NetworkApi.getInstance().cancel(this);

// 取消所有请求
NetworkApi.getInstance().cancelAll();
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

本模块依赖以下库：

- `com.lzy.net:okgo:3.0.4` - OkGo网络请求库
- `com.lzy.net:okserver:2.0.5` - OkGo服务器库
- `com.google.code.gson:gson:2.8.1` - JSON解析库
- `androidx.appcompat:appcompat:1.6.1` - Android支持库

## 注意事项

1. 使用前必须初始化OkGo（在Application中）
2. 必须设置TokenProvider以支持自动Token注入
3. 请求的tag用于取消请求，建议使用Activity或Fragment实例
4. BaseEntity的success判断基于code == 2000，可根据实际情况修改

## 扩展

可以根据业务需求创建更多的API类，参考 `TripManageApi` 的实现方式。

