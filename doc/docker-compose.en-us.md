# Docker Compose Deployment

The repository's [`docker/docker-compose.yml`](../docker/docker-compose.yml) starts ZrLog together with MySQL 8.4.

## Start the stack

```bash
cd docker
cp .env.example .env
```

Set `MYSQL_PASSWORD` in `.env` to a randomly generated strong password, then start the services:

```bash
docker compose up --detach
docker compose ps
```

ZrLog starts after MySQL becomes healthy. Visit [http://localhost:8080/install](http://localhost:8080/install) and use these database settings for the initial installation:

| Setting | Value |
| --- | --- |
| Host | `mysql` |
| Port | `3306` |
| Database | `MYSQL_DATABASE` from `.env`; defaults to `zrlog` |
| Username | `MYSQL_USER` from `.env`; defaults to `zrlog` |
| Password | `MYSQL_PASSWORD` from `.env` |

## Health checks

Compose checks MySQL and ZrLog separately. The MySQL probe verifies that the database process accepts
connections. The ZrLog probe calls `/api/public/version` to verify that the HTTP service is alive. The
version endpoint does not query the database, so it is a liveness check rather than database readiness.

## Data and production settings

Back up the `mysql-data`, `conf`, and `static` named volumes. The `static` volume contains attachments,
custom themes, and site assets. `docker compose down` preserves these volumes; do not run
`docker compose down --volumes` without a backup.

The default image versions are intended for getting started. For production, set `MYSQL_IMAGE` and
`ZRLOG_IMAGE` in `.env` to tested version tags or immutable digests, and serve the application through an
HTTPS reverse proxy. When the proxy runs on the same host, set `ZRLOG_BIND_ADDRESS=127.0.0.1` to avoid
exposing the application port directly.
