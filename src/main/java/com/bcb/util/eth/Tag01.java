package com.bcb.util.eth;

import org.apache.commons.lang3.StringUtils;

/**
 * 币种类型
 * Created by liq on 2018/1/15.
 */
public class Tag01 implements ITlvEnable {

    private byte[] tag = new byte[]{(byte) 0x01}; // 长度1
    private int len;            // 长度4
    private int walletType;     // 长度4
    private int descLen;        // 长度4
    private String desc;        // 长度descLen

    public Tag01(int walletType, String desc) {
        this.walletType = walletType;
        this.desc = desc;
    }

    @Override
    public byte[] getTlv() {
        if (StringUtils.isBlank(desc)) {
            return null;
        }
        byte bDesc[] = desc.getBytes();
        descLen = bDesc.length;
        len = descLen + 8;

        int resultLen = 5 + len;
        byte[] result = new byte[resultLen];

        System.arraycopy(tag, 0, result, 0, 1);
        System.arraycopy(ByteHelper.int2bytes(len), 0, result, 1, 4);
        System.arraycopy(ByteHelper.int2bytes(walletType), 0, result, 5, 4);
        System.arraycopy(ByteHelper.int2bytes(descLen), 0, result, 9, 4);
        System.arraycopy(bDesc, 0, result, 13, descLen);

        return result;
    }

}
