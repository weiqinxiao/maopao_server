package controllers;

import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

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
     *
     * @param digest
     * @return
     */
    private static String byteToStr(byte[] digest) {
        // TODO Auto-generated method stub
        String strDigest = "";
        for (int i = 0; i < digest.length; i++) {
            strDigest += byteToHexStr(digest[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
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

    public static Result handleMessage() {
        Http.Request request = request();
        String encryptType = StringUtils.isBlank(request.getQueryString("encrypt_type")) ?
                "raw" : request.getQueryString("encrypt_type");
        WxMpXmlMessage inMessage = null;
        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            Document document = request.body().asXml();
            String xml = getStringFromDoc(document);
            inMessage = WxMpXmlMessage.fromXml(xml);
        } else if ("aes".equals(encryptType)) {
            // TODO
            // 是aes加密的消息
//            String msgSignature = request.getQueryString("msg_signature");
//            inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpConfigStorage, timestamp, nonce, msgSignature);
        } else {
            // TODO
//            response.getWriter().println("不可识别的加密类型");
//            return;
        }

        WxMpXmlOutMessage outMessage = WxMpXmlOutMessage.TEXT()
                .content("平板君正在努力开发中...")
                .fromUser(inMessage.getToUserName())
                .toUser(inMessage.getFromUserName())
                .build();

        return ok(outMessage.toXml());

    }

    public static String getStringFromDoc(org.w3c.dom.Document doc)    {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
    }

}
