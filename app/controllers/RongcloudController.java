package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.F.Promise;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import util.CodeUtil;
import util.DBUtil;

import javax.management.ObjectName;
import java.util.List;

/**
 * Created by jiangecho on 15/11/7.
 */
public class RongcloudController extends Controller {

    private static final String APP_KEY = "k51hidwq1b7fb";
    private static final String APP_SECRET = "7giBocPZDvZE5x";

    private static String calcSignature(String appSecret, String nonce, String timestamp) {
        return CodeUtil.hexSHA1(appSecret + nonce + timestamp);
    }

    public static Promise<Result> fetchRongcloudToken() {

        long uid = ControllerUtil.getLoginUid();
        if (uid < 0) {
            return Promise.pure(ControllerUtil.newUnLoginResponse());
        }

        String rongcloudTokenUrl = "https://api.cn.ronghub.com/user/getToken.json";

        List<JsonNode> jsonNodeList = DBUtil.executeQuery("t_user", new String[]{"name", "head_url"}, "id = " + uid);
        ObjectNode resultNode = Json.newObject();
        if (jsonNodeList == null || jsonNodeList.size() == 0) {
            resultNode.put("code", -1);
            return Promise.pure(ok(resultNode));
        }

        ObjectNode postNode = Json.newObject();
        postNode.put("userId", uid);
        postNode.put("name", jsonNodeList.get(0).path("name"));
        postNode.put("portraitUri", jsonNodeList.get(0).path("head_url"));

        String nonce = String.valueOf(Math.random() * 1000000);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = calcSignature(APP_SECRET, nonce, timestamp);

        Promise<Result> fetchResultPromise = WS.url(rongcloudTokenUrl)
                .setHeader("App-Key", APP_KEY)
                .setHeader("Nonce", nonce)
                .setHeader("Timestamp", timestamp)
                .setHeader("Signature", signature)
                .post(jsonNodeList.get(0))
                .map(wsResponse -> wsResponse.asJson()).map(jsonNode -> {
                    if (jsonNode.path("code").asInt() == 200) {
                        resultNode.put("code", 0);
                        resultNode.put("token", jsonNode.path("token"));
                    } else {
                        resultNode.put("code", -1);
                        resultNode.put("rongCloudCode", jsonNode.path("code"));
                    }
                    return ok(resultNode);
                });


        return fetchResultPromise;
    }
}
