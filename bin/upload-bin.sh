bash -e bin/download-ossutil.sh || true
#upload
./ossutil config -i ${1} -k ${2} -e oss-cn-chengdu.aliyuncs.com
./ossutil cp --recursive -f ${4}/${5} oss://${3}/${5}

