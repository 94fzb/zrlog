# Docker Compose Deployment

The repository's [`docker/docker-compose.yml`](../docker/docker-compose.yml) starts ZrLog, MySQL 8.4,
and the independent [updater service](https://github.com/zrlog/zrlog-updater). No overlay is needed for this default
file.

## Start the stack

```bash
cd docker
cp .env.example .env
chmod 600 .env
```

Maintain deployment values in `.env`; the YAML references variables without duplicating their defaults.
Keep every setting from the example and fill in these empty values:

- `MYSQL_PASSWORD`: a randomly generated database password.
- `COMPOSE_PROJECT_NAME`: for a new site, you may use `zrlog`; existing sites must keep their original project name and
  volumes.
- `ZRLOG_DEPLOYMENT_DIR`: the absolute host path containing `.env` and the Compose file.
- `ZRLOG_UPDATER_TOKEN`: at least 32 random characters; generate one with `openssl rand -hex 32`.

All three image variables accept full references to accessible registries. The updater defaults to a
verified public Aliyun preview image for AMD64 and ARM64; `.env.example` also lists its Docker Hub reference.
Then start the services:

```bash
docker compose up --detach
docker compose ps
```

Run these commands in the deployment directory so Compose reads its `.env` automatically.
The updater explicitly reads the same file and writes the target image digest to `ZRLOG_IMAGE` after
an upgrade. Subsequent starts therefore use the upgraded image. Avoid overriding this value with
`export ZRLOG_IMAGE=...`, which can make manual starts and the updater use different configurations.
Keep the real `.env`, containing passwords and tokens, on the deployment host; commit only `.env.example`.

ZrLog starts after MySQL becomes healthy. Visit [http://localhost:8080/install](http://localhost:8080/install) and use
these database settings for the initial installation:

| Setting  | Value                                             |
|----------|---------------------------------------------------|
| Host     | `mysql`                                           |
| Port     | `3306`                                            |
| Database | `MYSQL_DATABASE` from `.env`; defaults to `zrlog` |
| Username | `MYSQL_USER` from `.env`; defaults to `zrlog`     |
| Password | `MYSQL_PASSWORD` from `.env`                      |

## Upgrades from the admin UI

The default application is still 3.9.2, which does not include this Docker UI upgrade integration.
Use an application version with the integration and a published application image manifest to enable it.
The updater has its own version and only replaces the `zrlog` service; it does not upgrade MySQL.

The updater port stays on the Compose network. Its shared token authenticates application requests.
It mounts the Docker socket and the host deployment directory at the same absolute path to preserve
Compose configuration and update the image reference in `.env`. Keep `ZRLOG_COMPOSE_FILES=docker-compose.yml`.

For an older/custom Compose file, the compatibility overlay `docker-compose.updater.yml` remains available.
Set `ZRLOG_COMPOSE_FILES=docker-compose.yml,docker-compose.updater.yml` and start with the same two
`-f` arguments in that order. Preserve the existing project name, image references and storage settings.

## Health checks

Compose checks all three services. The updater probe checks its own HTTP service. The MySQL probe verifies that the
database process accepts
connections. The ZrLog probe calls `/api/public/version` to verify that the HTTP service is alive. The
version endpoint does not query the database, so it is a liveness check rather than database readiness.

## Data and production settings

Back up the `mysql-data`, `conf`, `static`, and `updater-state` named volumes. The updater state volume
preserves upgrade jobs and recovery information. The `static` volume contains attachments,
custom themes, and site assets. `docker compose down` preserves these volumes; do not run
`docker compose down --volumes` without a backup.

The default image versions are intended for getting started. For production, set `MYSQL_IMAGE` and
`ZRLOG_IMAGE` in `.env` to tested version tags or immutable digests, and serve the application through an
HTTPS reverse proxy. When the proxy runs on the same host, set `ZRLOG_BIND_ADDRESS=127.0.0.1` to avoid
exposing the application port directly.
