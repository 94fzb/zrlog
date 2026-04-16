## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[Chinese](README.md) | [English](README.en-us.md)

ZrLog is a professional open-source blog system (CMS) developed in Java. Built specifically for content creation, it eliminates tedious configuration processes, offering a low memory footprint and one-click deployment capabilities. It features an embedded native Markdown editor and a modern admin panel powered by React + Ant Design to provide a lightweight and highly efficient writing workspace.

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

1. **Content Management**: Comprehensive handling for logs, categories, tags, and visitor comments.
2. **Extensibility**: Support plugin expansion mechanism ([How to write a plugin](https://blog.zrlog.com/zrlog-plugin-dev.html)) and seamless integration with third-party comment widgets.
3. **Theming Customization**: Highly adaptable theme system granting complete layout control ([How to create a custom theme](https://blog.zrlog.com/make-theme-for-zrlog.html)).
4. **Editing Experience**: Rich-text Markdown capabilities natively providing a clean environment tailored for premium content writing.
5. **Performance & Speed**: Enhanced built-in static page generation combined with shared data cashing logic.
6. **Cloud & Serverless Ready**: Smooth interactions built around third-party cloud data persistence (e.g. Qiniu natively) alongside extreme Serverless / D1 DB adaptabilities.
7. **System Dependability**: Silent online patching support with synchronized scheduled database backup measures.

### Quick Start (For Developers & Testing)

- Embed and initiate within your IDE by targeting `com.zrlog.web.Application`'s `main()` runtime context directly.
- Standard framework-independent invocation via Maven scripts:
    - Windows users execute `bin\mvn-run.cmd`
    - Unix/macOS execute `sh bin/mvn-run.sh`

### Installation & Deployment

* **Deployment Environment Qualifications**
    * JDK Setup: `v11` or onward (Notice: Avoid setting up an isolated JDK container if exploiting the GraalVM Native Image release variants).
    * Backend Databases Supported: `MySQL >= 5.7` or `Cloudflare D1` (Operated over Serverless-friendly Web APIs).

* **Method 1: Immediate Spin-up Using Docker (Highly Recommended)**
  Deploy without underlying ecosystem hurdles via the streamlined standard container command:
  ```bash
  docker run -p 8080:8080 -v $(pwd)/conf:/opt/zrlog/conf zrlog/zrlog
  ```

* **Method 2: Static Zip Extraction Sequence**
    * Download the [Latest Zip Compilation](https://www.zrlog.com/download) and decompress it.
    * Proceed to execute `bin/start.sh` (or `bin/start.bat` for Windows) mapped at the main repository depth.
    * Visit your instance at `http://host:port/install`.
    * Accomplish the configuration wizard sequence by embedding respective database profiles.

### ChangeLog

[View the complete ChangeLog iteration here](https://www.zrlog.com/changelog/index.html?ref=md)

### Sample Demo Interfaces

* Website Live Endpoint: [http://demo.zrlog.com](https://demo.zrlog.com)
* Administration Node: [admin/login](https://demo.zrlog.com/admin/login)
* Provided Username: admin / Password: 123456

### Inquiries & Support Contexts

* Official WeChat Node: hibegin
* Corporate/HelpDesk Emails: support@zrlog.com
* Feedback Tracker & Concerns submission: [Jump to our Bug Reports Feed](https://blog.zrlog.com/feedback.html).

### FAQ Guidelines

#### Docker Instance rejects database properties upon setup?
- Consider reviewing standard host-volume connections. Troubleshooting outlines available here: [FAQ References](https://blog.zrlog.com/faq-collect.html).

#### Peripheral operational roadblocks
Resolve distinct errors and look up functional bypasses directly via: [Categorized Platform FAQS](https://blog.zrlog.com/faq-collect.html).

### Open-Source Acknowledgments

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)
* [Cloudflare](https://www.cloudflare.com)

### License Agreements

ZrLog is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
