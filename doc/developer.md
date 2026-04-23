## ZrLog 开发指南

### 架构与核心源码仓库

ZrLog 项目采用独立子仓库的方式进行模块化开发与管理。本主仓库（`zrlog`）主要用于组装各个组件模块、实现依赖集成以及管理最终应用程序的生命周期流转。

系统的核心业务逻辑拆分维护在以下仓库中：

| 模块名称                                                                   | 最新版本情况                                                                                                                                                                             |
|--------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [基础核心库 (Base)](https://github.com/zrlog-extensions/zrlog-base)             | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-base.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-base)               |
| [安装初始化系统 (Install)](https://github.com/zrlog-extensions/zrlog-install-web) | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-install-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-install-web) |
| [前台页面渲染 (Pages)](https://github.com/zrlog-extensions/zrlog-blog-web)    | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-blog-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-blog-web)       |
| [管理后台系统 (Admin)](https://github.com/zrlog-extensions/zrlog-admin-web )     | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/zrlog-admin-web.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/zrlog-admin-web)     |
| [数据访问组件 (DAO)](https://github.com/94fzb/common-dao )                              | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/common-dao.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/common-dao)               |
| [轻量 Web 核心容器](https://github.com/94fzb/simplewebserver    )                 | [![Maven Central](https://img.shields.io/maven-central/v/com.hibegin/simplewebserver.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.hibegin/simplewebserver)     |

### 日常开发流程

目前该工程职责为产出发行包资源和加载相关的 Web 模块，具体的功能迭代应遵循下述步骤：

1. **核心逻辑变更**: 判断需要修改的业务边界（例如管理后台接口、前台渲染逻辑等），前往上述对应的子仓库修改相应的源码。
2. **核心包发布**: 在子仓库的代码调整与测试完成后，需利用其特定的 shell 脚本或 Maven 工具完成新版本的构建打包。（通常需要通过构建流程将快照或发布版本同步至 Maven 相关中央仓库）。
3. **主库依赖更新**: 切换回本工程主干，并在相应的配置文件（主要是 `pom.xml` 中版本占位符）中修改依赖其对应更新模块的最新版本（如 `3.x.x-SNAPSHOT`）。
4. **统一构建测试**: 重新打包主工程加载最新组件。

> **提示：本地调试技巧**
> 为提高局部开发验证效率，不仅支持从各个子模块的 `Application` 入口类直接下发独立运行联调；针对多模块改动时，也可以在模块端提前执行 `./mvnw clean install` 存入本地 Maven 缓存池，实现基于 SNAPSHOT 的临时调用。

### 开发文档与资源体系

涉及详细插件构建、主题等机制说明，敬请参考：[开发必看核心指引](https://blog.zrlog.com/for-developer)

---
