package com.bcb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kx on 2018/1/24.
 */
public class RandomUtil {

    public static String getUuid(String prefix){
        String uuid = prefix;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        uuid += sdf.format(new Date());
        uuid += GetRandomString(6);
        return uuid;
    }

    public static String GetNumericRandomString(Integer length)
    {
        return GetRandomString(length, Integer.valueOf(1));
    }

    public static String GetRandomString(Integer length)
    {
        return GetRandomString(length, Integer.valueOf(2));
    }

    private static String GetRandomString(Integer length, Integer type)
    {
        StringBuffer randomCode = new StringBuffer();

        for (int i = 0; i < length.intValue(); i++) {
            switch (type.intValue()) {
                case 1:
                    randomCode.append(getRandomInteger());
                    break;
                case 2:
                    randomCode.append(getRandomUpperCase());
                    break;
                case 3:
                    randomCode.append(getRandomLowerCase());
                    break;
                default:
                    randomCode.append(getRandomChar());
            }

        }

        return randomCode.toString();
    }

    private static String getRandomChar()
    {
        int index = (int)Math.round(Math.random() * 2.0D);
        switch (index) {
            case 0:
                return getRandomUpperCase();
            case 1:
                return getRandomLowerCase();
        }
        return getRandomInteger();
    }

    private static String getRandomUpperCase()
    {
        return String.valueOf((char)(int)Math.round(Math.random() * 25.0D + 65.0D));
    }

    private static String getRandomLowerCase()
    {
        return String.valueOf((char)(int)Math.round(Math.random() * 25.0D + 97.0D));
    }

    private static String getRandomInteger()
    {
        return String.valueOf(Math.round(Math.random() * 9.0D));
    }
}
