# 拼写检查 

程序在启动后会首先加载所有词

## 设计
* 中文拼写检查

  ![图片](https://github.com/yzygenuine/spellcheck/blob/master/spellcheck/docs/images/pinyin_spell.jpg)

* 拼音拼写检查

  ![图片](https://github.com/yzygenuine/spellcheck/blob/master/spellcheck/docs/images/pinyin_spell.jpg)
  
## 接口
 
* 重新加载所有词的请求,会加载项目文件
http://${host}:${post}/status?oper=reload


* 拼写检查的请求URL
http://${host}:${post}/spellcheck?input=${word}&sugNum=10&type=cn
