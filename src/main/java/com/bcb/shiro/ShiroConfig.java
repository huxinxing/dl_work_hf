package com.bcb.shiro;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
	
	@Bean
	public UserRealm getMyRealm(){
        UserRealm realm = new UserRealm();
		realm.setCachingEnabled(false);//关闭shrio的缓存
		return realm;
	}
	
	@Bean(name = "securityManager")
    public DefaultWebSecurityManager getManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        // 使用自己的realm
        manager.setRealm(getMyRealm());
        /*
         * 关闭shiro自带的session，详情见文档
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        manager.setSubjectDAO(subjectDAO);
        return manager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 添加自己的过滤器并且取名为jwt
//        Map<String, Filter> filterMap = new HashMap<>();
//        filterMap.put("jwt", new JWTFilter());
//        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(securityManager);
//        factoryBean.setUnauthorizedUrl("/");

        Map<String, String> filterRuleMap = new HashMap<>();
        filterRuleMap.put("/admin/login/**","anon");
        filterRuleMap.put("/admin/url/**", "anon");
        filterRuleMap.put("/admin/user/tree/list", "anon");
        filterRuleMap.put("/admin/project/export/**", "anon");
        filterRuleMap.put("/admin/project/file/upload", "anon");
        filterRuleMap.put("/admin/project/invest/rate", "anon");

//        filterRuleMap.put("/admin/sys/**", "jwt");
//        filterRuleMap.put("/admin/user/**", "jwt");
//        filterRuleMap.put("/admin/trade/**", "jwt");
//        filterRuleMap.put("/admin/project/**", "jwt");
//        filterRuleMap.put("/admin/audit/**", "jwt");
//        filterRuleMap.put("/department/**", "jwt");
//        filterRuleMap.put("/admin/encrypt/**", "jwt");
//        filterRuleMap.put("/file/**", "jwt");
//        filterRuleMap.put("/admin/userAgents/**", "jwt");

        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }

    /**
     * 下面的代码是添加注解支持
     */
//    @Bean
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
//        defaultAAP.setProxyTargetClass(true);
//        return defaultAAP;
//    }
//
//    @Bean
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }
}
