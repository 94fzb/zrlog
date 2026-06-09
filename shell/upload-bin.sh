#!/usr/bin/env bash
set -e

: "${1:?aws access key id is required}"
: "${2:?aws secret access key is required}"
: "${3:?bucket is required}"
: "${4:?source base path is required}"
: "${5:?source folder is required}"
: "${6:?endpoint url is required}"

#upload
aws --version
aws configure set aws_access_key_id "${1}"
aws configure set aws_secret_access_key "${2}"
aws configure set region auto
aws configure set output json
#do upload
originalPath="${4}/${5}"
aws s3 cp "${originalPath}" "s3://${3}/${5}/" --recursive --checksum-algorithm CRC32 --endpoint-url "${6}"
