package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import constant.Constant;
import play.mvc.Controller;
import play.mvc.Result;

import model.Record;
import model.RecordList;
import model.UserObject;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;
import util.StringUtil;
import util.TimeUtil;

import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by jiangecho on 15/5/28.
 */
public class ControllerUtil extends Controller {

    // TODO add some common methods here, such as query the count and so on
    public static Result getTopRecord(String tableName, int topCount, long startMillis) {
        String sql;
        //SELECT t_user.id, t_user.name, t_user.head_url, test.start, test.end FROM t_user
        // INNER JOIN (select * from t_challenge_record order by (end - start) desc) as test
        // ON t_user.id = test.owner_id WHERE test.start > 1438531200000 GROUP BY t_user.id
        // ORDER BY (test.end - test.start) DESC LIMIT  10;

        if ("t_train_record".equals(tableName)) {
            sql = "SELECT t_user.id, t_user.name, t_user.head_url, tmp.start, tmp.end FROM t_user " +
                    "INNER JOIN (SELECT * FROM t_train_record WHERE start > %d ORDER BY (end - start) DESC) AS tmp " +
                    "ON t_user.id = tmp.owner_id  GROUP BY t_user.id ORDER BY (tmp.end - tmp.start)" +
                    " DESC LIMIT %d";

        } else if ("t_challenge_record".equals(tableName)) {
            sql = "SELECT t_user.id, t_user.name, t_user.head_url, tmp.start, tmp.end FROM t_user " +
                    "INNER JOIN (SELECT * FROM t_challenge_record WHERE start > %d ORDER BY (end - start) DESC) AS tmp " +
                    "ON t_user.id = tmp.owner_id GROUP BY t_user.id ORDER BY (tmp.end - tmp.start)" +
                    " DESC LIMIT %d";

        } else {
            return null;
        }

        sql = String.format(sql, startMillis, topCount);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        UserObject userObject;
        Record record;
        RecordList recordList = new RecordList();
        String uid, name, headImgUrl;
        long start, end;
        try {
            connection = DB.getConnection();
            statement = connection.createStatement();
            resultSet = DBUtil.query(statement, sql);

            recordList.setCode(0);
            while (resultSet.next()) {
                uid = resultSet.getString("id");
                name = resultSet.getString("name");
                headImgUrl = resultSet.getString("head_url");

                start = resultSet.getLong("start");
                end = resultSet.getLong("end");

                userObject = new UserObject(Long.parseLong(uid), name, headImgUrl);
                record = new Record(userObject, start, end);

                recordList.addRecord(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                    connection.close();
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

        return ok(Json.toJson(recordList));
    }

    public static Result newUnLoginResponse() {
        ObjectNode objectNode = Json.newObject();
        objectNode.put(Constant.RESPONSE_CODE, Constant.UN_LOGIN);
        objectNode.put(Constant.RESPONSE_MSG, "paramter is null and not login");
        return ok(objectNode);
    }

    /**
     * @return uid >= 0, login; < 0, not login
     */
    public static long getLoginUid() {
        String tmp = session("uid");
        long uid = -1;
        try {
            uid = Long.parseLong(tmp);
        } catch (Exception e) {
            uid = -1;
        }

        return uid;
    }
}
