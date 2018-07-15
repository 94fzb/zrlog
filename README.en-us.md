## ZrLog [![Build Status](https://travis-ci.org/94fzb/zrlog.svg?branch=master)](https://travis-ci.org/94fzb/zrlog) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

[Chinese](README.md) | [English](README.en-us.md)

ZrLog is a blog/CMS program developed in Java. It is simple, easy to use, componentized, and has low memory footprint. Bring your own Markdown editor and let more focus on writing, rather than spending a lot of time learning the use of the program.

### Homepage

[https://www.zrlog.com](https://www.zrlog.com)

### Pictures

![](http://dl.zrlog.com/assets/screenprint/post-detail.png)

![](http://dl.zrlog.com/assets/screenprint/article-edit.png)

### Features

1. Provide management of logs, categories, tags, and comments
2. Support plugin mode [How to write a plugin](http://blog.zrlog.com/post/zrlog-plugin-dev)
3. Highly customizable theme features [How to make a set of themes](https://blog.zrlog.com/post/make-theme-for-zrlog)
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
    - Windows uses `bin\mvn-tomcat-run.cmd`
    - Unix uses `sh bin/mvn-tomcat-run.sh`
- The way to configure the server is the same as other web applications (not recommended, the above method is less likely to cause problems)

### Program installation

- Deployment environment prerequisites
    - 1.jre version >= 1.8
    - 2.tomcat version >=8,jetty > 8
    - 3.mysql
- Data initialization
    - 1.Download [latest war](http://dl.zrlog.com/release/zrlog.war) in the webapps directory of tomcat (change zrlog.war to ROOT.war to avoid entering the secondary directory)    
    - 2.Visit http://host:port/zrlog/install
    - 3.Fill in the database, administrator information, complete the installation

### ChangeLog

[View the full ChangeLog](https://www.zrlog.com/changelog?ref=md)

### Sample Website

* Website [http://demo.zrlog.com](http://demo.zrlog.com)
* Management address [admin/login](http://demo.zrlog.com/admin/login)
* Username admin
* Password 123456


### Getting help

* QQ group 6399942
* Mail xchun90@163.com
* Have any questions about the program, welcome feedback [http://blog.zrlog.com/post/feedback](http://blog.zrlog.com/post/feedback)

### FAQ

If you have some problems, you can go here to find [FAQ](https://blog.zrlog.com/post/faq-collect)

### Thanks

* [JFinal](http://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)

### License

ZrLog is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).