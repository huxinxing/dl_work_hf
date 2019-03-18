package com.bcb.util.eth;

import org.apache.commons.lang3.StringUtils;

/**
 * 项目信息
 * Created by liq on 2018/1/15.
 */
public class Tag51 implements ITlvEnable {

    private byte[] tag = new byte[]{(byte) 0x51};   // 长度1
    private int len;             // 长度4
    private String projectInfo;  // 项目主键，长度len

    public Tag51(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    @Override
    public byte[] getTlv() {
        if (StringUtils.isBlank(projectInfo)) {
            return null;
        }
        byte bProjectInfo[] = projectInfo.getBytes();
        len = bProjectInfo.length;

        int resultLen = 5 + len;
        byte[] result = new byte[resultLen];

        System.arraycopy(tag, 0, result, 0, 1);
        System.arraycopy(ByteHelper.int2bytes(len), 0, result, 1, 4);
        System.arraycopy(bProjectInfo, 0, result, 5, len);

        return result;
    }

}
