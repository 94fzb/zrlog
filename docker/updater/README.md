# ZrLog Docker updater

独立 HTTP 服务，接收后台升级任务，使用 Docker Compose 拉取并替换当前项目的 `zrlog`
服务。运行时、后台及发布流程已接入源码；当前工作区尚未发布对应镜像，首次使用前须确认
所选版本已完成正式发布，并通过真实 Compose 部署验收。

## 独立预览镜像

维护者可通过 `build preview docker image` 工作流指定源码 `ref`，设置
`updater_only=true`、`push_image=true`，独立构建并发布 updater，无需先构建整站应用包。
工作流在 AMD64、ARM64 原生 Runner 上执行真实 Docker 测试、Native HTTP/重启冒烟、
容器检查，再推送 Docker Hub 与阿里云，验证两边的 digest、签名及匿名拉取。

预览标签为 `1.0.0-preview.<源码短提交>.<运行编号>.<重试次数>`，实际值见工作流摘要和
`updater-preview-publication-*` 证据。两个仓库使用相同标签：

- `zrlog/zrlog-updater:<预览标签>`
- `registry.cn-chengdu.aliyuncs.com/hibegin/zrlog-updater:<预览标签>`

将完整引用填入 `.env` 的 `ZRLOG_UPDATER_IMAGE`。此流程不推进应用更新清单或正式版本；
使用后台升级功能还需要包含对应 base/admin 集成的应用版本和已发布的应用镜像清单。

## 构建

此 POM 独立于博客主工程父 POM，只引入 Gson；HTTP 服务使用 JDK 的 `jdk.httpserver`。
构建需要 JDK 21，Native 构建需要对应 GraalVM 和本机 C 工具链。

```bash
./mvnw -f docker/updater/pom.xml test package
./mvnw -f docker/updater/pom.xml -Pnative package
python3 docker/updater/smoke-native.py docker/updater/target/zrlog-updater
docker build -t zrlog-updater:local docker/updater
```

主工程根目录的 Maven Wrapper 可直接构建此模块。JVM 包仅用于开发、调试和无 GraalVM
环境验证。容器运行 Native 可执行文件，以及实际控制 Compose 所需的 Docker/Compose
客户端；不包含后台、安装器、博客页面、主题或数据库模块。

元数据配置来自包内 `build.properties`，沿用 `mirrorWebSite` 与
`updateVersionJsonFilename`。构建时可显式指定：

```bash
./mvnw -f docker/updater/pom.xml -Pnative package \
  -DmirrorWebSite=https://dl.zrlog.com/ \
  -DupdateVersionJsonFilename=last.docker-image.version.json
```

应用 `build.properties` 的 `dockerUpdateVersionJsonFilename` 必须与 updater 的
`updateVersionJsonFilename` 相同，两端 `mirrorWebSite` 也必须一致。应用原有
`updateVersionJsonFilename` 留给 ZIP/WAR，因同一 Native 程序可在 ZIP 和 Docker 中运行。
这些是构建资源配置；修改源文件后需要重新构建对应程序，后台不提供元数据地址输入框。

定制构建须让应用检查端与 updater 执行端采用同一元数据配置。HTTP API 不接受临时
元数据地址或任意镜像、容器、路径、命令。默认只从配置的元数据服务读取清单，失败
不会自动转到 GitHub latest。

## Compose 配置

使用 `docker/docker-compose.updater.yml` 作为原 Compose 文件的附加配置。将它和
原 Compose 文件、原 `.env` 放在同一部署目录。在原 `.env` 中补充：

```dotenv
# 必须是既有项目名；不要为了启用 updater 改项目名。
COMPOSE_PROJECT_NAME=your-existing-project
ZRLOG_DEPLOYMENT_DIR=/absolute/path/to/deployment
ZRLOG_UPDATER_TOKEN=replace-with-at-least-32-random-url-safe-characters
# 开发时可使用上文构建的本地镜像；正式使用已发布的完整镜像引用。
ZRLOG_UPDATER_IMAGE=zrlog-updater:local
```

首次接入需要用原项目名执行：

```bash
docker compose -f docker-compose.yml -f docker-compose.updater.yml up -d
```

目录必须按宿主机绝对路径挂入 updater 的相同路径，Compose 才能正确解释已有相对
挂载。自定义多个 Compose 文件时，通过 `ZRLOG_COMPOSE_FILES` 填写按相同顺序排列的
逗号分隔文件名。其列表必须匹配启动该项目使用的配置。源配置与现有容器的 Compose
配置 hash 不一致时停止升级；Docker/Compose 客户端版本变化造成 hash 差异时，应先用
一致版本协调部署。updater 的 HTTP 端口只在内部网络可用，不映射到宿主机。
站点对外端口必须固定，不能使用随机分配或端口范围；使用命名卷或显式绑定目录保存数据。

`ZRLOG_IMAGE`、`ZRLOG_UPDATER_IMAGE`、`MYSQL_IMAGE` 都接受完整仓库引用。应用镜像
可选择现有阿里云分发 `registry.cn-chengdu.aliyuncs.com/hibegin/zrlog:<version>`；updater
只在权威清单声明的仓库中选择当前平台。updater、MySQL 也必须选用该环境实际可访问的
来源。正式发布流程会生成 `zrlog/zrlog-updater:<version>` 和
`registry.cn-chengdu.aliyuncs.com/hibegin/zrlog-updater:<version>`；仅使用已经发布的版本。
updater 的正式镜像可处理 release/preview 两种应用升级，不需要另启一个预览 updater。

updater 需要 Docker socket 管理权限，以及部署目录写权限以原子更新 `.env` 中的
`ZRLOG_IMAGE`。Docker socket 具备宿主机容器管理能力，令牌仅供受信的 ZrLog 后台使用。

## HTTP 契约

除 `GET /health` 外，所有接口要求 `Authorization: Bearer <ZRLOG_UPDATER_TOKEN>`。
写请求要求 `Content-Type: application/json`，最大 4096 字节。所有响应禁止缓存。

| 请求 | 响应 |
| --- | --- |
| `GET /v1/capabilities` | Docker 与配置能力、当前仓库/平台、元数据来源；检查失败返回明确错误。 |
| `GET /v1/settings` | release 清单的可选仓库与已保存选择。 |
| `GET /v1/settings/preview` | preview 清单的可选仓库与已保存选择。 |
| `PUT /v1/settings` | 保存 `{ "repository": "完整仓库", "channel": "release" }`；空仓库沿用当前部署，不触发升级。 |
| `POST /v1/upgrades` | 接收 `{ "requestId": "唯一请求ID", "channel": "release", "version": "目标版本", "buildId": "目标构建ID" }`，返回 `202` 和任务。 |
| `GET /v1/upgrades/{requestId}` | 查询任务阶段、目标、结果及恢复提示。 |

任务 ID 为 16–80 位字母、数字、`_`、`-`。同一 ID 重试返回原任务；同一 ID 改变目标返回
409。同时只执行一个任务，其他提交返回 409。终态为 `succeeded`、`failed` 或
`interrupted`。所有阶段写入命名卷 `updater-state`，进程重启后的未完成任务标记为
interrupted，不盲目重新替换容器。

镜像下载成功且标签中的版本、源提交与清单一致后，才开始替换。更新 `.env` 后调用
Compose `up --no-deps --no-build --pull never --wait zrlog`，MySQL 不参与重建。验证同时
检查实际镜像 ID、容器健康状态和程序 `--version` 输出；它不等同于数据库功能验收。
写入前用 Compose 解析并比较完整配置，只有 `zrlog.image` 可以变化。`.env` 的多行
引号值、注释和原换行保留；不要在其他服务或其他设置中复用 `ZRLOG_IMAGE`，否则升级会
在改写配置前停止。
替换阶段失败保留目标镜像配置和错误结果，不自动回退已经可能迁移过数据的新应用。
恢复应结合任务的 `previousImage` 与站点备份操作，不能把镜像回退当成数据库回滚。

## 包与验证

Native 应用构建生成 `build.properties` 后，`bash docker/updater/build-package.sh <输出目录>`
按同一版本构建 updater、运行 Native 冒烟并生成独立 ZIP 与
`last.updater.Linux-{amd64,arm64}.version.json`。ZIP 只包含可执行文件、本文和 Compose 附加配置。
正式 Release Order 可显式批准两个 Linux updater ZIP，随后沿用应用包的 SHA-256、
SBOM、provenance、签名与 GitHub/dl 分发流程。

```bash
python3 docker/test_image_manifest.py
python3 shell/release/test_update_sources.py
# 仅在专用测试 Docker Engine 执行；测试创建并清理隔离项目、镜像和注册表。
ZRLOG_UPDATER_REAL_DOCKER=1 ./mvnw -f docker/updater/pom.xml test
```

`.github/workflows/docker-updater-checks.yml` 同时验证 JVM、Native 和真实 Docker 执行边界。
其中 Docker 测试使用最小镜像夹具；完整站点仍需完成安装、数据库和附件保留的升级验收。
