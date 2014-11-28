/**
 * FileServer
 * @title ExistsFileWorker.java
 * @package com.chn.fs.worker
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-上午9:53:14
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
import com.chn.fs.util.Cfg;
import com.chn.fs.util.KryoSerializer;
import com.chn.fs.util.LogAgent;
import com.chn.fs.worker.WorkerChain.Worker;

/**
 * @class ExistsFileWorker
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class ExistsFileWorker implements Worker {

    private BekeleyRepository fileCounts;//SHA - Count
    
    /* (non-Javadoc)
     * @see com.chn.fs.worker.WorkerChain.Worker#init(com.chn.fs.util.Cfg)
     */
    @Override
    public Worker init(Cfg cfg) {
        
        String berkeleydbLocation = cfg.get("fs.berkeleydb.root");
        String berkeleydbName = cfg.get("fs.berkeleydb.sha2count.name");
        if(berkeleydbLocation == null || berkeleydbLocation.length() == 0)
            throw new FSException("fs.berkeleydb.root 配置项不存在!");
        if(berkeleydbName == null || berkeleydbName.length() == 0)
            throw new FSException("fs.berkeleydb.sha2count.name 配置项不存在!");
        
        fileCounts = new BekeleyRepository(berkeleydbLocation, berkeleydbName);
        return this;
    }

    /* (non-Javadoc)
     * @see com.chn.fs.worker.WorkerChain.Worker#doAdd(com.chn.fs.message.WriteRequest, com.chn.fs.worker.WorkerChain)
     */
    @Override
    public void doAdd(WriteRequest request, WorkerChain chain) {
        
        byte[] contentSha = request.getDigest();
        //重复性检测
        byte[] countBytes = fileCounts.get(contentSha);
        if(countBytes != null && countBytes.length > 0) {
            int currentCount = KryoSerializer.deserialize(countBytes, int.class);
            fileCounts.set(contentSha, KryoSerializer.serialize(currentCount + request.getAmount()));
            LogAgent.info("[EXISTS][ADD][文件已存在]");
        } else{
            chain.doAdd(request);
            fileCounts.set(contentSha, KryoSerializer.serialize(request.getAmount()));
        }
    }

    /* (non-Javadoc)
     * @see com.chn.fs.worker.WorkerChain.Worker#doGet(com.chn.fs.message.ReadRequest, com.chn.fs.worker.WorkerChain)
     */
    @Override
    public byte[] doGet(ReadRequest request, WorkerChain chain) {
        
        return chain.doGet(request);
    }

    /* (non-Javadoc)
     * @see com.chn.fs.worker.WorkerChain.Worker#doDel(com.chn.fs.message.DeleteRequest, com.chn.fs.worker.WorkerChain)
     */
    @Override
    public void doDel(DeleteRequest request, WorkerChain chain) {
        
        byte[] contentSha = request.getDigest();
        byte[] countBytes = fileCounts.get(contentSha);
        if(countBytes != null && countBytes.length > 0) {
            int currentCount = KryoSerializer.deserialize(countBytes, int.class);
            if(currentCount == 1) {
                fileCounts.delete(contentSha);
                chain.doDel(request);
            } else {
                fileCounts.set(contentSha, KryoSerializer.serialize(currentCount - 1));
                LogAgent.info("[EXISTS][DEL][文件数目减一后返回]");
            }
        }
    }

    /* (non-Javadoc)
     * @see com.chn.fs.worker.WorkerChain.Worker#doCheck(com.chn.fs.message.ExistRequest, com.chn.fs.worker.WorkerChain)
     */
    @Override
    public boolean doCheck(ExistRequest request, WorkerChain chain) {
        
        byte[] contentSha = request.getDigest();
        //重复性检测
        byte[] countBytes = fileCounts.get(contentSha);
        if(countBytes != null && countBytes.length > 0) {
            int currentCount = KryoSerializer.deserialize(countBytes, int.class);
            if(currentCount > 0) {
                fileCounts.set(contentSha, KryoSerializer.serialize(currentCount + request.getAmount()));
                LogAgent.info("[EXISTS][CHECK][文件数目加" + request.getAmount() + "后返回]");
                return true;
            }
        }
        return chain.doCheck(request);
    }

    /* (non-Javadoc)
     * @see com.chn.fs.worker.WorkerChain.Worker#destroy()
     */
    @Override
    public Worker destroy() {
        
        if(fileCounts != null) fileCounts.destroy();
        return this;
    }

}
