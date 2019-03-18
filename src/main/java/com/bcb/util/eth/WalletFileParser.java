package com.bcb.util.eth;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.bcb.domain.entity.FinancingWalletInfo;


/**
 * Created by liq on 2018/1/15.
 */
public class WalletFileParser {
	
    public FinancingWalletInfo gainFinancingWalletInfoETH(byte[] data) throws Exception {
        FileUtil fileUtil = new FileUtil();
     //   byte[] src = InputStream2ByteArray("D:\\dl_workspace\\file\\GoodBProject_0000000000000001_00000003_BCB-Financing-2018-001.use.walletdesc");
        byte[] src = data;
        String result = fileUtil.parseICOProject(src, null);
        String[] resArry = result.split("_");
        
        FinancingWalletInfo financingWalletInfo = new FinancingWalletInfo();
        financingWalletInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        financingWalletInfo.setFinancingUuid(StringUtils.isNotBlank(resArry[2]) ? resArry[2] : "-1");
        financingWalletInfo.setToken("0x" + resArry[3]);
        financingWalletInfo.setWalletLevel(Integer.parseInt(resArry[0]));
        financingWalletInfo.setWalletType(Integer.parseInt(resArry[1]));
        financingWalletInfo.setStatus(1);
        financingWalletInfo.setFileByte(src);
        
        return financingWalletInfo;

    }

    public FinancingWalletInfo gainFinancingWalletInfoUSDX(byte[] data) throws Exception {
        FileUtil fileUtil = new FileUtil();
        //   byte[] src = InputStream2ByteArray("D:\\dl_workspace\\file\\GoodBProject_0000000000000001_00000003_BCB-Financing-2018-001.use.walletdesc");
        byte[] src = data;
        String result = fileUtil.parseICOProject(src, null);
        String[] resArry = result.split("_");

        FinancingWalletInfo financingWalletInfo = new FinancingWalletInfo();
        financingWalletInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        financingWalletInfo.setFinancingUuid(StringUtils.isNotBlank(resArry[2]) ? resArry[2] : "-1");
        financingWalletInfo.setToken(resArry[3]);
        financingWalletInfo.setWalletLevel(Integer.parseInt(resArry[0]));
        financingWalletInfo.setWalletType(1);   //钱包类型为主链BCB
        financingWalletInfo.setStatus(1);
        financingWalletInfo.setFileByte(src);

        return financingWalletInfo;

    }
    
    private static byte[] InputStream2ByteArray(String filePath) throws IOException {

    	InputStream in = new FileInputStream(filePath);
    	byte[] data = toByteArray(in);
    	in.close();

    	return data;
    }

    private static byte[] toByteArray(InputStream in) throws IOException {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	byte[] buffer = new byte[1024 * 4];
    	int n = 0;
    	while ((n = in.read(buffer)) != -1) {
    		out.write(buffer, 0, n);
    	}
    	return out.toByteArray();
	}


}
