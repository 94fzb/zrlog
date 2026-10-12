# Docker Compose 部署

仓库中的 [`docker/docker-compose.yml`](../docker/docker-compose.yml) 同时启动 ZrLog、MySQL 8.4
和独立的 [updater 服务](https://github.com/zrlog/zrlog-updater)。新配置无需再叠加 updater 文件。

## 启动

```bash
cd docker
cp .env.example .env
chmod 600 .env
```

所有部署参数统一在 `.env` 中维护，YAML 只引用变量；镜像版本、仓库和端口无需修改 YAML。
保留样例中的全部配置项，填写以下空值：

- `MYSQL_PASSWORD`：随机数据库密码。
- `COMPOSE_PROJECT_NAME`：新站点可填 `zrlog`；已有站点必须填写原项目名，以继续使用原数据卷。
- `ZRLOG_DEPLOYMENT_DIR`：当前部署目录的宿主机绝对路径（即包含 `.env` 和 Compose 文件的目录）。
- `ZRLOG_UPDATER_TOKEN`：至少 32 位随机令牌，可用 `openssl rand -hex 32` 生成。

`ZRLOG_IMAGE`、`ZRLOG_UPDATER_IMAGE`、`MYSQL_IMAGE` 均接受完整镜像地址。updater 默认固定
到已经公开验证的阿里云双架构预览镜像，`.env.example` 同时给出 Docker Hub 地址。
中国地区可将应用镜像改为 `registry.cn-chengdu.aliyuncs.com/hibegin/zrlog:3.9.2`，并选择可访问的
MySQL 镜像来源。然后启动服务：

```bash
docker compose up --detach
docker compose ps
```

以上命令在部署目录执行，Compose 会自动读取同目录的 `.env`。updater 也显式读取这份文件，
成功更新后将 `ZRLOG_IMAGE` 写为目标镜像 digest，后续启动继续使用升级后的版本。
不要用 shell 的 `export ZRLOG_IMAGE=...` 覆盖该值，否则人工启动和 updater 可能读到不同配置。
`.env` 包含密码和令牌，保存在部署机器上，不提交到 Git；仓库仅维护 `.env.example`。

MySQL 健康后 ZrLog 才会启动。访问 [http://localhost:8080/install](http://localhost:8080/install)，并使用以下数据库参数完成首次安装：

| 参数 | 值 |
| --- | --- |
| 主机 | `mysql` |
| 端口 | `3306` |
| 数据库 | `.env` 中的 `MYSQL_DATABASE`，默认 `zrlog` |
| 用户名 | `.env` 中的 `MYSQL_USER`，默认 `zrlog` |
| 密码 | `.env` 中的 `MYSQL_PASSWORD` |

## 后台升级

默认应用版本仍为 3.9.2，尚不包含本次 Docker 后台升级集成。使用包含该集成且有已发布
应用镜像清单的版本后，即可通过现有更新设置选择镜像仓库并提交升级任务。updater 的版本
与应用版本独立；它只替换 `zrlog` 服务，不升级 MySQL。

已发布的 `4.0.0-SNAPSHOT / f48fc58` 预览镜像包含后台升级集成，目前支持 `linux/amd64`。
需要体验此功能时，在 `.env` 中设置已验证的阿里云镜像：

```dotenv
ZRLOG_IMAGE=registry.cn-chengdu.aliyuncs.com/hibegin/zrlog@sha256:53cc959934bf5dbbe707ec64981c9bbebbc16bea71854279565ce5b675c677f0
```

部署后在「更新设置」开启预览版本检查，加载并保存镜像仓库选择。预览更新清单为
[`preview/last.docker-image.version.json`](https://dl.zrlog.com/preview/last.docker-image.version.json)。
dl 只存放 Docker 清单 JSON，镜像从所选 Docker Hub 或阿里云仓库拉取。

updater 端口仅在 Compose 网络内可用，通过共享令牌与应用通信。它挂载 Docker socket，
以及宿主机部署目录到容器中的相同绝对路径，用于保留 Compose 配置并更新 `.env` 的镜像引用。
保持 `ZRLOG_COMPOSE_FILES=docker-compose.yml` 与实际启动文件一致。

已有自定义 Compose 可使用 [`docker-compose.updater.yml`](../docker/docker-compose.updater.yml)
附加配置，此时设置 `ZRLOG_COMPOSE_FILES=docker-compose.yml,docker-compose.updater.yml`，
并用相同顺序的两个 `-f` 参数启动。已有站点只补齐所需配置，保留原项目名、镜像及存储设置。

## 健康检查

Compose 分别检查 MySQL、ZrLog 和 updater：updater 探针验证自身 HTTP 服务，MySQL 探针验证数据库进程可连接，ZrLog 探针通过
`/api/public/version` 验证 HTTP 服务存活。版本接口不执行数据库查询，因此它是 liveness
检查，不代表已安装站点的数据库 readiness。

## 数据与生产配置

`mysql-data`、`conf`、`static` 和 `updater-state` 是需要备份的命名卷。`updater-state` 保存
升级任务与恢复信息；`static` 包含附件、自定义主题和
站点静态文件。执行 `docker compose down` 会保留这些数据；不要在没有备份的情况下执行
`docker compose down --volumes`。

默认镜像版本适合快速开始。正式部署应在 `.env` 中将 `MYSQL_IMAGE` 和 `ZRLOG_IMAGE`
固定为经过验证的版本或不可变 digest，并通过反向代理提供 HTTPS。反向代理与 ZrLog
运行在同一台主机时，可设置 `ZRLOG_BIND_ADDRESS=127.0.0.1`，避免直接公开应用端口。
