# Docker Compose 部署

仓库中的 [`docker/docker-compose.yml`](../docker/docker-compose.yml) 可以同时启动 ZrLog 和 MySQL 8.4。

## 启动

```bash
cd docker
cp .env.example .env
```

编辑 `.env`，为 `MYSQL_PASSWORD` 设置一个随机强密码，然后启动服务：

```bash
docker compose up --detach
docker compose ps
```

MySQL 健康后 ZrLog 才会启动。访问 [http://localhost:8080/install](http://localhost:8080/install)，并使用以下数据库参数完成首次安装：

| 参数 | 值 |
| --- | --- |
| 主机 | `mysql` |
| 端口 | `3306` |
| 数据库 | `.env` 中的 `MYSQL_DATABASE`，默认 `zrlog` |
| 用户名 | `.env` 中的 `MYSQL_USER`，默认 `zrlog` |
| 密码 | `.env` 中的 `MYSQL_PASSWORD` |

## 健康检查

Compose 分别检查 MySQL 和 ZrLog：MySQL 探针验证数据库进程可连接，ZrLog 探针通过
`/api/public/version` 验证 HTTP 服务存活。版本接口不执行数据库查询，因此它是 liveness
检查，不代表已安装站点的数据库 readiness。

## 数据与生产配置

`mysql-data`、`conf` 和 `static` 是需要备份的命名卷。其中 `static` 包含附件、自定义主题和
站点静态文件。执行 `docker compose down` 会保留这些数据；不要在没有备份的情况下执行
`docker compose down --volumes`。

默认镜像版本适合快速开始。正式部署应在 `.env` 中将 `MYSQL_IMAGE` 和 `ZRLOG_IMAGE`
固定为经过验证的版本或不可变 digest，并通过反向代理提供 HTTPS。反向代理与 ZrLog
运行在同一台主机时，可设置 `ZRLOG_BIND_ADDRESS=127.0.0.1`，避免直接公开应用端口。
