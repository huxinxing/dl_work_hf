package com.bcb.service;


import com.bcb.domain.repository.PushUserRepository;
import com.bcb.domain.entity.PushUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

@Slf4j
@Service
public class PushUserService {

    @Autowired
    private PushUserRepository pushUserRepository;

    /**
     * 更新用户编号和极光编号的关联关系
     * @param userId
     * @param jgId
     */
    public void update(Integer userId, String jgId) throws Exception {
        try{
            // 查询是否已有记录
            PushUser pushUser = pushUserRepository.findByUserId(userId);
            if(pushUser == null){
                pushUser = new PushUser();
            }
            pushUser.setUserId(userId);
            pushUser.setJgId(jgId);
            pushUser.setModifyTime(new Timestamp(System.currentTimeMillis()));
            // 保存数据到数据库中
            pushUserRepository.save(pushUser);
        }catch (Exception ex){
            log.error("更新用户的极光编号信息("+userId.toString()+", "+jgId+")入库失败：", ex);
            throw new Exception("更新用户的极光编号失败");
        }
    }

    /**
     * 获取用户的极光编号
     * @param userId
     */
    public String get(Integer userId) throws Exception {
        try{
            // 查询是否已有记录
            PushUser pushUser = pushUserRepository.findByUserId(userId);
            return pushUser.getJgId();
        }catch (Exception ex){
            log.error("获取用户"+userId+"的极光编号失败：", ex);
            throw new Exception("没有找到用户"+userId+"记录");
        }
    }
}
