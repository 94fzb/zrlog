# Build the native executable first (mvn -Pnative package), then use docker/updater as the context.
# These bases can be supplied from an accessible registry during image construction.
ARG DOCKER_CLI_IMAGE=docker:28-cli
ARG UPDATER_BASE_IMAGE=ubuntu:noble
FROM ${DOCKER_CLI_IMAGE} AS docker-client
FROM ${UPDATER_BASE_IMAGE}
ARG ZRLOG_UPDATER_VERSION
ARG ZRLOG_UPDATER_COMMIT
LABEL org.opencontainers.image.version="${ZRLOG_UPDATER_VERSION}" \
      org.opencontainers.image.revision="${ZRLOG_UPDATER_COMMIT}" \
      org.opencontainers.image.source="https://github.com/94fzb/zrlog"
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates \
    && rm -rf /var/lib/apt/lists/*
COPY --from=docker-client /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker-client /usr/local/libexec/docker/cli-plugins/docker-compose /usr/local/libexec/docker/cli-plugins/docker-compose
COPY target/zrlog-updater /usr/local/bin/zrlog-updater
EXPOSE 8081
ENTRYPOINT ["/usr/local/bin/zrlog-updater"]
