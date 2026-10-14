## 测试

### 升级逻辑（war）

#### root

```
wget https://www.zrlog.com/install/zrlog-upgrade-jakarta-war.sh
```

#### blog

```
wget https://www.zrlog.com/install/zrlog-upgrade-jakarta-war-sub.sh
```

### 最新包

```
wget https://www.zrlog.com/install/zrlog-last-war.sh
```

### 后端隔离测试

运行 `./mvnw -q -U -pl zrlog-web -am test`。`-U` 刷新独立工程的远端 SNAPSHOT，避免缓存中的旧接口与主工程新源码不匹配；CI 覆盖率验证也使用该参数。共享工具来自 base 的 `zrlog-test-support`（test scope）。`InstalledTestSite` 通过真实 InstallService 创建临时 H2 站点，再读取生成的数据库配置验证主工程启动；不手工生成安装锁或伪造缓存。统一标准见 `zrlog-base/docs/test-support.md`。
