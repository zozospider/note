
- [Linux 内核的文件 Cache 管理机制介绍](https://www.ibm.com/developerworks/cn/linux/l-cache/index.html)
- [计算机底层知识拾遗（六）理解页缓存page cache和地址空间address_space](https://blog.csdn.net/ITer_ZC/article/details/44195731)
- [Page Cache, the Affair Between Memory and Files](https://manybutfinite.com/post/page-cache-the-affair-between-memory-and-files/)
- [有关zero-copy,mmap,direct-memory的一切](https://www.jianshu.com/p/03852a291c56)
- [浅谈 Linux 的 Zero Copy 技术](http://senlinzhan.github.io/2017/03/25/%E7%BD%91%E7%BB%9C%E7%BC%96%E7%A8%8B%E4%B8%AD%E7%9A%84zerocpoy%E6%8A%80%E6%9C%AF/)
- [零复制(zero copy)技术](https://www.cnblogs.com/f-ck-need-u/p/7615914.html)
- [深入剖析Linux IO原理和几种零拷贝机制的实现](https://juejin.im/post/5d84bd1f6fb9a06b2d780df7)
- [什么是零拷贝](https://www.cnblogs.com/victor2302/p/11381597.html) : 
也就是说，sendfile() 只是 splice() 的一个子集，在 Linux 2.6.23 中，sendfile() 这种机制的实现已经没有了，但是这个 API 以及相应的功能还存在，只不过 API 以及相应的功能是利用了 splice() 这种机制来实现的。总体来讲splice()是Linux 2.6.23 内核版本中替换sendfile()系统调用的一个方法，它不仅支持文件到Socket的直接传输，也支持文件到文件的直接传输I/O，但是其底层的传输过程和sendfile()并无区别。


- [The Linux Page Cache and pdflush](http://web.archive.org/web/20160518040713/http://www.westnet.com/~gsmith/content/linux-pdflush.htm)
- [Page cache](https://en.wikipedia.org/wiki/Page_cache)
