package com.bcb.util;

import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class IBanUtil {
	private static final int ADDRESS_LENGTH = 42;
	private static String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String toAddress(String str)
    {
    	if (str.contains("iban:")) {
        	String iban = str.split(":")[1].split("[?]")[0];
        	BigInteger decodeStr = Decode36Big(iban.substring(4));
        	
        	String tmp = decodeStr.toString(16);
        	while (tmp.length() < 40) {
				tmp = "0" + tmp;
			}
        	
        	// NOTE: certain tools include an extra leading 0, drop that
        	if ((tmp.length() == 42) && tmp.startsWith("0") && tmp.startsWith("00")) {
        		tmp = tmp.substring(2);
        	}
        	
        	return "0x" + tmp;
    	}
    	
    	return str;

    }

	/**
	 *  验证地址是否合法
	 * @param address
	 * @return
	 */
	public static Boolean isAddress(String address) {
		try {
			String iban = encodeAddressToIBAN(address);
			isIBAN(iban);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 验证iban是否合法
	 * @param iban
	 * @return
	 */
	public static Boolean isIBAN(String iban) {
		return "XE".equalsIgnoreCase(iban.substring(0, 2)) && mod9710(prepare(iban)) == 1;
	}
    
//    public static void main(String[] args) throws Exception {
//		System.out.println(toAddress("iban:XE167U3FXZJH06ZG28JW4OZMHNCGFICR7RN?amount=0&token=ETH"));
//		System.out.println(encodeAddressToIBAN("0x431521da6526cf887ad1b2dfa855d1c54e146413"));
//		System.out.println(isAddress("0x2C1dB53c26482c39D4AA0B2BdA9F7C08e2949AdD"));
//		System.out.println(isIBAN("XE167U3FXZJH06ZG28JW4OZMHNCGFICR7RN"));
//	}

    private static BigInteger Decode36Big(String value)
    {
    	String testString = new StringBuffer(value.toLowerCase()).reverse().toString();
    	char []tmpchar = testString.toCharArray();

    	BigInteger number = BigInteger.ZERO;
    	BigInteger x36 = new BigInteger("36");
    	int index = 0;
    	for (char character : tmpchar)
    	{
    		BigInteger temp = x36.pow(index);
    		Integer twst = CHARACTERS.indexOf(character);
    		BigInteger index0 = new BigInteger(twst.toString());
    		number = number.add(temp.multiply(index0));
    		index++;
    	}
    			
    	return number;
    }

    private static String base36Encode(BigInteger input) {
		BigInteger x36 = new BigInteger("36");

		StringBuilder sb = new StringBuilder();
		while (input.compareTo(BigInteger.ZERO) != 0)
		{
			BigInteger[] bis = input.divideAndRemainder(x36);
			int index = bis[1].intValue();
			input = bis[0];
			sb.append(CHARACTERS.charAt(index));
		}
		return sb.reverse().toString().toUpperCase();
	}

    private static String encodeAddressToIBAN(String address) throws Exception {
		if ((address.length() == ADDRESS_LENGTH) && (address.charAt(0) == '0') && (address.charAt(1) == 'x' || address.charAt(1) == 'X')) {
			if ((address.charAt(2) == '0') && (address.charAt(3) == '0')) {
				address = address.substring(4);
			} else {
				address = address.substring(2);
			}
		} else if ((address.length() == ADDRESS_LENGTH - 2)  && (address.charAt(0) != '0') && (address.charAt(1) != 'x' && address.charAt(1) != 'X')) {
			if ((address.charAt(0) == '0') && (address.charAt(1) == '0')) {
				address = address.substring(2);
			}
		} else {
			throw new Exception("Not a valid input for Ethereum IBAN");
		}

		BigInteger bigInteger = new BigInteger(address, 16);
		String iban = base36Encode(bigInteger);
		int checksum = 98 - mod9710(prepare("XE00" + iban));
		return "XE" + checksum + iban;
	}

	private static int mod9710(String input) {
		int m = 0;
		for (int i = 0; i < input.length(); i++) {
			m *= 10;
			m += input.charAt(i) - 48; // parse int
			m %= 97;
		}
		return m;
	}

	private static String prepare(String iban) {
		// move front to the back
		iban = iban.substring(4) + iban.substring(0, 4);

		// translate letters to numbers
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < iban.length(); i++) {
			int code = (int)iban.charAt(i);
			if (code >= 65 && code <= 90) {
				// A = 10, B = 11, ... Z = 35
				sb.append(code - 65 + 10);
			} else {
				sb.append(code - 48); //parse int
			}
		}

		return sb.toString();
	}
}
