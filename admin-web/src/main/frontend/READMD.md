### admin-web-frontend

1. error enospc system limit for number of file watchers reached watch '/app/public'

```
echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```