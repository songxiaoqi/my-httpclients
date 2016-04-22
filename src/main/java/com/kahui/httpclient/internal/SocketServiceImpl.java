package com.kahui.httpclient.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.kahui.httpclient.ISocketService;
import com.kahui.httpclient.dependents.Strings;

/**
 * socket 通讯服务
 * {@link ISocketService}
 */
public class SocketServiceImpl implements ISocketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServiceImpl.class);
    private final boolean ssl;
    private final String host;
    private final int port;

    private Socket socket = null;

    /**
     * 构造函数
     * @param host 主机
     * @param port 端口
     */
    public SocketServiceImpl(String host, int port) {
        this(host, port, false);
    }

    /**
     * 构造函数
     * @param host 主机
     * @param port 端口
     * @param ssl 是否采用ssl 进行链接
     */
    public SocketServiceImpl(String host, int port, boolean ssl) {
        Preconditions.checkNotNull(host);
        Preconditions.checkArgument(port > 0);

        this.host = host;
        this.port = port;
        this.ssl = ssl;
        LOGGER.info("this  is --------------------------host:"+this.host+"----------------port:"+this.port+"---------------------ssl:"+this.ssl);

        this.socket = createSocket(host, port, ssl);
        try {
            socket.setSoTimeout(1000*60*5);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(data);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Send data fail.", e);
        }
    }

    public int readByte() {
        try {
            InputStream in = socket.getInputStream();
            return in.read();
        } catch (Exception e) {
            throw new RuntimeException("Read data fail.", e);
        }
    }

    public byte[] read(int count) {
        byte[] result = new byte[count];

        try {
            InputStream in = socket.getInputStream();
            int index = 0;
            while (true) {
                int b = in.read(result, index, count - index);
                if (b != -1) {
                    index += b;
                } else {
                    throw new RuntimeException("Connection is closed.");
                }

                if (index == count) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Read data fail.", e);
        }

        return result;
    }

    public String readLine() {
        return readLine(null);
    }

    public String readLine(String encode) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        int ln = (int) '\n';
        try {

            InputStream in = socket.getInputStream();
            while (true) {
                int b = in.read();
                if (b == ln) {
                    break;
                }
                o.write(b);
            }
        } catch (Exception e) {
            throw new RuntimeException("Read line fail.", e);
        }

        String line;
        if (encode == null) {
            line = o.toString();
        } else {
            try {
                line = o.toString(encode);
            } catch (Exception e) {
                throw new RuntimeException("Decode fail.", e);
            }
        }

        //replace windows last char '\r'
        String result;
        if (Strings.isNullOrEmpty(line)) {
            result = line;
        } else if (line.charAt(line.length() - 1) == '\r') {
            result = line.substring(0, line.length() - 1);
        } else {
            result = line;
        }

        return result;
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 创建socket
     * @param host
     * @param port
     * @param ssl
     * @return
     */
    private Socket createSocket(String host, int port, boolean ssl) {
        Socket socket;
        try {
            if (ssl) {
                X509TrustManager xtm = new DefaultTrustManager();
                LOGGER.info("xtm-------------------------------"+xtm.toString());
                TrustManager mytm[] = {xtm};
                // 得到上下文
                SSLContext ctx = SSLContext.getInstance("SSL");
                LOGGER.info("ctx---------------------------------"+ctx.toString());
                // 初始化
                ctx.init(null, mytm, null);
                // 获得工厂
                SSLSocketFactory factory = ctx.getSocketFactory();

                LOGGER.info("factory----------------------------"+factory.toString());

                // 从工厂获得Socket连接
                socket = factory.createSocket(host, port);
                LOGGER.info("socket---------------------------------"+socket.toString());
            } else {
                socket = new Socket(host, port);
            }

            return socket;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("create socket fail.", e);
        }
    }
}
