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

FaaS ZIP 直接包含 GraalVM Native Image 生成的原始 `zrlog` 可执行文件，
不对主程序使用 UPX。UPX 压缩后的 Native Image 即使能通过 `upx -t`
并正常启动，仍可能在 FreeMarker 或 polyglot 模板渲染等较深运行路径中以
`SIGILL` 退出并生成 `core.bootstrap.*` 文件。

插件下载、最终 FaaS ZIP、`last.<arch>.faas.version.json` 以及发布到下载目录
仍由本工程负责。普通 Native ZIP 和 DEB 构建也不使用制品处理服务。
