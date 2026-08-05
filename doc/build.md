## 构建 ZrLog

> ZrLog 提供 Zip、War、Deb 等运行包，可部署在常见 Linux 环境中。Native Image 包也可用于树莓派等 arm64 Linux 设备。

### shell 目录

```
├── java
│   ├── build-final-java.sh
│   ├── package-dev-java-zip.sh
│   └── package-java-zip.sh
├── native
│   ├── build-final-native.sh
│   ├── package-faas-zip.sh
│   ├── package-native-deb.sh
│   ├── package-native-zip.sh
│   └── package-native.sh
```

### FaaS 主程序制品

FaaS 构建会启用 `faas-native` Maven profile，通过
`-R:-CompileInIsolates` 让 Truffle JIT 默认在主 isolate 中编译。这保留了 JIT，
同时避免 UPX 压缩后的 Native Image 在首次模板编译时创建次 isolate 而崩溃。
该 profile 只用于 FaaS，普通 Native ZIP 和 DEB 保持 GraalVM 默认设置。

FaaS workflow 在 native 主程序生成后、最终 ZIP 组装前调用
`.github/actions/process-artifact/process-artifact.sh`。调用方显式传入文件、制品名称、版本和
运行架构；脚本将未压缩主程序直接上传到 `zrlog-artifact-service`，等待 UPX 临时处理任务
完成，再从服务下载、校验并替换本地 `zrlog`。校验成功后脚本通知服务删除临时结果，
未确认的结果由服务按 TTL 回收。
插件下载、最终 FaaS ZIP、`last.<arch>.faas.version.json` 以及发布到下载目录仍由本工程负责。

同一实现也通过 `.github/actions/process-artifact/action.yml` 暴露为复合 Action。其他可信
仓库可以固定到具体提交，并通过 `artifact-file`、`artifact-name`、`artifact-version` 和
`artifact-architecture` 输入复用处理流程，无需依赖 ZrLog 的 `build.properties`。

GitHub 仓库需要配置：

```text
Variable:
  ARTIFACT_SERVICE_URL=https://webdav.zrlog.com/artifact

Secret:
  ARTIFACT_SERVICE_TOKEN=<zrlog-artifact-service 的 ARTIFACT_API_TOKEN>
```

`ARTIFACT_SERVICE_URL` 是制品服务的完整基址。服务通过
`ARTIFACT_CONTEXT_PATH=/artifact` 部署在子目录时，该变量也必须包含
`/artifact`；脚本会在此基址后追加 `/api/v1/...`。

R2 下载复用现有 FaaS workflow 的 `SECRET_ID`、`SECRET_KEY`、`BUCKET` 和 `HOST`
配置。普通 Native ZIP 和 DEB 构建不会调用制品处理服务。
