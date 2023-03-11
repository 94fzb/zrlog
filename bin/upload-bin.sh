#!/bin/sh
pip3 install coscmd
coscmd config -a ${1} -s ${2} -b ${3} -e cos.accelerate.myqcloud.com
coscmd upload -r ${4} /