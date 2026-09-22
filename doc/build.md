## 构建 ZrLog

> ZrLog 提供 Zip、War、Deb 等运行包，可部署在常见 Linux 环境中。Native Image 包也可用于树莓派等 arm64 Linux 设备。

### Polyglot / Hexo 运行时边界

这是产品约定，不以当前 JDK 是否具备运行 GraalJS 的技术条件为依据。

| 分发形态 | `zrlog-polyglot-template-scope` | Polyglot / GraalJS / Hexo |
| --- | --- | --- |
| 普通 JDK ZIP | `provided` | 不打包、不运行 |
| 普通 JDK WAR | `provided` | 不打包、不运行 |
| Native ZIP / DEB / FaaS | `compile` | 由 Native 构建启用 |

四款 Hexo 主题必须沿用同一个 scope 属性；默认 Freemarker 主题、WWW 与主题 SPI 不受此禁用规则影响。
Java 包中的 `provided` 用于排除依赖，不表示要求用户向 Servlet 容器安装 Polyglot。

普通 JDK 的 Markdown、文章发布等流程必须兼容引擎缺席，沿用非 Polyglot 路径或已提交的渲染内容。不能为服务端 Markdown 渲染、JDK 升级或修复测试而将 Java 包改为 `runtime`。

`shell/java/package-java-zip.sh` 为 ZIP/WAR 显式传入 `provided`；`shell/native/package-native.sh` 使用 `compile`。
`shell/java/test-java-package-contract.sh` 对真实 ZIP/WAR 检查 Polyglot、Graal 运行库、Hexo 主题和 `MarkdownJsRenderer` 均未进入运行时包。改变边界需要用户明确要求，并同时修订本说明、AGENTS 和包校验。

Hexo 依赖在主工程父 POM 的 `dependencies` 中统一声明，由 `zrlog-web` 和 `package` 直接继承。Native 单独构建 `package/pom.xml` 时，`compile` 才能直接生效；不要改回仅由 `zrlog-web` 传递，因为其默认 `provided` 依赖不会向下传递。

### 脚本目录

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

FaaS 包通过 `shell/native/package-faas-zip.sh` 预置主题 ZIP。Kernel 固定使用独立仓库
`zrlog-extensions/template-kernel` 的 `v0.1.0` Release，下载后验证脚本中固定的 SHA-256，
失败时终止组包。最终路径为 `static/include/templates/template-kernel.zip`；运行时沿用
现有包内主题安装机制，不自动切换当前站点主题，也不依赖 zrlog-www 市场登记。
升级 Kernel 时同时更新脚本中的版本和校验值，再核对最终 FaaS ZIP 内的主题文件。

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
