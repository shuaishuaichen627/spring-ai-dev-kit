package com.springai.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * SSE 工具类
 */
@Slf4j
public class SseEmitterUtil {

    /**
     * 发送消息
     */
    public static void send(SseEmitter emitter, String eventType, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(data));
        } catch (IOException e) {
            log.error("SSE 发送消息失败", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 发送完成信号
     */
    public static void complete(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception e) {
            log.error("SSE 完成失败", e);
        }
    }

    /**
     * 发送错误信号
     */
    public static void error(SseEmitter emitter, Throwable throwable) {
        try {
            emitter.completeWithError(throwable);
        } catch (Exception e) {
            log.error("SSE 错误处理失败", e);
        }
    }
}

