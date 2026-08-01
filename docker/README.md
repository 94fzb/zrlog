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
