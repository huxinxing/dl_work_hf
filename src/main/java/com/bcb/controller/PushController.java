package com.bcb.controller;

import com.bcb.annotation.LoginRequired;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.ResponseStatus;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.PushRecordDto;
import com.bcb.bean.dto.PushRecordSearchDto;
import com.bcb.domain.entity.PushRecord;
import com.bcb.service.PushRecordService;
import com.bcb.service.PushUserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/push")
public class PushController {

    @Autowired
    private PushRecordService pushRecordService;

    @Autowired
    private PushUserService pushUserService;

    //todo: 需要在数据库中增加push权限和菜单设置
    //@LoginRequired
    //@RequiresPermissions(value={"push"},logical= Logical.OR)
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ResponseResult add(@RequestBody PushRecordSearchDto dto) {
        // 新增推送消息
        if(dto == null){
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.DATA_WRONG.getKey(),ResponseStatus.DATA_WRONG.getValue());
        }
        PushRecord record = new PushRecord();
        BeanUtils.copyProperties(dto, record);
        try{
            pushRecordService.add(record);
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(),ResponseStatus.SUCCESS.getValue());
        }catch (Exception ex){
            log.error("新增消息失败："+dto.toString(),ex);
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ex.getMessage());
        }
    }

    @LoginRequired
    @RequiresPermissions(value={"push"},logical= Logical.OR)
    @RequestMapping(value = "/{userId}/get",method = RequestMethod.POST)
    public ResponseResult getJgId(@PathVariable Integer userId) {
        // 获取用户对应的极光编号
        String jgId = null;
        try{
            jgId = pushUserService.get(userId);
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(), jgId,ResponseStatus.SUCCESS.getValue());
        }catch (Exception ex){
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ex.getMessage());
        }
    }

    @LoginRequired
    @RequiresPermissions(value={"push"},logical= Logical.OR)
    @RequestMapping(value = "/{userId}/update/{jgId}",method = RequestMethod.POST)
    public ResponseResult updateJgId(@PathVariable Integer userId, @PathVariable String jgId) {
        // 更新用户的极光编号
        try{
            pushUserService.update(userId, jgId);
            return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(), jgId,ResponseStatus.SUCCESS.getValue());
        }catch (Exception ex){
            return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ex.getMessage());
        }
    }

    //@LoginRequired
    //@RequiresPermissions(value={"push"},logical= Logical.OR)
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public PageInfo<PushRecordDto> list(@RequestBody PushRecordSearchDto dto) {
        PageInfo<PushRecordDto> infos = new PageInfo<>();
        try{
            infos = pushRecordService.list(dto);

            //return new ResponseResult(WorldValue.SUCCESS,ResponseStatus.SUCCESS.getKey(), infos,ResponseStatus.SUCCESS.getValue());
        }catch (Exception ex){
            //return new ResponseResult(WorldValue.ERROR,ResponseStatus.CODE_WRONG.getKey(),ex.getMessage());
            log.error("获取消息列表失败：", ex);
        }
        return infos;
    }
}
