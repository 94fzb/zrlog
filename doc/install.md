## ZrLog 安装详细教程

- 快速体验：使用 Docker 启动，并挂载 `$(pwd)/conf:/opt/zrlog/conf`。
- 正式部署：按服务器环境选择 Docker、Native Image、Zip 或 WAR。
- 开发者启动：在 IDE 中运行 `com.zrlog.web.Application`，或使用 `bin/mvn-run.sh` / `bin\mvn-run.cmd`。

安装页会检查配置目录、数据库连接和安装锁文件，并在提交安装后显示实际执行进度。

[如何安装 ZrLog](https://blog.zrlog.com/run-zrlog-in-docker.html)


## FaaS 部署（无服务器）

[Cloudflare D1 & AWS Lambda](https://blog.zrlog.com/serverless-with-aws-lambda-and-cf-d1.html)
