package com.kahui.httpclient;

import org.apache.http.NameValuePair;

import com.kahui.httpclient.enums.HttpMethod;

import java.util.List;

/**
*
*/
public interface IRawHttpClientService {
   /**
    * 客户端请求服务
    * @param url       当前请求url
    * @param method    当前请求的方式(post or get)
    * @param header    List<NameValuePair> header  请求消息头以键值对的形式封装到list集合
    * @param params    List<NameValuePair> params  请求参数以键值对的形式封装到list集合
    * @param handle    IResponseHandle<T> handle响应结果处理类
    * @param encode    编码类型
    * @param <T>
    * @return
    */
   public <T> T request(String url, HttpMethod method, List<NameValuePair> header,
                        List<NameValuePair> params, IResponseHandle<T> handle, String encode);

   /**
    * 客户端请求服务
    * @param url       当前请求url
    * @param method    当前请求的方式(post or get)
    * @param header    List<NameValuePair> header  请求消息头以键值对的形式封装到list集合
    * @param params    List<NameValuePair> params  请求参数以键值对的形式封装到list集合
    * @param handle    IResponseHandle<T> handle响应结果处理类
    * @param encode    编码类型
    * @param <T>
    * @return
    */
   public <T> T requestAjax(String url, HttpMethod method, List<NameValuePair> header,
                            List<NameValuePair> params, IResponseHandle<T> handle, String encode);

   /**
    * 服务使用完需要释放
    */
   public void close();

   /**
    * 客户端请求服务
    * @param url       当前请求url
    * @param method    当前请求的方式(post or get)
    * @param header    List<NameValuePair> header  请求消息头以键值对的形式封装到list集合
    * @param params    String params  请求参数按照需求拼接
    * @param handle    IResponseHandle<T> handle响应结果处理类
    * @param encode    编码类型
    * @param <T>
    * @return
    */
   public <T> T requestAjaxSelf(String url, HttpMethod method, List<NameValuePair> header, String params, IResponseHandle<T> handle, String encode);
}
