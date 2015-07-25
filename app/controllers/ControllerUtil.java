package controllers;

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

        if ("t_train_record".equals(tableName)) {
            sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_train_record.start, t_train_record.end" +
                    " FROM t_user INNER JOIN t_train_record ON t_user.id = t_train_record.owner_id WHERE t_train_record.start > %d ORDER BY (t_train_record.end - t_train_record.start)" +
                    " DESC LIMIT %d";

        } else if ("t_challenge_record".equals(tableName)) {
            sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_challenge_record.start, t_challenge_record.end" +
                    " FROM t_user INNER JOIN t_challenge_record ON t_user.id = t_challenge_record.owner_id WHERE t_challenge_record.start > %d ORDER BY (t_challenge_record.end - t_challenge_record.start)" +
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
}
