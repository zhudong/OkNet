package com.fyb.networklib.api;

/**
 * 许可证信息模型类
 */
public class LicenseInfo {
    private String key;
    private String name;
    private String expiry_date;
    private boolean is_expired;
    private int days_remaining;
    private int hours_remaining;
    private int minutes_remaining;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public boolean isIs_expired() {
        return is_expired;
    }

    public void setIs_expired(boolean is_expired) {
        this.is_expired = is_expired;
    }

    public int getDays_remaining() {
        return days_remaining;
    }

    public void setDays_remaining(int days_remaining) {
        this.days_remaining = days_remaining;
    }

    public int getHours_remaining() {
        return hours_remaining;
    }

    public void setHours_remaining(int hours_remaining) {
        this.hours_remaining = hours_remaining;
    }

    public int getMinutes_remaining() {
        return minutes_remaining;
    }

    public void setMinutes_remaining(int minutes_remaining) {
        this.minutes_remaining = minutes_remaining;
    }

    @Override
    public String toString() {
        return "LicenseInfo{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                ", is_expired=" + is_expired +
                ", days_remaining=" + days_remaining +
                ", hours_remaining=" + hours_remaining +
                ", minutes_remaining=" + minutes_remaining +
                '}';
    }
}
