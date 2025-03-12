#!/usr/bin/env bash
echo "yarn version: $(yarn -v)"
bash -c "cd admin-web/src/main/frontend/ && yarn install --registry=https://registry.npmmirror.com"
bash -c "cd blog-web/src/main/frontend/ && yarn install --registry=https://registry.npmmirror.com"
