## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[中文](README.md) | [English](README.en-us.md)

ZrLog is a lightweight blog/Content Management System (CMS) developed in Java. Featuring a componentized architecture, it delivers a low memory footprint and seamless deployment logic. With its built-in Markdown editor, ZrLog is dedicated to providing users with an efficient and focused writing environment.

### Homepage

[https://www.zrlog.com](https://www.zrlog.com)

### Previews

#### Article Detail Page

![](https://www.zrlog.com/assets/screenprint/post-detail.png?v=2)

#### Article Edit Page

![](https://www.zrlog.com/assets/screenprint/article-edit-light.png?v=2)

#### Article Edit Page [Dark Mode]

![](https://www.zrlog.com/assets/screenprint/article-edit-dark.png?v=2)

#### Article Edit Page [PWA Full Screen]

![](https://www.zrlog.com/assets/screenprint/article-edit-light-pwa-full-screen.png?v=2)

#### Article Edit Page [PWA Full Screen - Settings]

![](https://www.zrlog.com/assets/screenprint/article-edit-light-pwa-full-screen-setting.png?v=2)

### Features

- **Content Management**: Provides comprehensive management for articles (logs), categories, tags, and comments.
- **Extensibility**: Supports core functionality expansion through a plugin mechanism ([How to write a plugin](https://blog.zrlog.com/zrlog-plugin-dev.html)) and integrates with third-party comment services.
- **Theming**: Features a flexible theme system that supports custom page views ([How to create a theme](https://blog.zrlog.com/make-theme-for-zrlog.html)).
- **Editing Experience**: Includes a built-in Markdown rich text editor tailored for streamlined content creation.
- **Performance Optimization**: Supports static page generation and system-level caching for public data, significantly improving response speed.
- **Cloud Integration**: Supports connecting with third-party cloud object storage services (e.g. Qiniu Cloud natively out-of-the-box).
- **System Availability**: Provides automatic scheduled database backups and supports online system upgrades.

### Quick Start

You can quickly boot up the application using one of the following methods:

- **Embedded Container Start**: Directly run the `main()` method in the `com.zrlog.web.Application` class via an IDE.
- **Maven Command Execution** (IDE independent):
    - Windows: execute `bin\mvn-run.cmd`
    - Unix/Linux/macOS: execute `sh bin/mvn-run.sh`

### Installation & Deployment

* **Environment Requirements**
    * JDK Version: `>= 11` (Note: If using the GraalVM Native Image packaging, a built-in JDK environment may not be required for the runtime executable).
    * Database: MySQL `>= 5.7` or Cloudflare D1 (accessed via Web API).

* **Data Initialization**
    * Download the [latest zip package](https://www.zrlog.com/download) and unzip it.
    * Run `bin/start.sh` (Unix/macOS) or `bin/start.bat` (Windows) in the root directory.
    * Visit `http://host:port/install` in your browser.
    * Provide the database connection and administrator account settings as guided to complete the setup.

### ChangeLog

[View the full ChangeLog](https://www.zrlog.com/changelog/index.html?ref=md)

### Sample Website

* Demo Website: [https://demo.zrlog.com](https://demo.zrlog.com)
* Admin Login: [admin/login](https://demo.zrlog.com/admin/login)
* Username: admin
* Password: 123456

### Support & Help

* WeChat: hibegin
* QQ Group: 6399942
* Email Support: support@zrlog.com
* Feedback: We welcome discussions and feedback on our [Feedback Page](https://blog.zrlog.com/feedback.html) or via GitHub Issues.

### FAQ

#### Unable to complete installation with correct database info under Docker

- Please refer to the troubleshooting guide: [Relevant Summaries](https://blog.zrlog.com/faq-collect.html)

#### Other operational issues

If you encounter errors during deployment, please check the official [FAQ collection](https://blog.zrlog.com/faq-collect.html).

### Thanks

Special thanks to the following open source projects and services:

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)
* [Cloudflare](https://www.cloudflare.com)

### License

ZrLog is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
