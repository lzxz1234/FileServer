# FileServer #

简易文件服务器，提供基本的存、取、删操作，并对相同文件的多次存放做合并处理，节省磁盘空间，基于 nfs-rpc 实现传输协议。

## 核心类 ##

- **FSServer**: 服务器启动类，依赖 Tomcat 启动时额外提供 HTTP 访问接口。
- **FSClient**: 全功能客户端类，提供全部存、取、删操作。
- **FSHttpClient**: HTTP 专用存文件接口，FSClient 的浅封装。

## 配置文件 ##

fileserver.properties：

    server.port=8081 --监听端口，NFS-RPC 监听，和 Tomcat 端口无关
    server.threads=10 --服务器处理线程数

	fs.root=e://fs/ --文件存储目录
	fs.berkeleydb.root=e://fs/berkeley  --BerkeleyDB 存储位置，主要用于文件摘要到文件路径的文件数目的映射
	fs.berkeleydb.sha2file.name=STORE.SHA.TO.FILE  --不用修改
	fs.berkeleydb.sha2count.name=STORE.SHA.TO.COUNT  --不用修改

## 使用样例 ##

可以参考 `FSHttpClient` 实现。

## 扩展 ##

### 关于缓存 ###

启动服务器时会初始化工作链，目前工作链仅两环：`ExistsFileWorker` 和 `BerkeleyBasedFileSystemWorker`，第一环决定文件是否存在及已经存在时的处理逻辑，第二环才会在磁盘上写入目标文件，需要缓存时另行添加缓存层。

### 关于 HTTP 下载接口 ###

目前下载接口是 `download.jsp` 提供的，它也是 `FSClient` 的浅封装，其中缺少 MIME 信息等内容，需要时在此处扩展。