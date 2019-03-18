package com.bcb.service;


import com.bcb.bean.dto.RoleDto;
import com.bcb.bean.dto.SystemLoginRoleAndMenu;
import com.bcb.domain.dao.RoleDao;
import com.bcb.domain.repository.SystemLoginRoleRepository;
import com.bcb.domain.entity.SystemLoginMenu;
import com.bcb.domain.entity.SystemLoginRole;
import com.bcb.util.annotation.TransactionEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private SystemLoginRoleRepository systemLoginRoleRepository;


    @TransactionEx
    public void insertRolePermission(Integer roleId, Integer[] menuIdArray) {
        roleDao.deletePermissionsById(roleId);//删除权限
        roleDao.insertNewPermissionsMenuId(roleId,menuIdArray);//插入新的菜单权限
    }


    public List<RoleDto> selectRoleById(Integer id) {
        List<SystemLoginRole> ls =  roleDao.selectRoleById(id);
        List<RoleDto> lss= new ArrayList<>();
        for (SystemLoginRole sl : ls) {
            RoleDto sd= new RoleDto();
            sd.setId(sl.getId());
            sd.setModifyTime(sl.getModifyTime());
            sd.setCreateTime(sl.getCreateTime());
            sd.setRoleName(sl.getRoleName());
            //权限不需要上级,dana,2018-03-22
            //sd.setParentId(sl.getParentId());
            lss.add(sd);
        }
        return lss;
    }


    @TransactionEx
    public void updateRolePermission(Integer id, String permissions) {
        roleDao.deletePermissionsById(id);//删除权限
        roleDao.insertNewPermissions(id,permissions);//插入新的权限
    }

    public List<SystemLoginRoleAndMenu> queryAllRolesAndMenu() {
        List<SystemLoginRoleAndMenu> listSystemLoginRoleAndMenu= new ArrayList<SystemLoginRoleAndMenu>();
        List<SystemLoginRole> listSystemLoginRole=systemLoginRoleRepository.findAll();
        for (SystemLoginRole tempSystemLoginRole:listSystemLoginRole) {
            SystemLoginRoleAndMenu tempSystemLoginRoleAndMenu=new SystemLoginRoleAndMenu();
            tempSystemLoginRoleAndMenu.setId(tempSystemLoginRole.getId());
            tempSystemLoginRoleAndMenu.setRoleName(tempSystemLoginRole.getRoleName());
            tempSystemLoginRoleAndMenu.setCreateTime(tempSystemLoginRole.getCreateTime());
            tempSystemLoginRoleAndMenu.setModifyTime(tempSystemLoginRole.getModifyTime());
            tempSystemLoginRoleAndMenu.setListSystemLoginMenu(roleDao.queryRolesPermission(tempSystemLoginRole.getId()));
            listSystemLoginRoleAndMenu.add(tempSystemLoginRoleAndMenu);
        }
        return listSystemLoginRoleAndMenu;
    }





    public List<SystemLoginRoleAndMenu> queryAllRolesAndMenuById(Integer id) {
        List<SystemLoginRoleAndMenu> listSystemLoginRoleAndMenu= new ArrayList<>();

        SystemLoginRole systemLoginRole=systemLoginRoleRepository.findById(id).get();

        SystemLoginRoleAndMenu tempSystemLoginRoleAndMenu=new SystemLoginRoleAndMenu();
        tempSystemLoginRoleAndMenu.setId(systemLoginRole.getId());
        tempSystemLoginRoleAndMenu.setRoleName(systemLoginRole.getRoleName());
        tempSystemLoginRoleAndMenu.setCreateTime(systemLoginRole.getCreateTime());
        tempSystemLoginRoleAndMenu.setModifyTime(systemLoginRole.getModifyTime());
        tempSystemLoginRoleAndMenu.setListSystemLoginMenu(roleDao.queryRolesPermission(systemLoginRole.getId()));
        listSystemLoginRoleAndMenu.add(tempSystemLoginRoleAndMenu);

        return listSystemLoginRoleAndMenu;
    }


    public List<SystemLoginMenu> queryRolesPermission(Integer id) {
        return roleDao.queryRolesPermission(id);
    }

    public List<SystemLoginRole> queryRoleUserOutName(Integer id,String name) {
        return roleDao.queryRoleUserOutName(id,name);
    }

}
