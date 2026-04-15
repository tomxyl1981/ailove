# 真实手机号码获取功能说明

## 📱 功能概述

本功能实现了多种方式获取用户真实手机号码的能力，包括系统API获取、运营商一键登录等方案。

## 🔧 技术实现

### 1. 权限配置
已在 `AndroidManifest.xml` 中添加必要权限：
```xml
<!-- 获取手机号码相关权限 -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.RECEIVE_SMS" />
```

### 2. 核心工具类

#### PhoneNumberUtils.java
提供基础的手机号码获取和验证功能：
- `getPhoneNumber()` - 获取本机号码（多方式尝试）
- `formatDisplayNumber()` - 格式化显示号码（隐藏中间四位）
- `isValidChinesePhoneNumber()` - 验证中国手机号码格式
- `hasPhonePermission()` - 检查权限状态

#### OperatorLoginUtils.java
提供运营商一键登录功能：
- `getOperatorType()` - 识别当前运营商
- `isSupportQuickLogin()` - 检查是否支持一键登录
- `getPhoneNumber()` - 通过运营商获取号码（模拟实现）

### 3. 获取策略

#### 第一层：系统API获取
```java
String phoneNumber = PhoneNumberUtils.getPhoneNumber(context);
```
- 使用 `TelephonyManager.getLine1Number()`
- 支持Android 6.0+双卡场景
- 自动清理和验证号码格式

#### 第二层：运营商一键登录
```java
OperatorLoginUtils.getPhoneNumber(context, callback);
```
- 识别中国移动/联通/电信
- 模拟运营商SDK调用（实际项目需集成真实SDK）
- 提供fallback机制

#### 第三层：默认号码
```java
phoneNumber = "13888888888"; // 备用测试号码
```

## 🎯 使用示例

### PhoneLoginActivity 集成示例

```java
// 1. 权限检查和请求
private void requestPhonePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    } else {
        loadPhoneNumber();
    }
}

// 2. 加载真实手机号码
private void loadPhoneNumber() {
    phoneNumber = PhoneNumberUtils.getPhoneNumber(this);
    
    if (phoneNumber != null && !phoneNumber.isEmpty()) {
        String displayNumber = PhoneNumberUtils.formatDisplayNumber(phoneNumber);
        displayPhoneNumber(displayNumber);
        Toast.makeText(this, "已获取本机号码", Toast.LENGTH_SHORT).show();
    } else {
        loadPhoneNumberViaOperator(); // 尝试运营商方式
    }
}

// 3. 运营商方式获取
private void loadPhoneNumberViaOperator() {
    OperatorLoginUtils.getPhoneNumber(this, new OperatorLoginUtils.PhoneNumberCallback() {
        @Override
        public void onSuccess(String number) {
            phoneNumber = number;
            String displayNumber = PhoneNumberUtils.formatDisplayNumber(number);
            displayPhoneNumber(displayNumber);
        }
        
        @Override
        public void onError(String error) {
            useDefaultPhoneNumber(); // 使用默认号码
        }
    });
}
```

## 📊 运营商支持

### 已支持的运营商
- ✅ 中国移动 (China Mobile)
- ✅ 中国联通 (China Unicom)  
- ✅ 中国电信 (China Telecom)

### 运营商识别代码
```java
switch (operator) {
    case "46000": case "46002": case "46007": case "46020":
        return OPERATOR_CHINA_MOBILE;  // 中国移动
    case "46001": case "46006": case "46009":
        return OPERATOR_CHINA_UNICOM;   // 中国联通
    case "46003": case "46005": case "46011":
        return OPERATOR_CHINA_TELECOM;  // 中国电信
}
```

## ⚠️ 注意事项

### 权限要求
1. **READ_PHONE_STATE** - 必需权限，用于获取本机号码
2. **Android 6.0+** - 需要动态申请权限
3. **用户授权** - 用户可以选择拒绝权限

### 限制条件
1. **SIM卡状态** - 需要插入有效SIM卡
2. **运营商支持** - 不同运营商支持程度不同
3. **系统版本** - Android 6.0以上支持双卡API
4. **网络环境** - 运营商一键登录需要网络连接

### fallback机制
当无法获取真实号码时：
1. 显示友好提示信息
2. 提供默认测试号码
3. 允许用户手动输入号码
4. 记录失败原因便于分析

## 🔧 实际部署建议

### 1. 集成真实运营商SDK
```java
// 中国移动认证服务SDK示例
// ChinaMobileAuthHelper.getLoginToken(context, listener);

// 中国联通统一认证SDK示例  
// ChinaUnicomAuthHelper.getToken(context, callback);

// 中国电信天翼账号SDK示例
// ChinaTelecomAuthHelper.getAccessToken(context, listener);
```

### 2. 服务器端配合
```java
// 验证运营商返回的token
// ApiClient.verifyOperatorToken(token, phoneNumber, callback);

// 获取最终的access_token用于业务接口
// ApiClient.getAccessToken(operatorToken, callback);
```

### 3. 错误处理优化
```java
// 记录获取失败的具体原因
Log.e(TAG, "获取手机号失败: " + errorType + ", 原因: " + errorMessage);

// 根据不同错误类型给出相应提示
switch(errorType) {
    case ERROR_NO_SIM:
        showNoSimCardDialog();
        break;
    case ERROR_PERMISSION_DENIED:
        showPermissionDeniedDialog();
        break;
    case ERROR_NETWORK:
        showNetworkErrorDialog();
        break;
}
```

## 📈 性能监控

### 关键指标
- 权限获取成功率
- 号码获取成功率  
- 平均获取耗时
- 各运营商成功率对比
- fallback使用频率

### 监控建议
```java
// 记录获取过程的关键时间点
long startTime = System.currentTimeMillis();
// ... 获取过程 ...
long endTime = System.currentTimeMillis();
long duration = endTime - startTime;

// 上报统计数据
Analytics.trackPhoneNumberAcquisition(
    "success", 
    operatorType, 
    duration, 
    methodUsed
);
```

## 🛡️ 安全考虑

### 数据保护
- 号码仅在必要时获取和使用
- 避免在日志中打印完整号码
- 使用加密存储敏感信息
- 遵循隐私政策要求

### 用户体验
- 清晰说明获取号码的目的
- 提供明确的权限请求说明
- 尊重用户的选择权
- 提供替代登录方式

---

**注意**: 此实现提供了完整的框架和模拟逻辑，实际生产环境中需要集成各运营商的官方SDK以获得最佳效果。