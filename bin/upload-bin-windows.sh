bash -e bin/download-upload-depenecy.sh || true
#upload
originalPath="${4}/${5}"
echo ${originalPath}
pscp -hostkey "ssh-ed25519 255 SHA256:npOpUqkVgzEG/WTKAJ1EoSc+2s41jxDgDk5rfpTOVN8" -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}