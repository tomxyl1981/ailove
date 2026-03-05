package com.ailove.app.utils;

/**
 * 数据验证工具类
 */
public class Validator {
    
    /**
     * 验证手机号码格式
     * @param mobile 手机号码
     * @return 是否为有效手机号码
     */
    public static boolean isMobile(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            return false;
        }
        
        // 中国大陆手机号码正则表达式
        String regex = "^1[3-9]\\d{9}$";
        return mobile.matches(regex);
    }
}