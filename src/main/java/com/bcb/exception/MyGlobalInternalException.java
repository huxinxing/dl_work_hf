package com.bcb.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义全局内部异常
 */
@Slf4j
public class MyGlobalInternalException extends  RuntimeException{

    private static final long serialVersionUID = 1L;

    /**
     *
     * @param message 抛出给用户能看到的
     * @param messageExtend 日志中记录详细的
     */
    public MyGlobalInternalException(String message,String messageExtend, Throwable e) {
        super(message);
        log.error(message+","+messageExtend,e);
    }

    public MyGlobalInternalException(String message, Throwable e) {
        super(message);
        log.error(message,e);
    }
}
