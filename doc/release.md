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
FaaS 和 Docker 制品构建。相关入口包括：

- `.github/workflows/java-build-release-package-zip.yml`
- `.github/workflows/native-build-release-package-zip.yml`
- `.github/workflows/deb-build-release-deb.yml`
- `.github/workflows/faas-build-release-package-zip.yml`
- `bin/add-build-info.sh`
- `shell/java/build-final-java.sh`
- `shell/native/build-final-native.sh`

构建产生的 `build.properties` 和 `last*.version.json` 仍由正式构建链生成和校验，不能在
开发工作区中作为手工发版依据。

## 禁止的旧流程

以下动作已经退出正式发布流程：

- 运行仓库内的 `shell/version.sh`。
- 在开发机上生成 GA commit 或 next-SNAPSHOT commit。
- `git reset --hard` 覆盖正式分支。
- force push `master`、`main`、`release` 或正式 tag。
- 绕过 Release Order 手工发布 Maven 包、Release 附件或官网 changelog。

正式 ref 只允许由 `zrlog-ops/scripts/promote-release-refs.py` 以 fast-forward、不可变 tag
和 non-force atomic push 的方式推进。
