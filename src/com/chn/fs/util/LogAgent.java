/**
 * FileServer
 * @title LogAgent.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:32:57
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @class LogAgent
 * @author lzxz1234
 * @description 单独日志记录，只是为了将所有业务日志加上 SHA 前缀，便于前后查找
 * <br>注：有可能在调用打印的时候还未生成 sha
 * @version v1.0
 */
public class LogAgent {

    private static Logger log = Logger.getLogger(LogAgent.class);
    
    private static ThreadLocal<List<String>> tl = new ThreadLocal<List<String>>();
    
    public static void reset() {
        
        tl.set(new ArrayList<String>());
    }
    
    public static void debug(String log) {
        
        tl.get().add(log);
    }
    
    public static void info(String log) {
        
        tl.get().add(log);
    }
    
    public static void error(String log) {
        
        tl.get().add(log);
    }
    
    public static void commit(String sha) {
        
        if(tl.get() != null)
            for(String str : tl.get())
                log.info("[" + sha + "]" + str);
    }
    
}
