FROM ubuntu:noble

LABEL maintainer="zrlog <support@zrlog.com>"

ARG TARGETARCH
ARG ZRLOG_CHANNEL=release
ARG CACHE_BUST=manual
ARG ZRLOG_NATIVE_MANIFEST_URL=
ARG ZRLOG_NATIVE_ZIP_URL=
ARG ZRLOG_NATIVE_ZIP_SHA256=
ARG ZRLOG_NATIVE_MIRROR_BASE_URL=
ARG ZRLOG_EXPECTED_SOURCE_COMMIT=
ARG ZRLOG_EXPECTED_VERSION=

RUN set -eux; \
    sed -i 's|http://archive.ubuntu.com/ubuntu/|http://mirrors.tuna.tsinghua.edu.cn/ubuntu/|g; s|http://security.ubuntu.com/ubuntu/|http://mirrors.tuna.tsinghua.edu.cn/ubuntu/|g' /etc/apt/sources.list.d/ubuntu.sources; \
    apt-get update; \
    apt-get install -y --no-install-recommends ca-certificates curl jq unzip; \
    rm -rf /var/lib/apt/lists/*

COPY resolve-native-package.sh /usr/local/bin/resolve-native-package

RUN set -eux; \
    target_arch="${TARGETARCH:-amd64}"; \
    chmod +x /usr/local/bin/resolve-native-package; \
    package="$(ZRLOG_NATIVE_ZIP_URL="${ZRLOG_NATIVE_ZIP_URL}" \
        ZRLOG_NATIVE_ZIP_SHA256="${ZRLOG_NATIVE_ZIP_SHA256}" \
        ZRLOG_NATIVE_MIRROR_BASE_URL="${ZRLOG_NATIVE_MIRROR_BASE_URL}" \
        ZRLOG_EXPECTED_SOURCE_COMMIT="${ZRLOG_EXPECTED_SOURCE_COMMIT}" \
        ZRLOG_EXPECTED_VERSION="${ZRLOG_EXPECTED_VERSION}" \
        /usr/local/bin/resolve-native-package \
        "${ZRLOG_CHANNEL}" "Linux-${target_arch}" "${ZRLOG_NATIVE_MANIFEST_URL}")"; \
    package_url="$(printf '%s\n' "${package}" | sed -n 's/^url=//p')"; \
    package_sha256="$(printf '%s\n' "${package}" | sed -n 's/^sha256=//p')"; \
    test -n "${package_url}"; \
    test -n "${package_sha256}"; \
    mkdir -p /opt/zrlog; \
    curl --fail --silent --show-error --location \
        --retry 3 --retry-all-errors --connect-timeout 15 --max-time 900 \
        -A "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36" \
        --output /tmp/zrlog.zip "${package_url}"; \
    printf '%s  %s\n' "${package_sha256}" /tmp/zrlog.zip | sha256sum --check --strict -; \
    unzip -tq /tmp/zrlog.zip; \
    unzip -q /tmp/zrlog.zip -d /opt/zrlog; \
    chmod a+x /opt/zrlog/zrlog; \
    rm /tmp/zrlog.zip; \
    test -n "${CACHE_BUST}"

WORKDIR /opt/zrlog

ENV DOCKER_MODE=true

CMD ["./zrlog"]
