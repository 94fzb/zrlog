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
  --certificate-identity-regexp '^https://github\.com/94fzb/zrlog/\.github/workflows/(java-build-release-package-zip|native-build-release-package-zip|faas-build-release-package-zip|deb-build-release-deb)\.yml@refs/heads/release$' \
  --certificate-oidc-issuer 'https://token.actions.githubusercontent.com' \
  "${artifact}"

sha256sum --check "${trust}/${artifact}.sha256"
jq -e '.bomFormat == "CycloneDX"' "${trust}/${artifact}.sbom.cdx.json"
jq '{artifact, source, build, evidence}' "${trust}/${artifact}.provenance.json"
```

`.provenance.json` 中的 `artifact.sha256` 必须等于安装包摘要，`source.commit` 必须等于 Release
tag 指向的完整提交，记录的 SBOM 摘要也必须与实际 SBOM 文件一致。

## 验证 Docker 镜像

从发布报告取得不可变 digest，不使用可移动的 `latest` 标签作为验证对象：

```shell
cosign verify \
  --certificate-identity-regexp '^https://github\.com/94fzb/zrlog/\.github/workflows/docker-release-publish\.yml@refs/heads/release$' \
  --certificate-oidc-issuer 'https://token.actions.githubusercontent.com' \
  'zrlog/zrlog@sha256:<digest>'
```

Linux Docker 镜像的 manifest 应同时包含 `linux/amd64` 和 `linux/arm64`。Windows Server
2022 镜像使用 `<version>-windows-ltsc2022` 独立标签；不要把 Linux 和 Windows 标签视为可
互换的运行环境。

Docker BuildKit 同时为 Linux 镜像生成 SBOM 和 provenance。Linux、Windows 镜像都会对
发布后的不可变 digest 进行签名。部署系统应固定 digest；版本标签只用于发现。
