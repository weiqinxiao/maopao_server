package controllers;

import constant.Constant;
import model.FollowList;
import model.UserObject;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.DBUtil;
import util.StringUtil;
import util.TextContentUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by jiangecho on 15/5/9.
 */
public class User extends Controller {

    /**
     * TODO: check the user exists or not
     *
     * @return
     */
    public static Result login() {
        Http.Request request = request();
        Map<String, String[]> params = request.body().asFormUrlEncoded();
        String openId = params.get("openId")[0];
        String name = params.get("name")[0];
        String headImgUrl = params.get("headImgUrl")[0];
        long id = -1;

        String nameWithOutNoneUnsupportChar = TextContentUtil.removeNonBmpUnicode(name);
        UserObject userObject = null;

        String querySql = "SELECT * FROM t_user WHERE openid = '%s'";
        String insertSql = "INSERT INTO t_user(name, head_url, created_at, openid) VALUES('%s', '%s', CURRENT_TIMESTAMP(), '%s')";
        querySql = String.format(querySql, openId);
        insertSql = String.format(insertSql, nameWithOutNoneUnsupportChar, headImgUrl, openId);

        Connection connection = DB.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(querySql);

            if (resultSet.next()) {
                id = resultSet.getLong("id");
            } else {
                id = DBUtil.insert(insertSql);
            }

            if (id > 0) {
                userObject = new UserObject(id, name, headImgUrl, System.currentTimeMillis());
                session("id", id + "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // TODO handle exception case
        return ok(Json.toJson(userObject));
    }

    public static Result follow(){
        return follow(1);
    }

    public static Result unFollow(){
        return follow(2);
    }

    /**
     *
     * @param flag 1, follow; 2, unFollow
     * @return
     */
    private static Result follow(int flag) {
        //String uid = session("id");
        String uid = "2";
        FollowList followList = new FollowList();
        if (uid == null || uid.trim().length() == 0) {
            followList.setCode(Constant.UN_LOGIN);
            return ok(Json.toJson(followList));
        }

        Http.Request request = request();
        Map<String, String[]> params = request.body().asFormUrlEncoded();
        String followId = null;
        if (params != null) {
            String[] values = params.get("followId");
            if (values != null) {
                followId = values[0];
            }
        }
        if (StringUtil.isEmputy(followId)) {
            followList.setCode(Constant.FOLLOW_ID_ERROR);
            return ok(Json.toJson(followList));
        }

        Connection connection = DB.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            if (flag == 1){
                followList = follow(statement, uid, followId);
            }else if (flag == 2){
                if (unFollow(statement, uid, followId)){
                    followList.setCode(0);
                }else {
                    followList.setCode(Constant.UN_FOLLOW_FAIL);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            followList.setCode(-1);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return ok(Json.toJson(followList));
    }

    private static FollowList follow(Statement statement, String uid, String followId) throws SQLException {
        String querySql = "SELECT * FROM t_owner_follow WHERE owner_id = '%s' and follow_owner_id = '%s'";
        String insertSql = "INSERT INTO t_owner_follow(owner_id, follow_owner_id) VALUES('%s', '%s')";
        FollowList followList = new FollowList();

        ResultSet resultSet;
        querySql = String.format(querySql, uid, followId);
        insertSql = String.format(insertSql, uid, followId);
        resultSet = statement.executeQuery(querySql);
        if (resultSet.next()) {
            // all followed
            followList.setCode(Constant.ALREADY_FOLLOWED);
        } else {
            resultSet.close();
            statement.execute(insertSql);
            followList.setCode(0);
        }
        return followList;

    }

    public static boolean unFollow(Statement statement, String uid, String followId) throws SQLException {
        String deleteSql = "DELETE FROM t_owner_follow WHERE owner_id = '%s' AND follow_owner_id = '%s'";
        deleteSql = String.format(deleteSql, uid, followId);
        int result = statement.executeUpdate(deleteSql);

        return result > 0 ? true : false;
    }

}
