mkdir -p conf/plugins
rm -rf conf/plugins/installed-plugins
mkdir -p conf/plugins/installed-plugins
wget https://dl.zrlog.com/plugin/core/plugin-core-amd64.bin > conf/plugins/
urls=(
  "static-plus-Linux-amd64.bin"
  "backup-sql-file-Linux-amd64.bin"
  # 这里可以继续添加其他插件 URL
)

for url in "${urls[@]}"; do
  filename=$(basename "$url")
  wget -O "conf/plugins/installed-plugins/$filename" "https://dl.zrlog.com/plugin/$url"
done
wget -O "conf/plugins/plugin-core-Linux-amd64.bin" "https://dl.zrlog.com/plugin/core/plugin-core-Linux-amd64.bin"
ln -s zrlog bootstrap
./mvnw -Ppackage-lambda-zip assembly:single -f "package-web/pom.xml"