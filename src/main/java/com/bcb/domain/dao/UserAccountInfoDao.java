package com.bcb.domain.dao;

import com.bcb.domain.entity.SystemButtonMenu;
import com.bcb.domain.entity.SystemLoginMenu;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserAccountInfoDao extends BaseDao{

    public List<SystemLoginMenu> queryMenuList(Integer accountId) {
        StringBuilder countSql = new StringBuilder();
        if (accountId==null){
            return null;
        }
        countSql.append("select slm.id,slm.MenuName,slm.MenuPath,slm.MenuKey,slm.Sort,slm.Status,slm.ParentId,slm.CreateTime from system_login_menu" );
        countSql.append(" slm left join system_role_permission sdp on slm.id = sdp.menuId  where slm.Status = 1 AND  slm.button = 0 AND sdp.roleId" );
        countSql.append(" = (select sld.Role from system_login_account sld where sld.id = "+accountId+") order by ParentId asc, Sort asc");
        return this.jdbcTemplate.query(countSql.toString(),new Object[]{},new BeanPropertyRowMapper<>(SystemLoginMenu.class));
    }

    public List<SystemLoginMenu> queryMenuListOrder() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from system_login_menu   order by ParentId asc ,Sort asc");
        return this.jdbcTemplate.query(sql.toString(),new Object[]{},new BeanPropertyRowMapper<>(SystemLoginMenu.class));
    }


    public List<SystemButtonMenu> queryButtonMenuList() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from system_button_menu where Status = 1");
        return this.jdbcTemplate.query(sql.toString(),new Object[]{},new BeanPropertyRowMapper<>(SystemButtonMenu.class));
    }

    public List<SystemLoginMenu> queryPermissionButtonMenuList(Integer accountId) {
        StringBuilder countSql = new StringBuilder();
        if (accountId==null){
            return null;
        }
        countSql.append("select slm.id,slm.MenuName,slm.MenuPath,slm.MenuKey,slm.Sort,slm.Status,slm.ParentId,slm.CreateTime from system_login_menu" );
        countSql.append(" slm left join system_role_permission sdp on slm.id = sdp.menuId  where slm.Status = 1 AND slm.button = 1 AND  sdp.roleId" );
        countSql.append(" = (select sld.Role from system_login_account sld where sld.id = "+accountId+") order by ParentId asc, Sort asc");
        return this.jdbcTemplate.query(countSql.toString(),new Object[]{},new BeanPropertyRowMapper<>(SystemLoginMenu.class));
    }
}
