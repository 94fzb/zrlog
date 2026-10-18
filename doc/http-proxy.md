# HTTP 代理

ZrLog 可以通过 HTTP 代理发送对外 HTTP/HTTPS 请求，包括更新检查、资源下载、AI 请求和 WebAPI 数据库访问。HTTPS 请求使用 CONNECT 隧道，并保留服务器证书校验。原生 MySQL JDBC、SQLite 不使用 HTTP 代理。当前不支持带账号密码的代理。

代理配置保存在运行目录的 `conf/proxy.properties`，不写入数据库，在数据库连接建立前读取。首次安装前也可以手动创建这个文件：

```properties
network.proxy.url=http://proxy.example.com:3128
network.proxy.noProxy=.example.com,*.internal
```

`network.proxy.url` 留空表示停用应用代理。`network.proxy.noProxy` 是可选的直连例外，用逗号分隔主机名、IP 或域名模式；`.example.com` 包含该域名及其子域名，`*.internal` 匹配子域名，`*` 表示全部直连。不支持端口或 CIDR 网段。本机回环地址始终直连，确保本地插件通信正常。

也可以通过环境变量配置：

```sh
export HTTP_PROXY=http://proxy.example.com:3128
export HTTPS_PROXY=http://proxy.example.com:3128
export NO_PROXY=.example.com,*.internal
```

`HTTP_PROXY` 控制 HTTP 请求，`HTTPS_PROXY` 控制 HTTPS 请求；两者的代理地址均使用 `http://`，可以指向不同代理。`NO_PROXY` 覆盖文件中的直连例外。兼容 `http_proxy`、`https_proxy`、`no_proxy`；同名大小写变量同时存在时，小写优先。环境变量优先于文件，空值也会覆盖：例如 `HTTPS_PROXY=` 停用 HTTPS 请求的应用代理。某个协议未配置环境变量和文件值时，保留其已有 JVM 网络策略。

安装完成后，具有系统管理权限的账号可在“系统信息 → 网络代理”（`/admin/system/proxy`）查看当前生效配置、保存文件配置和测试连接。文件配置始终可编辑；页面会提示哪些环境变量正在覆盖它，保存文件不会修改这些变量。连接测试按相同的环境变量优先规则，仅访问 `https://www.zrlog.com/`，不会保存或切换当前运行配置；测试设有 5 秒连接超时和 10 秒请求超时。

保存后需要重启 ZrLog。页面会提示配置是否等待重启，plugin-core 进程沿用主程序当前生效的代理配置。Docker 部署须持久化挂载 `conf` 目录，后台保存还需要该目录可写。独立插件或第三方库如果使用自己的网络客户端，需要遵守相同代理设置。

如果代理故障使 WebAPI 数据库无法连接，后台也可能无法访问。请直接修改 `conf/proxy.properties` 或部署环境变量，再重启程序。配置不会自动回退到直连。
