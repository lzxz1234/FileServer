/**
 * FileServer
 * @title FSException.java
 * @package com.chn.fs.exception
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:20:07
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.exception;

/**
 * @class FSException
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FSException extends RuntimeException {

    private static final long serialVersionUID = 337953627113751493L;

    public FSException(String msg) {
        
        super(msg);
    }
    
    public FSException(String fmt, Object... params) {
        
        super(String.format(fmt, params));
    }
    
    public FSException(String msg, Throwable e) {
        
        super(msg, e);
    }
    
    public FSException(Throwable e) {
        
        super(e);
    }
    
}
