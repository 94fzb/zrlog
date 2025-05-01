#bash -e shell/download-upload-dependency.sh || true
#upload
aws --version
aws configure set aws_access_key_id ${1}
aws configure set aws_secret_access_key ${2}
aws configure set region auto
aws configure set output json
#do upload
originalPath="${4}/${5}"
aws s3 cp ${originalPath} s3://${3}/${5}/ --recursive --checksum-algorithm CRC32 --endpoint-url ${6}