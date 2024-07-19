bash -e bin/download-upload-depenecy.sh || true
#upload
expect -c "
set timeout -1
spawn scp -r ${4}/${5} ${1}@dl.zrlog.com:${3}
expect {
    \"*assword\" {send \"${2}\r\";}
    \"yes/no\" {send \"yes\r\"; exp_continue;}
      }
expect eof"