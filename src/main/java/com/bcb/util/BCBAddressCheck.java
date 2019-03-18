package com.bcb.util;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BCBAddressCheck {


    public boolean checkAddress(String address) throws Exception {
        String checkStr = address.substring(3,address.length());
        byte[] s =  Base58.decode(checkStr);

        byte[] data = Arrays.copyOfRange(s, 0, s.length - 4);
        byte[] checksum = Arrays.copyOfRange(s, s.length - 4, s.length);

        byte[] actualChecksum = Arrays.copyOfRange(encodeRipeMD160(data),0,4);
        if (!Arrays.equals(checksum, actualChecksum)) {
            return false;
        } else {
            return true;
        }
    }

    private  byte[] encodeRipeMD160(byte[] data) throws Exception{
        RIPEMD160Digest d = new RIPEMD160Digest();
        d.update (data, 0, data.length);
        byte[] o = new byte[d.getDigestSize()];
        d.doFinal (o, 0);
        return o;
    }



}
