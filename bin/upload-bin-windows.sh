bash -e bin/download-upload-depenecy.sh || true
#upload
originalPath="${4}/${5}"
echo ${originalPath}
pscp -hostkey "ssh-ed25519 255 SHA256:VSwjLgUvOGj4yYu7sKHh1qK+kpZMemYUEc4INeeKDN8" -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}