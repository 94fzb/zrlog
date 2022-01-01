### admin-web-frontend

1. error enospc system limit for number of file watchers reached watch '/app/public'

```
echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

check `yarn.lock`

```shell
less yarn.lock |grep "https://" | cut -d '/' -f 3|sort|uniq
```