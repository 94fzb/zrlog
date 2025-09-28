## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[中文](README.md) | [English](README.en-us.md)

ZrLog是使用 Java 开发的博客/CMS程序，具有简约，易用，组件化，内存占用低等特点。自带 Markdown
编辑器，让更多的精力放在写作上，而不是花费大量时间在学习程序的使用上。

### 程序主页

[https://www.zrlog.com](https://www.zrlog.com)

### 一图胜千言

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

### 特性

1. 提供日志，分类，标签，评论的管理
2. 支持插件模式 [如何编写一个插件](https://blog.zrlog.com/zrlog-plugin-dev.html)
3. 高度可定制的主题功能 [如何制作一套主题](https://blog.zrlog.com/make-theme-for-zrlog.html)
4. 支持第三方评论插件
5. 提供 `markdown` 富文本编辑器，基本上满足了管理员的编辑需求
6. 页面静态化，缓存公共数据，访问速度更快
7. 支持扩展第三方云存储（默认七牛）
8. 支持数据库定时备份
9. 在线更新升级
10. ...

### 快速开始

- 直接通过内嵌入容器的方式进行启动，找到 `com.zrlog.web.Application` 通过这个 `main()` 进行启动
- 通过 Maven 命令的方式进行启动（不依赖任何 IDE）
    - Windows 使用 `bin\mvn-run.cmd`
    - Unix 使用 `sh bin/mvn-run.sh`

### 程序安装

* 部署环境前提
    * JDK 版本 >= 21（若选择 GraalVM Native Image 包，可以不安装 JDK）
    * MySQL >= 5.7 或者 Cloudflare D1（webapi 方式访问）

* 数据初始化
    * 下载 [最新 zip](https://www.zrlog.com/download) 解压，运行 bin/start.sh 或者是 bin/start.bat
    * 访问 `http://host:port/install`
    * 填写数据库，管理员信息，完成安装

### 变更日志

[查看完整的ChangeLog](https://www.zrlog.com/changelog/index.html?ref=md)

### 示例网站

* 网址 [https://demo.zrlog.com](https://demo.zrlog.com)
* 管理地址 [admin/login](https://demo.zrlog.com/admin/login)
* 用户名 admin
* 密码 123456

### 获取帮助

* 微信号 hibegin
* 邮件 support@zrlog.com
* 对程序有任何问题，欢迎反馈 [https://blog.zrlog.com/feedback.html](https://blog.zrlog.com/feedback.html)

### 常见问题

#### docker模式下，输入正确的数据库信息，仍无法完成安装

- https://blog.zrlog.com/faq-collect.html

#### 其它问题

如何你遇到了一些问题，可以先去这里找下 [常见问题](https://blog.zrlog.com/faq-collect.html)

### 感谢

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)
* [Cloudflare](https://www.cloudflare.com)

### 协议

ZrLog is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
