# 安朗拨号认证客户端
## 测试环境
   + Windows10 (1703)
   + Ubuntu (16.04)
   + Mac Os (12.5.6)
   + 理论上来说，只要有Java环境就可以运行
   
### Java版本
   +  JDK1.8
   
### 使用方法
1. 进入releases,运行以下代码即可(**首次运行需要设置一些必要信息**):
```
java -jar supplicant.jar
```
2. 此后如需修改信息，请修改或删除`config.json`文件。
3. config.json文件配置如下(utf-8编码):
```
{
    
    "entry":"认证方式, 如:internet",
    "network":"网卡名称(如需启用nat，请删除此项), 如:Qualcomm Atheros AR946x Wireless Network Adapter",
    "natIp": "路由器IP",
    "natMac": "路由器MAC",
    "username":"账户名",
    "password":"密码",
    "dhcp":false,
    "version":"蝴蝶版本号, 如: 3.8.2"
}
```
4. 只提供了最基础的功能，如有其他需求可自行开发或提交issues。
#### 开发说明
使用IntelliJ IDEA打开本项目,删除`Applicantion.java`、`Config.java`，根据`Supplicant.java`提供的方法进行开发即可。
    
#### 参考资料
+ [https://github.com/xingrz/swiftz-protocal](https://github.com/xingrz/swiftz-protocal)
