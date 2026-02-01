docker pull zrlog/zrlog-preview:latest
docker run -p 8080:8080 -v /tmp/$(pwgen 16 1):/opt/zrlog/conf --name zrlog-test-preview-$(pwgen 16 1) zrlog/zrlog-preview:latest