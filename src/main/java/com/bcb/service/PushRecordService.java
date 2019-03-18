package com.bcb.service;


import cn.jiguang.common.utils.StringUtils;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.dto.PushRecordDto;
import com.bcb.bean.dto.PushRecordSearchDto;
import com.bcb.domain.repository.PushRecordRepository;
import com.bcb.domain.entity.PushRecord;
import com.bcb.util.annotation.TransactionEx;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Service
public class PushRecordService {

    @Value("${jg.masterSecret}")
    private String masterSecret;

    @Value("${jg.appKey}")
    private String appKey;

    @Value("${jg.audience}")
    private String audience;
    private final String AUDIENCE_TEST="test";
    private final String AUDIENCE_ONLINE="online";

    private boolean isOnline(){
        return (audience.trim().compareToIgnoreCase(AUDIENCE_ONLINE) == 0);
    }

    private final String TYPE = "type";
    private final String VALUE = "value";

    @Autowired
    private PushRecordRepository pushRecordRepository;

    /**
     * 新增推送记录，同时推送出去
     * @param pushRecord
     */
    @TransactionEx
    public void add(PushRecord pushRecord){
        Boolean result = false;

        // 记录结果入库
        pushRecord.setState(false);
        pushRecord.setCreateTime(new Date());
        pushRecord = pushRecordRepository.save(pushRecord);
        // 极光推送
        result = push(pushRecord);

        pushRecord.setState(result);
        // 保存发送状态
        pushRecord = pushRecordRepository.save(pushRecord);

    }

    /**
     * 检索推送记录，支持过滤：targetId, 时间范围, type, title, message, 分页
     * @param pushRecordSearchDto
     */
    public PageInfo<PushRecordDto> list(PushRecordSearchDto pushRecordSearchDto){
        Pageable pageable = PageRequest.of(pushRecordSearchDto.getPageNum(), pushRecordSearchDto.getPageSize(), new Sort(Sort.Direction.DESC, "createTime"));
        Specification spec = build(pushRecordSearchDto);

        Page<PushRecord> pages = null;
        try{
            pages = pushRecordRepository.findAll(spec, pageable);
        }catch (Exception ex){
            log.error("查询数据库中推送消息失败，查询条件："+ pushRecordSearchDto.toString(), ex);
        }

        PageInfo<PushRecordDto> infos = new PageInfo<>();
        infos.setList(new ArrayList<>());
        for(PushRecord pr : pages.getContent()){
            PushRecordDto dto = new PushRecordDto();
            BeanUtils.copyProperties(pr, dto);
            infos.getList().add(dto);
        }

        infos.setSize(pages.getSize());
        infos.setPages(pages.getTotalPages());
        infos.setTotal(pages.getTotalElements());
        infos.setPageSize(pushRecordSearchDto.getPageSize());
        infos.setPageNum(pushRecordSearchDto.getPageNum());
        return infos;
    }

    /**
     * 支持过滤：targetId, 时间范围, type, title, message, 分页
     * @param dto
     * @return
     */
    private Specification build(PushRecordSearchDto dto){
        Specification spec = new Specification() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate pred = null;
                if(!StringUtils.isEmpty(dto.getTargetId())){
                    Predicate tmp = criteriaBuilder.equal(root.get("targetId"), dto.getTargetId());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getExtraValue())){
                    Predicate tmp = criteriaBuilder.like(root.get("extraValue"), "%"+dto.getExtraValue()+"%");
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(dto.getExtraType()!=null){
                    Predicate tmp = criteriaBuilder.equal(root.get("extraType"), dto.getExtraType());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(!StringUtils.isEmpty(dto.getTitle())){
                    Predicate tmp = criteriaBuilder.like(root.get("title"), "%"+dto.getTitle()+"%");
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }

                if(!StringUtils.isEmpty(dto.getMessage())){
                    Predicate tmp = criteriaBuilder.like(root.get("message"), "%"+dto.getMessage()+"%");
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(dto.getFrom() != null && dto.getTo()!=null && dto.getFrom().before(dto.getTo())){
                    Predicate tmp = criteriaBuilder.between(root.get("createTime"), dto.getFrom(), dto.getTo());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                if(dto.getState() != null){
                    Predicate tmp = criteriaBuilder.equal(root.get("state"), dto.getState());
                    if(pred == null){
                        pred = tmp;
                    }else{
                        pred = criteriaBuilder.and(pred, tmp);
                    }
                }
                return pred;
            }
        };
        return spec;
    }

    private Boolean push(PushRecord pushRecord){
        JPushClient pushClient = null;
        try{
            pushClient= new JPushClient(masterSecret, appKey);
        }catch (Exception ex){
            log.warn("创建极光推送对象失败：", ex);
        }
        if(pushClient == null){
            return false;
        }
        // 把消息id集成到extra value中
        if(pushRecord.getId() != null){
            try{
                JSONObject json = new JSONObject();
                try{
                    json = JSON.parseObject(pushRecord.getExtraValue());
                }catch (Exception e){

                }
                json.put("id", pushRecord.getId().toString());
                pushRecord.setExtraValue(json.toJSONString());
                log.debug("推送消息增加id："+pushRecord.toString());
            }catch (Exception ex){
            }
        }
        Message message = null;
        if(!StringUtils.isEmpty(pushRecord.getExtraValue())){
            message = Message.newBuilder().setTitle(pushRecord.getTitle()).setMsgContent(pushRecord.getMessage())
                    .addExtra(TYPE, pushRecord.getExtraType().toString()).addExtra(VALUE, pushRecord.getExtraValue()).build();
        }else{
            message = Message.newBuilder().setTitle(pushRecord.getTitle()).setMsgContent(pushRecord.getMessage())
                    .addExtra(TYPE, pushRecord.getExtraType().toString()).build();
        }

        PushPayload android = null;
        PushPayload ios = null;
        if(StringUtils.isEmpty(pushRecord.getTargetId())){
            android = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.tag(audience)).setMessage(message).build();

            ios = PushPayload.newBuilder().setOptions(Options.newBuilder().setApnsProduction(isOnline()).build()).setPlatform(Platform.ios()).setAudience(Audience.tag(audience)).
                    setNotification(Notification.newBuilder().setAlert(pushRecord.getTitle()+"："+pushRecord.getMessage()).
                            addPlatformNotification(IosNotification.newBuilder().incrBadge(1).
                                    addExtra(TYPE,pushRecord.getExtraType().toString()).addExtra(VALUE,pushRecord.getExtraValue()).build()).build()).build();
        }else{
            android = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.registrationId(pushRecord.getTargetId())).setMessage(message).build();

            ios = PushPayload.newBuilder().setOptions(Options.newBuilder().setApnsProduction(isOnline()).build()).setPlatform(Platform.ios()).setAudience(Audience.registrationId(pushRecord.getTargetId())).
                    setNotification(Notification.newBuilder().setAlert(pushRecord.getTitle()+"："+pushRecord.getMessage()).
                            addPlatformNotification(IosNotification.newBuilder().incrBadge(1).
                                    addExtra(TYPE,pushRecord.getExtraType().toString()).addExtra(VALUE,pushRecord.getExtraValue()).build()).build()).build();
        }
        Boolean status = false;
        try{
            PushResult result = pushClient.sendPush(android);
            log.info("极光推送android结果："+result.toString());
            status = true;
        }catch (Exception ex){
            log.error("发送android极光推送异常：", ex);
        }
        try{
            PushResult result = pushClient.sendPush(ios);
            log.info("极光推送ios结果："+result.toString());
            status = true;
        }catch (Exception ex){
            log.error("发送ios极光推送异常：", ex);
        }
        return status;
    }
}
