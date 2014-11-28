/**
 * FileServer
 * @title WorkerChain.java
 * @package com.chn.fs.worker
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-上午9:33:07
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.worker;

import java.util.Iterator;
import java.util.List;

import com.chn.fs.message.DeleteRequest;
import com.chn.fs.message.ExistRequest;
import com.chn.fs.message.ReadRequest;
import com.chn.fs.message.WriteRequest;
import com.chn.fs.util.Cfg;

/**
 * @class WorkerChain
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class WorkerChain {

    private Iterator<Worker> it;
    public WorkerChain(List<Worker> workerList) {
        it = workerList.iterator();
    }

    public void doAdd(WriteRequest request) {
        
        if(it.hasNext())
            it.next().doAdd(request, this);
    }
    public byte[] doGet(ReadRequest request) {

        return it.hasNext() ? it.next().doGet(request, this) : new byte[0];
    }
    public void doDel(DeleteRequest request) {
        
        if(it.hasNext())
            it.next().doDel(request, this);
    }
    public boolean doCheck(ExistRequest request) {
        
        return it.hasNext() ? it.next().doCheck(request, this) : false;
    }
    
    public static interface Worker {
        
        public abstract Worker init(Cfg cfg);
        
        public abstract void doAdd(WriteRequest request, WorkerChain chain);
        public abstract byte[] doGet(ReadRequest request, WorkerChain chain);
        public abstract void doDel(DeleteRequest request, WorkerChain chain);
        public abstract boolean doCheck(ExistRequest request, WorkerChain chain);
        
        public abstract Worker destroy();
        
    }
}
