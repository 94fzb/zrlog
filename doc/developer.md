## ZrLog 开发指南

### 架构与核心源码仓库

ZrLog 通过多个独立仓库维护核心模块。本仓库（`zrlog`）负责组装这些模块、集成依赖，并管理最终应用的启动和打包流程。

系统的核心业务逻辑拆分维护在以下仓库中：

| 模块名称                                                                   | 最新版本情况                                                                                                                                                                             |
|--------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [基础核心库 (Base)](https://github.com/zrlog-extensions/zrlog-base)             | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-base.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-base)               |
| [安装初始化系统 (Install)](https://github.com/zrlog-extensions/zrlog-install-web) | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-install-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-install-web) |
| [前台页面渲染 (Pages)](https://github.com/zrlog-extensions/zrlog-blog-web)    | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-blog-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-blog-web)       |
| [管理后台系统 (Admin)](https://github.com/zrlog-extensions/zrlog-admin-web)     | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-admin-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-admin-web)     |
| [数据访问组件 (DAO)](https://github.com/94fzb/common-dao)                              | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/common-dao.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/common-dao)               |
| [轻量 Web 核心容器](https://github.com/94fzb/simplewebserver)                 | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/simplewebserver.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/simplewebserver)     |

### 日常开发流程

本仓库主要负责产出发行包并加载相关 Web 模块。涉及功能迭代时，按下面流程处理：

1. **核心逻辑变更**: 判断需要修改的业务边界（例如管理后台接口、前台渲染逻辑等），前往上述对应的子仓库修改相应的源码。
2. **核心包发布**: 子仓库调整和测试完成后，通过对应的 Shell 脚本或 Maven 工具构建新版本，并同步到 Maven 仓库。
3. **主库依赖更新**: 回到本仓库，在 `pom.xml` 等配置中更新对应模块版本（例如 `3.x.x-SNAPSHOT`）。
4. **统一构建测试**: 重新构建本仓库，确认最新组件能被正常加载。

> **提示：本地调试技巧**
> 可以从各子模块的 `Application` 入口独立运行联调。涉及多个模块时，可先在模块仓库执行 `./mvnw clean install`，将 SNAPSHOT 安装到本地 Maven 仓库，再回到本仓库验证。

### 开发文档与资源体系

插件构建、主题开发等说明见：[开发指引](https://blog.zrlog.com/for-developer)

---
