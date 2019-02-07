## ZrLog [![Build Status](https://travis-ci.org/94fzb/zrlog.svg?branch=master)](https://travis-ci.org/94fzb/zrlog) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) 

[中文](README.md) | [English](README.en-us.md)

ZrLog是使用 Java 开发的博客/CMS程序，具有简约，易用，组件化，内存占用低等特点。自带 Markdown 编辑器，让更多的精力放在写作上，而不是花费大量时间在学习程序的使用上。

### 程序主页

[https://www.zrlog.com](https://www.zrlog.com)

### 一图胜千言

![](http://dl.zrlog.com/assets/screenprint/post-detail.png)

![](http://dl.zrlog.com/assets/screenprint/article-edit.png)

### 特性

1. 提供日志，分类，标签，评论的管理
2. 支持插件模式 [如何编写一个插件](http://blog.zrlog.com/post/zrlog-plugin-dev)
3. 高度可定制的主题功能 [如何制作一套主题](https://blog.zrlog.com/post/make-theme-for-zrlog)
4. 支持第三方评论插件
5. 提供 `editormd` 主流的富文本编辑器，基本上满足了管理员的编辑需求
6. 页面静态化，缓存公共数据，访问速度更快
7. 支持扩展第三方云存储（默认七牛）
8. 支持数据库定时备份
9. 在线更新升级<br/>
10. ...

### 快速开始

- 直接通过内嵌入 tomcat 的方式进行启动，找到 `com.zrlog.web.Application` 通过这个 `main()` 进行启动
- 通过 Maven 命令的方式进行启动（不依赖任何 IDE）
	- Windows 使用 `bin\mvn-tomcat-run.cmd`
	- Unix 使用 `sh bin/mvn-tomcat-run.sh`
- 配置 server 的方式与其它web程序一致（不建议使用，上面的方式出问题的可能性更小）

### 程序安装

* 部署环境前提
    * 1.jre 版本 >= 1.8
    * 2.tomcat 版本 >=8，jetty > 8
    * 3.mysql

* 数据初始化
    * 1.下载 [最新 war](http://dl.zrlog.com/release/zrlog.war) 放在 tomcat 的 webapps 目录 (将zrlog.war改为ROOT.war可避免输入二级目录)
    * 2.访问 http://host:port/zrlog/install 
    * 3.填写数据库，管理员信息，完成安装

### 变更日志

[查看完整的ChangeLog](https://www.zrlog.com/changelog?ref=md)

### 示例网站
 
* 网址 [http://demo.zrlog.com](http://demo.zrlog.com) 
* 管理地址 [admin/login](http://demo.zrlog.com/admin/login) 
* 用户名 admin 
* 密码 123456


### 获取帮助

* 微信号 hibegin
* 邮件 xchun90@163.com
* 对程序有任何问题，欢迎反馈 [https://blog.zrlog.com/post/feedback](https://blog.zrlog.com/post/feedback)

### 常见问题

如何你遇到了一些问题，可以先去这里找下 [常见问题](https://blog.zrlog.com/post/faq-collect)

### 感谢

* [JFinal](http://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [Jetbrains](https://www.jetbrains.com/)
* [e-lionel](http://www.e-lionel.com)

### 协议

ZrLog is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
