bash -e bin/download-upload-depenecy.sh || true
#upload
originalPath="${4}/${5}"
echo ${originalPath}
pscp -batch -unsafe -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}