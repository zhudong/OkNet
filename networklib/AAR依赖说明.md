# AAR依赖传递说明

## 核心问题

**AAR包是否包含其compile依赖？**

**答案：不包含！**

## AAR包内容

AAR包包含的内容：
- ✅ 编译后的class文件（networklib自己的代码）
- ✅ Android资源文件（如果有）
- ✅ AndroidManifest.xml
- ✅ proguard.txt（混淆规则）
- ✅ pom.xml（依赖声明）

AAR包不包含的内容：
- ❌ compile依赖的库文件（OkGo、Gson等）
- ❌ 传递依赖的库文件

## 为什么不包含依赖？

### 1. 避免依赖冲突
- 不同Library可能依赖相同库的不同版本
- 强制传递依赖可能导致版本冲突

### 2. 依赖管理原则
- 依赖管理应该由主项目控制
- Library只声明它需要什么依赖，不强制提供

### 3. Gradle设计哲学
- Gradle默认不传递compile依赖
- 需要传递依赖时应该使用`api`而不是`implementation`

## 使用networklib AAR的正确方式

### 错误的做法
```gradle
dependencies {
    // ❌ 只添加AAR，以为包含了所有依赖
    implementation files('libs/networklib-release.aar')
}
```

### 正确的做法
```gradle
dependencies {
    // ✅ 添加AAR
    implementation files('libs/networklib-release.aar')

    // ✅ 必须手动添加所有依赖
    implementation 'com.lzy.net:okgo:3.0.4'
    implementation 'com.lzy.net:okserver:2.0.5'
    implementation 'com.google.code.gson:gson:2.8.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
}
```

## 为什么Response对象不能用？

### 问题分析
```java
// ❌ 这行代码会编译错误
import com.lzy.okgo.model.Response; // 类找不到

public class MyCallback extends JsonCallback<BaseEntity<MyData>> {
    @Override
    public void onSuccess(Response<BaseEntity<MyData>> response) {
        // Response类不存在
    }
}
```

### 原因
1. networklib的JsonCallback中引用了`com.lzy.okgo.model.Response`
2. 但AAR不包含OkGo.jar，所以Response类不存在
3. 主项目必须添加OkGo依赖才能使用Response

### 解决方法
主项目添加OkGo依赖后，就可以正常使用：
```java
import com.lzy.okgo.model.Response; // ✅ 现在可以找到这个类

public class MyCallback extends JsonCallback<BaseEntity<MyData>> {
    @Override
    public void onSuccess(Response<BaseEntity<MyData>> response) {
        // ✅ Response类现在可用
        if (response.isSuccessful()) {
            BaseEntity<MyData> data = response.body();
        }
    }
}
```

## 如何让AAR包含依赖？

### 方法1：使用api依赖（不推荐）
```gradle
// networklib/build.gradle
dependencies {
    api 'com.lzy.net:okgo:3.0.4'  // 使用api而不是implementation
}
```
缺点：强制传递依赖，可能导致版本冲突。

### 方法2：打包成Fat AAR（复杂）
- 将所有依赖打包进AAR
- 需要自定义Gradle插件
- 不推荐，文件很大且容易冲突

### 方法3：提供完整依赖包（推荐）
- 提供AAR + 依赖说明文档
- 让使用者根据文档添加依赖
- 这是Android Library的标准做法

## 总结

1. **AAR不包含compile依赖**是正常现象
2. **使用AAR的项目必须手动添加所有依赖**
3. **Response等OkGo类需要OkGo依赖才能使用**
4. **这是Android Library的最佳实践**

## 验证方法

打包AAR后，可以检查：
1. AAR文件大小（如果包含依赖会很大）
2. 解压AAR查看classes.jar内容
3. 检查pom.xml中的依赖声明
4. 在测试项目中尝试使用，验证是否需要额外依赖
