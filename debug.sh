#!/bin/bash

# AiLove Android 真机调试快捷脚本

echo "🚀 AiLove Android 真机调试助手"
echo "================================"

# 检查参数
if [ "$1" == "help" ] || [ "$1" == "-h" ]; then
    echo "使用方法:"
    echo "  ./debug.sh              # 显示帮助信息"
    echo "  ./debug.sh connect      # 检查设备连接"
    echo "  ./debug.sh build        # 构建debug版本"
    echo "  ./debug.sh install      # 安装到设备"
    echo "  ./debug.sh run          # 构建并运行"
    echo "  ./debug.sh log          # 查看应用日志"
    echo "  ./debug.sh clean        # 清理项目"
    exit 0
fi

# 进入项目目录
cd "$(dirname "$0")"

case "$1" in
    "connect")
        echo "📱 检查设备连接..."
        adb devices
        ;;
    
    "build")
        echo "🔨 构建debug版本..."
        ./gradlew assembleDebug
        ;;
    
    "install")
        echo "📲 安装到设备..."
        ./gradlew installDebug
        ;;
    
    "run")
        echo "🏃‍♂️ 构建并运行应用..."
        ./gradlew clean assembleDebug installDebug
        echo "应用查看日志: ./debug.sh log"
        ;;
    
    "log")
        echo "📋 查看应用日志..."
        echo "按 Ctrl+C 退出日志查看"
        adb logcat | grep com.ailove.app
        ;;
    
    "clean")
        echo "🧹 清理项目..."
        ./gradlew clean
        ;;
    
    *)
        echo "❓ 未知命令: $1"
        echo "使用 './debug.sh help' 查看帮助信息"
        ;;
esac