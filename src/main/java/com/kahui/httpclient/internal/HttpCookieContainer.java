package com.kahui.httpclient.internal;

import com.google.common.collect.Maps;
import org.apache.http.NameValuePair;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *自定义cookie
 */
public class HttpCookieContainer {
    /**
     * 不需要的cookie信息
     */
    private static final String[] MISS_KEY = new String[]{"path", "expires","HttpOnly"};
    /**
     * 存储需要的cookie信息
     */
    private Map<String, String> cookies = Maps.newLinkedHashMap();

    /**
     * cookie添加信息
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        cookies.put(key, value);
    }

    /**
     * 移除信息
     * @param key
     */
    public void remove(String key) {
        cookies.remove(key);
    }

    /**
     * 过滤不需要的cookie信息，解析cookie的键值对信息存储到cookie
     * @param strCookies
     */
    public void accept(String strCookies) {
        String[] cookies = strCookies.split(";");
        String key;
        String value;
        for (String cookie : cookies) {
            int i = cookie.indexOf('=');
            if (i < 0) {
                continue;
            }

            key = cookie.substring(0, i).trim();
            value = cookie.substring(i + 1).trim();
            boolean b = false;
            for (String miss : MISS_KEY) {
                if (miss.equals(key)) {
                    b = true;
                    break;
                }
            }
            if (b){
                continue;
            }
            this.cookies.put(key, value);
        }
    }

    /**
     * 解析响应消息获取cookies信息并设置到cookie中
     * @param header
     */
    public void accept(List<NameValuePair> header) {
        for (NameValuePair httpHeadItem : header) {
            if ("Set-Cookie".equals(httpHeadItem.getName())) {
                String c = httpHeadItem.getValue();
                accept(c);
            }
        }
    }

    @Override
    /**
     * 重写toString方法
     */
    public String toString() {
        Set<Map.Entry<String, String>> entrySet = cookies.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (sb.length() != 0) {
                sb.append("; ");
            }

            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
        }
        return sb.toString();
    }
}
