/**
 * FileServer
 * @title ExistRequest.java
 * @package com.chn.fs.message
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:23:53
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.message;

import java.io.Serializable;

/**
 * @class ExistRequest
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class ExistRequest extends Request implements Serializable {
    
    private static final long serialVersionUID = -1618908547970854380L;
    
    private int amount;
    
    public ExistRequest() {
        
        super();
    }
    
    public ExistRequest(byte[] digest) {
        
        this(digest, 1);
    }
    
    public ExistRequest(byte[] digest, int amount) {
        
        super(digest);
        this.amount = amount;
    }
    
    public int getAmount() {
        
        return amount;
    }
    
    public void setAmount(int amount) {
        
        this.amount = amount;
    }
}