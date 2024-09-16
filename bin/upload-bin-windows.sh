bash -e bin/download-upload-depenecy.sh || true
#upload
originalPath="${4}/${5}"
echo ${originalPath}
pscp -hostkey "ssh-ed25519 255 SHA256:yigNlkR2q11I5j72PBWqW1pWdZnxM3p98vIbuZC8zDY" -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}