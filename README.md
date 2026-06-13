# NekoClock

> 一个基于 Jetpack Compose 开发的、支持高度自定义的 Android 全屏横屏桌面时钟。

## ✨ 项目亮点

- **沉浸式设计**：专为横屏使用优化，自动进入沉浸模式（隐藏系统栏），适合将闲置安卓设备作为桌面摆件。
- **高度个性化**：
  - **字体与样式**：支持 Monospace、Serif、SansSerif 等多种字体样式，可自由调节文字大小。
  - **色彩特效**：内置全色域颜色选择器，支持为时间文字添加**霓虹发光**（Glow）特效。
  - **背景美化**：支持内置壁纸、自定义本地图片背景，并支持 Android 12+ 的**毛玻璃**（Blur）背景效果。
- **实用功能**：
  - **防烧屏保护**：支持像素级微调位移，有效保护 OLED 屏幕。
  - **环境适应**：支持在应用内直接调节屏幕亮度，支持显示实时电量及充电状态。
  - **极简交互**：支持隐藏设置图标，通过点击右上角区域唤出设置，保持界面整洁。
- **发布级质量**：
  - **标准架构**：逻辑解耦，MainActivity 仅负责系统级配置，UI 逻辑由专用入口管理。
  - **国际化支持**：所有字符串均提取至资源文件，易于扩展多语言。
  - **性能优化**：采用 DataStore 异步存储，UI 响应流畅。

## 📸 功能截图

*(待补充：在此处添加应用截图)*

## 📦 安装与运行

本项目为 Android 工程，建议使用 **Android Studio Ladybug (2024.2.1)** 或更高版本开发。

1. **克隆仓库**：
   ```bash
   git clone https://github.com/qkxqh/NekoClock.git
   ```
2. **导入项目**：
   打开 Android Studio，选择 `Open` 并定位到项目根目录。
3. **运行**：
   连接 Android 设备（建议 API 31+，Target SDK 35），点击 `Run 'app'`。

## 🚀 快速上手

- **启动**：应用启动后默认进入全屏时钟界面。
- **进入设置**：
  - 默认点击右上角的 **⚙** 图标。
  - 若开启了“隐藏设置图标”，请点击屏幕右上角 1/4 区域即可唤出设置界面。
- **自定义壁纸**：在设置页点击壁纸列表末尾的 **+** 号，即可选择手机相册中的图片作为时钟背景。

## 📁 项目结构

```text
app/src/main/java/com/gkxqh/nekoclock/
├── MainActivity.kt          # 入口 Activity，负责系统权限与沉浸模式配置
├── data/
│   └── SettingsManager.kt   # 使用 DataStore 管理用户偏好设置
└── ui/
    ├── ClockApp.kt          # 应用 UI 总入口，处理导航、预测性返回与全局状态
    ├── components/          # 规范化的通用 UI 组件（支持资源国际化）
    ├── screens/
    │   ├── ClockScreen.kt   # 核心时钟显示逻辑（包含动画与防烧屏）
    │   └── SettingsScreen.kt# 丰富的设置项与颜色选择器
    └── theme/               # 统一的颜色（Color.kt）、字体（Type.kt）与主题定义
```

## 🛠️ 技术栈

- **UI 框架**：[Jetpack Compose](https://developer.android.com/jetpack/compose)
- **图片加载**：[Coil](https://github.com/coil-kt/coil)
- **数据存储**：[Jetpack DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore)
- **核心组件**：Material 3, Predictive Back Handler, Blur/Glow Effects

## 🤝 贡献

如果你有任何建议或发现了 Bug，欢迎提交 Issue 或 Pull Request。

## 📝 License

本项目采用 [MIT License](LICENSE) 开源。
