syncFolder=${4}
cp bin/download-ossutil.sh ${syncFolder}
cd ${syncFolder}
bash -e download-ossutil.sh || true
#upload
./ossutil config -i ${1} -k ${2} -e oss-accelerate.aliyuncs.com
./ossutil cp --recursive -f ${5} oss://${3}/${5}

