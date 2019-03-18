package com.bcb.util.eth;

/**
 * Created by liq on 2018/1/15.
 */
public class FileUtil {
    private String walletType = "";
    private String walletLevel = "";
    private String projectUuid = "";
    private String token = "";

    public byte[] getICOProject(String projectUuid) {
        Tag01 tag01 = new Tag01(3, "ETH");
        byte[] bTag01 = tag01.getTlv();

        Tag51 tag51 = new Tag51(projectUuid);
        byte[] bTag51 = tag51.getTlv();

        int tlvLen = bTag01.length + bTag51.length;

        byte[] result = new byte[tlvLen + 4];

        System.arraycopy(ByteHelper.int2bytes(tlvLen), 0, result, 0, 4);
        System.arraycopy(bTag01, 0, result, 4, bTag01.length);
        System.arraycopy(bTag51, 0, result, 4 + bTag01.length, bTag51.length);

        return result;
    }

    public String parseICOProject(byte[] src, String certificatePath) throws Exception {
        if (null == src || src.length < 270) {
            return null;
        }
        walletType = "";
        walletLevel = "";
        projectUuid = "";
        token = "";

        byte[] bWalletLevel = new byte[2];
        System.arraycopy(src, 8, bWalletLevel, 0, 2);
        System.out.println(ByteHelper.bytes2int(bWalletLevel));
        walletLevel = String.valueOf(ByteHelper.bytes2int(bWalletLevel));

        byte[] bContentLen = new byte[4];
        System.arraycopy(src, 10, bContentLen, 0, 4);
        int tlvLen = ByteHelper.bytes2int(bContentLen);

        byte[] bSignature = new byte[256];
        System.arraycopy(src, 14 + tlvLen, bSignature, 0, 256);

        byte[] bSource = new byte[src.length - 256];
        System.arraycopy(src, 0, bSource, 0, src.length - 256);
        /*if (!CertificateUtils.verifySign(bSource, bSignature, certificatePath)) {
            return null;
        }*/

        byte[] bTlv = new byte[tlvLen];
        System.arraycopy(src, 14, bTlv, 0, tlvLen);

        while (bTlv.length > 0) {
            bTlv = parseTlv(bTlv);
        }

        return walletLevel + "_" + walletType + "_" + projectUuid + "_" + token;
    }

    private byte[] parseTlv(byte[] bTlv) throws Exception {
        int tlvLen = bTlv.length;
        byte tag = bTlv[0];
        byte[] bLen = new byte[4];
        System.arraycopy(bTlv, 1, bLen, 0, 4);
        int len = ByteHelper.bytes2int(bLen);

        byte[] bContent = new byte[len];
        System.arraycopy(bTlv, 5, bContent, 0, len);
        if (tag == (byte) 0x01) {
            byte[] temp = new byte[4];
            System.arraycopy(bTlv, 5, temp, 0, 4);
            walletType = String.valueOf(ByteHelper.bytes2int(temp));
        } else if (tag == (byte) 0x20) {
            token = new String(bContent);
        } else if (tag == (byte) 0x51) {
            projectUuid = new String(bContent);
        }

        byte[] otherTlv = new byte[tlvLen - 5 - len];
        System.arraycopy(bTlv, 5 + len, otherTlv, 0, tlvLen - 5 - len);

        return otherTlv;
    }

}
