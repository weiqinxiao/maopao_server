package controllers;

import model.Record;
import model.RecordList;
import model.UserObject;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;

import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by jiangecho on 15/5/20.
 */
public class Challenge extends Controller{

    // Attention: when submit record, the owner field is null, because it is yourselef
    public static Result submitChallengeRecord(){
        String uid = session("id");
        String startMillis, endMillis;
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        startMillis = params.get("start")[0];
        endMillis = params.get("end")[0];

        Record record = new Record();
        // TODO checkout uid
        String sql = "INSERT INTO t_record(owner_id, start, end) VALUES('%s', %s, %s)";
        sql = String.format(sql, uid, startMillis, endMillis);
        long rowId = DBUtil.insert(sql);
        if (rowId > -1){
            record.setCode(0);
            record.setStartMillis(Long.parseLong(startMillis));
            record.setEndMillis(Long.parseLong(endMillis));
        }

        return ok(Json.toJson(record));
    }

    public static Result getTodayTopChallengeRecord(int topCount){
        String sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_record.start, t_record.end" +
                " FROM t_user INNER JOIN t_record ON t_user.id = t_record.owner_id WHERE t_record.start > %d ORDER BY (t_record.end - t_record.start)" +
                " DESC LIMIT %d";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStartMillis = calendar.getTime().getTime();
        sql = String.format(sql, todayStartMillis, topCount);

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
            while (resultSet.next()){
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
        }finally {
            try {
                if (resultSet != null){
                    resultSet.close();
                    connection.close();
                }
                if (statement != null){
                    statement.close();
                }
                if (connection != null){
                    connection.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }

        }

        return ok(Json.toJson(recordList));
    }


}
