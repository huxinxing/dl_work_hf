package com.bcb.domain.dao;

import com.bcb.domain.entity.SystemLoginAccount;
import com.bcb.domain.entity.SystemLoginRole;
import com.bcb.domain.entity.SystemLoginMenu;
import com.bcb.domain.entity.SystemRolePermission;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
* xujintao
* */

@Repository
public class RoleDao extends BaseDao{

    public List<SystemLoginRole> selectRoleById(Integer id) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from system_login_role where FIND_IN_SET(id, getChildLst("+id+"));");
        return this.jdbcTemplate.query(sql.toString(), new Object[]{},new BeanPropertyRowMapper<>(SystemLoginRole.class));
    }


    public void deletePermissionsById(Integer id) {
        String sql = "delete from system_role_permission where RoleId = "+id+"";
        this.jdbcTemplate.execute(sql);
    }


    public void insertNewPermissions(Integer id, String permissions) {
        String [] permissionArray = permissions.split(",");
        for (String permission: permissionArray ) {
             this.jdbcTemplate.execute("insert into system_role_permission (menuId,roleId) VALUES ("+permission+","+id+")");
        }

    }


    public void insertNewPermissionsMenuId(Integer roleId, Integer[] menuIdArray) {
        StringBuilder sql =  new StringBuilder();
        sql.append("insert into system_role_permission (menuId,roleId) VALUES ");
        for (Integer menuId: menuIdArray ) {
            sql.append("("+menuId+","+roleId+"),");
        }
        if (menuIdArray.length > 0)
            sql.deleteCharAt(sql.length() - 1);
        this.jdbcTemplate.execute(sql.toString());
    }

    public List<SystemLoginMenu> queryRolesPermission(Integer id) {
        StringBuilder sql =  new StringBuilder();
        sql.append("select slm.Id as Id,slm.MenuName as MenuName,slm.MenuPath as MenuPath,slm.MenuKey as MenuKey,slm.Sort as Sort,slm.Status,slm.ParentId as ParentId,slm.CreateTime as CreateTime from system_login_menu slm left" +
                " join system_role_permission sdp on slm.Id = sdp.menuId where sdp.RoleId = "+id+"");
        return this.jdbcTemplate.query(sql.toString(), new Object[]{},new BeanPropertyRowMapper<>(SystemLoginMenu.class));
    }

    public List<SystemLoginRole> queryRoleUserOutName(Integer id,String name) {
        StringBuilder sql =  new StringBuilder();
        sql.append("select * from system_login_role where id !="+id+" and RoleName = '"+name+"'");
        return this.jdbcTemplate.query(sql.toString(), new Object[]{},new BeanPropertyRowMapper<>(SystemLoginRole.class));
    }

    public List<SystemLoginAccount> querySystemLoginAccountWithOutName(Integer id, String name) {
        StringBuilder sql =  new StringBuilder();
        sql.append("select * from system_login_account where id !="+id+" and LoginName = '"+name+"'");
        return this.jdbcTemplate.query(sql.toString(), new Object[]{},new BeanPropertyRowMapper<>(SystemLoginAccount.class));
    }


    public List<SystemRolePermission> querySystemRolePermission(Integer roleId, Integer menuId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from system_role_permission where roleId="+roleId+" and menuId="+menuId+"");
        return this.jdbcTemplate.query(sql.toString(), new Object[]{},new BeanPropertyRowMapper<>(SystemRolePermission.class));
    }
}




