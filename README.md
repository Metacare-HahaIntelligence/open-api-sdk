# open-api-sdk

#### 介绍
提供哈哈云开放式Api的接入功能

#### 使用说明

1.  确定是在哪个环境使用Open-Api功能，从官网找出相应Open-Api基础地址(baseUrl)
2.  联系哈哈云运营人员，获取您所对应的AppKey&AppSecret
3.  在开发者文档中找出您要调用的Api路径(path)
4.  在您的代码中设置baseUrl, AppKey, AppSecret, path
5.  使用WssHttpHeader.java创建您的认证头，并在所有访问Open-Api的地方，都要携带该http头
6.  提供了调用的例子， 请参考Main.java

#### WSSE相关资料参考

1.  WSSE说明 [链接](https://www.oasis-open.org/committees/download.php/13392/wss-v1.1-spec-pr-UsernameTokenProfile-01.htm)
2.  WSSE抓包分析，参考1.3 WSSE认证部分  [链接](https://blog.csdn.net/haohaoge_jx/article/details/116223510)
3.  关于WSSE验证 [链接](https://blog.csdn.net/iteye_8658/article/details/81753214)
