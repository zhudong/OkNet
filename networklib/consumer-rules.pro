# Consumer ProGuard rules for NetworkLib
# These rules are applied when a project uses NetworkLib and enables ProGuard

# 进一步混淆NetworkLib的内部实现类（如果消费者项目启用了更激进的混淆）
-keep class com.fyb.networklib.api.NetworkApi { *; }
-keep class com.fyb.networklib.api.LicenseInfo { *; }
-keep class com.fyb.networklib.data.BaseEntity { *; }
-keep class com.fyb.networklib.util.JsonCallback { *; }
-keep class com.fyb.networklib.util.TokenProvider { *; }

# 保留Gson反序列化所需的字段名
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowobfuscation class com.fyb.networklib.api.LicenseInfo {
    <fields>;
}

-keepclassmembers,allowobfuscation class com.fyb.networklib.data.BaseEntity {
    <fields>;
}

