/**
 * FileServer
 * @title Counter.java
 * @package com.chn.fs.counter
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:15:20
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.counter;

import java.io.File;

/**
 * @class Counter
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public interface Counter {

    public abstract File nextFile();
    
}
