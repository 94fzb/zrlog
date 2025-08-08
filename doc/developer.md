## ZrLog 开发指南

### 实际源码仓库

| 工程                                                                       | 最新版本                                                                                                                                                                                    |  
|--------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [基础库 (Base)](https://github.com/zrlog-extensions/zrlog-base)             | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-base.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-base)               |
| [初始化程序 (Install)](https://github.com/zrlog-extensions/zrlog-install-web) | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-install-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-install-web) |
| [服务端页面渲染 (Pages)](https://github.com/zrlog-extensions/zrlog-blog-web)    | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-blog-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-blog-web)       |
| [管理后台 (Admin)](https://github.com/zrlog-extensions/zrlog-admin-web )     | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-admin-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-admin-web)     |
| [DAO](https://github.com/94fzb/common-dao )                              | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/common-dao.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/common-dao)               |
| [Web Core](https://github.com/94fzb/simplewebserver    )                 | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/simplewebserver.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/simplewebserver)     |

### 开发流程

1. 当前工程，仅完成打包和 web模块的载入，变更上述仓库的代码进行实际变更
2. 通过工程那的 shell 脚本进行打包（实际需要将代码发布 maven 中央仓库）
3. 改工程修改对应模块的依赖，完成代码变更

注：开发环境下可以通对应工程的 `Application` 进行调试开发，或者通过 `./mvnw clean install` 使用 SNAPSHOT 的方式进行快速预览

### 开发资源

[开发必看](https://blog.zrlog.com/for-developer)