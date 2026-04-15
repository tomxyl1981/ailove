# Android Studio 配置指南

## 项目概述
这是一个完整的 Android 真机调试项目，已配置好所有必要的文件以支持在 Android Studio 中开发和调试。

## 在 Android Studio 中打开项目

### 1. 打开项目
- 启动 Android Studio
- 选择 "Open an existing Android Studio project"
- 导航到 `/Users/xiaoyao/Desktop/AiLove/AiLove_Android` 目录
- 点击 "Open"

### 2. 等待项目同步
- Android Studio 会自动开始 Gradle 同步
- 等待同步完成（可能需要几分钟时间下载依赖）

### 3. 配置 SDK 和 JDK
如果提示缺少 SDK：
- 点击提示中的 "Install missing SDK package(s)"
- 或者手动配置：
  - File → Project Structure → SDK Location
  - 设置 Android SDK 路径
  - 确保 JDK 版本为 17 或更高

## 运行项目

### 方法一：使用运行配置（推荐）
1. 确保 USB 调试已开启的 Android 设备已连接
2. 在工具栏中选择 "app" 运行配置
3. 点击绿色运行按钮 ▶️

### 方法二：右键运行
1. 在项目视图中找到 `app/java/com.ailove.app/MainActivity.java`
2. 右键点击文件 → Run 'MainActivity'

### 方法三：使用 Gradle 任务
1. 打开右侧 Gradle 面板
2. 展开 Tasks → install
3. 双击 `installDebug`

## 调试功能

### 断点调试
- 在代码行号左侧点击设置断点
- 使用 Debug 模式运行（🐞 按钮）
- 支持变量监视、调用栈查看

### 实时日志查看
- View → Tool Windows → Logcat
- 选择连接的设备
- 过滤日志标签：`com.ailove.app`

### 性能监控
- Run → Profile 'app'
- 查看 CPU、内存、网络使用情况

## 常见问题解决

### 1. Gradle 同步失败
```
解决方案：
- 检查网络连接
- File → Invalidate Caches and Restart
- 删除 .gradle 文件夹重新同步
```

### 2. 设备未识别
```
解决方案：
- 确认手机已开启开发者选项和 USB 调试
- 重新插拔 USB 数据线
- Terminal 中运行：adb devices
```

### 3. 编译错误
```
解决方案：
- Build → Clean Project
- Build → Rebuild Project
- 检查 Java 版本兼容性
```

## 项目结构
```
AiLove_Android/
├── app/                    # 主应用模块
│   ├── src/main/
│   │   ├── java/          # Java 源代码
│   │   ├── res/           # 资源文件
│   │   └── AndroidManifest.xml
├── .idea/                 # Android Studio 配置
├── gradle/               # Gradle 包装器
└── build.gradle         # 构建配置文件
```

## 快捷键
- ⌘+R (Mac) / Ctrl+R (Windows): 运行应用
- ⌘+D (Mac) / Ctrl+D (Windows): 调试应用
- ⌘+F9 (Mac) / Ctrl+F9 (Windows): 编译项目
- ⌘+6 (Mac) / Alt+6 (Windows): 打开 Logcat

现在你的项目已经完全配置好，可以在 Android Studio 中正常开发和调试了！