拼写检查 布署

首先要设置好数据库相关信息

启动程序
将程序布置在tomcat服务中

程序在启动后会首先加载所有词

 启用定时服务 

定时读取文件数据写入数据库
运行命今为: 
http://${host}:${post}/words?oper=keyword2DB

从数据库中读取每天更新的数据来更新索引 
运行命令为：（每天更新搜索词库请求 ）
http://${host}:${post}/words?oper=loadOneDay
 
 
重新加载所有词的请求
http://${host}:${post}/words?oper=reload


拼写检查的请求URL
http://${host}:${post}/spellcheck?input=${word}&sugNum=10