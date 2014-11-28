/**
 * FileServer
 * @title FileSystemCounter.java
 * @package com.chn.fs.counter
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:17:44
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.counter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @class FileSystemCounter
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FileSystemCounter implements Counter {

    private final String ROOT;// Like : /storage/fs
    private final int loopFrom;
    private final int loopTo;
    private final String fileNameFormat;
    private volatile int currIndex;
    
    public FileSystemCounter(String root, int loopFrom, int loopTo) {
        
        if(root == null)
            throw new IllegalArgumentException("根路径不能为空!!");
        if(loopFrom > loopTo)
            throw new IllegalArgumentException("循环起始值不能大于结束值!!");
        
        root = root.replaceAll("[\\\\/]+$", "");
        this.ROOT = root;
        this.loopFrom = loopFrom;
        this.loopTo = loopTo;
        this.currIndex = loopFrom;
        this.fileNameFormat = "%s%0" + String.valueOf(loopTo).length() + "d";
    }
    
    /* (non-Javadoc)
     * @see com.aspire.nm.zxt.fs.counter.FileCounter#nextFile()
     */
    @Override
    public File nextFile() {
        
        String timePath = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int currIndex = getCurrIndex();
        File dir = parseFolderFromFileName(timePath);
        if(!dir.exists()) dir.mkdirs();
        return new File(dir, String.format(fileNameFormat, timePath, currIndex));
    }
    
    public File parseFolderFromFileName(String fileName) {
        
        StringBuilder sb = new StringBuilder(ROOT.length() + 3 + fileName.length());
        sb.append(ROOT);
        sb.append("/");
        sb.append(fileName, 0, 10);
        sb.append("/");
        sb.append(fileName, 10, 14);
        sb.append("/");
        return new File(sb.toString());
    }
    
    private synchronized int getCurrIndex() {
        
        return currIndex = (currIndex >= loopTo ? loopFrom : currIndex + 1);
    }

}
