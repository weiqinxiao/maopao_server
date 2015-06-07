package util;

import com.tencent.xinge.Message;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

/**
 * Created by jiangecho on 15/5/14.
 */
public class XGUtil {
    private static final long ACCESS_ID = 2100112110;
    private static final String SECRET_KEY = "9a4277af000f76b89d0f9d0c41f86e5c";

    private static XingeApp xingeApp = new XingeApp(2100112110, "9a4277af000f76b89d0f9d0c41f86e5c");

    public static JSONObject pushAccountAndroid(String title,String content,String account){
        return XingeApp.pushAccountAndroid(ACCESS_ID, SECRET_KEY, title, content, account);
    }

    public static JSONObject pushCommentNotificationToSingelAccountAndroid(String account, long tweetId){
        Message message = new Message();
        String tmp = "{\"tweetId\":%d}";
        String accountAndroid = "plank_" + account;
        message.setContent(String.format(tmp, tweetId));
        return xingeApp.pushSingleAccount(0, accountAndroid, message);
    }
}
