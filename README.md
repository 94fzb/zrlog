## ZrLog 使用 JFinal 构建的个人博客程序

[![Build Status](https://travis-ci.org/94fzb/zrlog.svg?branch=master)](https://travis-ci.org/94fzb/zrlog) [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) 

> ZrLog是使用Java开发的博客/CMS程序，具有简约，易用，组件化，内存占用低等特点。自带Markdown编辑器，让更多的精力放在写作上，而不是花费大量时间在学习程序的使用上。

### 程序主页

[http://www.zrlog.com](http://www.zrlog.com)

### 一图胜千言

![](http://static.blog.zrlog.com/attached/image/20180210/20180210150116_800.png)

![](http://static.blog.zrlog.com/attached/image/20180210/20180210150133_99.png)

### 功能
* 1.提供日志，分类，标签，评论的管理
* 2.支持插件模式 [如何编写一个插件](http://blog.zrlog.com/post/zrlog-plugin-dev)
* 3.高度可定制的主题功能 [如何制作一套主题](https://blog.zrlog.com/post/make-theme-for-zrlog)
* 4.支持第三方评论插件
* 5.提供 editormd 主流的富文本编辑器，基本上满足了管理员的编辑需求
* 6.页面静态化，缓存公共数据，访问速度更快
* 7.支持扩展第三方云存储（默认七牛）
* 8.支持数据库定时备份
* 9.在线更新升级
* ...

### 程序安装
#### 部署环境前提
* 1.jre 版本 >= 1.7
* 2.tomcat 版本 >=7，jetty > 7
* 3.mysql
#### 使用引导完成数据初始化
* 1.下载 [最新 war](http://dl.zrlog.com/release/zrlog.war) 放在 tomcat的webapps目录 (将zrlog.war改为ROOT.war可避免输入二级目录)
* 2.访问 http://host:port/zrlog/install 
* 3.填写数据库，管理员信息，完成安装

### ChangeLog

[查看完整的ChangeLog](CHANGELOG.md)

### 1.9 (2018-02-25)

#### 新特
* 加入一套新的主题（涉水轻舟-2018）
* 支持视频上传，页面使用`videojs`播放视频文件
* 更加简洁的管理后台登录页

#### 优化
* 安装向导过程填写数据库信息时，错误提示更加完善
* 文章配图自动填充时，支持外站链接
* 优化大量的代码，重构maven的结构，代码间的依赖更加清晰
* 超过数10余项的页面细节优化

#### 修复
* 1.8 更新后需要重启`webserver`后才能保存文章
* 其它已知bug修复

### 示例网站

* BAE : [http://xiaochun.duapp.com/](http://xiaochun.duapp.com/) 
    * 后台地址 [http://xiaochun.duapp.com/admin/login](http://xiaochun.duapp.com/admin/login) 
    * 用户名:admin 密码:123456
* demo : [http://demo.blog.zrlog.com](http://demo.blog.zrlog.com)

### 感谢

* [JFinal](http://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)
* [Gentelella](https://github.com/puikinsh/gentelella)
* [Jetbrains](https://www.jetbrains.com/)

### 联系方式
* QQ群 6399942
* 邮件 xchun90@163.com
* 对程序有任何问题，欢迎反馈 [http://blog.zrlog.com/post/feedback](http://blog.zrlog.com/post/feedback)

### FAQ

如何你遇到了一些问题，可以先去这里找下 [FAQ](https://blog.zrlog.com/post/faq-collect)

### License

ZrLog is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
