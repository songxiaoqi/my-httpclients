package com.kahui.httpclient;

import java.io.InputStream;
import java.net.Socket;

/**
 * 类似于telnet的服务
 */
public interface ISocketService {
    /**
     * 发送数据
     *
     * @param data
     */
    public void send(byte[] data);

    /**
     * 读取一个字节
     * 网路断开时会返回{@code -1}。
     * 请不要通过{@code -1}来判断网络数据是否读取完毕，否则会导致性能下降。
     * {@link Socket#getOutputStream()}{@link InputStream#read()}
     *
     * @return 从网路中读取到的字节 {@code byte} 或{@code -1}. {@code -1} 代表网络传输已终止。
     */
    public int readByte();

    /**
     * 读取特定长度的数据
     *
     * @param count 长度
     * @return 从网络中读取到的数据
     * @throws RuntimeException 网络断开导致无法读出count所指定数量的数据时会抛出异常。
     */
    public byte[] read(int count);

    /**
     * 读取一行字符串
     * @return
     */
    public String readLine();

    /**
     * 读取一行字符串
     * @param encode 字符编码
     * @return
     */
    public String readLine(String encode);

    /**
     * 关闭网络链接
     * 请注意无论什么情况下，使用完毕后务必关闭网路链接
     */
    public void close();
}
