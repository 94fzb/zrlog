## zrlog 安装详细教程

> zrlog对一些不太懂网站程序安装的人提供了安装引导。但是安装整个安装过程也不是想象的那么的流畅，针对这种问题，于是就有了下面这个比较完善的文档

[========]

### 部署前提

**如果使用了诸如阿里云这样的，提供了java网站运行环境的，可以跳过这部分的内容**

* 最低要求 mysql5.1 + jre6 + tomcat6
* 文档提供 `window server` 和 `linux server` 的安装步骤

#### 安装mysql

window
* 通过[mysql 官网](http://dev.mysql.com/downloads/mysql/)下载适合自己的版本的安装包,如果网络过慢可以通过 [站长之家]\(http://sc.chinaz.com/) 下载
* 解压完后，运行其中的 `.exe` 文件，一直按照步骤走完安装流程就可以了

**这里注意下，选择下编码 UTF-8 **

![](http://7xkqup.com1.z0.glb.clouddn.com/attached/image/20151013/20151013104404_5.png)

linux

* RedHat/CentOS
`yum -y install mysql-server`
* Debian/Ubuntu
`sudo apt-get -y install mysql-server`


创建用户

```
create database zrlog_test;
CREATE USER 'zrlog_test'@'%' IDENTIFIED BY '123456';
use mysql;
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EXECUTE, TRIGGER ON `zrlog\_test`.* TO 'zrlog_test'@'%'
```


### 安装Java

Java 这里分两种情况，JDK,JRE。如果不在服务运行 javac 这样的命令建议直接安装 jre 就可以了。

windows
* 通过[oralce](http://download.oracle.com) 下载自己合适的版本
* 运行 `.exe` 文件一直走完安装流程。
* 配置环境变量
* 控制面板控\制面板项\系统\高级设置
![](http://7xkqup.com1.z0.glb.clouddn.com/attached/image/20151013/20151013104607_718.png?imageView2/2/w/600)
* 开始搭建java的环境变量了额,选择新建一个命名为 JAVA_HOME 在变量值的那一行中填写C:\Program Files\Java\jdk1.6.0_43。
* 看看有没有Path这个,如果没有的话就新建一个吧 有的话。 千万不要把它给删除了额。 删除的话 会导致其实东不能用的额 比如系统的东西可能会存在不能访问额。 在变量值中的前填写 自己的JDK的路径 C:\Program Files\Java\jdk1.6.0_43\bin; 或者是 %JAVA_HOME%\bin; 这里%时说的这里的面的东西时变量 而不是单纯的路径额
* 查看是否电脑中友java环境的话 进入dos（win+R）框输入 java （这里必有一个空格额） -version 如果出现了 这样的图就可以说明OK了

linux
**介于Linux下面安装方式较多就说一个自己在用的**
* 解压 .tar.gz 文件
`tar -xvf jdk-*.tar.gz`

* 在 .basrhc 文件最后面追加
`vim .basrhc`
```
export JAVA_HOME=/home/xiaochun/dev/jdk1.8.0_45（填写自己对应的路径）
export PATH=$JAVA_HOME/bin:$PATH
```
`source .basrhc` 刷新下，然后用 `java -version` 接可以了

#### tomcat
**zrlog 一直用tomcat作为开发的webServer，在jetty下面可以正常运行（bae的），其他的webServer欢迎大家区尝试**

到 http://tomcat.apache.org 下载。window下面可以直接下载 `.exe`，由于webServer 解压后都是可以直接使用的(安装好了Java环境)，就不描述了

准备工作完成

[========]

下载最新的`war`包，同时可以通过下载最新的源码自己通过maven构建war包。

由于网络到处都是采集的，难免源码被人篡改，推荐使用 [http://dl.zrlog.com/release/zrlog.war](http://dl.zrlog.com/release/zrlog.war) 这个地址下载


* 将.war放到tomcat的webapps目录里面。如果tomcat里面不存在其他的程序，将zrlog.war改为ROOT.war，可以避免输入二级目录zrlog
* 启动 tomcat 查看控制是否有错误信息输出，若没有可以访问 http://host:port/zrlog/instll 开始使用向导安装


![](http://7xkqup.com1.z0.glb.clouddn.com/attached/image/20151013/20151013104630_178.png?imageView2/2/w/600)
![](http://7xkqup.com1.z0.glb.clouddn.com/attached/image/20151013/20151013104648_751.png?imageView2/2/w/600)
![](http://7xkqup.com1.z0.glb.clouddn.com/attached/image/20151013/20151013104849_364.png?imageView2/2/w/600)

**上面如果第一步无法通过的话，检查mysql数据库正确**
**在输入管理密码的时候，请认真输入（输入2次密码有过了）**

------------

写的有些杂，欢迎补充
