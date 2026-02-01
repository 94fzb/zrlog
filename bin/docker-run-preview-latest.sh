docker pull zrlog/zrlog-preview:latest
docker run -p 8080:8080 -v $(pwd)/package/src/main/webapp/WEB-INF:/opt/zrlog/conf --name zrlog-preview-7 zrlog/zrlog-preview:latest