package com.springai.common.constant;

/**
 * 通用常量
 */
public interface CommonConstants {
    
    /**
     * UTF-8 编码
     */
    String UTF8 = "UTF-8";
    
    /**
     * 成功标记
     */
    Integer SUCCESS = 200;
    
    /**
     * 失败标记
     */
    Integer FAIL = 500;
    
    /**
     * SSE 事件类型
     */
    interface SseEvent {
        String MESSAGE = "message";
        String ERROR = "error";
        String DONE = "done";
    }
}

