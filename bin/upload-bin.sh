originalPath="${4}/${5}"
echo ${originalPath}
bash -e bin/download-upload-depenecy.sh || true
# os check
OS="$(uname)"
case $OS in
  Linux)
    OS='linux'
    ;;
  Darwin)
    OS='mac'
    ;;
  *)
    OS='windows'
    ;;
esac
if [[ "${OS}" == "windows" ]]; then
#upload
pscp -hostkey "ssh-ed25519 255 SHA256:8r83U7DEUPSR49VK/E1VK9T+DGswIqO7wK6bjXHAD+s" -pw ${2} -r ${originalPath} ${1}@dl.zrlog.com:${3}
else
#upload
expect -c "
set timeout -1
spawn scp -r ${originalPath} ${1}@dl.zrlog.com:${3}
expect {
    \"*assword\" {send \"${2}\r\";}
    \"yes/no\" {send \"yes\r\"; exp_continue;}
      }
expect eof"
fi