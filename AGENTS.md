# AGENTS.md

这份文档是 AI Agent 在 `zrlog` 主工程内工作的入口规则。先读本文件，再按任务打开具体实现。若本地存在 `.codex/AGENTS.md`，同时读取其中的工作区约定；该目录不提交到仓库。

## 工程定位

`zrlog` 是 ZrLog 的主集成和打包工程，主要负责：

- 组装 `zrlog-base`、`zrlog-admin-web`、`zrlog-blog-web`、`zrlog-install-web` 等独立产物。
- 提供最终应用的启动入口、运行配置和模块加载。
- 产出 Zip、WAR、Docker、Native Image、Serverless 等发布形态所需的工程结构。
- 管理用户安装部署文档、构建脚本、release/update manifest 和包内资源。

不要把后台、前台、安装或基础库的业务实现长期放在本仓库。功能逻辑应回到对应子工程修改，再由 `zrlog` 更新依赖版本并做集成验证。

## 目录职责

| 路径 | 职责 |
| --- | --- |
| `zrlog-web/` | 主 Web 应用模块，负责应用启动、模块加载、运行配置和集成测试。 |
| `package/` | 发布包组装模块，维护最终分发包结构和依赖拷贝规则。 |
| `bin/` | 构建信息、构建元数据和发布辅助脚本。 |
| `shell/` | Java/Native 组包、CI、发布与校验脚本；Docker 自动化统一放在 `shell/docker/`。 |
| `docker/` | Compose、环境变量样例、镜像定义与用户说明；`resolve-native-package.sh` 是下载型 Dockerfile 的构建输入。 |
| `conf/` | 本地运行配置样例或调试配置，修改前确认不是用户本地状态。 |
| `doc/` | 用户安装、Compose 部署和制品验证说明；`build_system_info.md` 是构建生成的包内信息。 |

## 协作约定

涉及跨仓库开发、验收或发布时，先读取工作区提供的任务契约、验收要求和发布规则。仓库内文档面向产品使用者，本文件只保留职责边界、源码入口和必要的验证约束。

## 构建与验证

常用命令：

```bash
./mvnw -q -DskipTests compile
./mvnw -q -U -pl zrlog-web -am test
./mvnw -q -DskipTests package
```

修改 `zrlog-web` 行为时至少运行 `./mvnw -q -U -pl zrlog-web -am test`。修改打包、依赖范围、release 脚本或启动 classpath 时，需要检查真实产物结构，不能只依赖 Maven 编译通过。Docker 离线契约与 Compose 配置检查脚本在 `shell/docker/`；工作流契约检查为 `python3 shell/ci/test-package-workflows.py`。

预览、候选、正式包和 Docker 只有一个入口 `.github/workflows/build-package.yml`，渠道共用构建与证明步骤。
平台矩阵以该工作流为准，失败恢复重跑对应 job。
不得另写工具链与打包步骤；验收消费已归档的实际包并核对 source/producer/SHA-256。

## 边界规则

- **运行时边界：普通 JDK ZIP、WAR 不运行 Polyglot/GraalJS，也不携带 Hexo 主题。** 两类 Java 包的 `zrlog-polyglot-template-scope` 必须为 `provided`；只有 Native 构建使用 `compile` 并启用这些能力。
- JDK 版本升级、主题 SPI/JAR 拆分、Markdown 功能或包校验调整，都不是改变上述边界的理由。不得为通过测试改成 `runtime`、添加直接运行时依赖或要求 Java 包包含 `MarkdownJsRenderer`。
- 改变运行时边界必须有用户明确的新要求，并同步更新本文件与 `shell/java/test-java-package-contract.sh`。普通 JDK 中依赖 Polyglot 的功能必须处理引擎缺席，不能通过打包脚本隐式开启。

- `zrlog` 只做集成和打包，不拥有 admin/blog/install/base 的业务模型。
- updater 相关逻辑和专项测试由 `zrlog-updater` 维护；本工程只提供环境读取和宿主适配。WAR/ZIP/deb 专项装配验收使用 updater 的 `scripts/test-application-host.sh` 与本工程产物 classpath；包内 Provider/文案/兼容入口验收使用其 `scripts/test-application-package.sh`。本工程保留通用启动、Servlet、安装集成测试及包结构检查。
- 修改内部依赖版本前，先确认对应子工程已经构建或发布了目标版本。
- 正式 release ref 不能依赖 `SNAPSHOT`；开发和预览集成可以使用本地或远端 snapshot。
- 变更模块 classpath、依赖 scope、package 配置或启动参数时，要验证最终包内 `lib/`、manifest、启动日志和模块发现结果。
- 不提交本地 `conf/db.properties`、`conf/install.lock` 或临时测试包。
- 不为了主工程验证绕过 install 流程，预览包安装必须走真实安装接口。
- 开发设计、构建/发布流程、内部验收说明、任务和执行报告按工作区约定维护，不加入用户文档。本地协作配置不提交或打包。
- 构建与测试代码随主工程版本维护。Docker 自动化进入 `shell/docker/`，同步修改工作流调用、触发路径和验收命令，不维护另一套组包流程。

## AI 修改流程

1. 先判断任务属于主工程集成、发布打包、启动加载，还是应该回到子工程实现。
2. 读取当前 `pom.xml`、脚本和真实产物规则，不凭旧版本记忆修改依赖或包结构。
3. 保留工作区已有用户改动，不 reset、restore 或覆盖无关文件。
4. 修改跨仓库依赖时记录生产者、消费者和验证命令。
5. 修改发布或打包逻辑时检查生成物，并清理 `target`、临时 zip、解压目录等产物。
6. 最终回复说明改动边界、验证命令和未处理风险。

## 常见任务入口

| 任务 | 起点 |
| --- | --- |
| 调整启动或模块加载 | `zrlog-web/src/main/java` |
| 调整最终包结构 | `package/pom.xml` 和 `package/src` |
| 更新内部模块版本 | 根 `pom.xml` 的依赖和 dependencyManagement |
| 修改构建元数据 | `bin/add-build-info.sh`、`bin/build-system-info.sh`；不手改生成的 `doc/build_system_info.md` |
| Docker 构建、发布与验收 | `shell/docker/`、`.github/workflows/build-package.yml` |
| 安装体验集成验证 | `zrlog-web/src/test/`，通过真实安装接口创建测试站点 |

## 共享测试标准

遵守 `zrlog-base/docs/test-support.md`。通用测试工具仅以 test scope 引入。已安装站点的启动测试使用真实 InstallService 生成临时配置、锁文件和默认数据，不手写 install.lock。最终 ZIP/WAR、starter 不得包含 `zrlog-test-support` 或 `*-tests.jar`。
