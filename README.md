### Mybatis
1. 插件版本 1.3.2好用
2. 大坑 generator文件内的标签 网上大量带有commentGenerator导致报错 以为是顺序错误 最终按照官方顺序可行 http://www.mybatis.org/generator/configreference/xmlconfig.html
3. 独立项目使用效果好 在其他已有maven的项目里使用会有其他插件混合问题
4. CDATA问题直接用转移字符替换效果较好 http://www.w3school.com.cn/xml/xml_cdata.asp
5. XML DTD 学习 https://stackoverflow.com/questions/37019672/mybatis-generators-bug-configuration-items-should-be-ordered
6. idea使用参考 https://www.cnblogs.com/yjmyzz/p/4210554.html http://www.cnblogs.com/smileberry/p/4145872.html
7. 建表语句见resources包

### Sharding
1. http://shardingsphere.io/document/current/cn/manual/sharding-jdbc/usage/read-write-splitting/
2. http://shardingsphere.io/document/current/cn/manual/sharding-jdbc/configuration/config-yaml/
3. http://shardingsphere.io/document/current/cn/features/sharding/other-features/inline-expression/
4. https://www.jianshu.com/p/639253764705