package com.bcb.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义全局异常
 */
@Slf4j
public class MyGlobalParameterException extends  RuntimeException{

    private static final long serialVersionUID = 1L;

    public MyGlobalParameterException(String message) {
        super(message);
        log.error(message);
    }

    /**
     *
     * @param message 抛出给用户能看到的
     * @param messageExtend 日志中记录详细的
     */
    public MyGlobalParameterException(String message,String messageExtend) {
        super(message);
        log.error(message+","+messageExtend);
    }
    
    public MyGlobalParameterException(String message, Throwable e) {
        super(message);
        log.error(message,e);
    }

    /**
     *
     * @param message 抛出给用户能看到的
     * @param messageExtend 日志中记录详细的
     */
    public MyGlobalParameterException(String message,String messageExtend, Throwable e) {
        super(message);
        log.error(message+","+messageExtend,e);
    }
}
