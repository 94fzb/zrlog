#!/usr/bin/env bash
set -e

tmp_dir="/tmp/$(pwgen 16 1)"
container_name="zrlog-test-preview-$(pwgen 16 1)"

mkdir -p "${tmp_dir}"
docker pull zrlog/zrlog-preview:latest
docker run -p 8080:8080 -v "${tmp_dir}:/opt/zrlog/conf" --name "${container_name}" zrlog/zrlog-preview:latest
