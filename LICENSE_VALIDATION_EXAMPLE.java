/**
 * NetworkLib 许可证验证使用示例
 */
public class LicenseValidationExample {

    // 许可证服务器接口返回示例
    private static final String LICENSE_RESPONSE_EXAMPLE = "{\n" +
            "    \"key\": \"ABC123\",\n" +
            "    \"name\": \"测试许可证1\",\n" +
            "    \"expiry_date\": \"2026-12-31\",\n" +
            "    \"is_expired\": false,\n" +
            "    \"days_remaining\": 371,\n" +
            "    \"hours_remaining\": 13,\n" +
            "    \"minutes_remaining\": 4\n" +
            "}";

    /**
     * Application 初始化示例
     */
    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            try {
                // 使用许可证密钥初始化NetworkApi
                // 这会自动验证许可证并初始化OkGo
                NetworkApi.getInstance().init(this, "ABC123");
                Log.i("MyApp", "NetworkApi initialized successfully");

                // 检查许可证信息
                LicenseInfo license = NetworkApi.getInstance().getLicenseInfo();
                if (license != null) {
                    Log.i("License", "许可证名称: " + license.getName());
                    Log.i("License", "到期时间: " + license.getExpiry_date());
                    Log.i("License", "剩余天数: " + license.getDays_remaining());
                }

            } catch (RuntimeException e) {
                // 许可证验证失败
                Log.e("MyApp", "许可证验证失败: " + e.getMessage());
                // 在这里处理许可证过期的情况
                // 例如显示警告对话框、禁用网络功能等
                handleLicenseExpired();
            }
        }

        private void handleLicenseExpired() {
            // 处理许可证过期的情况
            Log.w("MyApp", "许可证已过期，NetworkApi功能被禁用");

            // 可以在这里：
            // 1. 显示用户友好的错误提示
            // 2. 引导用户更新许可证
            // 3. 限制应用功能
            // 4. 发送错误报告等
        }
    }

    /**
     * 网络请求使用示例
     */
    public class NetworkExample {

        /**
         * 正常使用NetworkApi进行网络请求
         * （需要先在Application中成功初始化）
         */
        public void makeNetworkRequest() {
            try {
                // 所有NetworkApi方法都会自动检查授权状态
                NetworkApi.post("https://api.example.com/users")
                    .upJson("{\"name\":\"张三\",\"age\":25}")
                    .execute(new JsonCallback<UserResponse>() {
                        @Override
                        public void onSuccess(Response<UserResponse> response) {
                            Log.i("Network", "请求成功: " + response.body());
                        }

                        @Override
                        public void onError(Response<UserResponse> response) {
                            Log.e("Network", "请求失败: " + response.getException().getMessage());
                        }
                    });

            } catch (RuntimeException e) {
                // 如果许可证过期，这里会抛出异常
                Log.e("Network", "网络请求失败: " + e.getMessage());
                // 处理无授权的情况
            }
        }

        /**
         * 配置NetworkApi（需要授权）
         */
        public void configureNetworkApi() {
            try {
                NetworkApi.getInstance()
                    .setRetryCount(5)
                    .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                    .addCommonHeaders(new HttpHeaders());

            } catch (RuntimeException e) {
                Log.e("Config", "配置失败: " + e.getMessage());
            }
        }
    }

    /**
     * 许可证服务器接口示例
     * 这是一个模拟的服务器接口实现
     */
    public class LicenseServer {

        // 模拟许可证数据库
        private static final Map<String, LicenseInfo> LICENSE_DATABASE = new HashMap<>();

        static {
            // 初始化测试许可证
            LicenseInfo license = new LicenseInfo();
            license.setKey("ABC123");
            license.setName("测试许可证1");
            license.setExpiry_date("2026-12-31");
            license.setIs_expired(false);
            license.setDays_remaining(371);
            license.setHours_remaining(13);
            license.setMinutes_remaining(4);
            LICENSE_DATABASE.put("ABC123", license);
        }

        /**
         * 许可证验证接口
         * GET /license/{LICENSE_KEY}
         * 例如：GET /license/ABC123
         */
        public LicenseInfo validateLicense(String key) {
            LicenseInfo license = LICENSE_DATABASE.get(key);
            if (license == null) {
                throw new RuntimeException("Invalid license key");
            }

            // 检查是否过期（实际项目中应该基于当前日期检查）
            // 这里为了演示，假设许可证未过期

            return license;
        }
    }
}
