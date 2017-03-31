## zrlog 使用 JFinal 构建的个人博客程序

> ZrLog是使用Java开发的博客/CMS程序，具有简约，易用，组件化，内存占用低等特点。自带Markdown编辑器，让更多的精力放在写作上，而不是花费大量时间在学习程序的使用上。

### 程序主页

[http://www.zrlog.com](http://www.zrlog.com)    

### 一图胜千言

![](http://static.blog.zrlog.com/attached/image/20170331/20170331202106_594.png)

![](http://static.blog.zrlog.com/attached/image/20170331/20170331201039_532.png)


### 部署环境前提
* 1.jre 版本 >= 1.7
* 2.tomcat 版本 >=7，jetty > 7
* 3.mysql

### 功能
* 1.提供日志，分类,标签，评论的管理
* 2.支持插件模式 [如何编写一个zrlog插件 http://blog.zrlog.com/post/zrlog-plugin-dev](http://blog.zrlog.com/post/zrlog-plugin-dev) 
* 3.高度可定制的主题功能
* 4.支持第三方评论插件
* 5.提供 editormd 主流的富文本编辑器，基本上满足了管理员的编辑需求
* 6.缓存公共数据,访问速度更快
* 7.支持页面静态化
* 8.支持扩展第三方云存储（默认七牛）
* 9.支持数据库定时备份
* 10.可发表私有文章
* ...

### 程序安装（war包）
* 1.下载 [最新 war](http://dl.zrlog.com/release/zrlog.war) 放在 tomcat的webapps目录 (将zrlog.war改为ROOT.war可避免输入二级目录)
* 2.访问 http://host:port/zrlog/install 
* 3.填写数据，管理员信息，完成安装

### 文档

[汇聚了一些常见问题](https://blog.zrlog.com/post/sort/doc)

### 联系方式
* QQ交流 504008147
* QQ群 6399942
* 邮件 xchun90@163.com
* 对程序有任何问题，欢迎反馈 [http://blog.zrlog.com/post/feedback](http://blog.zrlog.com/post/feedback)

### ChangeLog

[查看完整的ChangeLog](CHANGELOG.md)

#### 1.6 (2016-12-13)
**可能是最好用的开源Java博客程序**

#### 新特
* 自动更新功能
* 博客搜索结果高亮检索的关键字
* 七牛插件支持全站静态资源托管
* 添加本地主题上传
* 主题数据可以存放到数据库（及主题可以配置）
* 全新的后台管理界面
* 管理博客时支持按时间，浏览量等信息进行排序
* 提供多语言
* 添加mysql数据版本信息在管理后台主页

#### 优化
* 重构管理相关代码，实现了接口数据与模板数据渲染的控制器代码分离
* 简化分页数据的遍历，优化模板数据，更加轻松的编写主题
* 独立后台页面的javascript部分
* 优化安装引导界面
* 部分图标的优化
* 优化默认主题的一些样式
* 移除`Ehcache`，改用内存的方式存放全局数据（war体积减小到6M）

#### 修复
* 部分平台插件默认编码问题
* 程序停止后，对应的插件服务无法停止的问题
* 修复静态化开启后部分平台乱码问题

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