#!/usr/bin/env bash

# error codes
# 0 - exited without problems
# 1 - parameters not supported were used or some unexpected error occurred
# 2 - OS not supported by this script
# 3 - installed version of ossutil is up to date
# 4 - supported unzip tools are not availabale

set -e

#when adding a tool to the list make sure to also add its corresponding command further in the script
unzip_tools_list=('unzip' '7z' 'busybox')

#make sure unzip tool is available and choose one to work with
set +e
for tool in ${unzip_tools_list[*]}; do
    trash=$(hash "$tool" 2>>errors)
    if [ "$?" -eq 0 ]; then
        unzip_tool="$tool"
        break
    fi
done
set -e

# exit if no unzip tools available
if [ -z "$unzip_tool" ]; then
    printf "\nNone of the supported tools for extracting zip archives (${unzip_tools_list[*]}) were found. "
    printf "Please install one of them and try again.\n\n"
    exit 4
fi

#detect the platform
cmd_suffix=""
OS="$(uname)"
case $OS in
  Linux)
    OS='linux'
    ;;
  Darwin)
    OS='mac'
    binTgtDir=/usr/local/bin
    cmd_suffix='mac'
    ;;
  *)
    OS='windows'
    ;;
esac

OS_type="$(uname -m)"
case "$OS_type" in
  x86_64|amd64)
    OS_type='amd64'
    cmd_suffix=${cmd_suffix}'64'
    ;;
  i?86|x86)
    OS_type='386'
    cmd_suffix=${cmd_suffix}'32'
    ;;
  aarch64|arm64)
    OS_type='arm64'
    cmd_suffix=${cmd_suffix}'64'
    ;;
  arm*)
    OS_type='arm'
    cmd_suffix=${cmd_suffix}'32'
    ;;
  *)
    OS_type='amd64'
    ;;
esac


#download and unzip
version='1.7.16'
fileName=ossutil-v${version}-${OS}-${OS_type}.zip
#https://gosspublic.alicdn.com/ossutil/1.7.16/ossutil-v1.7.16-windows-amd64.zip
download_link="https://gosspublic.alicdn.com/ossutil/${version}/${fileName}"
echo 'download from '${download_link}
curl -OfsS "$download_link"
unzip_dir="."
# there should be an entry in this switch for each element of unzip_tools_list
case "$unzip_tool" in
  'unzip')
    unzip -j -o "$fileName" -d "$unzip_dir"
    ;;
  '7z')
    7z x "$fileName" "-o$unzip_dir"
    ;;
  'busybox')
    mkdir -p "$unzip_dir"
    busybox unzip "$ossutil_zip" -d "$unzip_dir"
    ;;
esac

case "$OS" in
  'linux')
    #binary
    mv ./ossutil64 ossutil
    chmod a+x ./ossutil
    ;;
  'mac')
    mv ./ossutilmac64 ossutil
    chmod a+x ./ossutil
    ;;
  *)
    mv ./ossutil64.exe ossutil
esac


#update version variable post install
version=$(ossutil --version 2>>errors | head -n 1)

echo "${version} has successfully download."
exit 0;
