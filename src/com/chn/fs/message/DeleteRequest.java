/**
 * FileServer
 * @title DeleteRequest.java
 * @package com.chn.fs.message
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:21:19
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.message;

import java.io.Serializable;

/**
 * @class DeleteRequest
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class DeleteRequest extends Request implements Serializable {

    private static final long serialVersionUID = -388932550786351489L;
    
    public DeleteRequest() {
        
        super();
    }
    
    public DeleteRequest(byte[] digest) {
        
        super(digest);
    }
    
}
