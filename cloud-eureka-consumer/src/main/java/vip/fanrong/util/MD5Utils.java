package vip.fanrong.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Rong on 2018/1/17.
 */
public class MD5Utils {
    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getMD5(String str) {
        md.update(str.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }
}
