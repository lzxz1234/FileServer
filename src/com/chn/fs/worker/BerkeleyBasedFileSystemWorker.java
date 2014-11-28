/**
 * FileServer
 * @title BerkeleyBasedFileSystemWorker.java
 * @package com.chn.fs.worker
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-上午9:29:29
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.worker;

import com.chn.fs.exception.FSException;
import com.chn.fs.message.DeleteRequest;
import com.chn.fs.message.ExistRequest;
import com.chn.fs.message.ReadRequest;
import com.chn.fs.message.WriteRequest;
import com.chn.fs.repository.BekeleyRepository;
import com.chn.fs.repository.FileSystemRepository;
import com.chn.fs.repository.Repository;
import com.chn.fs.util.Cfg;
import com.chn.fs.util.KryoSerializer;
import com.chn.fs.worker.WorkerChain.Worker;

/**
 * @class BerkeleyBasedFileSystemWorker
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class BerkeleyBasedFileSystemWorker implements Worker {

    private Repository<String, byte[]> repository;
    private BekeleyRepository shaToFiles;//SHA - FileName
    
    @Override
    public Worker init(Cfg cfg) {

        String fsRoot = cfg.get("fs.root");
        String berkeleydbLocation = cfg.get("fs.berkeleydb.root");
        String berkeleydbName = cfg.get("fs.berkeleydb.sha2file.name");
        if ((fsRoot == null) || "".equals(fsRoot.trim()))
            throw new FSException("fs.root 配置项不存在!");
        if(berkeleydbLocation == null || berkeleydbLocation.length() == 0)
            throw new FSException("fs.berkeleydb.root 配置项不存在!");
        if(berkeleydbName == null || berkeleydbName.length() == 0)
            throw new FSException("fs.berkeleydb.sha2file.name 配置项不存在!");
        
        repository = new FileSystemRepository(fsRoot);
        shaToFiles = new BekeleyRepository(berkeleydbLocation, berkeleydbName);
        return this;
    }

    @Override
    public void doAdd(WriteRequest request, WorkerChain chain) {
        
        byte[] contentSha = request.getDigest();
        String fileNameOndisk = this.repository.add(request.getBytes());
        
        shaToFiles.set(contentSha, KryoSerializer.serialize(fileNameOndisk));
    }

    @Override
    public byte[] doGet(ReadRequest request, WorkerChain chain) {
        
        byte[] contentSha = request.getDigest();
        byte[] fileNameOndiskArray = this.shaToFiles.get(contentSha);
        
        if(fileNameOndiskArray == null || fileNameOndiskArray.length == 0)
            return null; //文件不存在 
        return this.repository.get(KryoSerializer.deserialize(fileNameOndiskArray, String.class));
    }

    @Override
    public void doDel(DeleteRequest request, WorkerChain chain) {
        
        byte[] contentSha = request.getDigest();
        byte[] fileNameOndiskArray = this.shaToFiles.get(contentSha);
        this.repository.delete(KryoSerializer.deserialize(fileNameOndiskArray, String.class));
        this.shaToFiles.delete(contentSha);
    }

    @Override
    public Worker destroy() {
        
        if(shaToFiles != null) shaToFiles.destroy();
        return this;
    }

    @Override
    public boolean doCheck(ExistRequest request, WorkerChain chain) {
        
        return chain.doCheck(request);
    }
    
}
