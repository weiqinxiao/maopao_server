package util;

import com.tencent.xinge.XingeApp;
import org.json.JSONObject;

/**
 * Created by jiangecho on 15/5/14.
 */
public class XGUtil {
    private static final long ACCESS_ID = 2100112110;
    private static final String SECRET_KEY = "9a4277af000f76b89d0f9d0c41f86e5c";

    public static JSONObject pushAccountAndroid(String title,String content,String account){
        return XingeApp.pushAccountAndroid(ACCESS_ID, SECRET_KEY, title, content, account);
    }
}
