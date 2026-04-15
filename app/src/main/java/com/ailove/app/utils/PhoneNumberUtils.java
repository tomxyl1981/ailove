package com.ailove.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

/**
 * 手机号码获取工具类
 * 支持多种方式获取本机号码
 */
public class PhoneNumberUtils {
    private static final String TAG = "PhoneNumberUtils";
    
    /**
     * 获取本机手机号码（尝试多种方式）
     * @param context 上下文
     * @return 手机号码，获取失败返回null
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context context) {
        String phoneNumber = null;
        
        // 方式1: 通过TelephonyManager获取
        phoneNumber = getPhoneNumberFromTelephony(context);
        if (!TextUtils.isEmpty(phoneNumber)) {
            Log.d(TAG, "通过TelephonyManager获取到号码: " + phoneNumber);
            return formatPhoneNumber(phoneNumber);
        }
        
        // 方式2: Android 6.0+ 双卡支持
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            phoneNumber = getPhoneNumberFromSubscription(context);
            if (!TextUtils.isEmpty(phoneNumber)) {
                Log.d(TAG, "通过SubscriptionManager获取到号码: " + phoneNumber);
                return formatPhoneNumber(phoneNumber);
            }
        }
        
        Log.w(TAG, "无法获取本机号码");
        return null;
    }
    
    /**
     * 从TelephonyManager获取号码
     */
    @SuppressLint("MissingPermission")
    private static String getPhoneNumberFromTelephony(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) 
                context.getSystemService(Context.TELEPHONY_SERVICE);
            
            if (telephonyManager == null) {
                return null;
            }
            
            // 检查权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "缺少READ_PHONE_STATE权限");
                return null;
            }
            
            String phoneNumber = telephonyManager.getLine1Number();
            
            // 处理可能的空值或无效值
            if (TextUtils.isEmpty(phoneNumber) || "null".equals(phoneNumber)) {
                return null;
            }
            
            // 移除+86前缀等
            return cleanPhoneNumber(phoneNumber);
            
        } catch (Exception e) {
            Log.e(TAG, "通过TelephonyManager获取号码失败", e);
            return null;
        }
    }
    
    /**
     * 从SubscriptionManager获取号码（Android 6.0+双卡支持）
     */
    @SuppressLint("MissingPermission")
    private static String getPhoneNumberFromSubscription(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return null;
        }
        
        try {
            SubscriptionManager subscriptionManager = 
                (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            
            if (subscriptionManager == null) {
                return null;
            }
            
            // 检查权限
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "缺少READ_PHONE_STATE权限");
                return null;
            }
            
            List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptionInfos != null) {
                for (SubscriptionInfo info : subscriptionInfos) {
                    String phoneNumber = info.getNumber();
                    if (!TextUtils.isEmpty(phoneNumber) && !"null".equals(phoneNumber)) {
                        return cleanPhoneNumber(phoneNumber);
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "通过SubscriptionManager获取号码失败", e);
        }
        
        return null;
    }
    
    /**
     * 清理电话号码格式
     */
    private static String cleanPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }
        
        // 移除空格、横线等分隔符
        phoneNumber = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");
        
        // 处理国际格式
        if (phoneNumber.startsWith("+86")) {
            phoneNumber = phoneNumber.substring(3);
        } else if (phoneNumber.startsWith("86") && phoneNumber.length() > 10) {
            phoneNumber = phoneNumber.substring(2);
        }
        
        // 验证是否为有效的中国手机号码
        if (isValidChinesePhoneNumber(phoneNumber)) {
            return phoneNumber;
        }
        
        return null;
    }
    
    /**
     * 格式化显示手机号码（隐藏中间四位）
     */
    public static String formatDisplayNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return phoneNumber;
        }
        
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }
    
    /**
     * 验证是否为中国手机号码
     */
    private static boolean isValidChinesePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        
        // 中国手机号码通常是11位数字
        if (phoneNumber.length() != 11) {
            return false;
        }
        
        // 检查是否全为数字
        if (!phoneNumber.matches("^\\d+$")) {
            return false;
        }
        
        // 检查开头是否符合中国运营商号段
        String prefix = phoneNumber.substring(0, 3);
        String[] validPrefixes = {
            "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", // 移动
            "145", "147", "149", // 移动虚拟
            "150", "151", "152", "157", "158", "159", // 移动
            "165", "166", "167", // 虚拟运营商
            "170", "171", "172", "173", "175", "176", "177", "178", // 虚拟运营商
            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", // 各运营商
            "190", "191", "192", "193", "195", "196", "197", "198", "199" // 新号段
        };
        
        for (String validPrefix : validPrefixes) {
            if (prefix.equals(validPrefix)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 标准化手机号码格式
     */
    private static String formatPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        
        // 确保是11位数字
        if (phoneNumber.length() == 11 && phoneNumber.matches("^\\d+$")) {
            return phoneNumber;
        }
        
        return phoneNumber;
    }
    
    /**
     * 检查是否具备获取手机号码的权限
     */
    public static boolean hasPhonePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) 
               == PackageManager.PERMISSION_GRANTED;
    }
}