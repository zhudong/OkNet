# 如何生成JAR包

## 方法1：使用Gradle任务生成JAR（推荐）

### 生成Release版本的JAR

在项目根目录执行：

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

### 生成Debug版本的JAR

```bash
# Windows
gradlew :networklib:jarDebug

# Linux/Mac
./gradlew :networklib:jarDebug
```

### 生成包含所有依赖的Fat JAR（不推荐，文件会很大）

```bash
# Windows
gradlew :networklib:fatJarRelease

# Linux/Mac
./gradlew :networklib:fatJarRelease
```

## 方法2：在Android Studio中生成

1. 打开右侧的 `Gradle` 面板
2. 展开 `networklib` -> `Tasks` -> `other`
3. 双击 `jarRelease` 或 `jarDebug`
4. JAR文件将生成在 `networklib/build/libs/` 目录

## 注意事项

1. **生成的JAR不包含依赖**：生成的JAR只包含networklib模块的类文件，不包含OkGo、Gson等依赖库。使用时需要单独添加这些依赖。

2. **使用JAR时需要添加依赖**：
   ```gradle
   dependencies {
       // 添加networklib的JAR
       implementation files('libs/networklib-1.0.0-release.jar')
       
       // 必须添加以下依赖
       implementation 'com.lzy.net:okgo:3.0.4'
       implementation 'com.lzy.net:okserver:2.0.5'
       implementation 'com.google.code.gson:gson:2.8.1'
       implementation 'androidx.appcompat:appcompat:1.6.1'
   }
   ```

3. **推荐使用AAR而不是JAR**：对于Android项目，建议使用AAR格式，因为：
   - AAR可以包含Android资源文件
   - AAR可以包含AndroidManifest.xml
   - AAR更适合Android Library

   生成AAR：
   ```bash
   gradlew :networklib:assembleRelease
   ```
   生成的AAR位于：`networklib/build/outputs/aar/networklib-release.aar`

## JAR文件结构

生成的JAR包含以下包结构：
```
com/
   fyb/
    networklib/
      api/
        NetworkApi.class
        TripManageApi.class
      data/
        BaseEntity.class
      util/
        JsonCallback.class
        JsonConvert.class
        TokenProvider.class
        Convert.class
        LzyResponse.class
        SimpleResponse.class
```

## 在其他项目中使用JAR

1. 将JAR文件复制到项目的 `libs` 目录
2. 在 `build.gradle` 中添加：
   ```gradle
   dependencies {
       implementation files('libs/networklib-1.0.0-release.jar')
       
       // 添加必需的依赖
       implementation 'com.lzy.net:okgo:3.0.4'
       implementation 'com.lzy.net:okserver:2.0.5'
       implementation 'com.google.code.gson:gson:2.8.1'
       implementation 'androidx.appcompat:appcompat:1.6.1'
   }
   ```
3. 同步项目

