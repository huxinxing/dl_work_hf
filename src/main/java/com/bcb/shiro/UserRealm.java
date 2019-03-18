package com.bcb.shiro;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bcb.domain.dao.RoleDao;
import com.bcb.domain.repository.SystemButtonMenuRepository;
import com.bcb.domain.entity.SystemButtonMenu;
import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.domain.entity.SystemRolePermission;
import com.bcb.domain.enums.SystemButtonMenuStatusEnum;
import com.bcb.service.CommonService;
import com.bcb.util.TokenUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bcb.domain.entity.SystemLoginMenu;
import com.bcb.manager.LoginManager;


/**
 * 验证用户登录
 *
 * @author dana
 */
@Component("userRealm")
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private CommonService commonService;

	@Autowired
	private RoleDao roleDao;

	@Autowired
    private SystemButtonMenuRepository systemButtonMenuRepository;

	/**
	 * 大坑！，必须重写此方法，不然Shiro会报错
	 */
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JWTToken;
	}
	//权限资源角色
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SystemLoginAccount systemLoginAccount =(SystemLoginAccount) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		List<SystemLoginMenu> menus = commonService.queryMenuList(systemLoginAccount.getId());
		for (SystemLoginMenu menu : menus) {
			info.addStringPermission(menu.getMenuKey());
			List<SystemButtonMenu> sbmlist = systemButtonMenuRepository.querySystemButtonMenusByMenuIdAndStatus(menu.getId(), SystemButtonMenuStatusEnum.显示.getValue());
			for (SystemButtonMenu sbm: sbmlist) {
				List<SystemRolePermission> srpList = roleDao.querySystemRolePermission(systemLoginAccount.getRole(),sbm.getMenuId());
				Set<String> set = new HashSet();
				for (SystemRolePermission srp: srpList) {
					String [] str = srp.getCrud().split(",");
					for (int i = 0; i < str.length; i++) {
						set.add(str[i]);
					}
				}
				for (String it: set) {
					if(sbm.getId()==(it.equals("")?0:Integer.parseInt(it))){
						info.addStringPermission(sbm.getButtonKey());
					}
				}
			}
		}

		return info;
	}

	//登录验证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String tk = (String) token.getCredentials();
		// 解密获得username，用于和数据库进行对比
		Claims claims = TokenUtil.parseJWT(tk);
        String userName =(String) claims.get("jti");
        String passWord =(String) claims.get("iss");
		SystemLoginAccount accountDto = loginManager.validLogin(userName, passWord);
		if (accountDto==null) {
			throw new UnknownAccountException();
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(accountDto,tk,getName());
		return info;
	}
}