package com.kahui.httpclient.internal;

import org.apache.http.NameValuePair;

import com.kahui.httpclient.IResponseHandle;
import com.kahui.httpclient.dependents.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 *验证码处理
 */
public class BinaryResponseHandle implements IResponseHandle<Pair<List<NameValuePair>, byte[]>> {

    public Pair<List<NameValuePair>, byte[]> processResponse(List<NameValuePair> header, byte[] content, String encode) {
        byte[] decodedContent = content;
        for (NameValuePair item : header) {
            if ("Content-Encoding".equals(item.getName()) && "gzip".equals(item.getValue())) {
                try {
                    ByteArrayInputStream in = new ByteArrayInputStream(content);
                    GZIPInputStream gzipInputStream = new GZIPInputStream(in);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int count;
                    byte data[] = new byte[1024];
                    while ((count = gzipInputStream.read(data, 0, 1024)) != -1) {
                        outputStream.write(data, 0, count);
                    }
                    outputStream.flush();
                    decodedContent = outputStream.toByteArray();
                    break;
                } catch (Exception e) {
                    throw new RuntimeException("decode error.", e);
                }
            }
        }
        Pair<List<NameValuePair>, byte[]> pair = new Pair<List<NameValuePair>, byte[]>(header, decodedContent);
        return pair;
    }
}
