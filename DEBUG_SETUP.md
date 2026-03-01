# AiLove Android 真机调试配置指南

## 配置已完成的内容

### 1. 构建配置优化
- 已在 `app/build.gradle` 中添加了完整的调试配置
- 启用了 debug 构建类型
- 添加了测试依赖项
- 配置了调试签名信息

### 2. 权限配置增强
- 在 `AndroidManifest.xml` 中添加了调试所需权限
- 启用了应用的调试模式
- 为所有 Activity 添加了调试标志

## 真机调试步骤

### 第一步：设备准备
1. 使用 USB 数据线连接 Android 手机到电脑
2. 在手机上开启开发者选项：
   - 设置 → 关于手机 → 连续点击"版本号"7次
3. 开启 USB 调试：
   - 设置 → 开发者选项 → USB 调试（开启）

### 第二步：验证设备连接
在终端中执行以下命令检查设备连接状态：

```bash
cd /Users/xiaoyao/Desktop/AiLove/AiLove_Android
./gradlew devices
```

或者使用 adb 命令：
```bash
adb devices
```

正常情况下会显示类似输出：
```
List of devices attached
XXXXXXXXXXXXXXX    device
```

### 第三步：构建并安装调试版本
```bash
# 清理项目
./gradlew clean

# 构建 debug 版本
./gradlew assembleDebug

# 安装到连接的设备
./gradlew installDebug
```

### 第四步：启动调试会话
```bash
# 直接运行并调试
./gradlew installDebug run
```

或者在 Android Studio 中：
1. 选择 "Run" → "Run 'app'"
2. 选择连接的真机设备
3. 点击运行按钮

## 常见问题解决

### 1. 设备未识别
- 检查 USB 连接是否正常
- 尝试更换 USB 接口或数据线
- 在手机上确认 USB 调试授权对话框

### 2. 权限被拒绝
- 确保手机已开启开发者选项和 USB 调试
- 检查电脑是否已安装对应手机品牌的 USB 驱动

### 3. 安装失败
```bash
# 卸载旧版本后重新安装
adb uninstall com.ailove.app
./gradlew installDebug
```

### 4. 查看调试日志
```bash
# 实时查看应用日志
adb logcat | grep com.ailove.app

# 或者查看所有日志
adb logcat
```

## 调试技巧

### 1. 断点调试
- 在 Android Studio 中设置断点
- 使用 Debug 模式运行应用
- 可以查看变量值、调用栈等信息

### 2. 网络调试
- 应用已配置允许明文传输（cleartextTraffic）
- 可以使用 Charles Proxy 等工具进行网络抓包

### 3. 性能监控
```bash
# 查看内存使用情况
adb shell dumpsys meminfo com.ailove.app

# 查看CPU使用情况
adb shell top -p $(adb shell pidof com.ailove.app)
```

## 快速调试命令

```bash
# 一键清理、构建、安装
./gradlew clean assembleDebug installDebug

# 强制停止应用
adb shell am force-stop com.ailove.app

# 启动应用
adb shell am start -n com.ailove.app/.ui.activity.SplashActivity

# 查看应用进程
adb shell ps | grep ailove
```

配置完成！现在你可以开始愉快地进行真机调试了！ 🚀