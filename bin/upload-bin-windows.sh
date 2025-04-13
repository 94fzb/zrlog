bash -e bin/download-upload-depenecy.sh || true
#upload
originalPath="${4}/${5}"
echo ${originalPath}
pscp -hostkey "ssh-ed25519 255 SHA256:8r83U7DEUPSR49VK/E1VK9T+DGswIqO7wK6bjXHAD+s" -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}