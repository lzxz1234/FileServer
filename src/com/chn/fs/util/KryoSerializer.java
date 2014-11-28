/**
 * FileServer
 * @title KryoSerializer.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:08:49
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @class KryoSerializer
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class KryoSerializer {
    
    private static ThreadLocal<Kryo> threadKryo = new ThreadLocal<Kryo>();
    
    
    /**
     * 使用Kryo序列化方式，序列化对象。
     * 
     * @param   obj                 要序列化的对象，
     * 
     * @return  序列化的字节数组
     */
    public static byte[] serialize(Object obj) {
        
        byte[] result = null;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output out = null;
        try {
            out = new Output(baos);
            currentKryo().writeObject(out, obj);
            out.flush();
            result = baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(out);
        }
        
        return result;
    }
    
    /**
     * 使用Kryo序列化方式，反序列化对象。
     * 
     * @param   bytes                       要反序列化对象的字节数组
     * @param   clazz                       要反序列化对象的Class对象
     * 
     * @return  反序列化的对象
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        
        T result = null;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input in = null;
        try {
            in = new Input(bais);
            result = currentKryo().readObject(in, clazz);
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        return result;
    }
    
    /*
     * 获得与当前线程关联的Kryo对象。
     * 因为Kryo对象不是线程安全的。
     */
    private static Kryo currentKryo() {
        
        Kryo kryo = threadKryo.get();
        if (kryo == null) {
            kryo = new Kryo();
            threadKryo.set(kryo);
        }
        
        return kryo;
    }
}