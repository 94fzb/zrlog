# ZrLog 发布流程

ZrLog 正式版本统一通过 `zrlog-ops` 的 Release Order 流程发布。本仓库不再提供本地
`shell/version.sh`，也不允许通过手工改版本、强制推进分支或自行创建正式 tag 来绕过
Release Order。

## 职责边界

正式发布采用“AI 准备、人工批准、固定 CI/CD 执行”：

1. AI 完成功能实现、测试、发布说明草稿和 Release Order manifest。
2. 人工审核并合并只新增一份 manifest 的 Release Order PR。
3. `zrlog-ops` 固定 CI/CD 按受信仓库拓扑生成 GA commit、发布或复用 Maven 依赖、
   推进正式 ref、构建制品并发布 changelog。

AI 不执行 tag、Maven 发布、`release` 分支推进、Release 附件上传或官网 changelog 发布。
人工合并 Release Order PR 是正式发布的唯一授权面。

发布规范以 `zrlog-ops` 中的以下文件为准：

- `release/docs/ai-release-workflow.md`
- `release/docs/ci-release-executor.md`
- `release/config/repository-releases.json`
- `release/versions/vX.Y.Z.json`

## 发布拓扑

固定 CI/CD 按以下顺序处理主发布链：

1. `zrlog-install-web`
2. `zrlog-base`
3. `zrlog-blog-web`
4. `zrlog-admin-web`
5. `zrlog`

上游仓库正式版本必须先通过对应 workflow，并能从 Maven Central 解析，才允许继续处理
下游仓库。`zrlog` 的 GA commit 从 Release Order 批准的 `master` commit 机械生成，不修改
`master`；正式 `release` 分支和 `vX.Y.Z` tag 必须指向同一个 GA commit。

## 发布前检查

准备 Release Order 前至少确认：

- 本次范围内的代码和测试已经进入各仓库受信默认分支。
- manifest 中的 source commit 等于执行时的默认分支头。
- GA 依赖中不存在 `SNAPSHOT`。
- 发布安全门禁没有未豁免的 Critical/High 或 secret scanning 告警。
- changelog、附件清单、SHA-256 和回滚说明已经准备完成。
- 按 [发布制品验证](artifact-verification.md) 检查 Sigstore、SBOM 和 provenance 要求。

## 主工程构建

固定 CI/CD 推进 `release` 分支后，现有 GitHub Actions 继续负责 Java、Native、DEB、
FaaS 和 Docker 制品构建。Java/Native/DEB/FaaS 与 Docker 使用唯一入口
`.github/workflows/build-package.yml`，正式构建固定完整平台矩阵；日常预览默认 Linux amd64，
手动构建通过 `full_platforms=true` 开启完整矩阵，见[构建说明](build.md#统一打包工作流)。相关文件包括：

- `.github/workflows/build-package.yml`
- `bin/add-build-info.sh`
- `shell/java/build-final-java.sh`
- `shell/native/build-final-native.sh`

构建产生的 `build.properties` 和 `last*.version.json` 仍由正式构建链生成和校验，不能在
开发工作区中作为手工发版依据。

[zrlog-updater](https://github.com/zrlog/zrlog-updater) 独立维护 Docker 升级服务端、Native
ZIP 与 Docker Hub/阿里云镜像，使用自己的版本和源提交。本工程只维护应用镜像、应用
升级清单与 Compose 接入；应用 Release Order 不再生成或附带 updater 安装包。

Docker job 通过 `needs` 等待对应的 Linux amd64、Linux arm64 或 Windows Native 包成功；
Windows LTSC 2022 镜像继续在两个仓库发布和签名。Docker release/preview 在镜像验证后分别读取各仓库实际 digest，先写不可变镜像
清单，再条件更新 `last.docker-image.version.json`。GitHub 正式 Release 公开后，
`update-package-sources.yml` 逐项下载并校验 GitHub/dl 的包，验证字节一致后才补充
`zipSources`/`warSources`；保留原 URL 字段，失败不切换到未经验证的来源。

Linux 手动归档和自动发布共用 OCI 导出、程序摘要检查与索引组装；汇总 job 不受
`publish` 开关控制。正式发布复制已校验归档并保留 digest，对外推送、签名、更新 JSON
及别名推进才受发布条件控制。SNAPSHOT 的正式组包演练只记录工程组装证据，不能代替
GA 身份、无 SNAPSHOT 依赖门禁或正式发布成功。

updater 构建和发布入口为独立仓库的 `build-publish.yml`，不再由本工程调度。
其安装包使用 GitHub Release 与 `dl.zrlog.com/updater/{channel}/`；应用的更新元数据
仍使用 `dl.zrlog.com/{channel}/`。两者版本和发布生命周期独立，镜像仓库名保持兼容。

## 禁止的旧流程

以下动作已经退出正式发布流程：

- 运行仓库内的 `shell/version.sh`。
- 在开发机上生成 GA commit 或 next-SNAPSHOT commit。
- `git reset --hard` 覆盖正式分支。
- force push `master`、`main`、`release` 或正式 tag。
- 绕过 Release Order 手工发布 Maven 包、Release 附件或官网 changelog。

正式 ref 只允许由 `zrlog-ops/scripts/promote-release-refs.py` 以 fast-forward、不可变 tag
和 non-force atomic push 的方式推进。
