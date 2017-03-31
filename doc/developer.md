# zrlog 开发指南

## 版本说明

|版本|发布时间|说明|作者|
|---|---|---|---|
|v1.0|2015-9-30|文档初始化|@94fzb|
|v1.1|2016-3-22|加入插件拦截的请求路径|@94fzb|

## 参考资料

- [JFinal 官方手册](http://jfinal.com)
- [mysql 官方文档](http://doc.mysql.com)

## 手册目的

帮助使用`zrlog`做定制开发，或者是二次开发

## 数据字典
[看这里](http://doc.zrlog.com/database/index.html)

## 快速开发

### 搭建本地开发环境

**推荐使用eclipse搭建开发环境**

* 下载 `http://git.oschina.net/94fzb/zrlog`
* 由于项目使用 maven 首次导入项目使用`configure -> convert to maven project` 就可以
* 然后和普通的 web 项目一样

### JFinal部分

> 为了方便快速上手，简单说下项目中使用JFinal

JFinal 与 Strust，SpringMVC 这些框架类似,通过全局拦截器，或者是全局servlet类似，JFinal 使用的前者。
WEB-INF/web.xml

```xml
 <filter>
    <filter-name>JFinalFilter</filter-name>
    <filter-class>com.jfinal.core.JFinalFilter</filter-class>
    <init-param>
      <param-name>configClass</param-name>
      <param-value>com.fzb.blog.config.ZrlogConfig</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>JFinalFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

然后在 ZrlogConfig 中配置 JFinal 的核心组件


### Controller 说明
**由于只是个人博客系统,设计之初就没有太多的权限控制**

Zrlog 只有2种用户模式（需要登陆的，和访客模式）

这些路径是不需要登陆访问的

/install/ (通过`install.lock` 文件控制访问)
/post/ (与博客相关的)
/p/ /plugin/ (与插件相关的,第三方接口的 callback 多说使用)
/api/ (开放与博客相关的所有JSON数据)
/ (可以自行进行扩展)

管理员才可以访问的
/admin/

### 主题

主题使用数据库存储主题路径方式来控制，默认安装完成后会使用/include/templates/default/，主题文件夹里面需要包含 `index.jsp,page.jsp,detail.jsp` 3个页面。

index.jsp 为默认的打开的页面，若无其他定制与 page.jsp 相同就可以

**通过`ThemeUtil.createTemplate`方法可以快速创建这些页面**

### 插件
[如何编写一个zrlog插件 http://blog.94fzb.com/post/175](http://blog.94fzb.com/post/175)

### 设计原则

Zrlog 从一开始就把程序定位简洁，赖看，小巧，实用。

### JSON API

JSON API 主要用于非网页展示，用于客户端程序开发定制

## 注意事项

* 默认所有的 jsp 页面都不能通过浏览器访问

## zrlog 交流

* QQ群 6399942
* QQ 504008147
* 邮箱 xchun@zrlog.com


## 开源协议

**GPL**