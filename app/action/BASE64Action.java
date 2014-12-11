package action;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * BASE64加密解密
 */
public class BASE64Action {

    // 解密
    public static String Decrypt(String key) throws Exception {
        if (key == null || StringUtils.isEmpty(key)) {
            return null;
        }

        byte[] byteArray = (new BASE64Decoder()).decodeBuffer(key);
        return (new String(byteArray));
    }

    // 加密
    public static String Encrypt(String password) throws Exception {
        if (password == null || StringUtils.isEmpty(password)) {
            return null;
        }

        byte[] key = password.getBytes();
        return (new BASE64Encoder()).encodeBuffer(key);
    }

}
