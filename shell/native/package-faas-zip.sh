mkdir -p conf/plugins
rm -rf conf/plugins/*
mkdir -p conf/plugins/installed-plugins
mkdir -p static/include/templates
rm -rf static/include/templates/*
arch=${1}
urls=(
  "static-plus-${arch}.bin"
  "rss-${arch}.bin"
  "changyan-${arch}.bin"
  "article-arranger-${arch}.bin"
  "mail-${arch}.bin"
  "statistics-${arch}.bin"
  # 这里可以继续添加其他插件
)
UA="Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"

for url in "${urls[@]}"; do
  filename=$(basename "$url")
  wget --user-agent="${UA}" -O "conf/plugins/installed-plugins/$filename" "https://dl.zrlog.com/plugin/$url?v=$(date +%s)"
done
wget --user-agent="${UA}" -O "conf/plugins/plugin-core-${arch}.bin" "https://dl.zrlog.com/plugin/core/plugin-core-${arch}.bin?v=$(date +%s)"
#tempaltes
wget --user-agent="${UA}" -O "static/include/templates/template-sheshui.zip" "https://dl.zrlog.com/attachment/template/template-sheshui.zip?v=$(date +%s)"
wget --user-agent="${UA}" -O "static/include/templates/template-www.zip" "https://dl.zrlog.com/attachment/template/template-www.zip?v=$(date +%s)"
#cd conf && zip -r plugins.zip plugins/** plugins/**/**
#cd ..
ln -s zrlog bootstrap
./mvnw -Dproject.build.outputTimestamp=2013-01-01T00:00:00Z -Ppackage-faas-zip assembly:single -f "package/pom.xml"