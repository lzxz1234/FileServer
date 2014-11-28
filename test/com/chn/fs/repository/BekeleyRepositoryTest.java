/**
 * FileServer
 * @title BekeleyRepositoryTest.java
 * @package com.chn.fs.repository
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-下午5:31:32
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.repository;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.chn.fs.util.BlockLock;

/**
 * @class BekeleyRepositoryTest
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class BekeleyRepositoryTest {

    private BekeleyRepository repo;

    String storLocation = "D:\\berkeley-repo-rest";
    String dbName = "testdb";
    int testThread = 4;
    
    @Before
    public void before() {

        new File(storLocation).mkdirs();
        repo = new BekeleyRepository(storLocation, dbName);
        BlockLock.init(128);
    }

    @Test
    public void test() throws InterruptedException, UnsupportedEncodingException {
        
        final CountDownLatch latch = new CountDownLatch(testThread);
        final CyclicBarrier barrier = new CyclicBarrier(testThread);
        final ConcurrentHashMap<String, byte[]> map = new ConcurrentHashMap<String, byte[]>();
        for(int i = 0; i < testThread; i ++) 
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        
                        Random random = new Random();
                        byte[] content = null;
                        String[] keys = null;
                        String key = null;
                        
                        for(int i = 0; i < 1000000; i ++) {
                            switch(random.nextInt(4)) {
                            case 0://add
                                content = new byte[100];
                                random.nextBytes(content);
                                map.put(new String(repo.add(content), "ISO-8859-1"), content);
                                break;
                            case 1://get
                                keys = map.keySet().toArray(new String[0]);
                                if(keys.length == 0) continue;
                                key = keys[random.nextInt(keys.length)];
                                BlockLock.lock(key.getBytes("ISO-8859-1"));
                                Assert.assertArrayEquals(map.get(key), repo.get(key.getBytes("ISO-8859-1")));
                                BlockLock.unlock(key.getBytes("ISO-8859-1"));
                                break;
                            case 2://delete
                                keys = map.keySet().toArray(new String[0]);
                                if(keys.length == 0) continue;
                                key = keys[random.nextInt(keys.length)];
                                BlockLock.lock(key.getBytes("ISO-8859-1"));
                                map.remove(key);
                                repo.delete(key.getBytes("ISO-8859-1"));
                                BlockLock.unlock(key.getBytes("ISO-8859-1"));
                                break;
                            case 3://set
                                keys = map.keySet().toArray(new String[0]);
                                if(keys.length == 0) continue;
                                key = keys[random.nextInt(keys.length)];
                                content = new byte[100];
                                random.nextBytes(content);
                                BlockLock.lock(key.getBytes("ISO-8859-1"));
                                map.put(key, content);
                                repo.set(key.getBytes("ISO-8859-1"), content);
                                BlockLock.unlock(key.getBytes("ISO-8859-1"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            }).start();
        latch.await();
        
        Iterator<Entry<String, byte[]>> it = map.entrySet().iterator();
        Entry<String, byte[]> entry = null;
        while(it.hasNext()) {
            entry = it.next();
            Assert.assertArrayEquals(repo.get(entry.getKey().getBytes("ISO-8859-1")), entry.getValue());
        }
    }

    @After
    public void after() throws IOException {

        repo.destroy();
        FileUtils.deleteDirectory(new File(storLocation));
    }

}
