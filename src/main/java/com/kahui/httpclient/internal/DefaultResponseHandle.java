package com.kahui.httpclient.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kahui.httpclient.IResponseHandle;
import com.kahui.httpclient.dependents.Pair;

/**
 *
 */
public class DefaultResponseHandle implements IResponseHandle<Pair<List<NameValuePair>, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResponseHandle.class);

    public Pair<List<NameValuePair>, String> processResponse(List<NameValuePair> header, byte[] content, String encode) {
        try {
            if (LOGGER.isDebugEnabled()) {
                for (NameValuePair head : header) {
                    LOGGER.debug("Response head line =>>{}: {}", head.getName(), head.getValue());
                }
                if (content != null) {
                    LOGGER.debug("Response content =>>{}", new String(content, encode));
                }
            }


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
            Pair<List<NameValuePair>, String> result;
            if (decodedContent == null) {
                result = new Pair<List<NameValuePair>, String>(header, null);
            } else {
                result = new Pair<List<NameValuePair>, String>(header, new String(decodedContent, encode));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Process response fail.", e);
        }
    }
}
