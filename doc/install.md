## ZrLog 安装详细教程

- 快速体验：使用 Docker 启动，并挂载 `$(pwd)/conf:/opt/zrlog/conf`。
- 推荐路径：个人或本机使用 Native/Zip + SQLite；服务器使用 Docker Compose + MySQL。
- 高级路径：使用 Linux FaaS 包部署 AWS Lambda + Cloudflare D1。
- 其他支持形态：在已有 Servlet 容器和外部数据库时部署 WAR。
- 开发者启动：在 IDE 中运行 `com.zrlog.web.Application`，或使用 `bin/mvn-run.sh` / `bin\mvn-run.cmd`。

安装页会检查配置目录、数据库连接和安装锁文件，并在提交安装后显示实际执行进度。

## 首次安装保护

ZrLog 默认不启用安装口令。没有显式配置 `ZRLOG_INSTALL_TOKEN` 时，直接打开 `/install` 按向导安装即可，不需要查日志、找文件或复制口令。安装成功后生成的 `install.lock` 会阻止再次进入安装流程，请保留该文件；删除它不是正常的重新安装方式。

`install.lock` 在首次安装完成前还不存在。未安装实例若直接暴露到公网或不可信共享网络，其他人仍可能抢先初始化站点并创建管理员。公网、共享网络和无人值守环境应先限制访问，或在服务公开前由部署者配置高强度随机值作为 `ZRLOG_INSTALL_TOKEN`。ZrLog 不会自动生成这个值，也不会把它写进链接、临时文件或日志；安装人应从部署者或 Secret 管理系统取得同一个值。

配置生效后，安装页第一步会显示“安装口令”输入框；没有显示就表示当前进程没有启用口令保护。输入值仅保留在当前浏览器标签页，并随安装请求提交，不会进入 URL。安装完成并确认 `install.lock` 已持久化后，可以从运行环境移除口令。

各发布形态均使用同一个环境变量，但注入位置不同：

- **Zip**：在启动 `bin/start.sh` 或 `bin/run.sh` 的同一进程环境中导出 `ZRLOG_INSTALL_TOKEN`。Windows 应在启动脚本所在的终端或服务管理器中配置同名环境变量。
- **Native Image**：在执行 `./zrlog` 前，为该进程设置 `ZRLOG_INSTALL_TOKEN`。
- **Docker**：通过 `docker run` 的 `--env-file` / `-e ZRLOG_INSTALL_TOKEN`，或 Compose 的 `environment` / `env_file` 传入；不要将真实值写入镜像或提交到 Compose 文件。
- **DEB**：在 `/etc/default/zrlog` 中配置，再重启 `zrlog` 服务。具体步骤见[下文](#deb-发行包)。
- **WAR**：在 Tomcat、Jetty 或其服务管理器的进程环境中配置，再重新部署应用。具体步骤见[下文](#war-部署)。
- **FaaS**：在函数平台的环境变量或 Secret 配置中设置，并发布新版本。具体步骤见[下文](#faas-部署无服务器)。

例如，在受控的 Unix 终端中可先从密码管理器取得随机值，再启动 Zip 或 Native Image：

```bash
export ZRLOG_INSTALL_TOKEN='<随机安装令牌>'

# Zip
./bin/start.sh

# 或 Native Image
./zrlog
```

安装口令只增加首次安装的所有权校验，不替代 HTTPS、网络访问控制、配置目录权限和 Secret 管理。

### DEB 发行包

默认安装不需要修改配置，启动服务后直接访问 `/install`。只在公网或无人值守部署需要加固时，编辑 `/etc/default/zrlog`：

```bash
sudoedit /etc/default/zrlog
```

在文件中设置部署者自己保存的随机值：

```ini
# /etc/default/zrlog
ZRLOG_INSTALL_TOKEN='replace-with-a-random-value'
```

保存后重启服务：

```bash
sudo service zrlog restart
```

重新打开 `/install`，页面显示“安装口令”输入框即表示配置已由服务进程读取；在页面输入同一个值即可。不要依赖启动前临时执行 `export`，因为 `sudo` 和服务脚本不保证保留调用者环境。

正常安装不需要查看日志。页面无法打开或服务报错时，启动输出保存在 `/var/lib/zrlog/log/console.log`，应用日志位于 `/var/lib/zrlog/log/YYYY-MM-DD.log`。可同时跟踪两者：

```bash
sudo tail -F /var/lib/zrlog/log/console.log /var/lib/zrlog/log/$(date +%F).log
```

如果当天文件尚未创建，先列出目录并选择最新文件：

```bash
sudo ls -lt /var/lib/zrlog/log
```

### WAR 部署

默认不配置 `ZRLOG_INSTALL_TOKEN`，部署 WAR 后直接访问 `/<应用上下文>/install`。需要加固时，把环境变量配置到真正运行 Tomcat 或 Jetty 的服务进程中，再重启容器或重新部署应用。不要把真实值写入 WAR、请求 URL 或容器的普通日志；多节点部署必须使用同一个值。

例如，使用 systemd 管理 Tomcat 10 时，可以运行 `sudo systemctl edit tomcat10` 并添加：

```ini
[Service]
Environment="ZRLOG_INSTALL_TOKEN=replace-with-a-random-value"
```

然后执行：

```bash
sudo systemctl daemon-reload
sudo systemctl restart tomcat10
```

服务名不是 `tomcat10` 时请替换为实际名称。重新打开 `/<应用上下文>/install`，页面显示“安装口令”输入框即表示配置生效。

WAR 运行时由 Servlet 容器管理日志，ZrLog 不承诺一个固定的应用日志文件。systemd 安装的 Tomcat 10 通常可先查看：

```bash
sudo journalctl -u tomcat10 -f
```

部分发行版还会把日志写入 `/var/log/tomcat10/`，其中可能包含 `catalina.out`；实际路径以容器的日志配置为准。Jetty、Docker 中的 Tomcat 或托管容器应使用对应服务名、容器日志或平台日志。

### FaaS 部署（无服务器）

4.0.0 的高级 FaaS 路径为 AWS Lambda + Cloudflare D1。

默认不创建 `ZRLOG_INSTALL_TOKEN`，部署完成后直接访问公开路由下的 `/install`。公网部署需要加固时，在函数平台的环境变量或 Secret 配置中添加 `ZRLOG_INSTALL_TOKEN`，再发布或部署一个包含该配置的新版本。不要在函数代码的每次冷启动中随机生成口令；所有并发实例和版本别名在安装期间必须读取同一个值。

重新打开公开路由下的 `/install`，页面显示“安装口令”输入框即表示当前路由指向的函数版本已读取配置。如果没有显示，应先检查路由、版本或别名是否仍指向旧部署，而不是去日志里寻找口令。

FaaS 没有可依赖的本地日志路径，`/tmp` 也是单个运行实例的临时空间，不能用于分发或持久化安装口令。排错应使用平台日志；例如 AWS Lambda 可在 CloudWatch Logs 中查看对应函数，或在已配置 AWS CLI 和权限的终端运行：

```bash
aws logs tail "/aws/lambda/<function-name>" --follow
```

平台日志只用于确认函数启动、路由和安装错误，不会显示 `ZRLOG_INSTALL_TOKEN` 的值。

## 安装前升级

尚未完成安装时，支持在线升级的 Native Image 和 Zip 发行包会在安装页显示“立即升级”。也可以直接运行一次性升级命令：

```bash
# Native Image
./zrlog upgrade

# Zip 发行包中的 JAR
java -jar zrlog-starter.jar upgrade
```

`upgrade` 是子命令，不使用 `--upgrade`。正式版默认检查 release 通道，预览版默认检查 preview 通道；需要显式切换时可追加 `--channel=release` 或 `--channel=preview`。升级完成后进程会使用原有服务参数重新启动。

[如何安装 ZrLog](https://blog.zrlog.com/run-zrlog-in-docker.html)


## FaaS 部署资料

[AWS Lambda + Cloudflare D1](https://blog.zrlog.com/serverless-with-aws-lambda-and-cf-d1.html)
