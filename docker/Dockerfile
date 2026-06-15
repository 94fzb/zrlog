FROM ubuntu:noble

LABEL maintainer="zrlog <support@zrlog.com>"

ARG TARGETARCH=amd64
ARG ZRLOG_CHANNEL=release
ARG CACHE_BUST=manual

RUN set -eux; \
    sed -i 's|http://archive.ubuntu.com/ubuntu/|http://mirrors.tuna.tsinghua.edu.cn/ubuntu/|g; s|http://security.ubuntu.com/ubuntu/|http://mirrors.tuna.tsinghua.edu.cn/ubuntu/|g' /etc/apt/sources.list.d/ubuntu.sources; \
    apt-get update; \
    apt-get install -y --no-install-recommends ca-certificates curl unzip; \
    rm -rf /var/lib/apt/lists/*

RUN set -eux; \
    mkdir -p /opt/zrlog; \
    curl -fsSL \
        -A "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36" \
        -o /tmp/zrlog.zip \
        "https://pub-c16cba848aff4e6b8d8e0d00f0f741f0.r2.dev/${ZRLOG_CHANNEL}/zrlog-Linux-${TARGETARCH}.zip?${CACHE_BUST}"; \
    unzip /tmp/zrlog.zip -d /opt/zrlog; \
    chmod a+x /opt/zrlog/zrlog; \
    rm /tmp/zrlog.zip

WORKDIR /opt/zrlog

ENV DOCKER_MODE=true

CMD ["./zrlog"]
