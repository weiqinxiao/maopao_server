package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public static Result follow() {
        return follow(1);
    }

    public static Result unFollow() {
        return follow(2);
    }

    //fans count
    public static Result getFollowCount(long ownerId) {
        ObjectNode objectNode = Json.newObject();
        long uid;
        if (ownerId < 0) {
            uid = ControllerUtil.getLoginUid();
            if (uid < 0) {
                return ControllerUtil.newUnLoginResponse();
            }
        } else {
            uid = ownerId;
        }

        long count = DBUtil.queryCount(Constant.TABLE_FOLLOW, "owner_id = " + uid);
        objectNode.put("count", count);
        if (count >= 0) {
            objectNode.put(Constant.RESPONSE_CODE, 0);
        } else {
            objectNode.put(Constant.RESPONSE_CODE, -1);
        }

        return ok(objectNode);
    }

    public static Result getFollowedCount(long ownerId) {

        ObjectNode objectNode = Json.newObject();
        long uid;
        if (ownerId < 0) {
            uid = ControllerUtil.getLoginUid();
            if (uid < 0) {
                objectNode.put(Constant.RESPONSE_CODE, Constant.UN_LOGIN);
                objectNode.put(Constant.RESPONSE_MSG, "parameter is null and not login");
                return ok(objectNode);
            }
        } else {
            uid = ownerId;
        }

        long count = DBUtil.queryCount(Constant.TABLE_FOLLOW, "follow_owner_id= " + uid);
        objectNode.put("count", count);
        if (count >= 0) {
            objectNode.put(Constant.RESPONSE_CODE, 0);
        } else {
            objectNode.put(Constant.RESPONSE_CODE, -1);
        }

        return ok(objectNode);
    }

    /**
     * @param flag    1, follow; 2, followed
     * @param page pageIndex start from 1
     * @param pageSize
     * @param ownerId
     * @return
     */
    private static Result getFollowOrFollowedList(int flag, long ownerId, int page, int pageSize) {
        long uid = -1;
        if (ownerId < 0) {
            uid = ControllerUtil.getLoginUid();
            if (uid < 0) {
                return ControllerUtil.newUnLoginResponse();
            }
            ownerId = uid;
        }

        int offset = (page - 1) * pageSize;
        String sql;
        if (flag == 1) { // follow
            sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_user.created_at " +
                    "FROM t_user INNER JOIN t_owner_follow ON t_user.id = t_owner_follow.follow_owner_id WHERE owner_id = %d  ORDER BY t_owner_follow.id DESC LIMIT %d, %d";
            sql = String.format(sql, ownerId, offset, pageSize);
        } else { // followed
            sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_user.created_at " +
                    "FROM t_user INNER JOIN t_owner_follow ON t_user.id = t_owner_follow.owner_id WHERE follow_owner_id = %d  ORDER BY t_owner_follow.id DESC LIMIT %d, %d";
            sql = String.format(sql, ownerId, offset, pageSize);
        }

        Connection connection = DB.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        FollowList followList = new FollowList();
        ObjectNode objectNode = Json.newObject();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            UserObject userObject;
            followList.setCode(0);
            while (resultSet.next()) {
                userObject = new UserObject(resultSet);
                userObject.id = resultSet.getString("id");
                userObject.name = resultSet.getString("name");
                userObject.avatar = resultSet.getString("head_url");
                userObject.created_at = resultSet.getLong("created_at");
                followList.addFollow(userObject);
            }
            objectNode.put("code", 0);
            objectNode.put("data", Json.toJson(followList));
        } catch (SQLException e) {
            objectNode.put("code", -1);
            e.printStackTrace();
            followList.setCode(-1);
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

        return ok(Json.toJson(objectNode));
    }

    public static Result getFollowedList(long ownerId, int page, int pageSize) {
        return getFollowOrFollowedList(2, ownerId, page, pageSize);
    }

    public static Result getFollowList(long ownerId, int page, int pageSize){
        return getFollowOrFollowedList(1, ownerId, page, pageSize);
    }


    public static Result user(String uid) {
        final String querySql = "SELECT id, name, head_url FROM t_user WHERE id = " + uid;
        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;
        connection = DB.getConnection();
        UserObject userObject = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(querySql);
            if (resultSet.next()) {
                userObject = new UserObject(resultSet);
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

        ObjectNode objectNode = Json.newObject();
        if (userObject != null) {
            int fans_count = (int) DBUtil.queryCount(Constant.TABLE_FOLLOW, "owner_id = " + uid);
            int follows_count = (int) DBUtil.queryCount(Constant.TABLE_FOLLOW, "follow_owner_id= " + uid);
            userObject.fans_count = fans_count;
            userObject.follows_count = follows_count;

            objectNode.put("code", 0);
            objectNode.put("data", Json.toJson(userObject));
        } else {
            objectNode.put("code", -1);
        }
        return ok(objectNode);

    }

    public static Result currentUser() {
        //long uid = ControllerUtil.getLoginUid();
        long uid = 2;
        if (uid < 0) {
            return ControllerUtil.newUnLoginResponse();
        }

//        String id = resultSet.getString("id");
//        String name = resultSet.getString("name");
//        String headImgUrl = resultSet.getString("head_url");
        return user(uid + "");
    }


    /**
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
            if (flag == 1) {
                followList = follow(statement, uid, followId);
            } else if (flag == 2) {
                if (unFollow(statement, uid, followId)) {
                    followList.setCode(0);
                } else {
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

    private static boolean unFollow(Statement statement, String uid, String followId) throws SQLException {
        String deleteSql = "DELETE FROM t_owner_follow WHERE owner_id = '%s' AND follow_owner_id = '%s'";
        deleteSql = String.format(deleteSql, uid, followId);
        int result = statement.executeUpdate(deleteSql);

        return result > 0 ? true : false;
    }

}
