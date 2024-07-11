

# 小篆传包 <img src="./img/icon.png" alt="图标" style="with:30px; height:30px"/>
<a href="./doc/ENGLISH.md">English README</a>

一键上传Apk到多个应用市场，开源，免费

## 应用界面截图

### 1. 首页
<img src="./img/home.png" alt="首页"/>

### 2. 提交新版本页面
<img src="./img/submit.png" alt="提交新版本页面"/>

### 3. 新增APP页面
<img src="./img/add.png" alt="新增APP页面"/>
<img src="./img/huawei.png" alt="华为渠道配置"/>

## 特点：

1. 使用应用市场提供的Api传包功能，安全，稳定，快捷
2. 代码开源，完全免费，不会向第三方上传任何相关账号信息
3. 基于Compose Desktop 开发，支持Windows 和Mac OS


## 如何使用
1. 新增APP

2. 渠道包功能
3. 版本更新功能


## 下载地址
<a href="https://gitee.com/xigong93/XiaoZhuan/release">从Gitee下载(推荐)</a>

<a href="https://github.com/xigong93/XiaoZhuan/release">从Github下载</a>

## 功能限制

1. 仅支持华为、小米、OPPO、VIVO、荣耀 5个应用市场
2. 仅支持32位和64位合并版包，暂不支持分包上传
3. 仅支持更新已上架的APP，不支持新增APP

## 自己编译
<a href="./doc/Develop.md">请点击这里查看开发文档</a>

## 常见问题的解决

<a href="./doc/TroubleShotting.md">点击这里查看常见问题</a>


## 已知问题
1. 上传新版本后，然后获取应用市场审核状态，小米会显示上个版本正在审核中，OPPO会显示上个版本已上线
a. 小米的传包Api提供的是不显示当前审核的版本，只返回线上的最新版本
b. OPPO 提交新版本后，有几分钟的延迟，过几分钟后才会显示新版本正在审核中