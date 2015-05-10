package util;

import com.qiniu.util.Auth;

/**
 * Created by jiangecho on 15/5/9.
 */
public class QiniuUtil {
    private static final String APP_KEY = "KMA1TsVFfbaFVlS04nCwrdWB0hiGNLi_isuRsoHN";
    private static final String APP_SECRET = "eGyJuSrAUta7np-II2qjzhfe8FR7kIHCaRpi7guF";

    public static String getUploadToken(){
        Auth auth = Auth.create(APP_KEY, APP_SECRET);
        return auth.uploadToken("maopao");
    }
}
