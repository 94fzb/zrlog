## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/java-build-preview-package-zip.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/java-build-release-package-zip.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[Chinese](README.md) | [English](README.en-us.md)

ZrLog is an open-source blog system built with Java. It provides article, category, tag, comment, theme, plugin, static page generation, and online update features. It includes a Markdown editor, and its admin UI is built with React and Ant Design.

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

1. **Content Management**: Manage articles, categories, tags, and comments.
2. **Plugin Extensions**: Extend features through plugins ([How to write a plugin](https://blog.zrlog.com/zrlog-plugin-dev.html)) and connect third-party comment services.
3. **Themes**: Customize and switch blog themes ([How to create a custom theme](https://blog.zrlog.com/make-theme-for-zrlog.html)).
4. **Article Editing**: Use the built-in Markdown editor for common writing and preview workflows.
5. **Access Optimization**: Generate static pages and cache shared public data.
6. **Deployment Options**: Run with Docker, Zip, War, Native Image, or Serverless / D1 deployments.
7. **Operations**: Use scheduled database backups and online updates.

### Quick Start (Developer Startup)

- Run `com.zrlog.web.Application`'s `main()` method from your IDE for local debugging.
- Start with Maven scripts when not using an IDE:
    - Windows users execute `bin\mvn-run.cmd`
    - Unix/macOS execute `sh bin/mvn-run.sh`

### Installation & Deployment

- **Quick trial: Docker**

  Start the service and persist the config directory:
  ```bash
  docker run -p 8080:8080 -v $(pwd)/conf:/opt/zrlog/conf zrlog/zrlog
  ```
  Then visit `http://localhost:8080/install`. The installer checks config paths, database connectivity, and the install lock, then shows installation progress while it runs.

- **Production deployment: Docker / Native Image / Zip / WAR**

  - JDK: `>= 11` (not required when using the Native Image release package).
  - Database: `MySQL >= 5.7` or `Cloudflare D1` through Web API access.
  - For Zip packages, download the [latest package](https://www.zrlog.com/download), extract it, and run `bin/start.sh` or `bin/start.bat` on Windows.
  - WAR packages are for existing Tomcat / Jetty containers.
  - For Docker, make sure `/opt/zrlog/conf` is mounted to persistent storage; `db.properties` and `install.lock` are written there.
  - After installation, enter admin and create your first article.

- **Developer startup**

  Use your IDE to run `com.zrlog.web.Application`, or use the Maven scripts above.

### Changelog

[View the complete changelog](https://www.zrlog.com/changelog/index.html?ref=md)

### Demo

* Website: [https://demo.zrlog.com](https://demo.zrlog.com)
* Admin: [admin/login](https://demo.zrlog.com/admin/login)
* Username: admin
* Password: 123456

### Support

* WeChat: hibegin
* Email: support@zrlog.com
* Feedback and bug reports: [feedback page](https://blog.zrlog.com/feedback.html) or GitHub Issues.

### FAQ

#### Docker setup still fails after entering the correct database information
- Check container networking, the database address, and mounted directories first. More troubleshooting steps are available in the [FAQ](https://blog.zrlog.com/faq-collect.html).

#### Other questions
See the [FAQ](https://blog.zrlog.com/faq-collect.html).

### Open-Source Acknowledgments

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [JetBrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)
* [Cloudflare](https://www.cloudflare.com)

### License

ZrLog is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
