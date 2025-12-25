# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ============= NetworkLib 混淆配置 =============

# 保留NetworkLib的主要API类 - 只保留public方法和字段，不保留实现细节
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

# 保留枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

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
