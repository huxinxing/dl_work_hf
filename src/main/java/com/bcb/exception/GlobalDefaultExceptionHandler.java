package com.bcb.exception;

import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.MyResponseResult;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.ResultDtoFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalDefaultExceptionHandler {

	@ExceptionHandler(Exception.class) // 所有的异常都是Exception子类
	public ResponseResult defaultErrorHandler(HttpServletRequest request,Exception e) {
		JSONObject obj=new JSONObject();
		obj.put("URL:", request.getRequestURL().toString());
		obj.put("HTTP_METHOD : " , request.getMethod());
		obj.put("IP : " , request.getRemoteAddr());
		Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = enu.nextElement();
			obj.put(paraName+": " ,request.getParameter(paraName));
		}
		log.error(obj.toString(),e);
		String code = "";
		if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
			code = ResponseStatus.PATH_NOT_FOUND.getKey();
		} else {
			code = ResponseStatus.CODE_WRONG.getKey();
		}
		ResponseResult result = new ResponseResult("error",code,e.toString());
		return result;
	}

	// 捕捉UnauthorizedException
	@org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseResult defaultErrorHandler(UnauthorizedException e) {
		return new ResponseResult(ResponseStatus.CODE_NOPRESSMISSION.getKey(),ResponseStatus.CODE_NOPRESSMISSION.getValue(),"您没有该权限!");
	}


	/**
	 * 20180524zhw添加程序自定义异常------自定义参数异常
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = { MyGlobalParameterException.class })
	@ResponseBody
	public MyResponseResult runException(MyGlobalParameterException ex) {
		log.error("进入全局MyGlobalParameterException异常:");
		return ResultDtoFactory.toParameterFail(ex.getMessage(),null);
	}

	/**
	 * 20180524zhw添加程序自动抛出异常------SpringMVC参数转换异常
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
	@ResponseBody
	public MyResponseResult runException(MethodArgumentTypeMismatchException ex) {
		log.error("进入全局MethodArgumentTypeMismatchException异常:"+ex.getMessage());
		return ResultDtoFactory.toBusinessFail("参数格式不正确",null);
	}

	/**
	 * 20180524zhw添加程序自定义异常------通用业务异常
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = { MyGlobalBusinessException.class })
	@ResponseBody
	public MyResponseResult runException(MyGlobalBusinessException ex) {
		log.error("进入全局MyGlobalBusinessException异常:");
		return ResultDtoFactory.toBusinessFail(ex.getMessage(),null);
	}

	/**
	 * 20180524zhw添加程序自定义异常------通用内部捕捉错误
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = { MyGlobalInternalException.class })
	@ResponseBody
	public MyResponseResult runException(MyGlobalInternalException ex) {
		log.error("进入全局MyGlobalInternalException异常:");
		return ResultDtoFactory.toInternalFail(ex.getMessage(),null);
	}

}
