package action;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5EnCodeAction {

    private static final Logger log = LoggerFactory.getLogger(MD5EnCodeAction.class);
            
    /***
     * MD5加码 生成32位md5码，不可逆加密
     */
    public static String MD5Encrypt(String inStr) {
        MessageDigest md5 = null;
        
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
        
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                 int val = ((int) md5Bytes[i]) & 0xff;
                 if (val < 16){
                     hexValue.append("0");
                 }                 
                 hexValue.append(Integer.toHexString(val));
             }
            
        return hexValue.toString();
    }

    
}
