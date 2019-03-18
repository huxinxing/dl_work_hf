package com.bcb.interceptor;

import com.bcb.annotation.LoginRequired;
import com.bcb.config.RedisClient;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.shiro.JWTToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor{

    @Resource
    protected RedisClient redisClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        if (methodAnnotation != null) {
            //从请求头 获取Authorization
            String authorization = gainAuthorization(request);
            if (StringUtils.isEmpty(authorization)) {
                log.error("=====无请求头，重新登录=====");
                String str = "{\"url\":\"/login.html\",\"message\":\"已过期,请重新登录\"}";
                returnJson(response,str);
                return false;
            }else {
                JWTToken token = new JWTToken(authorization);
                // 提交给realm进行登入，如果错误他会抛出异常并被捕获
                SecurityUtils.getSubject().login(token);
                SystemLoginAccount account =(SystemLoginAccount) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
                if (account == null) {
                    log.error("====用户不存在，请重新登录===");
                    String str = "{\"url\":\"/login.html\",\"message\":\"用户不存在,请重新登录\"}";
                    returnJson(response,str);
                    return false;
                }
                if (StringUtils.isBlank(redisClient.get("user"+account.getId()))){
                    log.error("====账号登录超时===");
                    String str = "{\"url\":\"/login.html\",\"message\":\"账号登录超时,请重新登录\"}";
                    returnJson(response,str);
                    return false;
                }

                if (!authorization.equals(redisClient.get("user"+account.getId()))){
                    log.error("====账号被踢了===");
                    String str = "{\"url\":\"/login.html\",\"message\":\"账号被踢了,请重新登录\"}";
                    returnJson(response,str);
                    return false;
                }
                redisClient.resetTimeOut("user"+account.getId(),1800);
                request.setAttribute("account", account);
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private String  gainAuthorization(HttpServletRequest request) {
        Enumeration enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String key =enumeration.nextElement().toString();
            if(key.equalsIgnoreCase("Authorization"))
                return request.getHeader(key);
        }
        return null;
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception{
        PrintWriter writer = null;
        response.setStatus(302);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.write(json);
        } catch (IOException e) {
            log.error("response error",e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
