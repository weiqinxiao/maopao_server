package controllers;

import model.UserObject;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.DBUtil;

import java.sql.*;
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
        long id = -1;

        UserObject userObject = null;

        String querySql = "SELECT * FROM t_user WHERE openid = '%s'";
        String insertSql = "INSERT INTO t_user(name, head_url, created_at, openid) VALUES('%s', '%s', CURRENT_TIMESTAMP(), '%s')";
        querySql = String.format(querySql, openId);
        insertSql = String.format(insertSql, name, headImgUrl, openId);

        Connection connection = DB.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(querySql);

            if (resultSet.next()){
                id = resultSet.getLong("id");
            }else {
                id = DBUtil.insert(insertSql);
            }

            userObject = new UserObject(id, name, headImgUrl, System.currentTimeMillis());
            session("id", id + "");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (resultSet != null){
                    resultSet.close();
                }
                if (statement != null){
                    statement.close();
                }
                if (connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // TODO handle exception case
        return ok(Json.toJson(userObject));
    }
}
