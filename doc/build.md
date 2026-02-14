## 构建 ZrLog

> ZrLog 提供多种运行包 .zip .war .deb，可以运行在大部分操作系统上，甚至是 Android（需原生 arm64 的 Linux 环境） 和树莓派上

### shell 目录

```
├── java
│   ├── build-final-java.sh
│   ├── package-dev-java-zip.sh
│   └── package-java-zip.sh
├── native
│   ├── build-final-native.sh
│   ├── package-faas-zip.sh
│   ├── package-native-deb.sh
│   ├── package-native-zip.sh
│   └── package-native.sh
```