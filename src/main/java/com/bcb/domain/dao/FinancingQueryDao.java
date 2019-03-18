package com.bcb.domain.dao;

import com.bcb.bean.dto.financing.UserInverstDetailDto;
import com.bcb.bean.dto.financing.UserInverstStatisticsResponse;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FinancingQueryDao extends BaseDao{

    /**
     * 查询用户理财投资统计
     * @param userId
     * @return
     */
    public List<UserInverstStatisticsResponse> getInvestFinancingStatistics(Integer userId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT b.Title AS title,  SUM(a.bcb_amount) AS coinAmount, sum(a.usdx_amount) AS usdxAmount");
        sql.append(" FROM  user_financing_record a LEFT JOIN financing_base_info b ON a.financing_uuid = b.FinancingUuid  ");
        sql.append(" WHERE a.user_id = ? ");
        sql.append(" GROUP BY a.financing_uuid  ");
        return this.jdbcTemplate.query(sql.toString(), new Object[] {userId},
                new BeanPropertyRowMapper<>(UserInverstStatisticsResponse.class));
    }

    /**
     * 查询用户理财投资详情的数量(注意同一个项目算一条)
     * @param userId
     * @return
     */
    public Integer getInvestFinancingCount(Integer userId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT count(distinct(a.financing_uuid)) sum ");
        sql.append(" FROM  user_financing_record a ");
        sql.append(" WHERE a.user_id = ? ");
        return this.jdbcTemplate.queryForObject(sql.toString(),Integer.class,userId);
    }

    /**
     * 查询用户理财投资详情(注意同一个项目算一条)
     * @param userId
     * @return
     */
    public List<UserInverstDetailDto> getInvestFinancingList(Integer userId,Integer start,Integer length) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT b.title, c.name, sum(a.bcb_amount) AS coinAmount,sum(a.usdx_amount) AS usdxAmount ");
        sql.append(" FROM  user_financing_record a LEFT JOIN financing_base_info b ON a.financing_uuid   = b.FinancingUuid  ");
        sql.append(" LEFT JOIN system_coin c ON c.id = a.coin_id ");
        sql.append(" WHERE a.user_id = ? ");
        sql.append(" GROUP BY a.financing_uuid  order by a.block_create_time desc  ");
        sql.append(" limit ?,? ");
        return this.jdbcTemplate.query(sql.toString(), new Object[] {userId,start,length},
                new BeanPropertyRowMapper<>(UserInverstDetailDto.class));
    }

}
