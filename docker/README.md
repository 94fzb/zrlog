# ZrLog Official Docker Image

[ZrLog](https://www.zrlog.com) 是一款开源博客和内容管理系统，支持文章、分类、标签、
评论、主题、插件、静态化和在线升级。官方容器镜像基于 ZrLog Native Image，运行时无需
额外安装 JDK。

- [项目主页](https://www.zrlog.com)
- [源码仓库](https://github.com/94fzb/zrlog)
- [问题反馈](https://github.com/94fzb/zrlog/issues)
- [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## 镜像标签

| 标签 | 平台 | 发布范围 |
| --- | --- | --- |
| `latest`, `X.Y.Z` | `linux/amd64` | 所有当前正式版本 |
| `latest`, `X.Y.Z` | `linux/arm64` | 从 v3.9.0 开始 |
| `windows-ltsc2022`, `X.Y.Z-windows-ltsc2022` | `windows/amd64` | 从 v3.9.0 开始 |
| `preview`, `<version>-<buildId>-preview` | `linux/amd64` | 预览版本 |

`latest` 始终指向最新的 Linux 正式版，不包含 Windows 镜像。v3.8.x 及更早版本只有
Linux AMD64；从 v3.9.0 开始，Linux 标签同时包含 AMD64 和 ARM64。Windows 镜像使用
独立标签，最终运行层基于 Nano Server LTSC 2022。

Apple Silicon 上的 Docker Desktop 可以直接使用 v3.9.0 及后续版本的 Linux ARM64
镜像。Windows 镜像需要兼容 Windows Server 2022 容器的 Windows 主机，不能在 Linux
或 macOS Docker Engine 上运行。

## Linux 快速开始

```bash
mkdir -p conf

docker run --name zrlog \
  --detach \
  --restart unless-stopped \
  --publish 8080:8080 \
  --volume "$(pwd)/conf:/opt/zrlog/conf" \
  zrlog/zrlog:latest
```

启动后访问 [http://localhost:8080/install](http://localhost:8080/install) 完成安装。

`/opt/zrlog/conf` 必须挂载到持久化存储，安装生成的 `db.properties` 和 `install.lock`
会写入该目录。数据库需要单独提供，支持 MySQL 5.7 及以上版本，也支持通过 Web API
访问 Cloudflare D1。数据库运行在宿主机时，不要在安装页面填写 `localhost`，应使用
容器可以访问的宿主机地址。

生产环境建议使用具体版本标签或不可变 digest，不要依赖可移动的 `latest` 标签。

正式版与预览版统一使用 `zrlog/zrlog`，阿里云统一使用
`registry.cn-chengdu.aliyuncs.com/hibegin/zrlog`。`latest` 只由正式发布推进，`preview`
只由预览发布推进。旧 `zrlog/zrlog-preview` 仓库退役，不再发布或提供兼容入口；旧配置改用
`zrlog/zrlog:preview`。

固定预览标签沿用安装包的 `<version>-<buildId>-preview` 命名，例如
`4.0.0-SNAPSHOT-f48fc58-preview`。buildId 原样读取包元数据：主工程为短提交号，
依赖更新可为 `base-1234567`、`adminweb-1234567`、`blogweb-1234567`，不另取当前主工程
提交号或完整 Git SHA。完整 SHA 保留在 `sourceCommit` 与 OCI revision 标签中用于追溯。

## Docker Compose 与后台升级

`docker-compose.yml` 提供 ZrLog、MySQL、独立 updater 和持久化卷。复制 `.env.example` 为 `.env`，
填写数据库密码、项目名、部署目录绝对路径和随机 updater 令牌后启动。
镜像版本、仓库、端口等部署参数统一在 `.env` 维护，保留样例的全部配置项，无需修改 YAML。
Compose 与 updater 读取同一份 `.env`；该文件含密码与令牌，不提交到 Git。
`ZRLOG_IMAGE`、`ZRLOG_UPDATER_IMAGE`、`MYSQL_IMAGE` 都可填写该环境能访问的完整镜像地址；
ZrLog 官方阿里云仓库为 `registry.cn-chengdu.aliyuncs.com/hibegin/zrlog`。

后台升级需要包含此功能的 ZrLog 版本，以及独立的 updater 容器。启用方法见
[Compose 部署说明](../doc/docker-compose.md)。默认配置已包含 updater，旧版或自定义配置可使用
附加配置 `docker-compose.updater.yml`。默认应用 3.9.2 尚不包含本次后台升级集成。
已有站点必须保留原项目名、数据卷和部署目录；首次接入
需要管理员更新部署配置，接入后日常升级可在后台完成。

在「更新设置」加载可用镜像仓库并保存选择，再到升级页面检查版本、确认风险并开始升级。
updater 会先下载并校验所选仓库的镜像，再替换 `zrlog` 容器；MySQL、配置和附件沿用原有
存储。替换期间网站会短暂断开，页面恢复后继续查询同一任务。失败时保留任务记录；
下载失败不会停止旧容器，也不会静默切换到 Docker Hub。

元数据由程序 `build.properties` 配置，默认 `https://dl.zrlog.com/`。界面中的安装包源
（dl/GitHub）用于 ZIP/WAR 更新，Docker 更新使用清单中的镜像仓库。GitHub 包来源只在
正式 Release 附件实际发布并通过校验后可用。Linux 正式镜像清单支持 AMD64/ARM64，
当前预览镜像清单仅声明 AMD64；已有 Windows 容器不在此 Compose updater 的范围内。

### 发布 Docker 预览清单

`dl.zrlog.com` 只保存 Docker 更新清单 JSON，记录版本、各镜像仓库的 digest 和已验证平台，
不保存 Docker 镜像或镜像 TAR。镜像本体由 Docker Hub 与阿里云分发。

`docker-preview-publish.yml` 可使用 `native_run_id` 复用成功的
`docker-updater-acceptance.yml` 构建；`ref` 必须解析为该构建的精确应用提交。
流水线验证来源、包 SHA-256、大小和 `build.properties`，只把包内 Native 可执行文件装入镜像，
不重新构建程序或上传该 ZIP 到 dl。此入口目前仅支持 `linux/amd64`，不更新 `latest` 标签。

在 `push_image=true` 时，流水线先发布到两个镜像仓库，再匿名拉取并检查版本、源码标签及
Native 文件 SHA-256。Bash/jq/AWS CLI 脚本随后写入
`preview/docker-image/<version>-<buildId>.json`，校验回读后条件更新
`preview/last.docker-image.version.json`。最后验证两个 dl 公开地址返回同一份 JSON。
只修正镜像 tag 且版本、源码、仓库、平台和 digest 全部相同时，允许将修订清单写入
`preview/docker-image/<version>-<buildId>/aliases/<JSON-SHA256>.json` 后推进通道入口，
不会覆盖原不可变清单。
这些动作不更新正式版通道或 GitHub Release 附件。离线检查入口是
`bash docker/test-image-manifest.sh`。

重跑时复用已有不可变标签，重新检查镜像与清单。阿里云使用从 Docker Hub 复制的运行
镜像生成兼容索引；构建证明保留在 Docker Hub，不把阿里云不支持的 OCI attestation
格式上传到该仓库。两个仓库的索引 digest 因此分别读取和记录。

若最后的 dl 公开回读被 Cloudflare challenge 拦截，流水线保持失败并输出 HTTP 状态与
`cf-mitigated`，不能把上传成功当成公开可用。应另行核对实际部署网络的匿名清单响应、
SHA-256 和 updater 仓库列表；已经发布的镜像与不可变清单仍保留。

## Windows Server 2022

从 v3.9.0 开始，可以在 PowerShell 和 Windows 容器模式下运行：

```powershell
docker volume create zrlog-conf

docker run --name zrlog `
  --detach `
  --restart unless-stopped `
  --publish 8080:8080 `
  --mount "type=volume,source=zrlog-conf,target=C:\opt\zrlog\conf" `
  zrlog/zrlog:windows-ltsc2022
```

随后访问 [http://localhost:8080/install](http://localhost:8080/install)。

## 制品验证

正式发布的 Linux 和 Windows 镜像 digest 均使用 Cosign 签名；Linux 镜像同时生成 SBOM
和 provenance。生产部署建议固定 digest，并按[发布制品验证文档](https://github.com/94fzb/zrlog/blob/master/doc/artifact-verification.md)
验证发布身份。
