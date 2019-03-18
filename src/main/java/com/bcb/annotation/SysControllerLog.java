package com.bcb.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: 平台事务日志拦截注解.
 *
 * @author yunqiangdi
 * @version 1.0
 * @since 2017-05-31 1:59 PM
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SysControllerLog {
	//执行的操作
	ActionType actionType();
	//操作模块的内容描述
	String commentDescribe();

}
