package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constant.Constant;
import model.SyncRecord;
import model.SyncRecordList;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/10/26.
 */
public class Sync extends Controller {
    public static Result upload() {
        String uid = session("id");
        //String uid = "100000";
        SyncRecordList result = new SyncRecordList();
        if (uid == null || uid.length() == 0) {
            result.setCode(Constant.UN_LOGIN);
            return ok(Json.toJson(result));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = request().body().asJson();
        SyncRecordList syncRecordList = null;
        try {
            syncRecordList = objectMapper.treeToValue(jsonNode, SyncRecordList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (syncRecordList == null || syncRecordList.getRecords() == null || syncRecordList.getRecords().size() == 0) {
            result.setCode(0);
            return ok(Json.toJson(result));
        }


        String table = syncRecordList.getTable();
        switch (table) {
            case "train":
            case "challenge":
                return insertDayRecord(uid, syncRecordList);
            default:
                return insertDetailRecord(uid, syncRecordList);
        }
    }

    private static Result insertDayRecord(String uid, SyncRecordList syncRecordList) {
        // t_train and t_challenge 用户存储用户每天的数据，不是每次的数据
        String table;
        if (syncRecordList.getTable().equals("train")) {
            table = "t_train";
        } else {
            table = "t_challenge";
        }

        SyncRecordList result = new SyncRecordList();
        final String sql = "INSERT INTO " + table + " (owner_id, date, duration) VALUES('%s', '%s', %d) ON DUPLICATE KEY UPDATE duration = %d";
        String tmp;
        List<String> sqls = new ArrayList<>();
        for (SyncRecord record : syncRecordList.getRecords()) {
            tmp = String.format(sql, uid, record.getDate(), record.getDuration(), record.getDuration());
            sqls.add(tmp);
        }

        int count = DBUtil.bulkInsert(sqls);
        if (count > 0) {
            result.setCode(0);
        } else {
            result.setCode(-1);
        }
        return ok(Json.toJson(result));
    }

    private static Result insertDetailRecord(String uid, SyncRecordList syncRecordList) {
        // t_train_record and t_challenge_record 用户存储用户每天的数据，不是每次的数据
        String table;
        if (syncRecordList.getTable().equals("trainDetail")) {
            table = "t_train_record";
        } else {
            table = "t_challenge_record";
        }

        SyncRecordList result = new SyncRecordList();
        final String sql = "INSERT INTO " + table + " (owner_id, start, end) VALUES('%s', %d, %d)";
        String tmp;
        List<String> sqls = new ArrayList<>();
        for (SyncRecord record : syncRecordList.getRecords()) {
            tmp = String.format(sql, uid, record.getStartMillis(), record.getEndMillis());
            sqls.add(tmp);
        }

        int count = DBUtil.bulkInsert(sqls);
        if (count > 0) {
            result.setCode(0);
        } else {
            result.setCode(-1);
        }
        return ok(Json.toJson(result));

    }

    //http://localhost:9000/api/sync/download/t_train?limit=1
    public static Result downloadDayRecord(String table, int limit) {
        String owner_id = session("id");
        //String owner_id = "100000";
        SyncRecordList syncRecordList = new SyncRecordList();
        if (owner_id == null || owner_id.length() == 0) {
            syncRecordList.setCode(Constant.UN_LOGIN);
            return ok(Json.toJson(syncRecordList));
        }

        Connection connection = DB.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "SELECT date, duration from " + table + " WHERE owner_id = " + owner_id + " order BY id DESC limit " + limit;
            ResultSet resultSet = statement.executeQuery(sql);
            SyncRecord syncRecord;
            while (resultSet.next()) {
                syncRecord = new SyncRecord();
                syncRecord.setDate(resultSet.getString("date"));
                syncRecord.setDuration(resultSet.getLong("duration"));
                syncRecordList.addRecord(syncRecord);
            }
            syncRecordList.setCode(0);
            syncRecordList.setTable(table);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

        return ok(Json.toJson(syncRecordList));
    }


}
