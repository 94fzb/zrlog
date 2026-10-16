# ZrLog 发布制品验证

正式 GitHub Release 除各平台安装包外，还包含：

- `SHA256SUMS`
- `SHA256SUMS.sigstore.json`
- `zrlog-<版本>-<短提交>-release-trust.zip`

trust 压缩包为每个安装包保存 `.sha256`、CycloneDX `.sbom.cdx.json`、Sigstore
`.sigstore.json` 和 `.provenance.json`。签名使用 GitHub Actions OIDC 短期身份，不依赖长期私钥。

## 验证 Release 清单

先下载需要的安装包、`SHA256SUMS`、签名 bundle 和 trust 压缩包，再安装
[Cosign](https://docs.sigstore.dev/cosign/system_config/installation/)：

```shell
cosign verify-blob \
  --bundle SHA256SUMS.sigstore.json \
  --certificate-identity-regexp '^https://github\.com/zrlog/zrlog-ops/\.github/workflows/release-order-merged\.yml@refs/heads/main$' \
  --certificate-oidc-issuer 'https://token.actions.githubusercontent.com' \
  SHA256SUMS

sha256sum --check --ignore-missing SHA256SUMS
```

macOS 可用 `shasum -a 256 <安装包>`，再与 `SHA256SUMS` 对应行人工比较。
不要只执行 checksum 而跳过 Sigstore 验证，否则无法确认 checksum 的发布者身份。

## 验证单个构建

解压 trust 文件后，以 Java ZIP 为例：

```shell
artifact='zrlog-X.Y.Z-abcdef0-release.zip'
trust='zrlog-X.Y.Z-abcdef0-release-trust'

cosign verify-blob \
  --bundle "${trust}/${artifact}.sigstore.json" \
  --certificate-identity-regexp '^https://github\.com/94fzb/zrlog/\.github/workflows/build-package\.yml@refs/heads/release$' \
  --certificate-oidc-issuer 'https://token.actions.githubusercontent.com' \
  "${artifact}"

sha256sum --check "${trust}/${artifact}.sha256"
jq -e '.bomFormat == "CycloneDX"' "${trust}/${artifact}.sbom.cdx.json"
jq '{artifact, source, build, evidence}' "${trust}/${artifact}.provenance.json"
```

`.provenance.json` 中的 `artifact.sha256` 必须等于安装包摘要，`source.commit` 必须等于 Release
tag 指向的完整提交，记录的 SBOM 摘要也必须与实际 SBOM 文件一致。

统一打包工作流后的构建签名使用 `build-package.yml@refs/heads/release` 身份。
旧包仍按其已批准订单中的原构建身份验证，不改写历史签名或放宽为任意工作流。

## 验证 Docker 镜像

从发布报告取得不可变 digest，不使用可移动的 `latest` 标签作为验证对象：

```shell
cosign verify \
  --certificate-identity-regexp '^https://github\.com/94fzb/zrlog/\.github/workflows/build-package\.yml@refs/heads/release$' \
  --certificate-oidc-issuer 'https://token.actions.githubusercontent.com' \
  'zrlog/zrlog@sha256:<digest>'
```

Linux Docker 镜像的 manifest 应同时包含 `linux/amd64` 和 `linux/arm64`。Windows Server
2022 镜像使用 `<version>-windows-ltsc2022` 独立标签；不要把 Linux 和 Windows 标签视为可
互换的运行环境。

Docker BuildKit 为 Docker Hub 中的 Linux 镜像生成 SBOM 和 provenance。阿里云仓库不接受
BuildKit 使用的 OCI 附属 manifest，因此 workflow 会单独推送不含附属 manifest 的同一平台
镜像，并在发布多架构 manifest 前验证两端平台镜像 digest 完全一致。由于 Docker Hub 的 index
额外包含 attestation descriptor，两端的多架构 index digest 应分别记录，不能要求二者相同。
预览与正式渠道的 Linux、Windows 镜像均对发布后的不可变 digest 签名。部署系统应固定 digest；版本标签
只用于发现。

手动构建归档 `docker-<平台>-<渠道>-<attempt>`，包含可用 `docker load` 导入的 `image.tar`、
`image.sha256` 及对应 Native 包的来源索引、更新 JSON 和可执行文件 SHA-256。它不代表镜像已发布。
