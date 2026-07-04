# AGENTS.md

这份文档是 AI Agent 在 `zrlog` 主工程内工作的入口规则。进入本仓库后，先读本文件，再按任务打开 `doc/`、`pom.xml`、`zrlog-web/` 或 `package/` 下的具体实现。

## 工程定位

`zrlog` 是 ZrLog 的主集成和打包工程，主要负责：

- 组装 `zrlog-base`、`zrlog-admin-web`、`zrlog-blog-web`、`zrlog-install-web` 等独立产物。
- 提供最终应用的启动入口、运行配置和模块加载。
- 产出 Zip、WAR、Docker、Native Image、Serverless 等发布形态所需的工程结构。
- 管理主工程文档、构建脚本、release/update manifest 和包内资源。

不要把后台、前台、安装或基础库的业务实现长期放在本仓库。功能逻辑应回到对应子工程修改，再由 `zrlog` 更新依赖版本并做集成验证。

## 目录职责

| 路径 | 职责 |
| --- | --- |
| `zrlog-web/` | 主 Web 应用模块，负责应用启动、模块加载、运行配置和集成测试。 |
| `package/` | 发布包组装模块，维护最终分发包结构和依赖拷贝规则。 |
| `bin/` | 构建信息、构建元数据和发布辅助脚本。 |
| `shell/` | 本地 Maven/版本脚本和运维辅助脚本。 |
| `conf/` | 本地运行配置样例或调试配置，修改前确认不是用户本地状态。 |
| `doc/` | 主工程开发、构建、安装、测试和发布文档。 |

## 必读文档

- [开发指南](doc/developer.md)
- [构建说明](doc/build.md)
- [测试说明](doc/test.md)
- [发布说明](doc/release.md)
- `zrlog-ops/docs/repository-structure-guide.md`

涉及跨仓库开发、验收或发布时，以 `zrlog-ops` 中的任务契约、验收 YAML 和发布规则为总入口。

## 构建与验证

常用命令：

```bash
mvn -q -DskipTests compile
mvn -q -pl zrlog-web -am test
mvn -q -DskipTests package
```

修改 `zrlog-web` 行为时至少运行 `mvn -q -pl zrlog-web -am test`。修改打包、依赖范围、release 脚本或启动 classpath 时，需要检查真实产物结构，不能只依赖 Maven 编译通过。

## 边界规则

- `zrlog` 只做集成和打包，不拥有 admin/blog/install/base 的业务模型。
- 修改内部依赖版本前，先确认对应子工程已经构建或发布了目标版本。
- 正式 release ref 不能依赖 `SNAPSHOT`；开发和预览集成可以使用本地或远端 snapshot。
- 变更模块 classpath、依赖 scope、package 配置或启动参数时，要验证最终包内 `lib/`、manifest、启动日志和模块发现结果。
- 不提交本地 `conf/db.properties`、`conf/install.lock` 或临时测试包。
- 不为了主工程验证绕过 install 流程，预览包安装必须走真实安装接口。

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
| 修改构建元数据 | `bin/`、`shell/`、`doc/build_system_info.md` |
| 安装体验集成验证 | `zrlog-ops/acceptance/zrlog-preview-package-install.yaml` |
