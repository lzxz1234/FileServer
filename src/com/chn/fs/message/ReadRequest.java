/**
 * FileServer
 * @title ReadRequest.java
 * @package com.chn.fs.message
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:24:57
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.message;

import java.io.Serializable;

/**
 * @class ReadRequest
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class ReadRequest extends Request implements Serializable {
    
    private static final long serialVersionUID = 8508641873375195914L;
    
    public ReadRequest() {
        
        super();
    }
    
    public ReadRequest(byte[] digest) {
        
        super(digest);
    }
    
}