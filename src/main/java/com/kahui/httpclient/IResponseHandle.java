package com.kahui.httpclient;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 *
 */
public interface IResponseHandle<T>{

    /**
     * 响应结果处理类
     * @param header    List<NameValuePair>响应消息头，以键值对的形式封装到list集合
     * @param content   byte[]响应的正文
     * @param encode    编码类型
     * @return
     */
    public T processResponse(List<NameValuePair> header,byte[] content,String encode);
}
