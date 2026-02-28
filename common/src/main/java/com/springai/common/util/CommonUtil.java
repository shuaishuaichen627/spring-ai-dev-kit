package com.springai.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;

/**
 * 通用工具类
 */
public class CommonUtil {

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return StrUtil.isEmpty(str);
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StrUtil.isNotEmpty(str);
    }

    /**
     * MD5 加密
     */
    public static String md5(String str) {
        return SecureUtil.md5(str);
    }

    /**
     * 对象转 JSON
     */
    public static String toJson(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }

    /**
     * JSON 转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSONUtil.toBean(json, clazz);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(java.util.Date date) {
        return DateUtil.formatDateTime(date);
    }

    /**
     * 生成唯一 ID
     */
    public static String generateId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}

