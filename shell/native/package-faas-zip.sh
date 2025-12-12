mkdir -p conf/plugins
rm -rf conf/plugins/*
mkdir -p conf/plugins/installed-plugins
mkdir -p static/include/templates
rm -rf static/include/templates/*
arch=${1}
urls=(
  "static-plus-${arch}.bin"
  "rss-${arch}.bin"
  "comment-${arch}.bin"
  "article-arranger-${arch}.bin"
  "mail-${arch}.bin"
  "statistics-${arch}.bin"
  # 这里可以继续添加其他插件
)
UA="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36"
mirrorWebSite=https://pub-c16cba848aff4e6b8d8e0d00f0f741f0.r2.dev

for url in "${urls[@]}"; do
  filename=$(basename "$url")
  wget --user-agent="${UA}" -O "conf/plugins/installed-plugins/$filename" "${mirrorWebSite}/plugin/$url?v=$(date +%s)"
done
wget --user-agent="${UA}" -O "conf/plugins/plugin-core-${arch}.bin" "${mirrorWebSite}/plugin/core/faas/plugin-core-${arch}.bin?v=$(date +%s)"
#tempaltes
wget --user-agent="${UA}" -O "static/include/templates/template-sheshui.zip" "${mirrorWebSite}/attachment/template/template-sheshui.zip?v=$(date +%s)"
wget --user-agent="${UA}" -O "static/include/templates/template-www.zip" "${mirrorWebSite}/attachment/template/template-www.zip?v=$(date +%s)"
#cd conf && zip -r plugins.zip plugins/** plugins/**/**
#cd ..
ln -s zrlog bootstrap
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Ppackage-faas-zip assembly:single -f "package/pom.xml"