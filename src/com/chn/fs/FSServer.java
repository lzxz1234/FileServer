/**
 * FileServer
 * @title FSServer.java
 * @package com.chn.fs
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-上午10:05:04
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import code.google.nfs.rpc.netty4.server.Netty4Server;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;
import code.google.nfs.rpc.server.Server;
import code.google.nfs.rpc.server.ServerProcessor;

import com.chn.fs.message.DeleteRequest;
import com.chn.fs.message.ExistRequest;
import com.chn.fs.message.ReadRequest;
import com.chn.fs.message.Request;
import com.chn.fs.message.WriteRequest;
import com.chn.fs.util.BlockLock;
import com.chn.fs.util.Cfg;
import com.chn.fs.util.LogAgent;
import com.chn.fs.util.MD;
import com.chn.fs.worker.BerkeleyBasedFileSystemWorker;
import com.chn.fs.worker.ExistsFileWorker;
import com.chn.fs.worker.WorkerChain;
import com.chn.fs.worker.WorkerChain.Worker;

/**
 * @class FSServer
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FSServer implements ConstantCode, ServletContextListener {

    private Logger log = Logger.getLogger(FSServer.class);
    
    private Server server;
    private List<Worker> workerList = new ArrayList<Worker>();
    
    private abstract class DefaultProcessor<T extends Request> implements ServerProcessor {

        @Override
        public Object handle(Object request) throws Exception {
            
            @SuppressWarnings("unchecked")
            T req = (T) request;
            try {
                BlockLock.lock(req.getDigest());
                LogAgent.reset();
                
                return this.realWork(req);
            } catch (Exception e) {
                log.info("[执行请求失败]", e);
                return RESPONSE_CODE_FALSE;
            } finally {
                LogAgent.commit(MD.getHexString(req.getDigest()));
                BlockLock.unlock(req.getDigest());
            }
        }
        public abstract Object realWork(T req);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        
        for(Worker worker : workerList) {
            worker.destroy();
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        
        Cfg conf = Cfg.getCfg("/fileserver.properties");
        int port = conf.get(int.class, "server.port");
        int threadCount = conf.get(int.class, "server.threads");
        
        BlockLock.init(128);
        this.server = new Netty4Server(threadCount);
        this.workerList.add(new ExistsFileWorker().init(conf));
        this.workerList.add(new BerkeleyBasedFileSystemWorker().init(conf));
        
        this.server.registerProcessor(SimpleProcessorProtocol.TYPE, ExistRequest.class.getName(), new DefaultProcessor<ExistRequest>() {
            @Override
            public Object realWork(ExistRequest req) {
                boolean flag = new WorkerChain(workerList).doCheck(req);
                return flag ? RESPONSE_CODE_TRUE : RESPONSE_CODE_FALSE;
            }
        });
        this.server.registerProcessor(SimpleProcessorProtocol.TYPE, WriteRequest.class.getName(), new DefaultProcessor<WriteRequest>() {
            @Override
            public Object realWork(WriteRequest req) {
                new WorkerChain(workerList).doAdd(req);
                return RESPONSE_CODE_TRUE;
            }
        });
        this.server.registerProcessor(SimpleProcessorProtocol.TYPE, ReadRequest.class.getName(), new DefaultProcessor<ReadRequest>() {
            @Override
            public Object realWork(ReadRequest req) {
                return new WorkerChain(workerList).doGet(req);
            }
        });
        this.server.registerProcessor(SimpleProcessorProtocol.TYPE, DeleteRequest.class.getName(), new DefaultProcessor<DeleteRequest>() {
            @Override
            public Object realWork(DeleteRequest req) {
                new WorkerChain(workerList).doDel(req);
                return RESPONSE_CODE_TRUE;
            }
        });
        
        try {
            server.start(port, null);
            log.info("FsServer[" + port + "]启动成功。");
        } catch (Exception e) {
            log.error("FsServer[" + port + "]启动失败！", e);
        }
    }
    
}
