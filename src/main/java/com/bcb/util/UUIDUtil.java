package com.bcb.util;

import java.util.UUID;

/**
 * @ProjectName: Source
 * @Package: com.bcb.util
 * @ClassName: UUIDUtil
 * @Description: java类作用描述
 * @Author: qiang wen
 * @CreateDate: 4/24/2018 5:00 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 4/24/2018 5:00 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class UUIDUtil {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
