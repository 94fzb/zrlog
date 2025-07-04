#!/bin/bash
set -e

while true; do
  # 发送请求，-i 输出头和体，-s 静默模式
  RESPONSE=$(curl -i -s "http://${AWS_LAMBDA_RUNTIME_API}/2018-06-01/runtime/invocation/next")

#  echo "${RESPONSE}"
  # 把响应头和体分离：
  # 先取请求 ID （从响应头中）
  REQUEST_ID=$(echo "$RESPONSE" | grep 'Lambda-Runtime-Aws-Request-Id:' | cut -d' ' -f2 | sed 's/\r//g')

  BODY=$(echo "$RESPONSE" | sed -n '/^\r$/,$p' | sed '1d')
  # 这里处理 EVENT，比如打印或者传给程序
  echo "RequestId: $REQUEST_ID"
  echo "$BODY" | ./zrlog
  # 回复 Lambda 运行环境
  curl -s -X POST "http://${AWS_LAMBDA_RUNTIME_API}/2018-06-01/runtime/invocation/${REQUEST_ID}/response" --data-binary @"/tmp/temp/${REQUEST_ID}.json"
done
