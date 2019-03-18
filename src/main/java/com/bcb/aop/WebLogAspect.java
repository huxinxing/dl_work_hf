package com.bcb.aop;

import com.bcb.annotation.ActionType;
import com.bcb.annotation.SysControllerLog;
import com.bcb.domain.entity.SystemLogOperation;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;

@Slf4j
@Aspect
@Component
@Order(-5)
public class WebLogAspect {
	@Autowired
	private CommonService commonService;

	@Pointcut("@annotation(com.bcb.annotation.SysControllerLog)")
	public void webLog() {
		//null
	}

	@After("webLog()")
	public void doBefore(JoinPoint joinPoint) throws ClassNotFoundException, IOException {
		// 通过反射获取参入的参数
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Class targetClass = Class.forName(targetName);
		String commentDescribe = "";  		//执行的操作
		ActionType actionType=null;   	//操作模块的内容描述
		Method[] methods = targetClass.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					commentDescribe = method.getAnnotation(SysControllerLog.class).commentDescribe();
					actionType = method.getAnnotation(SysControllerLog.class).actionType();
					break;
				}
			}
		}
		//定义插入变量
		SystemLogOperation tempLog=new SystemLogOperation();
		StringBuilder sbParameterContext = new StringBuilder();
		//sbParameterContext.append("WebLogAspect.doBefore()");
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		//获取系统登录info
		SystemLoginAccount systemLoginAccount =(SystemLoginAccount) request.getAttribute("account");
        if (systemLoginAccount==null){
            tempLog.setOperatorName("");
        }else {
            tempLog.setOperatorName(systemLoginAccount.getLoginName());
        }
		tempLog.setRequestUrl(request.getRequestURL().toString());
		tempLog.setHttpMethod( request.getMethod());
		tempLog.setIp(request.getRemoteAddr());
		tempLog.setClassName(joinPoint.getSignature().getDeclaringTypeName());
		tempLog.setMethodName(joinPoint.getSignature().getName());
		Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName =  enu.nextElement();
			if(!paraName.contains("pass")){
				sbParameterContext.append(paraName + ": " + request.getParameter(paraName)+";");
			}
		}
		tempLog.setParameterContext(sbParameterContext.toString());
		Date now = new Date();
        tempLog.setCreateTime(now) ;
        //设注解传来的数据执行的操作、操作模块的内容描述
		tempLog.setCommentDescribe(commentDescribe);
		if(actionType!=null) {
			tempLog.setActionType(actionType.getText());
		}
		commonService.saveOperationLog(tempLog);
	}

	@AfterReturning("webLog()")
	public void doAfterReturning(JoinPoint joinPoint) {
		// 处理完请求，返回内容
		log.info("WebLogAspect.doAfterReturning()");
	}



}
