## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[中文](README.md) | [English](README.en-us.md)

ZrLog 是一款基于 Java 开发的博客/内容管理系统（CMS）。项目采用组件化设计，具备内存占用低、易于部署等工程特性，并内置 Markdown 编辑器，致力于为用户提供高效、专注的写作环境。

### 程序主页

[https://www.zrlog.com](https://www.zrlog.com)

### 界面预览

#### 文章详情页

![](https://www.zrlog.com/assets/screenprint/post-detail.png?v=2)

#### 文章编辑页

![](https://www.zrlog.com/assets/screenprint/article-edit-light.png?v=2)

#### 文章编辑页【暗黑模式】

![](https://www.zrlog.com/assets/screenprint/article-edit-dark.png?v=2)

#### 文章编辑页【PWA全屏】

![](https://www.zrlog.com/assets/screenprint/article-edit-light-pwa-full-screen.png?v=2)

#### 文章编辑页【PWA全屏-打开设置抽屉】

![](https://www.zrlog.com/assets/screenprint/article-edit-light-pwa-full-screen-setting.png?v=2)

### 功能特性

- **内容管理**: 提供完整的文章（日志）、分类、标签与评论管理功能。
- **扩展性**: 支持通过插件机制扩展核心功能（[如何编写一个插件](https://blog.zrlog.com/zrlog-plugin-dev.html)），以及整合第三方评论服务。
- **主题化**: 具备灵活的主题系统，支持自定义页面视图（[如何制作一套主题](https://blog.zrlog.com/make-theme-for-zrlog.html)）。
- **编辑体验**: 内置针对管理员优化的 Markdown 格式富文本编辑器。
- **性能优化**: 支持页面静态化处理，并对公共数据进行系统级缓存，提升响应速度。
- **云端集成**: 支持对接第三方云存储对象服务（默认集成七牛云）。
- **系统可用性**: 提供数据库自动定时备份功能，支持系统在线升级操作。

### 快速开始

可以通过以下方式之一快速启动工程：

- **内嵌容器启动**: 直接通过 IDE 找到并运行 `com.zrlog.web.Application` 类中的 `main()` 方法。
- **Maven 命令环境启动** (不依赖特定 IDE):
    - Windows 系统请执行: `bin\mvn-run.cmd`
    - Unix/Linux/macOS 系统请执行: `sh bin/mvn-run.sh`

### 程序安装部署

* **环境要求**
    * JDK 环境：JDK >= 11 (注：若选用构建版 GraalVM Native Image 原生打包方式，可不依赖运行环境系统内置 JDK)。
    * 数据库：MySQL >= 5.7 或 Cloudflare D1（由 webapi 方式访问）。

* **数据初始化**
    * 获取 [最新的 zip 安装包](https://www.zrlog.com/download) 并解压。
    * 在根目录运行 `bin/start.sh` (Unix/macOS 平台) 或 `bin/start.bat` (Windows 平台)。
    * 启动后，通过浏览器访问 `http://host:port/install`。
    * 根据引导页面输入相应的数据库连接信息和管理员账号配置，即可完成安装。

### 变更日志

[查看完整的 ChangeLog](https://www.zrlog.com/changelog/index.html?ref=md)

### 示例展示

* 演示站点: [https://demo.zrlog.com](https://demo.zrlog.com)
* 后台管理地址: [admin/login](https://demo.zrlog.com/admin/login)
* 演示账号: admin
* 演示密码: 123456

### 获得支持与帮助

* 微信号: hibegin
* 邮件支持: support@zrlog.com
* 缺陷反馈与建议: 欢迎在 [反馈页面](https://blog.zrlog.com/feedback.html) 或 GitHub Issues 中提交

### 常见问题

#### Docker 模式下，正确的数据库信息仍无法完成安装

- 参考排查方案：[相关使用总结](https://blog.zrlog.com/faq-collect.html)

#### 其他运行问题

如果在部署或使用时遇到异常，建议先查阅官方 [常见问题整理](https://blog.zrlog.com/faq-collect.html)。

### 鸣谢

感谢以下优秀开源项目及服务的支持：

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)
* [Cloudflare](https://www.cloudflare.com)

### 许可证

ZrLog 作为开源软件，遵循 [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html) 协议发布。
