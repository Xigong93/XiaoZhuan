# 构建说明

## 如何构建项目
1. IntelliJ idea 2024,需安装Compose 插件
2. 下载源码后，使用idea导入
3. 等待依赖下载完成，即可运行

## Windows 电脑
如果要编译安装包，需要在系统语言设置中，全局开启utf-8编码，不然打包会报错或乱码。

## 打包命令

- 不支持交叉编译，需要在Windows和Mac 分别构建对应的安装包

- 需安装JDK 17，并配置JAVA_HOME
### Windows
```shell
./gradlew packageWindows
```

### Mac
```shell
chmod +x ./gradlew && ./gradlew packageMac
```

安装包在`build/packages`目录下

## Api传包文档

- 华为 https://developer.huawei.com/consumer/cn/doc/AppGallery-connect-Guides/agcapi-updateappinfo-0000001158245317
- 小米 https://dev.mi.com/distribute/doc/details?pId=1134
- OPPO https://open.oppomobile.com/new/developmentDoc/info?id=10998
- VIVO https://dev.vivo.com.cn/documentCenter/doc/327
- 荣耀 https://developer.honor.com/cn/doc/guides/101359