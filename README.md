## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[中文](README.md) | [English](README.en-us.md)

ZrLog 是一款基于 Java 的开源博客系统。它提供文章、分类、标签、评论、主题、插件、静态化和在线升级等功能，内置 Markdown 编辑器，管理界面基于 React 和 Ant Design 构建。

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

1. **内容管理**: 支持文章、分类、标签和评论管理。
2. **插件扩展**: 支持通过插件扩展功能（[如何编写一个插件](https://blog.zrlog.com/zrlog-plugin-dev.html)），也可以接入第三方评论服务。
3. **主题系统**: 支持自定义和切换博客主题（[如何制作一套主题](https://blog.zrlog.com/make-theme-for-zrlog.html)）。
4. **文章编辑**: 内置 Markdown 编辑器，支持常用写作和预览流程。
5. **访问优化**: 支持页面静态化和公共数据缓存。
6. **部署形态**: 支持 Docker、Zip、War、Native Image 和 Serverless / D1 相关部署方式。
7. **运维能力**: 支持数据库定时备份和在线升级。

### 快速开始（开发者启动）

- 在 IDE 中运行 `com.zrlog.web.Application` 的 `main()` 方法进行调试。
- 通过 Maven 脚本启动（不依赖 IDE）：
    - Windows 环境执行 `bin\mvn-run.cmd`
    - Unix 平台执行 `sh bin/mvn-run.sh`

### 程序安装与部署

- **快速体验：Docker**

  使用容器启动服务，并持久化配置目录：
  ```bash
  docker run -p 8080:8080 -v $(pwd)/conf:/opt/zrlog/conf zrlog/zrlog
  ```
  启动后访问 `http://localhost:8080/install`。安装页会检查配置目录、数据库连接和安装锁文件，并在安装过程中显示实际执行进度。

- **正式部署：Docker / Native Image / Zip / WAR**

  - JDK 版本：`>= 11`（Native Image 发行包免装 JDK）。
  - 数据库支持：`MySQL >= 5.7`，或者 `Cloudflare D1`（通过 Web API 方式代理访问）。
  - Zip 包下载后解压，在目录中运行 `bin/start.sh`，Windows 使用 `bin/start.bat`。
  - WAR 包适合已有 Tomcat / Jetty 容器的部署流程。
  - Docker 部署请确认 `/opt/zrlog/conf` 已挂载到持久化目录；`db.properties` 和 `install.lock` 会写入该目录。
  - 安装完成后进入后台开始创建第一篇文章。

- **开发者启动**

  本地调试优先使用 IDE 运行 `com.zrlog.web.Application`，或使用上方 Maven 脚本启动。

### 变更日志

[查看完整的变更日志](https://www.zrlog.com/changelog/index.html?ref=md)

### 示例网站

* 演示站点: [https://demo.zrlog.com](https://demo.zrlog.com)
* 后台管理地址: [admin/login](https://demo.zrlog.com/admin/login)
* 演示账号: admin
* 演示密码: 123456

### 获得支持与帮助

* 微信号: hibegin
* 邮件支持: support@zrlog.com
* 缺陷反馈与建议: 欢迎在 [反馈页面](https://blog.zrlog.com/feedback.html) 或 GitHub Issues 中提交

### 常见问题

#### Docker 模式下，输入正确的数据库信息，仍无法完成安装
- 先检查容器网络、数据库地址和挂载目录。更多排查步骤见：[排查配置文档](https://blog.zrlog.com/faq-collect.html)。

#### 其他问题检索
更多问题可查看：[FAQ](https://blog.zrlog.com/faq-collect.html)。

### 鸣谢

感谢以下优秀开源项目及服务的支持：

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [JetBrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)
* [Cloudflare](https://www.cloudflare.com)

### 协议声明

ZrLog 是基于 [Apache 2.0 协议](https://www.apache.org/licenses/LICENSE-2.0.html) 发布的开源软件。
