## ZrLog 使用 JFinal 构建的个人博客程序

> ZrLog是使用Java开发的博客/CMS程序，具有简约，易用，组件化，内存占用低等特点。自带Markdown编辑器，让更多的精力放在写作上，而不是花费大量时间在学习程序的使用上。

### 程序主页

[http://www.zrlog.com](http://www.zrlog.com)

### 一图胜千言

![](http://static.blog.zrlog.com/attached/image/20170331/20170331202106_594.png)

![](http://static.blog.zrlog.com/attached/image/20170529/20170529193819_945.png)


### 部署环境前提
* 1.jre 版本 >= 1.7
* 2.tomcat 版本 >=7，jetty > 7
* 3.mysql

### 功能
* 1.提供日志，分类，标签，评论的管理
* 2.支持插件模式 [如何编写一个zrlog插件 http://blog.zrlog.com/post/zrlog-plugin-dev](http://blog.zrlog.com/post/zrlog-plugin-dev)
* 3.高度可定制的主题功能
* 4.支持第三方评论插件
* 5.提供 editormd 主流的富文本编辑器，基本上满足了管理员的编辑需求
* 6.缓存公共数据，访问速度更快
* 7.支持页面静态化
* 8.支持扩展第三方云存储（默认七牛）
* 9.支持数据库定时备份
* 10.在线更新升级
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

#### 1.7 (2016-5-31)
**可能是最好用的开源Java博客程序**

#### 新特
* 文章，分类别名支持中文
* 更新管理界面添加手动检测按钮
* 增强了主题开发（引入dev.jsp可以快速浏览存放在request域的数据，便于模板页面读取的渲染）
* https的支持，需要在nginx.conf文件的http块里面添加 `proxy_set_header X-Forwarded-Protocol $scheme;`
* 在网站设置里添加对会话过期时间的控制
* 改进了插件功能，使用vue.js客户端渲染替换原有使用freemarker服务端渲染
* 备份插件支持window系统
* 增加新的畅言评论框（原多说已宣布关闭，实在令人惋惜）
* 开源协议由GPLv2变更到Apache

#### 优化
* 升级JFinal到3.1，将Java版本提升至1.7
* 优化对后台管理页面的静态资源缓存
* 优化插件服务的内存占用
* 下载插件核心服务时关闭缓存
* 优化程序更新流程，更新的检查机制
* 管理主面板添加系统编码信息
* 默认主题添加标题设置，避免域名过长的情况下，样式被破坏的问题
* 优化主题管理界面
* 废弃session的方式控制权限，变更为Cookie验证
* 优化/api/\*的错误请求，改为响应json数据
* 优化编辑文章的方式，由原来的弹窗标题改为跳转到撰写文章界面进行编辑
* 安装界面添加安装需要的注意事项
* 启动插件使用java的完整路径进行启动，避免部分云平台没有将java添加到PATH中，无法正常启动的问题
* 删除一些没有使用资源文件，默认主题的使用通用的头像图片
* 优化文章编辑页的文章分类的选择框的样式，优化一些其它的样式
* 完善一些页面的i18n，后台管理界面添加主题预览状态的提示

#### 修复
* 导航条数据无法更新
* 默认主题无法上传图片
* 关闭更新功能后，无法正常启动的bug（感谢 [@说好不上学](https://www.weekdragon.cn/) 发现的bug）
* 修复上一篇，下一篇的请求地址错误
* 修复主题无法上传的问题
* 修复IE浏览器，管理员登陆成功后无法正常的跳转
* 修复website表value的长度不够的问题
* 修复mysql5.7以上版本，需要配置`sql_mode`（group by语法无法正常执行）的问题
* 修复Window系统下，升级过程中无法正常解压生成新的war文件
* 修复标签添加后，无法通过标签进行定位文章
* 修复主题预览状态，预览文章页面主题的资源文件路径错误的问题
* 修复IE下使用 localhost 进行访问，无法进行进行登陆（IE限制Cookie的domain字段，不能设置为localhost）
* 修复插件的运行路径无法跟随程序路径变化而变化的问题（Windows的文件完整路径到Linux下面文件却成了文件名）

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

### License

ZrLog is Open Source software released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).