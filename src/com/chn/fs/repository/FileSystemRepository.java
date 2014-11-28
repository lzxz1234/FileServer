/**
 * FileServer
 * @title FileSystemRepository.java
 * @package com.chn.fs.repository
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:24:19
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.chn.fs.counter.Counter;
import com.chn.fs.counter.FileSystemCounter;
import com.chn.fs.exception.FSException;
import com.chn.fs.util.LogAgent;

/**
 * @class FileSystemRepository
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FileSystemRepository implements Repository<String, byte[]> {

    private Logger log = Logger.getLogger(FileSystemRepository.class);
    
    private Counter fileCounter;//文件生成计数器
    
    public FileSystemRepository(String fsRoot) {
        
        this.fileCounter = new FileSystemCounter(fsRoot, 0, 999);
        log.info("文件池初始化成功： " + fsRoot);
    }

    /* (non-Javadoc)
     * @see com.chn.fs.repository.Repository#add(java.lang.Object)
     */
    @Override
    public String add(byte[] content) {
        
        File nextFile = fileCounter.nextFile();
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(nextFile));
            IOUtils.write(content, os);
            os.flush();
            LogAgent.info("[FILE][ADD]写文件成功：" + nextFile.getPath());
            return nextFile.getAbsolutePath();
        } catch (IOException e) {
            throw new FSException(e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    /* (non-Javadoc)
     * @see com.chn.fs.repository.Repository#get(java.lang.Object)
     */
    @Override
    public byte[] get(String filePath) {
        
        InputStream is = null;
        try {
            File targetFile = new File(filePath);
            if(!targetFile.exists()) return null;
            
            is = new FileInputStream(targetFile);
            LogAgent.info("[FILE][READ]读取文件成功：" + targetFile.getPath());
            return IOUtils.toByteArray(new BufferedInputStream(is));
        } catch (IOException e) {
            throw new FSException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /* (non-Javadoc)
     * @see com.chn.fs.repository.Repository#delete(java.lang.Object)
     */
    @Override
    public void delete(String filePath) {
        
        File deleteFile = new File(filePath);
        FileUtils.deleteQuietly(deleteFile);
        LogAgent.info("[FILE][DEL]删除文件成功：" + deleteFile.getPath());
    }

}
