## 构建 ZrLog

> ZrLog 提供 Zip、War、Deb 等运行包，可部署在常见 Linux 环境中。Native Image 包也可用于树莓派等 arm64 Linux 设备。

### macOS Intel Native 构建兼容

macOS Intel Native 单独保留 ZrLog 3.9.2 已使用的构建组合：GraalVM `25.0.1`、
Native Maven 插件 `0.11.2` 及其对应的默认 reachability metadata。`25.0.1` 是上游
支持 macOS x64 的最后版本；新版 Native Build Tools 的 metadata schema 校验与它不兼容。
`package/pom.xml` 的 `native-macos-intel-compat` profile 仅在 macOS `x86_64` JVM 上
自动激活，适用于本地、预览和正式构建。其他平台继续使用默认的新版本插件与 GraalVM 25 更新。
此兼容组合只固定构建工具，不冻结应用版本或业务依赖。

升级默认插件时保留该兼容 profile；变更 Intel 专用版本前须确认它仍支持 GraalVM `25.0.1`。
构建成功与产品运行验收分别记录。

### 统一打包工作流

Java、Native ZIP、DEB、FaaS 和 Docker 的预览、候选与正式构建只有一个入口：
`.github/workflows/build-package.yml`。同一份包构建步骤通过 YAML anchor 复用，统一工具链、
组包命令、来源记录、签名、上传和归档；分支及参数选择渠道和平台。

- 日常 `master` 和依赖更新构建四类 Linux amd64 包，Native 完成后构建 Docker 预览镜像。
- 手动运行默认 `package_type=all`、`full_platforms=false`；也可选择 `java`、`native`、
  `deb` 或 `faas`。`full_platforms=true` 开启所选类型的完整矩阵：Java 只需 Linux amd64；
  Native ZIP 包含 Linux amd64/arm64、macOS Intel/ARM、Windows；DEB/FaaS 包含 Linux 两架构。
- 手动 `channel=release` 使用正式组包命令，实际包及 JSON 只归档到 Actions。
  手动运行同样构建所选 Native 平台的 Docker 镜像并归档 TAR、来源和 SHA-256，
  Linux 镜像也执行版本、源码标签、程序摘要及完整架构索引校验，不签名、不上传下载站、
  不推送镜像；FaaS 二进制压缩仍需原制品服务配置。
- `release` 分支 push 固定全部包类型、完整平台，执行正式签名、包上传及镜像发布。

例如，按固定工程分支检查所有 Native 平台：

```bash
gh workflow run build-package.yml -R 94fzb/zrlog \
  --ref <工程分支> -f package_type=native -f full_platforms=true -f channel=release
```

日常构建省略 `full_platforms` 或传 `false`。每个平台归档为
`package-<类型>-<runner>-<attempt>`，内含 `.package-output/<渠道>/` 下的实际包、更新 JSON，
以及 `.package-evidence/build.json` 的完整源码 SHA、实际 Java 版本、runner、producer run 和包 SHA-256。
测试下载这些实际产物并核对索引；不同源码、依赖或渠道的包分别记录，共用组包步骤不保证
两次独立重建的字节相同。

预览与正式渠道共用相同的工具链、打包、制品证明、Dockerfile 和镜像发布步骤，分支只决定
渠道身份与发布目标；不按渠道删减包类型或维护两套构建实现。平台选择沿用上述日常与正式入口规则。

Java、Native、DEB、FaaS 及所选平台并行构建。Docker 通过同一 workflow 的 `needs` 只等待
对应 Linux amd64、Linux arm64 或 Windows Native job，不等待 macOS 或其他包类型。
所有 Docker 平台均支持预览和正式渠道。Docker 按 Native job 输出的 artifact ID 下载本次
实际归档，核对 source、producer run、渠道、平台、索引、更新 JSON、大小与 SHA-256，
从已校验 ZIP 提取可执行文件作为同一套 Dockerfile 的输入，不重新编译或重新下载通道包。
Linux 镜像在所有模式下先生成同一 OCI 归档，保留 BuildKit provenance 与 SBOM；从归档加载
运行镜像，校验版本、源码标签和可执行文件 SHA-256。各平台成功后，汇总 job 在所有模式下
校验 OCI blob 摘要、来源及完整平台集合，生成 Docker Hub（含证明）和阿里云（运行镜像）
两份 OCI 索引归档；两个索引的运行镜像 digest 必须相同。`image-assembly.json` 记录各输入
Native ZIP、可执行文件与 OCI 归档摘要，以及输出索引摘要。发布只复制这些已校验的 OCI
归档，要求保留 digest，不重新构建镜像；之后回读远端索引、签名、生成更新 JSON 并推进别名。
手动 `release` 模式使用 SNAPSHOT 时仍完成上述组装检查，但明确记录
`distributionMetadataEligible=false`，不生成可被误认为 GA 的 Docker 更新 JSON。
Windows 使用 LTSC 2022 独立标签，预览不覆盖正式标签。

### 更新元数据配置

`buildId` 与 `sourceCommit` 用途不同。`bin/add-build-info.sh` 默认以
`git rev-parse --short HEAD` 生成 buildId；子工程通知预览构建时通过 `ZRLOG_BUILD_ID`
传入 `base-<7位短号>`、`adminweb-<7位短号>` 或 `blogweb-<7位短号>`。安装包和 Docker
固定预览标签必须复用实际包里的 buildId，不能改用主工程完整 SHA，否则依赖更新也可能
碰撞到同一个镜像标签。完整源码身份由 `sourceCommit` 单独保存。

Native ZIP 预览与 Docker 预览使用同一 `build-package.yml`：先构建并上传
Native 包，再由对应 Docker job 使用本次归档发布双仓库镜像。Docker 失败不改变 Native job
结果。Docker 失败后选择 **Re-run failed jobs** 或对 Docker job 选择 **Re-run this job**，
可按原 artifact ID 复用 Native 包补发镜像，不重新编译或上传 Native 包；归档保留 14 天。
Java、DEB、FaaS 和正式版也由该入口构建。恢复和校验细节见
[Docker 预览发布说明](../docker/README.md#发布-docker-预览清单)。

`bin/add-build-info.sh` 生成应用包内 `build.properties`。以下配置由构建决定：

| 属性 | 默认值 | 用途 |
| --- | --- | --- |
| `mirrorWebSite` | `https://dl.zrlog.com/` | 版本元数据基址 |
| `updateVersionJsonFilename` | 由包类型和架构确定 | ZIP/WAR/Native 文件包清单 |
| `dockerUpdateVersionJsonFilename` | `last.docker-image.version.json` | Docker 运行模式的镜像清单 |

定制构建可分别传入 `ZRLOG_MIRROR_WEBSITE`、`ZRLOG_UPDATE_VERSION_JSON_FILENAME` 和
`ZRLOG_DOCKER_UPDATE_VERSION_JSON_FILENAME`。实际检查地址是
`<mirrorWebSite>/<release或preview>/<清单文件名>`，要求 HTTPS。后台更新设置选择
安装包下载源或镜像仓库，元数据地址不通过页面修改。

独立 [Docker updater](https://github.com/zrlog/zrlog-updater) 使用自己的 `build.properties`；它的
`mirrorWebSite` 与应用一致，`updateVersionJsonFilename` 对应应用的 Docker 清单名。
两端配置不一致时拒绝开启容器升级。updater 使用独立 POM，只带 Gson 和 JDK HTTP 服务，
不继承主工程的页面、安装、数据库或主题依赖。

应用使用同一独立仓库的 `zrlog-updater-spi`、`zrlog-updater-common` 和分类型实现，版本由
父 POM 的 `zrlog-updater.version` 选择。base/admin 的生产依赖只包含 SPI。
`shell/lib/updater-modules.sh` 为 Web 和独立 package 构建选择 runtime scope，其余实现只用于测试。
JDK ZIP 选 ZIP/Docker，WAR 选 WAR，Native ZIP 选 Native/Docker，FaaS 选 FaaS，deb 选 deb；
common/SPI 随实现依赖传入。主工程的 ZIP/WAR 包契约校验构件、依赖和无关实现/HTTP 服务的缺席；
真实 ServiceLoader、文案和兼容入口验收由 updater 工程维护。
该依赖需由 updater 的应用构件发布流程先提供；正式应用版本不能使用 SNAPSHOT。

Web 启动直接编译依赖 updater-common 的 `UpdaterUtils`。`create(runtime, context)`
内部选择部署类型、识别可信启动 JAR，经 SPI 自动发现并创建实现，返回实际实现与解析后上下文。
主工程 `ApplicationUpdaterFactory` 只读取宿主环境、classpath 和应用目录，
由 base 的 `com.zrlog.common.adapter.UpdaterHosts` 提供 HTTP、路径及生命周期能力，
再用 `adapt` 包装旧接口，无需传创建回调。定时检查位于 service 的业务层；
base 不再定义 `com.zrlog.common.updater` 包，升级 DTO/契约由 updater SPI 提供。
Servlet WAR 使用相同入口。base/admin 仍只生产依赖 SPI，
不把共享 updater 创建逻辑留在 Web 工具类，也不把宿主能力反向引入 updater-common。
升级专项测试也由 updater 工程维护；主工程编译后生成 test classpath，交给 updater 的
`scripts/test-application-host.sh` 执行 WAR/ZIP 装配、工厂和 deb CLI 验收。
实际 ZIP/WAR 交给 updater 的 `scripts/test-application-package.sh <应用包路径>`，
校验包内 Provider、语言资源和 base 兼容入口，测试源码不放回主工程。

迁入的 updater 构件从 `4.0.0-SNAPSHOT` 开始。六类 Provider 和各类独立语言资源由
updater 维护；主工程按 Docker、FaaS、deb、Native、ZIP 选择，Servlet 包选择 WAR。
deb 的可执行文件、init.d 和 systemd unit 必须归 root 管理。应用用户只可写
`/var/lib/zrlog/upgrade-request/{release,preview}.request`；oneshot 使用固定 CLI
`upgrade --channel=<通道> --non-interactive --json`，进入该分支前不启动 Web/数据库。
安装后用 `shell/native/test-deb-package-contract.sh <包.deb>` 检查权限及 unit。
deb 清单的 `debPackageVersion` 来自实际 dpkg 包，buildId 继续使用现有短提交规范。

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
上传、轮询和删除确认的 JSON 响应通过临时文件接收，确保 `curl` 重试时丢弃前次的
错误响应，避免网关 504 HTML 与后续成功 JSON 串接。临时文件在脚本退出时清理。
可运行 `python3 .github/actions/process-artifact/test_process_artifact.py`，使用本机 HTTP
夹具验证超时重试、真实下载文件校验、原文件保护和清理确认（需要 curl、jq、sha256sum）。
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
