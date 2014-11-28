/**
 * FileServer
 * @title WriteRequest.java
 * @package com.chn.fs.message
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:42:47
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.message;

import java.io.Serializable;

/**
 * @class WriteRequest
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class WriteRequest extends Request implements Serializable {
    
    private static final long serialVersionUID = 1551373181323009437L;
    
    private byte[] bytes;
    private int amount;
    
    
    public WriteRequest() {
        
        super();
    }
    
    public WriteRequest(byte[] digest, byte[] bytes) {
        
        this(digest, bytes, 1);
    }
    
    public WriteRequest(byte[] digest, byte[] bytes, int amount) {
        
        super(digest);
        this.bytes = bytes;
        this.amount = amount;
    }
    
    public byte[] getBytes() {
        
        return bytes;
    }
    
    public void setBytes(byte[] bytes) {
        
        this.bytes = bytes;
    }
    
    public int getAmount() {
        
        return amount;
    }
    
    public void setAmount(int amount) {
        
        this.amount = amount;
    }
}