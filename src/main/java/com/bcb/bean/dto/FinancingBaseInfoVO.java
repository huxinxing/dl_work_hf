package com.bcb.bean.dto;



import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bcb.bean.dto.statistics.FinancingLanagerMessageDto;
import lombok.Data;

/**
 * @ProjectName: Source
 * @Package: com.bcb.bean.dto
 * @ClassName: FinancingBaseInfoVO
 * @Description: 理财信息显示实体类
 * @Author: qiang wen
 * @CreateDate: 5/7/2018 8:03 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 5/7/2018 8:03 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
@Data
public class FinancingBaseInfoVO {

    private String financingUuid;

    private String createTime; // 记录时间

    private String title;

    private String tag;

    private String coinType;

    private Integer freezeNumber;  // 冻结时间

    private String freezeUnit;     // 冻结单元，日、月、年

    private String attentions;     // 注意事件

    private BigDecimal expense; // 代投费

    private BigDecimal foundationRate; // 超额收益基金公司返点比例

    private String lang;	       // 语言类型，多个用,区分

    private String showLang;// 语言显示

    private String paymentType;    // 支付币种，多个用,区分

    private Integer status;        // 理财状态,0-初始状态,1-募集中,2-募集结束,3-返币结束,-1-无效

    private BigDecimal annualRate; // 年化率

    private BigDecimal totalRate;  // 总收益率

    private BigDecimal Amount; 		   // 理财可销售总量

    private BigDecimal coinLimit;  // 单笔投资限额

    private BigDecimal coinMinLimit; // 单笔限额最小值

    private String serialNum;  // 理财编号

    private BigDecimal createBcb2Usdx; // 理财成立时期BCB价格

    private String financingIconUrl;//理财列表背景墙url

    private Map<String,String> token;//理财钱包地址
    
    private BigDecimal SubscriptionFeeRate; //认购费折扣率
    
    private BigDecimal DiscountRate; //产品折扣率

    private List<FinancingLanagerMessageDto> financingLanagerMessageList;
    
    private List<FinancingBaseInfoVO> BaseInfoVOList;// 理财多语言基本信息集合

}
