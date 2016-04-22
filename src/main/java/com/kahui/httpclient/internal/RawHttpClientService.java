package com.kahui.httpclient.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.kahui.httpclient.IRawHttpClientService;
import com.kahui.httpclient.IResponseHandle;
import com.kahui.httpclient.ISocketService;
import com.kahui.httpclient.dependents.Strings;
import com.kahui.httpclient.enums.HttpMethod;

/**
 *
 */
public class RawHttpClientService implements IRawHttpClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RawHttpClientService.class);
    private final ISocketService socketService;

    public RawHttpClientService(String host, int port) {
        this(host, port, false);
    }

    public RawHttpClientService(String host, int port, boolean ssl) {
        socketService = (ISocketService) new SocketServiceImpl(host, port, ssl);
    }

    public <T> T request(String url, HttpMethod method, List<NameValuePair> header, List<NameValuePair> params, IResponseHandle<T> handle, String encode) {
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(method);
        Preconditions.checkNotNull(handle);
        Preconditions.checkNotNull(encode);
        Preconditions.checkArgument(encode.length() > 0);

        try {
            StringBuilder req = new StringBuilder();
            StringBuilder content = new StringBuilder();
            byte[] contentByte = null;
            if (method == HttpMethod.GET) {
                req.append("GET ");
                req.append(url);
                if (params == null || params.size() == 0) {
                } else {
                    if (url.indexOf('?') < 0) {
                        req.append('?');
                    } else {
                        req.append('&');
                    }
                    buildParams(params, encode, req);
                }
            } else {
                req.append("POST ");
                req.append(url);
                buildParams(params, encode, content);
            }

            req.append(' ');
            req.append("HTTP/1.1");
            req.append('\n');

            for (NameValuePair pair : header) {
                req.append(pair.getName());
                req.append(": ");
                req.append(pair.getValue());
                req.append('\n');
            }

            if (method == HttpMethod.POST) {
                contentByte = content.toString().getBytes(encode);
                req.append("Content-Length");
                req.append(": ");
                req.append(contentByte.length);
                req.append('\n');
            }
            //end char '\n'
            req.append('\n');
            String requestReport = req.toString();
            socketService.send(requestReport.getBytes(encode));
            LOGGER.debug("Request header ==>>{}", requestReport);
            if (contentByte != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Reqest content ==>>{}", content.toString());
                }
                socketService.send(contentByte);
            }
            List<NameValuePair> headLines = readHeader();

            LOGGER.debug(headLines.toString());

            byte[] responseContent = readContent(headLines);
            return handle.processResponse(headLines, responseContent, encode);
        } catch (Exception e) {
            throw new RuntimeException("Request(" + url + ") fail.", e);
        }

    }

    public void close() {
        socketService.close();
    }

    private byte[] readContent(List<NameValuePair> headLines) throws IOException {
        byte[] content = null;
        for (NameValuePair pair : headLines) {
            if ("Transfer-Encoding".equalsIgnoreCase(pair.getName()) && "chunked".equalsIgnoreCase(pair.getValue())) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (true) {
                    String line = socketService.readLine();
                    if (Strings.isNullOrEmpty(line)) {
                        continue;
                    }

                    int c = Integer.parseInt(line, 16);
                    if (c <= 0) {
                        break;
                    }
                    out.write(socketService.read(c));
                }
                content = out.toByteArray();
                break;
            } else if ("Content-length".equalsIgnoreCase(pair.getName())) {
                int c = Integer.parseInt(pair.getValue());
                content = socketService.read(c);
                break;
            }
        }
        return content;
    }

    public <T> T requestAjax(String url, HttpMethod method, List<NameValuePair> header, List<NameValuePair> params, IResponseHandle<T> handle, String encode) {
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(method);
        Preconditions.checkNotNull(handle);
        Preconditions.checkNotNull(encode);
        Preconditions.checkArgument(encode.length() > 0);

        try {
            StringBuilder req = new StringBuilder();
            StringBuilder content = new StringBuilder();
            byte[] contentByte = null;
            if (method == HttpMethod.GET) {
                req.append("GET ");
                req.append(url);
                if (params == null || params.size() == 0) {
                } else {
                    if (url.indexOf('?') < 0) {
                        req.append('?');
                    } else {
                        req.append('&');
                    }
                    buildParams(params, encode, req);
                }
            } else {
                req.append("POST ");
                req.append(url);
                if(null != params && params.size()>0){
                    buildParamsAjax(params, encode, content);
                }
            }

            req.append(' ');
            req.append("HTTP/1.1");
            req.append('\n');

            for (NameValuePair pair : header) {
                req.append(pair.getName());
                req.append(": ");
                req.append(pair.getValue());
                req.append('\n');
            }

            if (method == HttpMethod.POST && null != params) {
                contentByte = content.toString().getBytes(encode);
                req.append("Content-Length");
                req.append(": ");
                req.append(contentByte.length);
                req.append('\n');
            }
            //end char '\n'
            req.append('\n');
            String requestReport = req.toString();
            socketService.send(requestReport.getBytes(encode));
            LOGGER.debug("Request header ==>>{}", requestReport);
            if (contentByte != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Reqest content ==>>{}", content.toString());
                }
                socketService.send(contentByte);
            }
            List<NameValuePair> headLines = readHeader();

            byte[] responseContent = readContent(headLines);
            return handle.processResponse(headLines, responseContent, encode);
        } catch (Exception e) {
            throw new RuntimeException("Request(" + url + ") fail.", e);
        }

    }

    public <T> T requestAjaxSelf(String url, HttpMethod method, List<NameValuePair> header, String params, IResponseHandle<T> handle, String encode) {
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(method);
        Preconditions.checkNotNull(handle);
        Preconditions.checkNotNull(encode);
        Preconditions.checkArgument(encode.length() > 0);

        try {
            StringBuilder req = new StringBuilder();
            byte[] contentByte = null;
            if (method == HttpMethod.GET) {
                req.append("GET ");
                req.append(url);
                if (params == null || params.equals("")) {
                } else {
                        req.append(params);
                }
            } else {
                req.append("POST ");
                req.append(url);
            }

            req.append(' ');
            req.append("HTTP/1.1");
            req.append('\n');

            for (NameValuePair pair : header) {
                req.append(pair.getName());
                req.append(": ");
                req.append(pair.getValue());
                req.append('\n');
            }

            if (method == HttpMethod.POST && null != params) {
                contentByte = params.toString().getBytes(encode);
                LOGGER.debug("content---",contentByte.toString());
                req.append("Content-Length");
                req.append(": ");
                req.append(contentByte.length);
                req.append('\n');
            }
            //end char '\n'
            req.append('\n');
            String requestReport = req.toString();
            socketService.send(requestReport.getBytes(encode));
            LOGGER.debug("Request header ==>>{}", requestReport);
            if (contentByte != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Reqest content ==>>{}", params);
                }
                socketService.send(contentByte);
            }
            List<NameValuePair> headLines = readHeader();

            byte[] responseContent = readContent(headLines);
            return handle.processResponse(headLines, responseContent, encode);
        } catch (Exception e) {
            throw new RuntimeException("Request(" + url + ") fail.", e);
        }

    }

    private List<NameValuePair> readHeader() {
        List<NameValuePair> headLines = Lists.newArrayList();
        String line;
        int index;
        BasicNameValuePair pair = null;
        while (true) {
            line = socketService.readLine();
            LOGGER.debug("header line =>> {}", line);
            if (Strings.isNullOrEmpty(line)) {
                if(headLines.size() == 0){
                    continue;
                }
                break;
            }
            index = line.indexOf(":");
            if (index > 0) {
                pair = new BasicNameValuePair(line.substring(0, index).trim(), line.substring(index + 1).trim());
            } else {
                pair = new BasicNameValuePair("HTTP_STATUS", line);
            }
            headLines.add(pair);
        }

        return headLines;
    }

    private void buildParams(List<NameValuePair> params, String encode, StringBuilder sb) {
        try {
            if(null != params){
                for (NameValuePair pair : params) {
                    sb.append(URLEncoder.encode(pair.getName(),encode));

                    sb.append('=');
                    sb.append(URLEncoder.encode(pair.getValue(),encode));
                    sb.append('&');
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            LOGGER.debug(sb.toString());
            System.out.println("参数");
            System.out.println(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException("Build params fail.", e);
        }
    }
    private void buildParamsAjax(List<NameValuePair> params, String encode, StringBuilder sb) {
        try {
            sb.append("{");
            for (NameValuePair pair : params) {
                sb.append("\"");
                sb.append(URLEncoder.encode(pair.getName(),encode));
                sb.append("\"");

                sb.append(':');
                sb.append("\"");
                sb.append(URLEncoder.encode(pair.getValue(),encode));
                sb.append("\"");
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
            LOGGER.debug(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException("Build params fail.", e);
        }
    }
}
