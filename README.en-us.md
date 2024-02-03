## ZrLog ![build-preview](https://github.com/94fzb/zrlog/actions/workflows/build-preview-packge.yml/badge.svg) ![build-release](https://github.com/94fzb/zrlog/actions/workflows/build-release-packge.yml/badge.svg) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[Chinese](README.md) | [English](README.en-us.md)

ZrLog is a blog/CMS program developed in Java. It is simple, easy to use, componentized, and has low memory footprint.
Bring your own Markdown editor and let more focus on writing, rather than spending a lot of time learning the use of the
program.

### Homepage

[https://www.zrlog.com](https://www.zrlog.com)

### Pictures

![](https://www.zrlog.com/assets/screenprint/post-detail.png)

![](https://www.zrlog.com/assets/screenprint/article-edit.png)

### Features

1. Provide management of logs, categories, tags, and comments
2. Support plugin mode [How to write a plugin](https://blog.zrlog.com/zrlog-plugin-dev)
3. Highly customizable theme features [How to make a set of themes](https://blog.zrlog.com/make-theme-for-zrlog)
4. Support third party comment plugin
5. Provide `editormd` mainstream rich text editor, basically meet the editing needs of administrators
6. Page static, cache public data, faster access
7. Support for extending third-party cloud storage (default seven cattle)
8. Support database scheduled backup
9. Online update upgrade<br/>
10. ...

### Quick start

- Start directly by embedding tomcat and find `com.zrlog.web.Application` to start with this `main()`
- Start with Maven commands (without relying on any IDE)
    - Windows uses `bin\mvn-run.cmd`
    - Unix uses `sh bin/mvn-run.sh`

### Program installation

- Deployment environment prerequisites
    - 1.jre version >= 21
    - 2.mysql
- Data initialization
    - 1.Download [latest zip](https://dl.zrlog.com/release/zrlog.zip) unzip, and run sh bin/run.sh or bin\run.bat (for windows)
    - 2.Visit http://host:port/install
    - 3.Fill in the database, administrator information, complete the installation

### ChangeLog

[View the full ChangeLog](https://www.zrlog.com/changelog/index.html?ref=md)

### Sample Website

* Website [http://demo.zrlog.com](http://demo.zrlog.com)
* Management address [admin/login](http://demo.zrlog.com/admin/login)
* Username admin
* Password 123456

### Getting help

* QQ group 6399942
* Mail xchun90@163.com
* Have any questions about the program, welcome
  feedback [http://blog.zrlog.com/feedback](http://blog.zrlog.com/feedback)

### FAQ

If you have some problems, you can go here to find [FAQ](https://blog.zrlog.com/faq-collect)

### Thanks

* [JFinal](https://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [AntD](https://ant.design)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)

### License

ZrLog is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).