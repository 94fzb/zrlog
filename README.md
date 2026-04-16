## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[中文](README.md) | [English](README.en-us.md)

ZrLog 是一款由 Java 开发的专业开源博客系统（CMS）。项目专为个人博主与内容创作者而生，摒弃繁琐配置，具备内存占用低、一键部署等工程特性。内置原生 Markdown 编辑器，且管理界面基于 React + Ant Design 构建，致力于为您提供既轻量又高效的写作空间。

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

1. **内容管理**: 提供完整的文章（日志）、分类、标签与评论管理功能。
2. **扩展性**: 支持通过插件机制扩展特定功能（[如何编写一个插件](https://blog.zrlog.com/zrlog-plugin-dev.html)），以及原生整合多平台第三方评论。
3. **主题化**: 拥有强大的主题系统，自由定制与切换博客外观（[如何制作一套主题](https://blog.zrlog.com/make-theme-for-zrlog.html)）。
4. **编辑体验**: 内置所见即所得的 Markdown 格式富文本编辑器，专注优质内容输出。
5. **性能优化**: 支持页面静态化与公共数据缓存，访问极速流畅。
6. **云化设施**: 原生支持扩容对接第三方云端存储（如七牛云）；完美适配底层 Serverless / D1 无服务器架构。
7. **高可用性**: 支持后台数据库定时自动备份机制，在线升级。

### 快速开始（针对开发者）

- 直接通过内嵌入容器的方式进行启动，在 IDE 中找到并运行 `com.zrlog.web.Application` 的 `main()` 方法进入直接调试。
- 通过 Maven 命令的方式进行构建启动（不依赖任何 IDE 环境）：
    - Windows 环境执行 `bin\mvn-run.cmd`
    - Unix平台执行 `sh bin/mvn-run.sh`

### 程序安装部署

* **环境前置要求**
    * JDK 版本： `>= 11`（注：若选择 GraalVM Native Image 发行包免装环境，则不受此限制）。
    * 数据库支持：`MySQL >= 5.7`，或者 `Cloudflare D1`（通过 webapi 方式代理访问）。

* **方式一：Docker 极速部署方案（主推特性）**
  抛弃繁复配置过程，只需一行命令直达服务：
  ```bash
  docker run -p 8080:8080 -v $(pwd)/conf:/opt/zrlog/conf zrlog/zrlog
  ```

* **方式二：常规包数据初始化**
    * 下载 [最新 zip 安装包](https://www.zrlog.com/download) 进行磁盘解压。
    * 在文件根目录运行引导进程脚本 `bin/start.sh`，或者在 Windows 中执行 `bin/start.bat`。
    * 然后访问本地网页 `http://host:port/install` 入口。
    * 在引导中填写数据库，管理员信息，即可完成最终安装。

### 变更日志

[查看完整的 ChangeLog](https://www.zrlog.com/changelog/index.html?ref=md)

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
- 由于容器内外隔离等机制约束，如有疑问可查网络挂载思路指引：[排查配置文档](https://blog.zrlog.com/faq-collect.html)。

#### 其它问题检索
针对业务异常与自定义卡点找寻解答 👉 [深入全量 FAQ](https://blog.zrlog.com/faq-collect.html)。

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

### 协议声明

ZrLog is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
