# NetworkLib 发布指南

本指南介绍如何将 NetworkLib 发布到 GitHub 并通过 JitPack 分发。

## 前置要求

1. **GitHub 仓库**: 确保项目已经推送到公开的 GitHub 仓库
2. **Git**: 确保本地有 Git 环境
3. **版本标签**: 为发布做准备

## 发布步骤

### 1. 提交代码到 GitHub

确保所有更改已提交并推送到 GitHub：

```bash
git add .
git commit -m "Release version 1.0.0"
git push origin main
```

### 2. 创建版本标签 (Tag)

为发布创建 Git 标签：

```bash
# 创建带注释的标签
git tag -a v1.0.0 -m "Release version 1.0.0"

# 推送标签到 GitHub
git push origin v1.0.0
```

### 3. 验证 JitPack 构建

1. 访问 [JitPack](https://jitpack.io)
2. 搜索你的仓库: `zhudong/OkNet`
3. 点击你刚创建的标签 (v1.0.0)
4. 等待 JitPack 完成构建

### 4. 在其他项目中使用

一旦 JitPack 构建成功，你就可以在其他项目中使用：

```gradle
// 在 settings.gradle 或根 build.gradle 中添加 JitPack 仓库
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

// 在模块的 build.gradle 中添加依赖
dependencies {
    implementation 'com.github.zhudong:OkNet:v1.0.0'
}
```

## 传递依赖说明

NetworkLib 使用 `api` 配置声明核心依赖，这意味着：

- `com.lzy.net:okgo:3.0.4` - 网络请求库
- `com.lzy.net:okserver:2.0.5` - OkGo 服务库
- `com.google.code.gson:gson:2.8.1` - JSON 解析库

这些依赖会自动传递给使用 NetworkLib 的项目，无需手动添加。

## 版本管理

- **主版本**: 用于重大功能更新 (1.0.0, 2.0.0)
- **次版本**: 用于新功能添加 (1.1.0, 1.2.0)
- **补丁版本**: 用于 bug 修复 (1.0.1, 1.0.2)
- **预发布版本**: 用于测试版本 (1.0.0-beta, 1.0.0-rc1)

## 故障排除

### JitPack 构建失败

1. 检查 `jitpack.yml` 配置
2. 确保 `build.gradle` 配置正确
3. 查看 JitPack 的构建日志获取详细错误信息

### 依赖无法传递

1. 确认使用的是 `api` 而不是 `implementation`
2. 检查 AAR 文件是否正确生成
3. 在测试项目中清理缓存: `./gradlew clean`

### 常见问题

**Q: 为什么 JitPack 显示 "Building" 状态很久？**
A: JitPack 构建可能需要几分钟到几十分钟，首次构建通常需要更长时间。

**Q: 如何更新已发布的版本？**
A: 创建新的标签并推送，JitPack 会自动为新标签构建。

**Q: 可以删除已发布的版本吗？**
A: 不可以删除已发布的标签，但可以创建新的标签来覆盖。

## 本地测试

在发布前，可以本地测试发布配置：

```bash
# 构建 AAR
./gradlew :networklib:assembleRelease

# 本地发布测试
./gradlew :networklib:publishToMavenLocal

# 查看发布信息
./gradlew :networklib:printPublishingInfo
```

## 相关文件

- `networklib/build.gradle` - 库的构建配置
- `jitpack.yml` - JitPack 构建配置
- `networklib/README.md` - 使用说明文档
