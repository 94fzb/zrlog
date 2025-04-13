bash -e bin/download-upload-depenecy.sh || true
#upload
originalPath="${4}/${5}"
echo ${originalPath}
echo y | plink -pw ${2} ${1}@dl.zrlog.com "echo host trusted"
pscp -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}