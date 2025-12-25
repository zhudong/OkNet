# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ============= NetworkLib 混淆配置 =============

# 商业保护：保留API但深度混淆实现
-keep class com.fyb.networklib.api.NetworkApi {
    public static <methods>;
    public <init>();
    public <methods>;
    public <fields>;
}

# 混淆私有方法和内部实现
-allowaccessmodification
-repackageclasses 'com.fyb.networklib.api'
-keep class com.fyb.networklib.api.LicenseInfo {
    public <methods>;
    public <fields>;
}

# 保留NetworkLib数据模型类（商业保护：保留API但混淆实现）
-keep class com.fyb.networklib.data.BaseEntity { *; }
-keep class com.fyb.networklib.data.** { *; }

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

# 保留枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 商业保护：深度混淆配置
-optimizationpasses 5
-overloadaggressively
-repackageclasses 'com.fyb.networklib'

# 移除调试信息
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# 混淆字符串常量（可选，会影响性能）
#-obfuscateStrings

# ============= OkGo 混淆配置 =============

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# OkGo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}

# Gson specific classes used by OkGo
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Gson
-keep class com.google.gson.** {*;}
-dontwarn com.google.gson.**

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ============= 通用混淆配置 =============

# 保留所有实现Serializable接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留所有实现Parcelable接口的类成员
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 保留所有JNI调用的方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留所有枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留自定义View的构造方法
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
