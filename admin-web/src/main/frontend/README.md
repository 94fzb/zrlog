## admin-web-frontend

### error enospc system limit for number of file watchers reached watch '/app/public'

Answer: [Inotify Watches Limit (Linux)](https://intellij-support.jetbrains.com/hc/en-us/articles/15268113529362-Inotify-Watches-Limit-Linux)

```
echo fs.inotify.max_user_watches=1048576 | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

### check `yarn.lock`

```shell
less yarn.lock |grep "https://" | cut -d '/' -f 3|sort|uniq
```

### fetch too slow

```
yarn install --registry=https://registry.npmmirror.com
```


### Using Prettier as the default formatter

![](dev/webstorm-prettier-settings.png)