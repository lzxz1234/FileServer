/**
 * FileServer
 * @title Repository.java
 * @package com.chn.fs.repository
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:23:06
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.repository;


/**
 * @class Repository
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public interface Repository<K, V> {

    public abstract K add(V content);
    
    public abstract V get(K key);
    
    public abstract void delete(K key);
    
}
