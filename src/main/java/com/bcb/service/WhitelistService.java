package com.bcb.service;


import com.bcb.bean.dto.white.WhiteDto;
import com.bcb.domain.entity.FinancingActivityInterstCoupons;
import com.bcb.domain.repository.WhitelistRepository;
import com.bcb.bean.dto.WhitelistSearchDto;
import com.bcb.domain.entity.Whitelist;
import com.bcb.util.DateUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WhitelistService {

    @Autowired
    private WhitelistRepository whitelistRepository;


    /**
     * 获取白名单
     *
     * @param walletAddress
     */
    public PageInfo<WhiteDto> whiteListService(String walletAddress, Integer pageSize, Integer pageNum) throws Exception {
        try {
            // 查询是否已有记录
            Pageable pageable = PageRequest.of(pageNum-1, pageSize, new Sort(Sort.Direction.DESC, "modifyTime"));
            Specification<Whitelist> specification = new Specification<Whitelist>() {
                @Override
                public Predicate toPredicate(Root<Whitelist> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    if (!StringUtils.isEmpty(walletAddress)) {
                        Predicate predicate = criteriaBuilder.equal(root.get("address").as(Integer.class), walletAddress);
                        predicates.add(predicate);
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            };
            Page<Whitelist> pages = whitelistRepository.findAll(specification, pageable);
            List<WhiteDto> resultList = new ArrayList<>();
            for (Whitelist whitelist : pages.getContent()){
                WhiteDto whiteDto = new WhiteDto();
                whiteDto.setId(whitelist.getId().toString());
                whiteDto.setWalletAddress(ObjectUtils.isEmpty(whitelist.getAddress())?"-":whitelist.getAddress());
                whiteDto.setRemark(ObjectUtils.isEmpty(whitelist.getRemark())?"-":whitelist.getRemark());
                whiteDto.setModifyTime(DateUtil.DateToStr(whitelist.getModifyTime(),"yyyy-MM-dd HH:mm:ss"));
                resultList.add(whiteDto);
            }
            PageInfo<WhiteDto> infos = new PageInfo<>();
            infos.setList(resultList);
            infos.setTotal(pages.getTotalElements());
            return infos;
        } catch (Exception ex) {
            log.error("获取白名单失败：", ex);
            throw new Exception("获取白名单失败");
        }
    }

    /**
     * 新增白名单
     *
     * @param walletAddress
     */
    public String whiteAddService(String walletAddress) throws Exception {

        Whitelist record = whitelistRepository.findByAddress(walletAddress);
        if (record != null) {
            throw new Exception("地址已存在");
        }
        try {
            Whitelist whitelistUpate = new Whitelist();
            whitelistUpate.setModifyTime(new Timestamp(System.currentTimeMillis()));
            whitelistUpate.setAddress(walletAddress);
            whitelistUpate.setState(true);
            // 保存数据到数据库中
            whitelistRepository.save(whitelistUpate);
        } catch (Exception ex) {
            log.error("新增白名单信息(" + record.toString() + ")入库失败：", ex);
            throw new Exception("新增白名单失败");
        }
        return "白名单新增成功";
    }

    /**
     * 删除白名单
     *
     * @param ids
     */
    public String whiteDelService(Integer ids) throws Exception {
        // 删除数据到数据库中
        whitelistRepository.deleteById(ids);
        return "删除成功";
    }

    /**
     * 修改白名单备注
     */
    public String whiteUpdateRemarkService(Integer whiteId,String remark) throws Exception {
        Whitelist whitelist = whitelistRepository.findOneById(whiteId);
        if(ObjectUtils.isEmpty(whiteId)){
            throw new Exception("获取白名单失败 whiteId:" + whiteId);
        }
        whitelist.setRemark(remark);
        whitelistRepository.save(whitelist);
        return "白名单修改成功";
    }
}
