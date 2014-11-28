/**
 * FileServer
 * @title BlockLock.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:15:19
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class BlockLock
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class BlockLock {

    private static int mask;
    private static ReentrantLock[] locks;
    
    public static void init(int count) {
        
        int reallyCount, maskIndex = 0;
        while((reallyCount = 1 << maskIndex ++) < count);
        count = reallyCount;
        mask = reallyCount - 1;
        locks = new ReentrantLock[count];
        
        for(int i = 0; i < count; i ++) 
            locks[i] = new ReentrantLock();
    }
    
    public static boolean hasQueuedThreads(byte[] bytes) {
        
        int simpleHash = simpleHash(bytes);
        return locks[simpleHash & mask].hasQueuedThreads();
    }
    
    public static void lock(Serializable seri) {
        
        lock(KryoSerializer.serialize(seri));
    }
    
    public static void lock(byte[] bytes) {
        
        int simpleHash = simpleHash(bytes);
        locks[simpleHash & mask].lock();
    }
    
    public static void unlock(Serializable seri) {
        
        unlock(KryoSerializer.serialize(seri));
    }
    
    public static void unlock(byte[] bytes) {
        
        int simpleHash = simpleHash(bytes);
        locks[simpleHash & mask].unlock();
    }
    
    private static int simpleHash(byte[] bytes) {
        
        int simpleHash = 0;
        for(byte b : bytes) 
            simpleHash = 31 * simpleHash + b;
        return simpleHash;
    }
    
}
