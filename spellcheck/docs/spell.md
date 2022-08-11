# 拼写检查
## 1) 词库来源

词库来源在数据库中，以方便扩展，只要每天都会产生一个当天日期相关的搜索词库表，包含相关信息

## 2) 建立索引

因为一个拼音是对应多个中文词的，所以建立个数据结构，以ConCurrentHashMap<String,CopyOnWriteArrayList<integer>>的形式，存放在内存中，以便快速查询。
中文转化相应的拼写，使用第三方包pinyin4j，将中文转换为相应的拼音与拼音缩写。
比如：中国-> zhongguo, zhong guo, zg(拼音缩写)

相应的索引结构为：
Zhongguo->中国(文档ID)
//Zhong guo ->中国(文档ID)
Zg-> 中国(文档ID)

## 3) 检查并返回结果

用户输入拼音，zhongguo

通过Map找到对应的中文集合，其中以某个权重来排序（可能是以搜索量），并返回某个数量的结果，结果以字符串数组形式返回。
当有多个拼查组成的时候 ，先将其各自分开，然后采用正向最大匹配或者，反向最大匹配获得最佳的结果。

比如输入的是：zhongguo ren

zhongguo ren-> zhong guo ren-> zhongguoren-》中国人
yizhenjianxie de-》yi zhen jian xie de
yi zhen jian xie de-> yizhenjianxiede 找不到
yi zhen jian xie-》yizhenjianxie->一针见血   （返回最大匹配）

中文检查包括中文纠错与汉字拼音纠错