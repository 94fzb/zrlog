mkdir -p conf/plugins
rm -rf conf/plugins/installed-plugins
mkdir -p conf/plugins/installed-plugins
urls=(
  "static-plus-Linux-amd64.bin"
  "backup-sql-file-Linux-amd64.bin"
  # 这里可以继续添加其他插件 URL
)

for url in "${urls[@]}"; do
  filename=$(basename "$url")
  wget -O "conf/plugins/installed-plugins/$filename" "https://pub-c16cba848aff4e6b8d8e0d00f0f741f0.r2.dev/plugin/$url"
done
wget -O "conf/plugins/plugin-core-Linux-amd64.bin" "https://pub-c16cba848aff4e6b8d8e0d00f0f741f0.r2.dev/plugin/core/plugin-core-Linux-amd64.bin"
ln -s zrlog bootstrap
./mvnw -Ppackage-lambda-zip assembly:single -f "package-web/pom.xml"