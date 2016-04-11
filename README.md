## zrlog 使用 JFinal 构建的个人博客程序
> blog 虽然慢慢的消失, 但是总有不愿意去用QQ空间,微博,微信等,而是希望有个自己的个人主页,于是就有这个博客程序

### 程序主页

[http://www.zrlog.com](http://www.zrlog.com)    

### 部署环境前提
* 1.jre 版本 >= 1.6
* 2.tomcat 版本 >=6，jetty > 6
* 3.mysql

### 功能
* 1.实现了的日志，分类，评论的管理
* 2.支持插件模式 [如何编写一个zrlog插件 http://blog.zrlog.com/post/zrlog-plugin-dev](http://blog.zrlog.com/post/zrlog-plugin-dev) 
* 3.后台动态变更前台主题
* 4.支持第三方评论插件
* 5.提供 editormd 主流的富文本编辑器，基本上满足了管理员的编辑需求
* 6.使用ehcache缓存公共数据,访问速度更快
* 7.支持页面静态化
* 8.支持扩展第三方云存储（默认七牛）
* 9.支持数据库定时备份
* 10.可发表私有文章

### 系统安装
* 1.下载 [最新 war](http://dl.zrlog.com/release/zrlog.war) 放在 tomcat的webapps目录 (将zrlog.war改为ROOT.war可避免输入二级目录)
* 2.访问 http://host:port/zrlog/install 
* 3.填写数据，管理员信息，完成安装

### 联系方式
* QQ交流 504008147
* QQ群 6399942
* 邮件 xchun90@163.com
* 对程序有任何问题，欢迎反馈 [http://blog.zrlog.com/post/feedback](http://blog.zrlog.com/post/feedback)

### ChangeLog

[查看完整的ChangeLog](CHANGELOG.md)

#### 1.5 (2016-4-12)
**可能是最好用的开源Java博客系统**

* 新特
    * 构建全新的插件模式
    * 多说，七牛，备份数据库，邮件服务改为插件方式实现（需要进行下载才可以使用）
    * 记录管理界面的侧边栏状态，可设置默认管理界面的主题
    * 移除大量并未使用到的静态文件 （war包体积缩小到7M左右）
    * 可在`zrlog.com`下载主题，插件
    
* 优化
    * 使用Maven管理jar文件,JFinal 升级到 2.2
    * 优化主题管理页面
    * 管理员登录忽略大小写
    * 优化文章管理页面的弹出框，弃用Zdialog，使用eModal
    * log4j 日志文件分天存储，方便查找
    * 开启静态化后，只是储存 /post/* 的文章页
    * 其他css问题

* 修复
    * 修复 v1.4.4 搜索乱码
    * 修复 editor.md 与 bootstrap 的样式问题 (升级 editor.md 到v1.5)
    * 修复 editor.md z-index 问题
    * 修复在编辑过程中尚未触发存草稿导致就尝试预览 `NullPointerException`

### 感谢

* [JFinal](http://jfinal.com)
* [Editor.md](https://pandao.github.io/editor.md/)
* [SheShui.me](http://sheshui.me)

### 示例网站

* BAE ： [http://xiaochun.duapp.com/](http://xiaochun.duapp.com/) 
    * 后台地址 [http://xiaochun.duapp.com/admin/login](http://xiaochun.duapp.com/admin/login) 用户名:admin 密码:123456
    * **请不要随意改密码这些操作**
* demo : [http://demo.blog.zrlog.com](http://demo.blog.zrlog.com)