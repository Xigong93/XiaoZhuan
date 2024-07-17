# 功能介绍

## 一. 新增APP

### 1. 基本信息的填写
<img src="../img/add.png" style="border:2px solid #f4f4f4;border-radius:20px"/>

**操作说明**：
1. ApplicationId 应用包名请正确填写，否则可能无法正常进行后续操作，如不懂可询问开发人员
2. 开启渠道包，此设置通常用于统计各个应用市场的用户新增情况，如Umeng
>开启后，每个应用市场需提供一个安装包，程序会根据设置的文件名中的标识自动识别（fileNameIdentify)



### 2. Api传包相关参数的获取
<b>请使用应用市场的主账号获取Api传包相关操作，不要使用子账号，否则可能权限不足，或没有操作入口</b>

####  2.1 华为

**操作截图**：
<img src="../img/huawei/01.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/huawei/02.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/huawei/03.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/huawei/04.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/huawei/05.png" style="border:2px solid #f4f4f4;border-radius:20px"/>

**操作说明**：
1. 打开网页：
https://developer.huawei.com/consumer/cn/service/josp/agc/index.html#/myApp
2. 点击顶部的“全部服务”
3. 找到“开发工具”中的“Connect API”
4. 点击创建
5. 名称任意填写，项目不要选，使用默认N/A即可，角色选择“APP管理员”
6. 复制“客户端ID”和“密钥” 到“小篆传包”APP中

#### 2.2 小米

**操作截图**：
<img src="../img/mi/01.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/mi/02.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/mi/03.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/mi/04.png" style="border:2px solid #f4f4f4;border-radius:20px"/>

**操作说明**：
1. 打开网页：
https://dev.mi.com/platform/console
2. 点击“应用游戏”
3. 找到要操作的APP，点击“管理”
4. 点击下方的“自动发布接口”
5. 下载公钥文件，并获取密钥，然后复制密钥，（上方是公钥，下方是私钥）
6. 填写相关参数到“小篆传包”APP中


#### 2.3 OPPO
**操作截图**：
<img src="../img/oppo/01.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/oppo/02.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/oppo/03.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/oppo/04.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/oppo/05.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/oppo/06.png" style="border:2px solid #f4f4f4;border-radius:20px"/>

**操作说明**：

OPPO 应用市场的操作稍微有点复杂

1. 打开网页：
https://open.oppomobile.com/new/ecological/app
2.  点击顶部的“产品” ，然后找到并点击“我的API”
3. 点击左上角“Api服务”右边的导航条，我的显示为“服务端应用”，你的页面可能不一定是这个，会弹出应用的编辑弹框
4. 在弹框中，点击右下角的“新建应用”
5. 新增应用，选择“服务端应用”，然后按截图选择即可
6. 然后返回“选择应用”弹框，再点击“服务端应用”，找到刚刚创建的应用，复制"client_id"和“client_secret"填写到”小篆传包“APP中


#### 2.4 VIVO
**操作截图**：
<img src="../img/vivo/01.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/vivo/02.png" style="border:2px solid #f4f4f4;border-radius:20px"/> 

**操作说明**：
1. 打开网页：
https://dev.vivo.com.cn/contacts/details
2. 点击左边“api管理”
3. 复制对应的“access_key”和“access_secret”填写到”小篆传包“APP中
4. 注意我的截图是已经激活这个功能，你的账号首次是需要激活这个功能，可能会报错，刷新页面即可正常获取参数


#### 2.5 荣耀
**操作截图**：
<img src="../img/honor/01.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/honor/02.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/honor/03.png" style="border:2px solid #f4f4f4;border-radius:20px"/>
<img src="../img/honor/04.png" style="border:2px solid #f4f4f4;border-radius:20px"/>

**操作说明**：
1. 打开网页：
https://developer.honor.com/cn
2. 点击右上角“管理中心”
3. 选择左边的“凭证”
4. 点击右边的“申请凭证”
5. 复制“Client_id”和“密钥” 填写相关参数到“小篆传包”APP中

