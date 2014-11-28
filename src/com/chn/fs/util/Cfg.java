/**
 * FileServer
 * @title Cfg.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午5:49:03
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @class Cfg
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class Cfg {
    
    private final Properties prop;
    private final Map<Class<?>, Converter> converters = new HashMap<Class<?>, Converter>();
    
    
    private Cfg(Properties prop) {
        
        this.prop = prop;
        converters.put(String.class, new StringConverter());
        converters.put(byte.class, new ByteConverter());
        converters.put(Byte.class, new ByteConverter());
        converters.put(int.class, new IntegerConverter());
        converters.put(Integer.class, new IntegerConverter());
        converters.put(short.class, new ShortConverter());
        converters.put(Short.class, new ShortConverter());
        converters.put(long.class, new LongConverter());
        converters.put(Long.class, new LongConverter());
        converters.put(float.class, new FloatConverter());
        converters.put(Float.class, new FloatConverter());
        converters.put(double.class, new DoubleConverter());
        converters.put(Double.class, new DoubleConverter());
        converters.put(boolean.class, new BooleanConverter());
        converters.put(Boolean.class, new BooleanConverter());
        converters.put(Date.class, new DateConverter());
        converters.put(Time.class, new TimeConverter());
        converters.put(Timestamp.class, new TimestampConverter());
        converters.put(Class.class, new ClassConverter());
    }
    
    public static Cfg getCfg(String classpath) {
        
        InputStream is = Cfg.class.getResourceAsStream(classpath);
        if(is == null)
            throw new RuntimeException(String.format("资源 [%s] 不存在！！", classpath));
        
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            throw new RuntimeException("资源读取错误", e);
        }
        return new Cfg(prop);
    }
    
    public String get(String key) {
        
        if(key == null)
            throw new RuntimeException("主键不能为空！！");
        return prop.getProperty(key);
    }
    
    public String get(String key, String defaultValue) {
        
        if(key == null)
            throw new RuntimeException("主键不能为空！！");
        return prop.getProperty(key, defaultValue);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String key) {
        
        if(clazz == null)
            throw new RuntimeException("目标类型不能为空！！");
        if(key == null)
            throw new RuntimeException("主键不能为空！！");
        Converter conv = this.converters.get(clazz);
        if(conv == null)
            throw new RuntimeException(String.format("不支持的目标类型[%s]！！", clazz.getName()));
        return (T)conv.convert(this.get(key));
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String key, T defaultValue) {
        
        if(clazz == null)
            throw new RuntimeException("目标类型不能为空！！");
        if(key == null)
            throw new RuntimeException("主键不能为空！！");
        Converter conv = this.converters.get(clazz);
        if(conv == null)
            throw new RuntimeException(String.format("不支持的目标类型[%s]！！", clazz.getName()));
        String configValue = this.get(key);
        return configValue == null ? defaultValue : (T)conv.convert(configValue);
    }
    
    private static interface Converter {
        
        public Object convert(String s);
    }
    
    private static class StringConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            return s;
        }
    }
    
    private static class ByteConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Byte result = null;
            try {
                result = Byte.valueOf(s.trim());
            } catch (NumberFormatException nfe) {}
            
            return result;
        }
    }
    
    private static class IntegerConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Integer result = null;
            try {
                result = Integer.valueOf(s.trim());
            } catch (NumberFormatException nfe) {}
            
            return result;
        }
    }
    
    private static class ShortConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Short result = null;
            try {
                result = Short.valueOf(s.trim());
            } catch (NumberFormatException nfe) {}
            
            return result;
        }
    }
    
    private static class LongConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Long result = null;
            try {
                result = Long.valueOf(s.trim());
            } catch (NumberFormatException nfe) {}
            
            return result;
        }
    }
    
    private static class FloatConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Float result = null;
            try {
                result = Float.valueOf(s.trim());
            } catch (NumberFormatException nfe) {}
            
            return result;
        }
    }
    
    private static class DoubleConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Double result = null;
            try {
                result = Double.valueOf(s.trim());
            } catch (NumberFormatException nfe) {}
            
            return result;
        }
    }
    
    private static class BooleanConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            //Boolean result = (T) Boolean.valueOf(s.trim());
            Boolean result = null;
            String value = s.trim();
            if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) 
                    || "y".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
                result = Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) 
                    || "n".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
                result = Boolean.FALSE;
            }
            
            return result;
        }
    }
    
    private static class DateConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Date result = null;
            try {
                result = new Date((new SimpleDateFormat("yyyy-MM-dd")).parse(s.trim()).getTime());
            } catch (ParseException e1) {
                try {
                    result = new Date((new SimpleDateFormat("yyyyMMdd")).parse(s.trim()).getTime());
                } catch (ParseException e2) {}
            }
            
            return result;
        }
    }
    
    private static class TimeConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Time result = null;
            try {
                result = new Time((new SimpleDateFormat("HH:mm:ss.SSS")).parse(s.trim()).getTime());
            } catch (ParseException e1) {
                try {
                    result = new Time((new SimpleDateFormat("HHmmssSSS")).parse(s.trim()).getTime());
                } catch (ParseException e2) {
                    try {
                        result = new Time((new SimpleDateFormat("HH:mm:ss")).parse(s.trim()).getTime());
                    } catch (ParseException e3) {
                        try {
                            result = new Time((new SimpleDateFormat("HHmmss")).parse(s.trim()).getTime());
                        } catch (ParseException e4) {}
                    }
                }
            }
            
            return result;
        }
    }
    
    private static class TimestampConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Timestamp result = null;
            try {
                result = new Timestamp((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).parse(s.trim()).getTime());
            } catch (ParseException e1) {
                try {
                    result = new Timestamp((new SimpleDateFormat("yyyyMMddHHmmssSSS")).parse(s.trim()).getTime());
                } catch (ParseException e2) {
                    try {
                        result = new Timestamp((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(s.trim()).getTime());
                    } catch (ParseException e3) {
                        try {
                            result = new Timestamp((new SimpleDateFormat("yyyyMMddHHmmss")).parse(s.trim()).getTime());
                        } catch (ParseException e4) {}
                    }
                }
            }
            
            return result;
        }
    }
    
    private static class ClassConverter implements Converter {
        
        @Override
        public Object convert(String s) {
            
            Class<?> result = null;
            try {
                result = Class.forName(s);
            } catch (ClassNotFoundException e) {}
            
            return result;
        }
    }
    
}
