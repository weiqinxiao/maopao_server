package controllers;

import constant.Constant;
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
import java.util.Map;

/**
 * Created by jiangecho on 15/5/20.
 */
public class Challenge extends Controller{

    // Attention: when submit record, the owner field is null, because it is yourselef
    public static Result submitChallengeRecord(){
        String uid = session("id");

        Record record = new Record();
        if (uid == null || uid.length() == 0){
            record.setCode(Constant.UN_LOGIN);
            return ok(Json.toJson(record));
        }
        String startMillis, endMillis;
        Map<String, String[]> params = request().body().asFormUrlEncoded();
        startMillis = params.get("start")[0];
        endMillis = params.get("end")[0];

        // TODO checkout uid
        String sql = "INSERT INTO t_challenge_record(owner_id, start, end) VALUES('%s', %s, %s)";
        sql = String.format(sql, uid, startMillis, endMillis);
        long rowId = DBUtil.insert(sql);
        if (rowId > -1){
            record.setCode(0);
            record.setStartMillis(Long.parseLong(startMillis));
            record.setEndMillis(Long.parseLong(endMillis));
        }

        return ok(Json.toJson(record));
    }

    public static Result getTodayTopChallengeRecorad(int topCount){
        String tableName = "t_challenge_record";
        long todayStartMillis = TimeUtil.getTodayStartMillis();
        return ControllerUtil.getTopRecord(tableName, topCount, todayStartMillis);
    }

    public static Result getCurrentWeekChallengeRecord(int topCount){
        String tableName = "t_challenge_record";
        long currentWeekStartMillis = TimeUtil.getCurrentWeekStartMillis();
        return ControllerUtil.getTopRecord(tableName, topCount, currentWeekStartMillis);
    }

    public static Result getTodayChallengeCount(){
        long count = DBUtil.queryCount(Constant.TABLE_CHALLENGE_DETAIL , "start > " + TimeUtil.getTodayStartMillis());
        return ok(Long.toString(count + 100)); // haha, at least 100
    }

}
