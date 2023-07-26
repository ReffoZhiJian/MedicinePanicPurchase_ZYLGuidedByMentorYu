# MedicinePanicPurchase_ZYLGuidedByMentorYu

#### 项目源码地址:

- 码云Gittee地址: https://gitee.com/reffozhijian/MedicinePanicPurchase_ZYLGuidedByMentorYu

- GitHub地址: https://github.com/reffozhijian/MedicinePanicPurchase_ZYLGuidedByMentorYu


#### 介绍：
2023.05-2023.08———————医疗数字化系统药品抢购模块———————后端开发（后端开发-ZYL；指导-余导师）

#### 我的成果：
- 使用Redis实现验证码登录、药品排行等功能，编码具体解决击穿、穿透、雪崩问题；
- 使用分布式锁、Lua脚本、Redisson实现紧缺药品的并发抢购功能，防止超卖及囤药；
- 使用Stream消息队列优化药品下单速度，使用Feed流实现药品上新的推送提醒功能。


#### 安装教程
1.  在src/main/resources/ **application.yml** 文件中修改Redis、MySQL的账户地址及密码等（修改为自己的）；
2.  在自己的本地数据库新建MedicinePanicPurchase数据库，并在该数据库下运行src/main/resources/db/ **MedicinePanicPurchase.sql** 文件，启动自己在yml文件中相应的Redis等配置；
3.  在IDEA中运行src/main/java/com/zylpractice/MedicinePanicPurchase/**MedicinePanicPurchaseApplication**文件。
