package com.kahui.httpclient.enums;


import java.util.List;

import com.kahui.httpclient.dependents.IndexedEnum;

/**
 *
 */
public enum HttpMethod implements IndexedEnum {
    GET(1, "get"),
    POST(2, "post");


    private static final List<HttpMethod> INDEXS = IndexedEnum.IndexedEnumUtil.toIndexes(HttpMethod.values());

    /**
     * 绱㈠紩
     */
    private int index;
    private String name;

    HttpMethod(int index, String name) {
        this.index = index;
        this.name = name;
    }

    /**
     * 鏍规嵁index 鑾峰彇鎸囧畾搴旇骞垮憡褰㈠紡鍚嶇О
     *
     * @param index
     * @return
     */
    public static HttpMethod indexOf(final int index) {
        return IndexedEnumUtil.valueOf(INDEXS, index);
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}

