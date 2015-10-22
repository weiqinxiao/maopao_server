package controllers;

import org.apache.commons.codec.digest.DigestUtils;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.security.Signature;
import java.util.Arrays;

/**
 * Created by jiangecho on 15/10/22.
 */
public class WxController extends Controller {
    private static final String TOKEN = "jiangecho";

    public static Result checkSignature() {
        Http.Request request = request();
        String signature = request.getQueryString("signature");
        String timestamp = request.getQueryString("timestamp");
        String nonce = request.getQueryString("nonce");
        String echostr = request.getQueryString("echostr");

        String[] array = new String[3];
        array[0] = TOKEN;
        array[1] = timestamp;
        array[2] = nonce;
        Arrays.sort(array);

        String tmp = "";
        for (String str : array) {
            tmp += str;
        }
        byte[] bytes = DigestUtils.sha1(tmp);
        String sha1 = byteToStr(bytes);
        if (sha1.equalsIgnoreCase(signature)) {
            return ok(echostr);
        } else {
            return ok("check signature fail");
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param digest
     * @return
     */
    private static String byteToStr(byte[] digest) {
        // TODO Auto-generated method stub
        String strDigest = "";
        for(int i = 0; i < digest.length; i++){
            strDigest += byteToHexStr(digest[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     * @param b
     * @return
     */
    private static String byteToHexStr(byte b) {
        // TODO Auto-generated method stub
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(b >>> 4) & 0X0F];
        tempArr[1] = Digit[b & 0X0F];

        String s = new String(tempArr);
        return s;
    }
}
