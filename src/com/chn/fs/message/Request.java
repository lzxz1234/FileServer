/**
 * FileServer
 * @title Request.java
 * @package com.chn.fs.message
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-上午10:34:03
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.message;

import java.io.Serializable;

/**
 * @class Request
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 5273104818059264199L;
    
    private byte[] digest;
    
    public Request() {
    }
    
    public Request(byte[] digest) {
        
        this.digest = digest;
    }
    
    public byte[] getDigest() {
        
        return digest;
    }
    
    public void setDigest(byte[] digest) {
        
        this.digest = digest;
    }
    
}
