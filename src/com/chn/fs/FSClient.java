/**
 * FileServer
 * @title FSClient.java
 * @package com.chn.fs
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月27日-上午11:02:53
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs;

import java.io.WriteAbortedException;
import java.net.ConnectException;
import java.net.SocketException;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.netty4.client.Netty4ClientFactory;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;

import com.chn.fs.message.DeleteRequest;
import com.chn.fs.message.ExistRequest;
import com.chn.fs.message.ReadRequest;
import com.chn.fs.message.WriteRequest;
import com.chn.fs.util.MD;

/**
 * @class FSClient
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class FSClient implements ConstantCode {

    private static final int DEFAULT_CALL_TIMEOUT       = 30000;
    private static final int DEFAULT_CLIENT_NUMS        = 100;
    private static final int DEFAULT_CONNECT_TIMEOUT    = 10000;
    
    private String serverIp;
    private int serverPort;
    private int callTimeout = DEFAULT_CALL_TIMEOUT;
    private int clientNums = DEFAULT_CLIENT_NUMS;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    
    private Client client;
    
    
    /**
     * 构造方法。
     * 
     * @param   serverIp        服务器IP
     * @param   serverPort      服务器端口
     */
    public FSClient(String serverIp, int serverPort) {
        
        this(serverIp, serverPort, DEFAULT_CALL_TIMEOUT, DEFAULT_CLIENT_NUMS, DEFAULT_CONNECT_TIMEOUT);
    }
    
    /**
     * 构造方法。
     * 
     * @param   serverIp        服务器IP
     * @param   serverPort      服务器端口
     * @param   callTimeout     调用超时时间，单位：毫秒
     */
    public FSClient(String serverIp, int serverPort, int callTimeout) {
        
        this(serverIp, serverPort, callTimeout, DEFAULT_CLIENT_NUMS, DEFAULT_CONNECT_TIMEOUT);
    }
    
    /**
     * 构造方法。
     * 
     * @param   serverIp        服务器IP
     * @param   serverPort      服务器端口
     * @param   callTimeout     调用超时时间，单位：毫秒
     * @param   clientNums      客户端网络连接数量，推荐1
     */
    public FSClient(String serverIp, int serverPort, int callTimeout, int clientNums) {
        
        this(serverIp, serverPort, callTimeout, clientNums, DEFAULT_CONNECT_TIMEOUT);
    }
    
    /**
     * 构造方法。
     * 
     * @param   serverIp        服务器IP
     * @param   serverPort      服务器端口
     * @param   callTimeout     调用超时时间，单位：毫秒
     * @param   clientNums      客户端网络连接数量，推荐1
     * @param   connectTimeout  连接超时时间，单位：毫秒
     */
    public FSClient(String serverIp, int serverPort, int callTimeout, int clientNums, int connectTimeout) {
        
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.callTimeout = callTimeout;
        this.clientNums = clientNums;
        this.connectTimeout = connectTimeout;
    }
    
    /**
     * 写入文件。
     * 
     * @param   bytes                   文件内容字节数组
     * @param   amount                  文件份数
     * 
     * @return  文件摘要
     * 
     * @throws  ConnectException        当RPC连接错误时抛出
     * @throws  SocketException         当RPC调用出现通讯故障时抛出
     * @throws  WriteAbortedException   当RPC返回写入错误时抛出
     * @throws  InterruptedException    当RPC调用被中断时抛出
     */
    public byte[] write(byte[] bytes, int amount) throws ConnectException, 
        SocketException, WriteAbortedException, InterruptedException {
        
        byte[] digest = MD.digest("SHA-256", bytes);
        
        ExistRequest existReq = new ExistRequest(digest, amount);
        String existResp = (String) this.call(existReq);
        if (RESPONSE_CODE_FALSE.equals(existResp)) {
            WriteRequest writeReq = new WriteRequest(digest, bytes, amount);
            String writeResp = (String) this.call(writeReq);
            if (RESPONSE_CODE_FALSE.equals(writeResp)) {
                throw new WriteAbortedException("写入文件失败！", new Exception());
            } 
        }
        
        return digest;
    }
    
    /**
     * 读取文件。
     * 
     * @param   digest                  文件摘要
     * 
     * @return  文件内容
     * 
     * @throws  ConnectException        当RPC连接错误时抛出
     * @throws  SocketException         当RPC调用出现通讯故障时抛出
     * @throws  InterruptedException    当RPC调用被中断时抛出
     */
    public byte[] read(byte[] digest) throws ConnectException, SocketException, 
        InterruptedException {
        
        return (byte[]) this.call(new ReadRequest(digest));
    }
    
    /**
     * 删除
     * @param   digest                  文件摘要
     * 
     * @return  文件内容
     * 
     * @throws  ConnectException        当RPC连接错误时抛出
     * @throws  SocketException         当RPC调用出现通讯故障时抛出
     * @throws  InterruptedException    当RPC调用被中断时抛出
     */
    public boolean delete(byte[] digest) throws ConnectException, SocketException, 
        InterruptedException {
        
        String result = (String) this.call(new DeleteRequest(digest));
        
        if (RESPONSE_CODE_FALSE.equals(result)) {
            return false;
        } else if (RESPONSE_CODE_TRUE.equals(result)) {
            return true;
        }
        
        return true;
    }
    
    /*
     * 调用RPC。
     * 
     * @param   request                 请求对象
     * 
     * @return  响应对象
     * 
     * @throws  ConnectException        当RPC连接错误时抛出
     * @throws  SocketException         当RPC调用出现通讯故障时抛出
     * @throws  InterruptedException    当RPC调用被中断时抛出
     */
    private Object call(Object request) throws ConnectException, SocketException, 
        InterruptedException {
        
        if (request == null) throw new IllegalArgumentException("RPC请求参数不能为null！");
        
        if (!this.isConnected()) throw new ConnectException("RPC连接错误！");
        
        Object resp = null;
        boolean retry = false;
        do {
            try {
                resp = this.client.invokeSync(request, this.callTimeout, Codecs.JAVA_CODEC, 
                                              SimpleProcessorProtocol.TYPE);
                retry = false;
            } catch (Exception e) {
                String errMsg = e.getMessage();
                if ((errMsg != null) && errMsg.startsWith("receive response timeout")) {
                    retry = true;
                } else if ((errMsg != null) && errMsg.equals("Get response error")) {
                    throw new InterruptedException("RPC调用被中断！");
                } else {
                    this.closeClient();
                    throw new SocketException("RPC调用通讯错误！");
                }
            }
        } while (retry);
        
        return resp;
    }
    
    /*
     * 判断是否连接到RPC服务器，如果没有连接则尝试连接。
     * 
     * @return  是否连接到RPC服务器。
     */
    private boolean isConnected() {
         
        if (this.client == null) {
            try {
                this.client = Netty4ClientFactory.getInstance().get(this.serverIp, 
                        this.serverPort, this.connectTimeout, this.clientNums);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("连接服务器失败！！", e);
            }
        } else {
            return true;
        }
    }
    
    /*
     * 关闭客户端连接。
     */
    private void closeClient() {
        
        Netty4ClientFactory.getInstance().removeClient(this.serverIp + ":" + this.serverPort, this.client);
        this.client = null;
    }
    
}
