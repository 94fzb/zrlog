# ZrLog 发布流程

本文档用于记录 `zrlog` 正式版本的发布步骤，避免下次发布时遗漏跨仓库联动、版本号推进和官网更新动作。

对于 `zrlog` 仓库本身的版本切换、提交、打 tag、同步 `release` 分支，项目已经提供了发布脚本，正常发布时应优先直接调用脚本，而不是手工逐条执行 Git 和 Maven 命令。

## 1. 发布涉及的仓库

一次完整发布通常会涉及以下仓库：

- `zrlog-base`
- `zrlog-admin-web`
- `zrlog-blog-web`
- `zrlog`
- `zrlog-www`

发布顺序不是并行的，必须先发布前置依赖，再回到 `zrlog` 做整合发布。

## 2. 发布前确认

发布前至少检查以下内容：

- `zrlog-base` 本次版本需要带上的底层能力已经完成并已可发布。
- `admin-web`、`blog-web` 本次版本需要带上的功能和修复已经完成。
- `zrlog` 中引用的依赖版本已经准备好升级。
- `release` 分支可正常触发 GitHub Actions。
- 下载站 `https://dl.zrlog.com/` 正常可用。
- `zrlog-www` 可正常发布 changelog 页面。

## 3. 版本联动点

`zrlog` 主仓库里和发布最相关的版本位置：

- 根 `pom.xml` 的 parent 版本：`com.hibegin:zrlog-base`
- 根 `pom.xml` 的项目版本：`<version>x.y.z-SNAPSHOT</version>`
- 根 `pom.xml` 的依赖版本：
  - `zrlog-install-web.version`
  - `zrlog-blog-web.version`
  - `zrlog-admin-web.version`
- `zrlog-web/pom.xml` 的父版本

重点是：

- 先发布 `zrlog-base`
- 先发布 `admin-web`
- 再发布 `blog-web`
- 然后在 `zrlog` 里升级 parent 版本以及 `zrlog-admin-web.version` 和 `zrlog-blog-web.version`

## 4. 标准发布顺序

### 4.1 发布前置依赖

先在各自仓库发布本次要被 `zrlog` 引用的正式版本：

1. 发布 `zrlog-base`
2. 发布 `zrlog-admin-web`
3. 发布 `zrlog-blog-web`
4. 如有需要，确认 `zrlog-install-web` 是否也要升级

### 4.2 在 zrlog 中升级依赖

回到 `zrlog` 仓库：

1. 修改根 `pom.xml` 中的依赖版本：
   - parent `com.hibegin:zrlog-base`
   - `zrlog-admin-web.version`
   - `zrlog-blog-web.version`
   - 必要时 `zrlog-install-web.version`
2. 本地完成联调和适配。
3. 确认版本相关资源是否正常生成，例如：
   - `zrlog-web/src/main/resources/pure-deps.json`
   - `zrlog-web/src/main/resources/build.properties`

### 4.3 切正式版号

`zrlog` 仓库建议优先使用发布脚本：

```bash
bash shell/version.sh <patch-version>
```

例如当前基线是 `3.3`，要发布 `3.3.4` 时，执行：

```bash
bash shell/version.sh 4
```

脚本位置：

- `shell/version.sh`

脚本当前会自动执行以下动作：

1. 将 `master` 当前版本设置为正式版 `3.3.<n>`
2. 提交 `[shell-release]release version 3.3.<n>`
3. 切到 `release`
4. 用 `master` 强制覆盖 `release`
5. 创建 tag `v3.3.<n>`
6. 推送 tag
7. 强制推送 `release`
8. 回到 `master`
9. 将版本推进到下一个 `3.3.<n+1>-SNAPSHOT`
10. 提交 `[shell-release]next version 3.3.<n+1>-SNAPSHOT`
11. 强制推送 `master`

也就是说，版本从：

- `x.y.z-SNAPSHOT`

切到：

- `x.y.z`

再自动推进到下一个开发版：

- `x.y.(z+1)-SNAPSHOT`

### 4.4 脚本使用注意事项

`shell/version.sh` 不是普通辅助脚本，它包含以下高风险动作：

- `git reset --hard master`
- `git push origin release -f`
- `git push -f`

因此使用前必须确认：

- 当前工作区是干净的，没有未提交变更
- `master` 上已经包含本次发布需要的全部代码
- `release` 分支允许被 `master` 强制覆盖
- 当前脚本中的 `baseVersion` 与本次发布的大版本一致

如果本次发布不满足这些前提，再退回手工流程。

### 4.5 推送 release 分支触发构建

`zrlog` 正式版构建是通过推送 `release` 分支触发的，不是通过打 tag 触发。

当前会触发以下 workflow：

- `java-build-release-package-zip.yml`
- `native-build-release-package-zip.yml`
- `deb-build-release-deb.yml`
- `faas-build-release-package-zip.yml`

对应产物包括：

- Java ZIP
- WAR
- Native ZIP
- DEB
- FaaS ZIP

对应打包脚本：

- `shell/java/build-final-java.sh`
- `shell/native/build-final-native.sh`

### 4.6 发布元数据生成规则

构建过程中会执行：

- `bin/add-build-info.sh`

脚本会写入：

- `version`
- `runMode`
- `buildId`
- `buildTime`
- `fileArch`

这些信息最终进入：

- `zrlog-web/src/main/resources/build.properties`

下载站侧会生成：

- `last.version.json`
- `last.<arch>.version.json`

## 5. 手工发布兜底流程

如果没有使用 `shell/version.sh`，或者需要手工控制发布节奏，则按下面步骤执行：

1. 在 `master` 将版本改为正式版 `X.Y.Z`
2. 提交：

```text
[shell-release]release version X.Y.Z
```

3. 切到 `release`
4. 用 `master` 更新 `release`
5. 推送 `release`
6. 打 tag：

```text
vX.Y.Z
```

7. 等待 `release` 分支构建完成并确认产物正常
8. 回到 `master`
9. 将版本推进到下一个开发版：

```text
X.Y.(Z+1)-SNAPSHOT
```

10. 提交：

```text
[shell-release]next version X.Y.(Z+1)-SNAPSHOT
```

历史上可参考的提交风格仍然是：

- `[shell-release]release version 3.3.2`
- `[shell-release]next version 3.3.3-SNAPSHOT`

## 6. zrlog-www 更新规则

正式版发布后，还需要更新官网仓库 `zrlog-www`。

现在的规则已经简化：

- 不再编写 `download/version-X.Y.Z.json`
- 只新增 changelog Markdown 文件

文件命名规则：

```text
static/changelog/X.Y.Z-<zrlog_release_commit_short_sha>.md
```

例如：

```text
static/changelog/3.3.2-6ae177d.md
```

changelog 文件会被 `zrlog-www` 自动用于：

- 下载页版本简介
- 下载页最近 3 个版本展示
- changelog 详情页

也就是说，官网发布时只需要补 changelog，不需要再手工维护版本 JSON。

## 7. 推荐发布 Checklist

下次发布建议直接按下面顺序执行：

1. 发布 `zrlog-base`
2. 发布 `zrlog-admin-web`
3. 发布 `zrlog-blog-web`
4. 回到 `zrlog`，升级 `zrlog-base` parent 和依赖版本
5. 本地联调验证
6. 确认工作区干净，执行 `bash shell/version.sh <patch-version>`
7. 确认 tag 与 `release` 分支都已推送成功
8. 等待 GitHub Actions 的 release workflow 全部成功
9. 确认下载站产物可用
10. 更新 `zrlog-www/static/changelog/X.Y.Z-<commit>.md`
11. 发布 `zrlog-www`

如果不使用脚本，则改为执行第 5 节的手工兜底流程。

## 8. 常见遗漏项

最容易遗漏的是以下几项：

- 漏发 `zrlog-base`，导致 `zrlog` parent 版本没有对应正式版
- 只发了 `zrlog`，但没有先发 `admin-web` / `blog-web`
- 改了 `zrlog` 版本号，但没有同步 parent 和依赖版本
- `release` 分支没有更新，导致正式构建没有触发
- 打了 tag，但官网 `zrlog-www` 没补 changelog
- 发布完成后忘记把 `master` 推进回下一个 `-SNAPSHOT`

## 9. 相关文件

可参考以下文件：

- `pom.xml`
- `zrlog-web/pom.xml`
- `pom.xml` 中的 `<parent>`
- `shell/version.sh`
- `.github/workflows/java-build-release-package-zip.yml`
- `.github/workflows/native-build-release-package-zip.yml`
- `.github/workflows/deb-build-release-deb.yml`
- `.github/workflows/faas-build-release-package-zip.yml`
- `bin/add-build-info.sh`
- `shell/java/build-final-java.sh`
- `shell/native/build-final-native.sh`
