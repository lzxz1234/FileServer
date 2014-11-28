/**
 * FileServer
 * @title BekeleyRepository.java
 * @package com.chn.fs.repository
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:37:32
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.repository;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.chn.fs.worker.BerkeleyBasedFileSystemWorker;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * @class BekeleyRepository
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class BekeleyRepository implements Repository<byte[], byte[]> {

    private Logger log = Logger.getLogger(BerkeleyBasedFileSystemWorker.class);
    
    private String dbName;
    private Environment dbEnvironment;
    private Database database;
    
    public BekeleyRepository(String berkeleydbLocation, String berkeleydbName) {
        
        this.dbName = berkeleydbName;
        File storDir = new File(berkeleydbLocation);
        if(!storDir.exists()) storDir.mkdirs();
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreateVoid(true);
        envConfig.setTransactionalVoid(false);
        envConfig.setCacheSizeVoid(512 * 1024 * 1024);
        
        dbEnvironment = new Environment(storDir, envConfig);
        
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreateVoid(true);
        dbConfig.setTransactionalVoid(false);
        dbConfig.setSortedDuplicates(false);
        dbConfig.setDeferredWrite(true);
        
        database = dbEnvironment.openDatabase(null, dbName, dbConfig);
        log.info(String.format("BekeleyDb初始化成功：%s-%s", berkeleydbLocation, berkeleydbName));
    }

    /* (non-Javadoc)
     * @see com.chn.fs.repository.Repository#add(java.lang.Object)
     */
    @Override
    public byte[] add(byte[] content) {
        
        byte[] key = UUID.randomUUID().toString().getBytes();
        set(key, content);
        return key;
    }
    
    public void set(byte[] key, byte[] content) {
        
        DatabaseEntry _key = new DatabaseEntry(key);
        DatabaseEntry _value = new DatabaseEntry(content);
        
        this.database.put(null, _key, _value);
    }

    /* (non-Javadoc)
     * @see com.chn.fs.repository.Repository#get(java.lang.Object)
     */
    @Override
    public byte[] get(byte[] key) {
        
        DatabaseEntry _key = new DatabaseEntry(key);
        DatabaseEntry _value = new DatabaseEntry();
        OperationStatus status = this.database.get(null, _key, _value, LockMode.READ_COMMITTED);
        if(status == OperationStatus.SUCCESS)
            return _value.getData();
        return null;//NOTFOUND returns null
    }

    /* (non-Javadoc)
     * @see com.chn.fs.repository.Repository#delete(java.lang.Object)
     */
    @Override
    public void delete(byte[] key) {
        
        DatabaseEntry _key = new DatabaseEntry(key);
        this.database.delete(null, _key);//NOTFOUND and SUCCESS allis SUCCESS
    }

    public void destroy() {
        
        if(database != null) {
            database.close();
        }
        if(dbEnvironment != null) {
            dbEnvironment.sync();
            dbEnvironment.cleanLog();
            dbEnvironment.close();
        }
    }

}
