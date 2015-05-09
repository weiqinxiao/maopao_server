package controllers;

import model.UserObject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.DBUtil;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by jiangecho on 15/5/9.
 */
public class User extends Controller{

    /**
     * TODO: check the user exists or not
     * @return
     */
    public static Result login(){
        Http.Request request = request();
        Map<String, String[]> params = request.body().asFormUrlEncoded();
        String openId = params.get("openId")[0];
        String name = params.get("name")[0];
        String headImgUrl = params.get("headImgUrl")[0];

        String sql = "INSERT INTO t_user(name, head_url, created_at, openid) VALUES('%s', '%s', CURRENT_TIMESTAMP(), '%s')";
        sql = String.format(sql, name, headImgUrl, openId);

        long id = DBUtil.insert(sql);
        UserObject userObject = new UserObject(id, name, headImgUrl);

        session("id", id + "");
        return ok(Json.toJson(userObject));
    }
}
